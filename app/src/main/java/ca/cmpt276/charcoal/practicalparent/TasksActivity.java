package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ca.cmpt276.charcoal.practicalparent.model.Task;
import ca.cmpt276.charcoal.practicalparent.model.TasksManager;


/**
 *  Creates ListView for Tasks Activity, and registers clicks for user interaction
 */
public class TasksActivity extends AppCompatActivity {
    private ArrayAdapter<Task> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = findViewById(R.id.toolbar_task);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EditTaskActivity.makeLaunchIntent(TasksActivity.this, -1);
                startActivity(intent);
            }
        });

        populateListView();
        registerClickCallback();
    }

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, TasksActivity.class);
    }

    private void populateListView() {
        TasksManager manager = TasksManager.getInstance();
        List<Task> tasks = manager.getTasks();
        if (tasks != null) {
            adapter = new ArrayAdapter<>(this, R.layout.row_task, tasks);

            ListView list = findViewById(R.id.list_tasks);
            list.setAdapter(adapter);
        }
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.list_tasks);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = TaskInformationActivity.makeLaunchIntent(TasksActivity.this, position);
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