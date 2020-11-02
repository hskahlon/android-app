package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupCoinActivityBtn();
        setupTimeOutActivityBtn();


    }

    private void setupTimeOutActivityBtn() {
        Button btn = (Button) findViewById(R.id.timeOutBtn);

        btn.setOnClickListener(v -> {
            Intent i = TimeOutActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void setupCoinActivityBtn() {
        Button btn = (Button) findViewById(R.id.coinflipActivity);

        btn.setOnClickListener(v -> {
            Intent i = CoinFlipActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }


}