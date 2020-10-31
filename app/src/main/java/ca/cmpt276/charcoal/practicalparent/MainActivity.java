package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupChildren();
    }

    private void setupChildren() {
        List<Child> children = EditChildActivity.getSavedChildren(this);
        if (children != null) {
            ChildManager manager = ChildManager.getInstance();
            manager.setChildren(children);
        }
    }
}