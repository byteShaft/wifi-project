package com.byteshaft.wififinder;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s9iper1 on 3/14/18.
 */

public class SelectWifiActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "WiFiDemo";
    public WifiManager wifi;
    private WiFiScanReceiver wiFiScanReceiver;
    private ListView listView;
    private ArrayList<Wifi> wifiArrayList;
    private Adapter arrayAdapter;
    public static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1001;
    private String selectedBuilding;
    private String selectedLevel;
    private AlertDialog alertDialog;
    private static SelectWifiActivity instance;

    public static SelectWifiActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_wifi);
        instance = this;
        listView =  findViewById(R.id.list_view);
        selectedBuilding = getIntent().getStringExtra("building");
        selectedLevel = getIntent().getStringExtra("level");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted   ActivityCompat.requestPermissions(thisActivity,
            ActivityCompat.requestPermissions(this,
                    new String[]{(Manifest.permission.ACCESS_COARSE_LOCATION)},
                    MY_PERMISSIONS_REQUEST_READ_LOCATION);
        } else {
            if (Helpers.locationEnabled(getApplicationContext())) {
                start();
            } else {
                Helpers.dialogForLocationEnableManually(this);
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Wifi wifi = wifiArrayList.get(i);
                Log.i("TAG", "wifi " + wifi.getSsid());
                final View dialogView = getLayoutInflater().inflate(R.layout.layout_dialog, null);
                alertDialog = new AlertDialog.Builder(SelectWifiActivity.this).create();
                alertDialog.setTitle("Select Wifi name");
                alertDialog.setCancelable(false);
                alertDialog.setMessage("please desired name for this wifi");
                final EditText etComments = (EditText) dialogView.findViewById(R.id.etComments);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etComments.getText().toString().trim().isEmpty()) {
                            Toast.makeText(SelectWifiActivity.this, "please enter desired name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(getApplicationContext(), SelectClass.class);
                        intent.putExtra("wifi_name", etComments.getText().toString());
                        intent.putExtra("wifi", wifi);
                        intent.putExtra("level", selectedLevel);
                        intent.putExtra("building", selectedBuilding);
                        startActivity(intent);

                    }
                });


                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });


                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Helpers.locationEnabled(getApplicationContext())) {
                        start();
                    } else {
                        Helpers.dialogForLocationEnableManually(this);
                    }

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppGlobals.LOCATION_ENABLE) {
            start();
        }
    }

    private void start() {
        wiFiScanReceiver = new WiFiScanReceiver();
        wifiArrayList = new ArrayList<>();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.startScan();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (wiFiScanReceiver != null) {
            unregisterReceiver(wiFiScanReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wiFiScanReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, WifiStrength.class);
        intent.putExtra("wifi", wifiArrayList);
        startActivity(intent);
    }

    public class WiFiScanReceiver extends BroadcastReceiver {

        private static final String TAG = "WiFiScanReceiver";

        @Override
        public void onReceive(Context c, Intent intent) {
            Log.i(TAG, "receiver called");
            Log.i(TAG, "scan results size " + wifi.getScanResults().size());
            if (wifiArrayList.size() > 0 ) {
                wifiArrayList.clear();
            }
            if (arrayAdapter != null) {
                arrayAdapter.notifyDataSetChanged();
            }
            List<ScanResult> results = wifi.getScanResults();
            for (ScanResult result : results) {
                Wifi wifi = new Wifi();
                wifi.setSsid(result.SSID);
                Log.i(TAG, "ssid " + result.SSID);
                wifi.setStrength(result.level);
                wifiArrayList.add(wifi);
            }
            arrayAdapter = new Adapter(getApplicationContext(), wifiArrayList);
            listView.setAdapter(arrayAdapter);
        }

    }

    private class Adapter extends ArrayAdapter<Wifi> {

        private ViewHolder viewHolder;
        private ArrayList<Wifi> wifis;

        public Adapter(Context context, ArrayList<Wifi> wifis) {
            super(context, R.layout.raw);
            this.wifis = wifis;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.raw, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.strength = (TextView) convertView.findViewById(R.id.strength);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Wifi wifi = wifis.get(position);
            Log.i("TAG", " wifi null " + String.valueOf(wifi == null));
            viewHolder.name.setText(wifi.getSsid());
            viewHolder.strength.setText(String.valueOf(wifi.getStrength()));
            return convertView;
        }

        @Override
        public int getCount() {
            return wifis.size();
        }
    }

    class ViewHolder {

        TextView name;
        TextView strength;
    }
}

