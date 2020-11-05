package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class RecordActivity extends AppCompatActivity {
    private Button currentRecords;
    private Button priorRecords;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setUpIntialButton();
        setUpRecordSelectorButtons();


    }

    private void setUpIntialButton() {
        currentRecords = findViewById(R.id.currentRecordBtn);
        currentRecords.setBackgroundColor(getColor(R.color.selectedRecord));
        // create view
        updateView();
    }

    private void updateView() {
        
    }

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, RecordActivity.class);
    }
    private void setUpRecordSelectorButtons() {
        currentRecords = findViewById(R.id.currentRecordBtn);
        priorRecords = findViewById(R.id.priorRecordsBtn);
        currentRecords.setOnClickListener(v -> {
            currentRecords.setBackgroundColor(getColor(R.color.selectedRecord));
            priorRecords.setBackgroundColor(getColor(R.color.unSelectedRecord));
        });
        priorRecords.setOnClickListener(v -> {
            priorRecords.setBackgroundColor(getColor(R.color.selectedRecord));
            currentRecords.setBackgroundColor(getColor(R.color.unSelectedRecord));
        });

    }
}