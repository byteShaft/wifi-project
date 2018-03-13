package com.byteshaft.wififinder;

/**
 * Created by s9iper1 on 1/10/18.
 */

public class DbConstants {

    public static final String DATABASE_NAME = "wifi.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "wifi";
    public static final String ORIGINAL_NAME = "name";
    public static final String DESIRED_NAME = "desired_name";
    public static final String LEVEL = "level";
    public static final String BUILDING = "building";
    public static final String CLASS = "class";
    public static final String ID_COLUMN = "ID";

    public static final String TABLE_CREATE =
            "CREATE TABLE " +
                    TABLE_NAME + "(" +
                    ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ORIGINAL_NAME + " TEXT , " +
                    DESIRED_NAME + " TEXT , " +
                    LEVEL + " TEXT , " +
                    CLASS + " TEXT , " +
                    BUILDING + " TEXT " + " ) ";


}
