package ca.cmpt276.charcoal.practicalparent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;
import ca.cmpt276.charcoal.practicalparent.model.Task;
import ca.cmpt276.charcoal.practicalparent.model.TasksManager;

/**
 *  Sets up Edit Child Activity, Allows for Editing children, and saving data
 */
public class EditChildActivity extends AppCompatActivity {
    private static String TAG = "EditChildActivity";
    private static final String PREFS_NAME = "SavedData";
    private static final String TASKS_PREF = "Tasks";

    private static final String CHILDREN_PREFS = "My children";
    public static final String EXTRA_CHILD_INDEX = "ca.cmpt276.charcoal.practicalparent - childIndex";
    public ImageView childPhotoDefault;
    private int childIndex;
    private EditText nameBox;
    private final ChildManager manager = ChildManager.getInstance();
    private final TasksManager tasksManager = TasksManager.getInstance();
    ImageView imageToUpload;
    private static final int RESULT_LOAD_IMAGE = 1;

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

        childPhotoDefault = (ImageView) findViewById(R.id.childImage);
        childPhotoDefault.setImageResource(R.drawable.editchild_default_image);

        extractIntentData();
        preFillNameBox();
        setupImportImageButton();
//        setupEditImageCameraButton();
    }

    private void preFillNameBox() {
        if (childIndex >= 0) {
            Child currentChild = manager.getChild(childIndex);
            nameBox.setText(currentChild.getName());
        }
    }

    // TODO: Finish this function and its Activity
    private void setupImportImageButton() {
        Button importImageButton = findViewById(R.id.editImageUpload_btn);
        importImageButton.setOnClickListener(v -> dispatchChooseImportIntent());
    }

    private void dispatchChooseImportIntent() {
        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePictureIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }

    // TODO: Finish this function and its Activity
//    private void setupEditImageCameraButton() {
//        Button editImageCameraButton = findViewById(R.id.editImageCamera_btn);
//        editImageCameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(EditChildActivity.this, "Take a photo", Toast.LENGTH_SHORT)
//                        .show();
//                Intent intent = new Intent(EditChildActivity.this, EditChildImageCameraActivity.class);
////                startActivity(intent);
//            }
//        });
//    }

    private void setupSaveButton() {
        Button saveBtn = findViewById(R.id.editChildSave_btn);
        saveBtn.setOnClickListener(v -> saveChild());
    }

    private void saveChild() {
        String childName = nameBox.getText().toString();
        if (nameIsValid(childName)) {
            if (childIndex >= 0) {
                Child currentChild = manager.getChild(childIndex);
                currentChild.setName(childName);
            } else {
                manager.add(new Child(childName));
            }
            saveChildren();
            finish();
        }
    }

    private boolean nameIsValid(String childName) {
        if (childName.length() == 0) {
            nameBox.setError(getString(R.string.editChildNameError));
            return false;
        } else {
            for (Child child : manager.getChildren()) {
                if (childName.equals(child.getName())) {
                    nameBox.setError("Children names must be unique");
                    return false;
                }
            }
        }
        return true;
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
                reassignTaskForDeletedChild(childIndex);
                manager.remove(childIndex);
                saveChildren();
                finish();
            } else {
                Toast.makeText(this, "You can only delete a child you are editing", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void reassignTaskForDeletedChild(int deletedChildIndex) {
        List<Task> tasks = tasksManager.getTasks();
        for (Task task : tasks) {
            if (task.getChildIdx() == deletedChildIndex) {
                task.decrementChildIdx();
            }
        }
        tasksManager.setTasks(tasks);
        saveTasksInSharedPrefs();
        return;
    }

    private void saveTasksInSharedPrefs() {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        List<Task> tasks = tasksManager.getTasks();
        Gson gson = new Gson();
        String json = gson.toJson(tasks);
        Log.i(TAG, json + "" );

        editor.putString(TASKS_PREF, json);
        editor.apply();
    }

    private void saveChildren() {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        List<Child> children = manager.getChildren();
        Gson gson = new Gson();
        String json = gson.toJson(children);
        editor.putString(CHILDREN_PREFS, json);
        editor.apply();
    }

    // Gson serialization code found here:
    //   https://stackoverflow.com/questions/28107647/how-to-save-listobject-to-sharedpreferences/28107838
    public static List<Child> getSavedChildren(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        List<Child> children;
        String serializedChildren = prefs.getString(CHILDREN_PREFS, null);
        if (serializedChildren != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Child>>(){}.getType();
            children = gson.fromJson(serializedChildren, type);
            return children;
        } else {
            return null;
        }
    }
}
