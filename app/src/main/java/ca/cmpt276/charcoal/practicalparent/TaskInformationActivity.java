package ca.cmpt276.charcoal.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;
import ca.cmpt276.charcoal.practicalparent.model.Task;
import ca.cmpt276.charcoal.practicalparent.model.TasksManager;

/**
 * Displays information about the selected task including the name and which child is assigned
 * Allows the user to mark the task as complete which advances it to the next child
 */
public class TaskInformationActivity extends AppCompatActivity {
    public static final String EXTRA_TASK_INDEX = "ca.cmpt276.charcoal.practicalparent - taskIndex";
    private int taskIndex;
    private final ChildManager childManager = ChildManager.getInstance();
    private final TasksManager taskManager = TasksManager.getInstance();
    private TextView childNameBox;
    private TextView taskNameBox;


    public static Intent makeLaunchIntent(Context context, int childIndex) {
        Intent intent = new Intent(context, TaskInformationActivity.class);
        intent.putExtra(EXTRA_TASK_INDEX, childIndex);
        return intent;
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        taskIndex = intent.getIntExtra(EXTRA_TASK_INDEX, -1);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_information);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        childNameBox = findViewById(R.id.text_child_name_task);
        taskNameBox = findViewById(R.id.text_info_task_name);
        extractIntentData();

        setupFinishedTaskButton();
        populateTextViews();
    }

    private void populateTextViews() {
        if (taskIndex >= 0) {
            Task currentTask = taskManager.getTask(taskIndex);
            taskNameBox.setText(currentTask.getTaskName());
            if( childManager.getChildren().size() <= 0){
                childNameBox.setText(R.string.no_child_added);
            } else{
                Child currentChild = childManager.getChild(currentTask.getChildIdx());
                childNameBox.setText(String.format("%s", currentChild.getName()));
            }
        }
    }

    private void setupFinishedTaskButton() {
        Button finishedTaskButton = findViewById(R.id.button_task_finished);
        finishedTaskButton.setOnClickListener(v -> {
            if(childManager.getChildren().size() <= 0 ){
                Toast.makeText(TaskInformationActivity.this,"No Child Added ",Toast.LENGTH_SHORT)
                        .show();
            }
            else{
                taskManager.reassignChildIdx(taskIndex);
                EditTaskActivity.saveTasksInSharedPrefs(this);
                finish();
            }
        });
    }
}