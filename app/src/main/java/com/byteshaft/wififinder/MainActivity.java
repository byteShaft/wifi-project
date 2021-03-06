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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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
    private TextView location;
    private boolean foreground = false;
    private WifiDatabase wifiDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        foreground = true;
        wifiDatabase = new WifiDatabase(getApplicationContext());
        listView =  findViewById(R.id.list_view);
        nextButton = findViewById(R.id.get_rssi_button);
        location = findViewById(R.id.location);
        nextButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_module_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.admin_menu:
                startActivity(new Intent(getApplicationContext(), MainAdmin.class));
                return true;

                default: return false;

        }
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
    protected void onPause() {
        super.onPause();
        foreground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        foreground = true;
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
            ScanResult bestSignal = null;
            if (wifiName.size() > 0 ) {
                wifiName.clear();
            }
            if (arrayAdapter != null) {
                arrayAdapter.notifyDataSetChanged();
            }
            List<ScanResult> results = wifi.getScanResults();
            for (ScanResult result : results) {
                if (bestSignal == null
                        || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0)
                    bestSignal = result;
                Wifi wifi = new Wifi();
                wifi.setSsid(result.SSID);
                Log.i(TAG, "ssid " + result.SSID);
                wifi.setStrength(result.level);
                wifiName.add(wifi);
            }
            arrayAdapter = new Adapter(getApplicationContext(), wifiName);
            listView.setAdapter(arrayAdapter);
            String message = String.format("%s networks found. %s is the strongest.",
                    results.size(), bestSignal.SSID);
            Log.i("TAG", message);
            Log.i("TAG", "" + wifiDatabase.getRecordsByName(bestSignal.SSID));
            JSONObject jsonObject = wifiDatabase.getRecordsByName(bestSignal.SSID);
            StringBuilder stringBuilder = new StringBuilder();
            try {
                stringBuilder.append("Class: ");
                stringBuilder.append(jsonObject.getString("class"));
                stringBuilder.append("\n");
                stringBuilder.append("Level: ");
                stringBuilder.append(jsonObject.getString("level"));
                stringBuilder.append("\n");
                stringBuilder.append("Building: ");
                stringBuilder.append(jsonObject.getString("building"));
                stringBuilder.append("\n");
                stringBuilder.append("Desired Name: ");
                stringBuilder.append(jsonObject.getString("desired_name"));
                stringBuilder.append("\n");
                stringBuilder.append("Wifi SSID: ");
                stringBuilder.append(jsonObject.getString("name"));
                stringBuilder.append("\n");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            location.setText(stringBuilder.toString());

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
