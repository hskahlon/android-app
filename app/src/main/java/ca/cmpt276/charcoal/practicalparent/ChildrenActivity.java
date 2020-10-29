package ca.cmpt276.charcoal.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChildrenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.addChild_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChildrenActivity.this, "Take me to AddChildActivity", Toast.LENGTH_SHORT).show();
            }
        });

        populateListView();
        registerClickCallback();
    }

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, ChildrenActivity.class);
    }

    private void populateListView() {
        String[] children = {"Jimmy", "Beth", "Walter"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.child_item, children);

        ListView list = findViewById(R.id.children_list);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.children_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                String message = "You clicked " + textView.getText();
                Toast.makeText(ChildrenActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}