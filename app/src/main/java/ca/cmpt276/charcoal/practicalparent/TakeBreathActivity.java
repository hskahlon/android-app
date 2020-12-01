package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class TakeBreathActivity extends AppCompatActivity {
    String TAG = "TakeBreathActivity";
    private Button beginBtn;
    private Button inhaleExhaleBtn;
    private TextView headingText, helpText;
    private boolean isThreeSecondRunCallBackPresent = false;
    private boolean isInhaling = true;
    private boolean completeExhale = false;
    private int numBreathLeft = 0;

    private final State beginState = new BeginState();
    private final State waitForInhaleState = new WaitForInhaleState();
    private final State inhalingState = new InhalingState();
    private final State inhaledForThreeSecondState = new InhaledForThreeSecondsState();
    private final State inhaledForTenSecondState = new InhaledForTenSecondsState();
    private final State doneInhaleState = new DoneInhaleState();
    private final State exhalingState = new ExhalingState();
    private final State exhaledForThreeSecondState = new ExhaledForThreeSecondState();
    private final State doneExhaleState = new DoneExhaleState();
    private final State moreBreatheState = new MoreBreatheState();


    private State currentState = new IdleState();


    private abstract class State {
        // Empty implementations, so derived classses don't need to
        // overide methods they don't care about
        void handleClickOn() {}
        void handleClickOff() {}
        void handleClickBegin() {}
        void handleExit() {}
        void handleEnter() {}
        void handleHoldingDownButton() {}
        void handleReleaseButton() {}
    }
    private void setState(State newState) {
        currentState.handleExit();
        currentState = newState;
        currentState.handleEnter();
    }


    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, TakeBreathActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setupTexts();
        setupBtns();

        setState(beginState);
    }

    private void setupTexts() {
        helpText = findViewById(R.id.text_help);
        headingText = findViewById(R.id.text_heading);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupBtns() {
        beginBtn = findViewById(R.id.button_begin);
        beginBtn.setOnClickListener((view) -> currentState.handleClickBegin());
        inhaleExhaleBtn = findViewById(R.id.button_inhale_exhale);
        inhaleExhaleBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        currentState.handleHoldingDownButton();
                        break;
                    }
                    default: {
                        currentState.handleReleaseButton();
                        break;
                    }
                }
                return true;
            }
        });
    }

    // Does nothing, Avoids null checks with Null Object Pattern
    private class IdleState extends State {

    }

    private class BeginState extends State{
        @Override
        void handleClickBegin() {
            //TODO: save data
            beginBtn.setVisibility(View.INVISIBLE);
            inhaleExhaleBtn.setVisibility(View.VISIBLE);
            setState(waitForInhaleState);
        }
        @Override
        void handleExit() {
            Log.i(TAG, "Exiting Begin State");
        }

        @Override
        void handleEnter() {
            Log.i(TAG, "Entering Begin State");
            beginBtn.setVisibility(View.VISIBLE);
            inhaleExhaleBtn.setVisibility(View.INVISIBLE);
            helpText.setText("");
        }
    }

    private class WaitForInhaleState extends State {
        @Override
        void handleEnter() {
            inhaleExhaleBtn.setText("In");
            helpText.setText("Hold Button and Breathe In");
        }
        @Override
        void handleHoldingDownButton() { setState(inhalingState); }
    }

    private class InhalingState extends State {
        Handler timerHandler = new Handler();
        Runnable threeSecondRun = () -> {
            Log.i(TAG, "user holds it for 3 seconds..");
            setState(inhaledForThreeSecondState);
        };

        @Override
        void handleReleaseButton() {
            Log.i(TAG, "Release button!");

            //TODO: STOP ANIMATION
            setState(waitForInhaleState);
            Log.i(TAG, "Going from inhaling state to waitForInhaling State");
        }

        @Override
        void handleEnter() {
            Log.i(TAG,"Entering Inhaling State");
            helpText.setText("In inhale State");
            Log.i(TAG, "holding button ...");
            timerHandler.postDelayed(threeSecondRun, 3000);
            //TODO: START ANIMATION AND SOUND
        }

        @Override
        void handleExit() {
            timerHandler.removeCallbacks(threeSecondRun);
        }
    }

    private class InhaledForThreeSecondsState extends State {
        Handler timerHandler = new Handler();
        Runnable tenSecondRun = () -> {
            Log.i(TAG, "user holds it for 10 seconds..");
            setState(inhaledForTenSecondState);
        };
        @Override
        void handleEnter() {
            Log.i(TAG,"Entering inhaled For Three Second State");
            helpText.setText("Breathed in for 3 seconds! Feel free to release Button and breathe out");
            timerHandler.postDelayed(tenSecondRun, 7000);
            inhaleExhaleBtn.setText("Out!");
        }
        @Override
        void handleReleaseButton() {
            setState(doneInhaleState);
        }
        @Override
        void handleExit() {
            timerHandler.removeCallbacks(tenSecondRun);
        }
    }

    private class InhaledForTenSecondsState extends State {
        @Override
        void handleEnter() {
            Log.i(TAG,"Entering Inhaled For Ten Second State");
            helpText.setText(R.string.msg_release_button_for_inhale);
        }
        @Override
        void handleReleaseButton() {
            setState(doneInhaleState);
        }
        @Override
        void handleExit() {
        }
    }

    private class DoneInhaleState extends State {
        @Override
        void handleEnter() {
            //TODO: STOP ANIMATION AND SOUND
            Log.i(TAG,"Entering Done Inhaling State");
            setState(exhalingState);
        }
        @Override
        void handleExit() {
        }
    }

    private class ExhalingState extends State {
        Handler timerHandler = new Handler();
        Runnable threeSecondRun = () -> {
            Log.i(TAG, "3 seconds are up");
            setState(exhaledForThreeSecondState);
        };
        @Override
        void handleEnter() {
            Log.i(TAG,"Entering Exhaling State");
            helpText.setText("Now Breathe Out");
            //TODO :START EXHALING ANIMATION AND SOUND
            timerHandler.postDelayed(threeSecondRun,3000);
        }
        @Override
        void handleExit() {
            timerHandler.removeCallbacks(threeSecondRun);
        }
    }

    private class ExhaledForThreeSecondState extends State {
        Handler timerHandler = new Handler();
        Runnable tenSecondRun = () -> {
            Log.i(TAG, "10 seconds are up");
            setState(doneExhaleState);
        };
        @Override
        void handleEnter() {
            //TODO: Update Count of remaining breaths
            if(numBreathLeft>0){
                inhaleExhaleBtn.setText("In");
            }else{
                inhaleExhaleBtn.setText("Good Job");
            }
            timerHandler.postDelayed(tenSecondRun,7000);
        }
        @Override
        void handleHoldingDownButton() {
            setState(doneExhaleState);
        }
        @Override
        void handleExit() {
            timerHandler.removeCallbacks(tenSecondRun);
        }
    }

    private class DoneExhaleState extends State {
        @Override
        void handleEnter() {
            //TODO: stop animation and sound
            setState(moreBreatheState);
        }
        @Override
        void handleExit() {
        }
    }

    private class MoreBreatheState extends State {
        @Override
        void handleEnter() {
            if(numBreathLeft>0){
                setState(waitForInhaleState);
            }else{
                setState(beginState);
            }
        }
        @Override
        void handleExit() {
        }
    }
    private void failedInhale() {
        View view = findViewById(R.id.image_breathe);

        int cx = view.getWidth() /2;
        int cy = view.getHeight() / 2;

        float intialRadius = (float) Math.hypot(cx,cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view,cx,cy,intialRadius,0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
                resetAnimation();
            }
        });

        anim.start();
    }

    private void animateExhale(int duration) {
        image_Breathe.setColorFilter(ContextCompat.getColor(this, R.color.exhale_blue));
        View view = findViewById(R.id.image_breathe);

        int cx = view.getWidth() /2;
        int cy = view.getHeight() / 2;

        float intialRadius = (float) Math.hypot(cx,cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(view,cx,cy,intialRadius,0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
                resetAnimation();
            }
        });
        anim.setDuration(duration);
        anim.start();

    }

    private void resetAnimation() {

        image_Breathe = findViewById(R.id.image_breathe);
        ViewGroup.LayoutParams params =  image_Breathe.getLayoutParams();
        params.height=4;
        params.width=17;
        image_Breathe.setLayoutParams(params);
    }

    private void incrementCircle() {

        image_Breathe = findViewById(R.id.image_breathe);
        image_Breathe.setColorFilter(ContextCompat.getColor(this, R.color.breathe_green));
        image_Breathe.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params =  image_Breathe.getLayoutParams();
        params.height+=5;
        params.width+=5;
        image_Breathe.setLayoutParams(params);

    }
    private void decrementCircle() {
        image_Breathe = findViewById(R.id.image_breathe);
        image_Breathe.setVisibility(View.VISIBLE);
        image_Breathe.setColorFilter(ContextCompat.getColor(this, R.color.exhale_blue));
        ViewGroup.LayoutParams params =  image_Breathe.getLayoutParams();
        params.height-=20;
        params.width-=20;
        image_Breathe.setLayoutParams(params);
    }

    // TODO: Select Real Sound, Currently Plays CoinFlip Sound
    private void inhaleSound() {
        final MediaPlayer inhale = MediaPlayer.create(this, R.raw.coinflip);
        inhale.start();
    }

    private void exhaleSound() {
        final MediaPlayer exhale = MediaPlayer.create(this, R.raw.coinflip);
        exhale.start();
    }
}
