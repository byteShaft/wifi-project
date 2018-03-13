package com.byteshaft.wififinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by s9iper1 on 1/10/18.
 */

public class WifiDatabase extends SQLiteOpenHelper {

    private SQLiteDatabase globalDatabase;

    public WifiDatabase(Context context) {
        super(context, DbConstants.DATABASE_NAME, null, DbConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DbConstants.TABLE_CREATE);
        Log.i("TAG", "Database created !!!");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(String.format("DROP TABLE IF EXISTS%s",
                DbConstants.TABLE_NAME));
        onCreate(sqLiteDatabase);
    }

    public void createNewEntry(Wifi wifi, String building, String  desiredName, String level, String classSelected) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbConstants.ORIGINAL_NAME, wifi.getSsid());
        values.put(DbConstants.BUILDING, building);
        values.put(DbConstants.DESIRED_NAME, desiredName);
        values.put(DbConstants.LEVEL, level);
        values.put(DbConstants.CLASS, classSelected);
        sqLiteDatabase.insert(DbConstants.TABLE_NAME, null, values);
        Log.i("TAG", "created New Entry");
        sqLiteDatabase.close();
    }

    public JSONObject getRecordsByName(String originalName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT * FROM " + DbConstants.TABLE_NAME + " WHERE " +
                DbConstants.ORIGINAL_NAME + " ='"+ originalName+"'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Log.i("TAG", " count " + cursor.getCount());
        JSONObject jsonObject = new JSONObject();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(
                    DbConstants.ORIGINAL_NAME));
            String building = cursor.getString(cursor.getColumnIndex(
                    DbConstants.BUILDING));
            int desiredName =  cursor.getInt(cursor.getColumnIndex(
                    DbConstants.DESIRED_NAME));
            String level = cursor.getString(cursor.getColumnIndex(
                    DbConstants.LEVEL));
            String selectedClass = cursor.getString(cursor.getColumnIndex(
                    DbConstants.CLASS));

            try {
                jsonObject.put("class", selectedClass);
                jsonObject.put("level", level);
                jsonObject.put("building", building);
                jsonObject.put("desired_name", desiredName);
                jsonObject.put("name", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        sqLiteDatabase.close();
        return jsonObject;
    }


    public boolean checkIfRecordExist(String name, String selectedClass, String building, String level) {
        SQLiteDatabase sqldb = getReadableDatabase();
        String Query = "Select * FROM " + DbConstants.TABLE_NAME + " WHERE " +
                DbConstants.ORIGINAL_NAME + " ='"+ name+"' AND " + DbConstants.CLASS
                + " ='" + selectedClass + "' AND " + DbConstants.LEVEL
                + " ='" + level + "' AND " + DbConstants.BUILDING
                + " ='" + building ;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
