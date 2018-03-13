package com.byteshaft.wififinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by s9iper1 on 3/14/18.
 */

public class SelectClass extends AppCompatActivity {

    private EditText selectClass;
    private Button save;
    private Wifi wifi;
    private String selectedLevel;
    private String selectedBuilding;
    private String wifiName;
    private WifiDatabase wifiDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_class);
        wifi  = (Wifi) getIntent().getSerializableExtra("wifi");
        wifiName = getIntent().getStringExtra("wifi_name");
        selectedBuilding = getIntent().getStringExtra("building");
        selectedLevel = getIntent().getStringExtra("level");
        selectClass = findViewById(R.id.class_name);
        wifiDatabase = new WifiDatabase(getApplicationContext());
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectClass.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SelectClass.this, "please enter class name", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(SelectClass.this, " " + selectedBuilding + " " +
                        selectedLevel + " desired name " + wifiName + " class " + selectClass.getText().toString()
                        +"\n will be saved", Toast.LENGTH_SHORT).show();
                wifiDatabase.createNewEntry(wifi, selectedBuilding, wifiName, selectedLevel, selectClass.getText().toString());
                Toast.makeText(SelectClass.this, "saved!", Toast.LENGTH_SHORT).show();
                MainAdmin.getInstance().finish();
                SelectWifiActivity.getInstance().finish();
                finish();
            }
        });

    }
}
