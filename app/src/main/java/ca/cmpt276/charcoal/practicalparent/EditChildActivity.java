package ca.cmpt276.charcoal.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;

public class EditChildActivity extends AppCompatActivity {
    public static final String EXTRA_CHILD_INDEX = "ca.cmpt276.charcoal.practicalparent - childIndex";
    private int childIndex;
    private EditText nameBox;
    private final ChildManager manager = ChildManager.getInstance();

    public static Intent makeLaunchIntent(Context context, int childIndex) {
        Intent intent = new Intent(context, EditChildActivity.class);
        intent.putExtra(EXTRA_CHILD_INDEX, childIndex);
        return intent;
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        childIndex = intent.getIntExtra(EXTRA_CHILD_INDEX, -1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setupSaveButton();
        nameBox = findViewById(R.id.childNameTextBox);
        extractIntentData();
        preFillNameBox();

    }

    private void preFillNameBox() {
        if (childIndex >= 0) {
            Child currentChild = manager.getChild(childIndex);
            nameBox.setText(currentChild.getName());
        }
    }

    private void setupSaveButton() {
        Button saveBtn = findViewById(R.id.editChildSave_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChild();
            }
        });
    }

    private void saveChild() {
        String childName = nameBox.getText().toString();
        if (nameIsValid(childName)) {
            if (childName.length() >= 0) {
                Child currentChild = manager.getChild(childIndex);
                currentChild.setName(childName);
            } else {
                manager.add(new Child(childName));
            }
        }
    }

    private boolean nameIsValid(String childName) {
        if (childName.length() < 0) {
            nameBox.setError(getString(R.string.editChildNameError));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_child, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            if (childIndex >= 0) {
                manager.remove(childIndex);
            } else {
                Toast.makeText(this, "You can only delete a child you are editing", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}