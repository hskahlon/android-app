package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ca.cmpt276.charcoal.practicalparent.R;

import ca.cmpt276.charcoal.practicalparent.coinFlip;
import ca.cmpt276.charcoal.practicalparent.ChildrenActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupCoinActivityBtn();


    }

    private void setupCoinActivityBtn() {
        Button btn = (Button) findViewById(R.id.coinflipActivity);

        btn.setOnClickListener(v -> {
            Intent i = coinFlip.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });

    }

}