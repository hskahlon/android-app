package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class TakeBreathActivity extends AppCompatActivity {
    public static final int EXHALE_DURATION = 10000;
    public static final int INTIAL_HEIGHT = 4;
    public static final int INTIAL_WIDTH = 17;
    public static final int INCREMENT_FACTOR = 3;
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
    private ImageView image_Breathe;
    MediaPlayer inhale;
    MediaPlayer exhale;
    private State currentState = new IdleState();

    private abstract class State {
        // Empty implementations, so derived classses don't need to
        // overide methods they don't care about
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
        setupButtons();
        setState(beginState);
    }

    private void setupTexts() {
        helpText = findViewById(R.id.text_help);
        headingText = findViewById(R.id.text_heading);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupButtons() {
        beginBtn = findViewById(R.id.button_begin);
        beginBtn.setOnClickListener((view) -> currentState.handleClickBegin());
        inhaleExhaleBtn = findViewById(R.id.button_inhale_exhale);
        inhaleExhaleBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    currentState.handleHoldingDownButton();
                } else {
                    currentState.handleReleaseButton();
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
            stopAnimation();
            inhaleExhaleBtn.setText(R.string.msg_in);
            helpText.setText(R.string.msg_inhale);
        }
        @Override
        void handleHoldingDownButton() {
            setState(inhalingState);

        }
    }

    private class InhalingState extends State {
        Handler timerHandler = new Handler();
        Runnable threeSecondRun = () -> {
            Log.i(TAG, "user holds it for 3 seconds..");
            setState(inhaledForThreeSecondState);
        };

        Handler inflateCircle = new Handler();


        Runnable myAction = new Runnable(){

            @Override
            public void run() {

                incrementCircle();

                inflateCircle.postDelayed(this,5);
            }
        };

        @Override
        void handleReleaseButton() {
            Log.i(TAG, "Release button!");
            setState(waitForInhaleState);
            Log.i(TAG, "Going from inhaling state to waitForInhaling State");
            failedInhale();
            stopInhaleSound();
        }

        @Override
        void handleEnter() {
            helpText.setText("In inhale State");
            Log.i(TAG,"Entering Inhaling State");
            helpText.setText(R.string.msg_inhaling_state);
            Log.i(TAG, "holding button ...");
            timerHandler.postDelayed(threeSecondRun, 3000);
            startInhaleSound();

            if (inflateCircle==null) {
                inflateCircle = new Handler();
            }
            inflateCircle.postDelayed(myAction,5);

        }

        @Override
        void handleExit() {
            timerHandler.removeCallbacks(threeSecondRun);
            inflateCircle.removeCallbacks(myAction);
            inflateCircle = null;
        }
    }

    private class InhaledForThreeSecondsState extends State {
        Handler timerHandler = new Handler();
        Runnable tenSecondRun = () -> {
            Log.i(TAG, "user holds it for 10 seconds..");
            setState(inhaledForTenSecondState);
        };

        Handler inflateCircle = new Handler();

        Runnable myAction = new Runnable(){

            @Override
            public void run() {
                incrementCircle();
                inflateCircle.postDelayed(this,5);
            }
        };

        @Override
        void handleEnter() {
            Log.i(TAG,"Entering inhaled For Three Second State");
            helpText.setText(R.string.msg_inhaled_for_three_seconds);
            timerHandler.postDelayed(tenSecondRun, 7000);
            inhaleExhaleBtn.setText("Out!");
            inflateCircle.postDelayed(myAction,5);


            inhaleExhaleBtn.setText(R.string.msg_out);
        }
        @Override
        void handleReleaseButton() {
            setState(doneInhaleState);
        }
        @Override
        void handleExit() {
            timerHandler.removeCallbacks(tenSecondRun);
            inflateCircle.removeCallbacks(myAction);
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
            stopInhaleSound();
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
            helpText.setText(R.string.msg_exhale);
            stopInhaleSound();
            timerHandler.postDelayed(threeSecondRun,3000);

            autoAnimateExhale(EXHALE_DURATION);
            playExhaleSound();

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
                inhaleExhaleBtn.setText(R.string.msg_in);
            }else{
                inhaleExhaleBtn.setText(R.string.msg_good_job);
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

            setState(moreBreatheState);
            stopExhaleSound();
            stopAnimation();

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
                stopAnimation();
            }
        });

        anim.start();
    }

    private void autoAnimateExhale(int duration) {
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
                stopAnimation();
            }
        });
        anim.setDuration(duration);
        anim.start();

    }

    private void stopAnimation() {
        image_Breathe = findViewById(R.id.image_breathe);
        ViewGroup.LayoutParams params =  image_Breathe.getLayoutParams();
        params.height= INTIAL_HEIGHT;
        params.width= INTIAL_WIDTH;
        image_Breathe.setLayoutParams(params);
        image_Breathe.setVisibility(View.INVISIBLE);
    }

    private void incrementCircle() {
        image_Breathe = findViewById(R.id.image_breathe);
        image_Breathe.setColorFilter(ContextCompat.getColor(this, R.color.breathe_green));
        image_Breathe.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params =  image_Breathe.getLayoutParams();
        params.height += INCREMENT_FACTOR;
        params.width += INCREMENT_FACTOR;
        image_Breathe.setLayoutParams(params);

    }

    private void startInhaleSound() {
        if(inhale==null){
            inhale = MediaPlayer.create(this, R.raw.inhale);
            inhale.start();
        }
    }
    private void stopInhaleSound() {
        if (inhale!=null) {
            inhale.stop();
            inhale.release();
            inhale = null;
        }
    }

    private void playExhaleSound() {
        if (exhale==null){
            exhale = MediaPlayer.create(this, R.raw.exhale);
            exhale.start();
        }

    }
    private void stopExhaleSound() {
        if (exhale!=null) {
            exhale.stop();
            exhale.release();
            exhale = null;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopExhaleSound();
        stopInhaleSound();
    }
}
