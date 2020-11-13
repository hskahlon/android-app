package ca.cmpt276.charcoal.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.cmpt276.charcoal.practicalparent.model.Task;
import ca.cmpt276.charcoal.practicalparent.model.TasksManager;

/**
 *  Sets up Edit Task Activity, Allows for Editing Tasks, and saving data
 */
public class EditTaskActivity extends AppCompatActivity {
    private static String TAG = "EditTaskActivity";
    private static final String PREFS_NAME = "SavedTasksData";
    private static final String TASKS_PREF = "Tasks";
    public static final String EXTRA_TASK_INDEX = "ca.cmpt276.charcoal.practicalparent - childIndex";
    private int taskIndex;
    private EditText nameBox;
    private final TasksManager manager = TasksManager.getInstance();

    public static Intent makeLaunchIntent(Context context, int childIndex) {
        Intent intent = new Intent(context, EditTaskActivity.class);
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
        setContentView(R.layout.activity_edit_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setupSaveButton();
        nameBox = findViewById(R.id.taskNameTextBox);
        extractIntentData();
        preFillNameBox();
    }

    private void preFillNameBox() {
        if (taskIndex >= 0) {
            Task currentTask = manager.getTaskIdx(taskIndex);
            nameBox.setText(currentTask.getTaskName());
        }
    }

    private void setupSaveButton() {
        Button saveBtn = findViewById(R.id.editTaskSave_btn);
        saveBtn.setOnClickListener(v -> saveTaskInManager());
    }

    private void saveTaskInManager() {
        String taskName = nameBox.getText().toString();
        if (nameIsValid(taskName)) {
            if (taskIndex >= 0) {
                Task currentTask = manager.getTaskIdx(taskIndex);
                currentTask.setTaskName(taskName);
            } else {
                manager.add(new Task(taskName));
            }
            saveTasksInSharedPrefs();
            finish();
        }
    }

    private boolean nameIsValid(String taskName) {
        if (taskName.length() == 0) {
            nameBox.setError(getString(R.string.editChildNameError));
            return false;
        } else {
            for (Task task : manager.getTasks()) {
                if (taskName.equals(task.getTaskName())) {
                    nameBox.setError("Tasks names must be unique");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_task, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            if (taskIndex >= 0) {
                manager.remove(taskIndex);
                saveTasksInSharedPrefs();
                finish();
            } else {
                Toast.makeText(this, "You can only delete a child you are editing", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTasksInSharedPrefs() {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

//        List<Task> tasks = manager.getTasks();
//        Gson gson = new Gson();
//        String json = gson.toJson(tasks);
//        Log.i(TAG, json + "" );
//
//        editor.apply();

        Set<String> set = new HashSet<String>();
        for (int i = 0; i < manager.getArraySize(); i++) {
            set.add(manager.getTaskIdx(i).getJSONObject().toString());
        }
                Log.i(TAG, set + "" );

        editor.putStringSet(PREFS_NAME, set);
        editor.apply();
    }

    // Gson serialization code found here:
    //   https://stackoverflow.com/questions/28107647/how-to-save-listobject-to-sharedpreferences/28107838
    public static List<Task> getSavedTasks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        List<Task> tasks;
//        String serializedTasks = prefs.getString(TASKS_PREF, null);
//        Log.i(TAG , "seriallizedTasks: " + serializedTasks);
//        if (serializedTasks != null) {
//            Gson gson = new Gson();
//            Type type = new TypeToken<List<Task>>(){}.getType();
//            Log.i(TAG, "Type: " + type);
//            tasks = gson.fromJson(serializedTasks, type);
//            Log.i(TAG, "tasks after getting the data from sharedPref: " + tasks);
//            return tasks;
//        } else {
//            return null;
//        }
        List<Task> tasks = new ArrayList<>();
        Set<String> set = prefs.getStringSet(PREFS_NAME, null);
        if (set != null) {
            for (String s : set) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String taskName = jsonObject.getString("taskName");
                    String child = jsonObject.getString("child");
                    Task task = new Task(taskName);

                    tasks.add(task);
                    Log.i(TAG, "tasks after getting the data from sharedPref: " + tasks);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return tasks;
        } else {
            return null;
        }
    }
}