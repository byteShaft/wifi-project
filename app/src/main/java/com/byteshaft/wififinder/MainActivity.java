package com.byteshaft.wififinder;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "WiFiDemo";
    public WifiManager wifi;
    public Button nextButton;
    private WiFiScanReceiver wiFiScanReceiver;
    private ListView listView;
    private ArrayList<Wifi> wifiName;
    private Adapter arrayAdapter;
    public static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView =  findViewById(R.id.list_view);
        nextButton = findViewById(R.id.get_rssi_button);
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
        nextButton.setOnClickListener(this);

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
        wifiName = new ArrayList<>();
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
        intent.putExtra("wifi", wifiName);
        startActivity(intent);
    }

    public class WiFiScanReceiver extends BroadcastReceiver {

        private static final String TAG = "WiFiScanReceiver";

        @Override
        public void onReceive(Context c, Intent intent) {
            Log.i(TAG, "receiver called");
            Log.i(TAG, "scan results size " + wifi.getScanResults().size());
            if (wifiName.size() > 0 ) {
                wifiName.clear();
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
                wifiName.add(wifi);
            }
            arrayAdapter = new Adapter(getApplicationContext(), wifiName);
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
