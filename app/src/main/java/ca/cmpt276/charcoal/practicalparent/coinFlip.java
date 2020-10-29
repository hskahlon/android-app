package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;



public class coinFlip extends AppCompatActivity {

    private Button btn;
    private ImageView coin;
    private static final int YROTATE = 1800;
    private static final int DURATION = 300;
    private static final float SCALEX = 0.5f;
    private static final float SCALEY = 0.5f;

    // Animations
    Animation rightAnim, leftAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        setupCoinButton();

        coin = (ImageView) findViewById(R.id.coinImageView);

    }

    private void setupCoinButton() {
        btn = (Button) findViewById(R.id.flipBtn);
        btn.setOnClickListener(v -> {
            int randomChoice = getRandom();
            flipCoin(randomChoice);

        });

    }

    private int getRandom() {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(2);
    }

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context,coinFlip.class);
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


                                if (randomChoice == 0) {
                                    coin.setImageResource(R.drawable.ic_tails);
                                } else {
                                    coin.setImageResource(R.drawable.ic_heads);
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

    }

}