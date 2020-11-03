package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;



public class CoinFlipActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn;
    private ImageView coin;
    private static final int YROTATE = 1800;
    private static final int DURATION = 300;
    private static final float SCALEX = 0.5f;
    private static final float SCALEY = 0.5f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        setupCoinButton();

        coin = findViewById(R.id.coinImageView);

        Button heads = findViewById(R.id.selectHeads);
        Button tails = findViewById(R.id.selectTails);
        heads.setOnClickListener(this);
        tails.setOnClickListener(this);

        // Enable "up" on toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectHeads:
                Toast.makeText(this, "Heads selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.selectTails:
                Toast.makeText(this, "Tails selected", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setupCoinButton() {
        btn = findViewById(R.id.flipBtn);
        btn.setOnClickListener(v -> {
            int randomChoice = getRandom();
            flipCoin(randomChoice);
        });
    }


    //TODO
//    private void setupHeadTailSelectorButtons() {
//        btn = findViewById(R.id.selectHeads)
//    }

//    private void updateHeadTailSelector() {
//
//    }

    private int getRandom() {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(2);
    }

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, CoinFlipActivity.class);
    }



    private void flipCoin(int randomChoice) {

        final View currentCoin = coin;

        // Rotates by 1800 degrees -> changes view at last flip
            currentCoin.animate().withLayer()
                    .rotationYBy(YROTATE)
                    .setDuration(DURATION)
                    .scaleXBy(SCALEX)
                    .scaleYBy(SCALEY)
                    .withEndAction(
                            () -> {

                                TextView result = findViewById(R.id.coinFlipResultText);
                                if (randomChoice == 0) {
                                    coin.setImageResource(R.drawable.ic_tails);
//
                                } else {
                                    coin.setImageResource(R.drawable.ic_heads);
//
                                }

                                //second quarter turn
                                currentCoin.setRotationY(-YROTATE);
                                currentCoin.animate().withLayer()
                                        .rotationY(0)
                                        .scaleXBy(-SCALEY)
                                        .scaleYBy(-SCALEY)
                                        .setDuration(DURATION)
                                        .start();


                            }

                    ).start();

            new Handler().postDelayed(() -> {
                TextView result = findViewById(R.id.coinFlipResultText);
                if (randomChoice==0)
                {
                    result.setText(R.string.tailsString);
                }
                else
                {
                    result.setText(R.string.headsString);
                }
            }, DURATION*2);

    }
}