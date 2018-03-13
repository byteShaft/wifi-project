package com.byteshaft.wififinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by husnain on 3/13/18.
 */

public class WifiStrength extends AppCompatActivity {

    private Button button;
    private EditText editText;
    private String editTextValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signal);
        button = (Button) findViewById(R.id.ssid_button);
        editText = (EditText) findViewById(R.id.ssid_edit_text);
        final ArrayList<Wifi> myList = (ArrayList<Wifi>) getIntent().getSerializableExtra("wifi");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               editTextValue = editText.getText().toString();
                for (Wifi wifi : myList) {
                    Log.i("TAg", " name " + wifi.getSsid());
                    if ((editTextValue.contentEquals(wifi.getSsid()))) {
                        Toast.makeText(getApplicationContext(), "SSID: "+ editTextValue + " RSSI: "+
                                wifi.getStrength(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


}
