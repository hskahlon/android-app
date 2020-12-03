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
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ca.cmpt276.charcoal.practicalparent.model.Task;


public class TakeBreathActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String PREFS_NAME = "SavedData";
    private static final String NUM_BREATHS_TO_TAKE_PREFS = "NumBreathsToTake";

    public static final int EXHALE_DURATION = 10000;
    public static final int INTIAL_HEIGHT = 4;
    public static final int INTIAL_WIDTH = 17;
    public static final int INCREMENT_FACTOR = 3;
    String TAG = "TakeBreathActivity";
    private Button beginBtn;
    private Button inhaleExhaleBtn;
    private TextView headerText, helpText;
    private CustomSpinner setNumBreathSpinner;
    private boolean isThreeSecondRunCallBackPresent = false;
    private boolean isInhaling = true;
    private boolean completeExhale = false;
    private int startNumBreathToTake = 3;
    private int numBreathLeft = 3;

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
        setupSpinner();
        setState(beginState);
    }

    private void setupSpinner() {
        setNumBreathSpinner = findViewById(R.id.spinner_set_number_of_breath);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.msg_num_breath, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setNumBreathSpinner.setAdapter(adapter);
        setNumBreathSpinner.setOnItemSelectedListener(this);
        setNumBreathSpinner.setSelection(getSavedNumBreathToTake());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String chosenNumber = parent.getItemAtPosition(position).toString();
        startNumBreathToTake = Integer.parseInt(chosenNumber);
        numBreathLeft = startNumBreathToTake;
        Log.i(TAG, "Selected drop down numBreathTake: " + numBreathLeft);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void setupTexts() {
        helpText = findViewById(R.id.text_help);
        headerText = findViewById(R.id.text_header_first_part);
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

    private void saveNumBreathToTakeInSharedPrefs() {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(NUM_BREATHS_TO_TAKE_PREFS, Integer.toString(startNumBreathToTake));
        editor.apply();
    }

    private int getSavedNumBreathToTake() {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String serializedNumBreath = prefs.getString(NUM_BREATHS_TO_TAKE_PREFS, "3");
        Log.i(TAG, "serializedNumb: " + serializedNumBreath);
        return Integer.parseInt(serializedNumBreath)-1;
    }

    // Does nothing, Avoids null checks with Null Object Pattern
    private class IdleState extends State {
    }

    private class BeginState extends State{
        @Override
        void handleClickBegin() {
            //TODO: save data
            setupUIWhenClickBegin();
            setState(waitForInhaleState);
            saveNumBreathToTakeInSharedPrefs();
        }

        @Override
        void handleExit() {
            Log.i(TAG, "Exiting Begin State");
        }

        @Override
        void handleEnter() {
            Log.i(TAG, "Entering Begin State");
            setupUIWhenEnterBeginState();
        }

        private void setupUIWhenEnterBeginState() {
            beginBtn.setVisibility(View.VISIBLE);
            inhaleExhaleBtn.setVisibility(View.INVISIBLE);
            setNumBreathSpinner.setVisibility(View.VISIBLE);
            helpText.setText("");
            numBreathLeft = startNumBreathToTake;
        }

        private void setupUIWhenClickBegin() {
            beginBtn.setVisibility(View.INVISIBLE);
            inhaleExhaleBtn.setVisibility(View.VISIBLE);
            setNumBreathSpinner.setVisibility(View.INVISIBLE);
            headerText.setText(String.format(getString(R.string.msg_header_first_part), startNumBreathToTake));
        }
    }

    private class WaitForInhaleState extends State {
        @Override
        void handleEnter() {
            stopAnimation();
            inhaleExhaleBtn.setText(R.string.action_in);
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
            inflateCircle.postDelayed(myAction,5);
            inhaleExhaleBtn.setText(R.string.action_out);
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
    }

    private class DoneInhaleState extends State {
        @Override
        void handleEnter() {
            //TODO: STOP ANIMATION AND SOUND
            Log.i(TAG,"Entering Done Inhaling State");
            setState(exhalingState);
            stopInhaleSound();
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
            numBreathLeft--;
            headerText.setText(String.format(getString(R.string.msg_header_first_part), numBreathLeft));
            if(numBreathLeft>0){
                inhaleExhaleBtn.setText(R.string.action_in);
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
