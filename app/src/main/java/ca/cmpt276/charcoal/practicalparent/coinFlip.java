package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;



public class coinFlip extends AppCompatActivity {

    private Button btn;
    private ImageView coin;

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

        final View v = coin;

        // Rotates by 90 degrees -> change view -> finish rotation
        v.animate().withLayer()
                .rotationY(90)
                .setDuration(300)
                .scaleXBy(0.5f)
                .scaleYBy(0.5f)
                .withEndAction(
                        () -> {

                    if (randomChoice==0)
                    {
                        coin.setImageResource(R.drawable.ic_temp_tail);
                    }

                    else {
                        coin.setImageResource(R.drawable.ic_temp_head);
                    }

                             //second quarter turn
                            v.setRotationY(-90);
                            v.animate().withLayer()
                                    .rotationY(0)
                                    .scaleXBy(-0.5f)
                                    .scaleYBy(-0.5f)
                                    .setDuration(300)
                                    .start();
                        }
                ).start();

    }

}