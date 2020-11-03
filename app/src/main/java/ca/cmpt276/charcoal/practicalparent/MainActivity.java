package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.util.List;

import ca.cmpt276.charcoal.practicalparent.model.Child;
import ca.cmpt276.charcoal.practicalparent.model.ChildManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupCoinActivityBtn();
        setupChildrenActivityBtn();
        setupTimeOutActivityBtn();
        setupChildren();

    }

    private void setupCoinActivityBtn() {
        Button btn = (Button) findViewById(R.id.coinflipActivity);

        btn.setOnClickListener(v -> {
            Intent i = CoinFlipActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void setupChildren() {
        List<Child> children = EditChildActivity.getSavedChildren(this);
        if (children != null) {
            ChildManager manager = ChildManager.getInstance();
            manager.setChildren(children);
        }
    }

    private void setupTimeOutActivityBtn() {
        Button btn = (Button) findViewById(R.id.timeoutActivity);

        btn.setOnClickListener(v -> {
            Intent i = TimeOutActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }

    //TODO: Hook-up RecordsActivity code with the Records button

    private void setupChildrenActivityBtn() {
        Button btn = (Button) findViewById(R.id.childrenActivity);

        btn.setOnClickListener(v -> {
            Intent i = ChildrenActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }

}