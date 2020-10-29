package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ca.cmpt276.charcoal.practicalparent.ui.ChildrenActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(ChildrenActivity.makeLaunchIntent(this));
    }
}