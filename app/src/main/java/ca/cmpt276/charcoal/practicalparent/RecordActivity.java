package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
    }
    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, RecordActivity.class);
    }
}