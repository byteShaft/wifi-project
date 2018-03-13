package com.byteshaft.wififinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by s9iper1 on 3/14/18.
 */

public class MainAdmin extends AppCompatActivity {

    private Spinner levelSpinner;
    private Spinner buildingSpinner;
    private Button nextButton;
    private String selectedLevel;
    private String selectedBuilding;
    private static MainAdmin instance;

    public static MainAdmin getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);
        instance = this;
        levelSpinner = findViewById(R.id.level);
        buildingSpinner = findViewById(R.id.building);
        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectWifiActivity.class);
                intent.putExtra("level", selectedLevel);
                intent.putExtra("building", selectedBuilding);
                startActivity(intent);

            }
        });
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("TAG", ""  + adapterView.getItemAtPosition(i));
                selectedLevel = (String) adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedBuilding = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
