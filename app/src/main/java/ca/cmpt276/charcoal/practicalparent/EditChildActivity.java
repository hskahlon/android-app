package ca.cmpt276.charcoal.practicalparent;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;
import ca.cmpt276.charcoal.practicalparent.model.TasksManager;

/**
 *  Sets up Edit Child Activity, Allows for Editing children, and saving data
 */
public class EditChildActivity extends AppCompatActivity {
    private static String TAG = "EditChildActivity";
    private static final String PREFS_NAME = "SavedData";

    private static final String CHILDREN_PREFS = "My children";
    public static final String EXTRA_CHILD_INDEX = "ca.cmpt276.charcoal.practicalparent - childIndex";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public ImageView childPhoto;
    private int childIndex;
    private EditText nameBox;
    private final ChildManager childManager = ChildManager.getInstance();
    private final TasksManager tasksManager = TasksManager.getInstance();

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
        nameBox = findViewById(R.id.edit_child_name);

        childPhoto = findViewById(R.id.image_child);
        childPhoto.setImageResource(R.drawable.editchild_default_image);

        extractIntentData();
        preFillNameBox();
        setupImportImageButton();
        setupCameraImageButton();
    }

    private void preFillNameBox() {
        if (childIndex >= 0) {
            Child currentChild = childManager.getChild(childIndex);
            nameBox.setText(currentChild.getName());
        }
    }

    // TODO: Finish this function and its Activity
    private void setupImportImageButton() {
        Button importImageButton = findViewById(R.id.button_image_import);
        importImageButton.setOnClickListener(v -> {
            Toast.makeText(EditChildActivity.this, "Upload a photo", Toast.LENGTH_SHORT)
                    .show();
            Intent intent = new Intent(EditChildActivity.this, ImportImageActivity.class);
//                startActivity(intent);
        });
    }

    // TODO: Finish this function and its Activity
    private void setupCameraImageButton() {
        Button editImageCameraButton = findViewById(R.id.button_get_camera_image);
        editImageCameraButton.setOnClickListener(v -> dispatchTakePictureIntent());
    }

    private void setupSaveButton() {
        Button saveBtn = findViewById(R.id.button_save_child);
        saveBtn.setOnClickListener(v -> saveChild());
    }

    private void saveChild() {
        String childName = nameBox.getText().toString();
        if (nameIsValid(childName)) {
            if (childIndex >= 0) {
                Child currentChild = childManager.getChild(childIndex);
                currentChild.setName(childName);
            } else {
                childManager.add(new Child(childName));
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
            for (Child child : childManager.getChildren()) {
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
        if (childIndex == -1) {
            menu.findItem(R.id.action_delete)
                    .setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            childManager.remove(childIndex);
            saveChildren();
            reassignTaskForDeletedChild(childIndex);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void reassignTaskForDeletedChild(int deletedChildIndex) {
        tasksManager.reassignTaskForDeletedChild(deletedChildIndex);
        EditTaskActivity.saveTasksInSharedPrefs(this);
    }

    private void saveChildren() {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        List<Child> children = childManager.getChildren();
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Your device does not have a camera", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            childPhoto.setImageBitmap(imageBitmap);
        }
    }
}
