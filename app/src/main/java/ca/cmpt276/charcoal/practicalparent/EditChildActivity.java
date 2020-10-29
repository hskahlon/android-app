package ca.cmpt276.charcoal.practicalparent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditChildActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupSaveButton();

    }

    private void setupSaveButton() {
        Button saveBtn = findViewById(R.id.editChildSave_btn);
        saveBtn.setOnClickListener(v -> Toast.makeText(EditChildActivity.this, "Save name to sharedPrefs", Toast.LENGTH_SHORT).show());
    }
}