package ca.cmpt276.charcoal.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;


public class ChildrenActivity extends AppCompatActivity {
    private ArrayAdapter<Child> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.addChild_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EditChildActivity.makeLaunchIntent(ChildrenActivity.this, -1);
                startActivity(intent);
            }
        });

        populateListView();
        registerClickCallback();
    }

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, ChildrenActivity.class);
    }

    private void populateListView() {
        ChildManager manager = ChildManager.getInstance();
        List<Child> children = manager.getChildren();
        if (children != null) {
            adapter = new ArrayAdapter<>(this, R.layout.child_item, children);

            ListView list = findViewById(R.id.children_list);
            list.setAdapter(adapter);
        }
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.children_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = EditChildActivity.makeLaunchIntent(ChildrenActivity.this, position);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        adapter.notifyDataSetChanged();
        super.onStart();
    }
}