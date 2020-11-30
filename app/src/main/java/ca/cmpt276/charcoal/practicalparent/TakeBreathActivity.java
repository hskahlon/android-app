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
import android.widget.Toast;


public class TakeBreathActivity extends AppCompatActivity {
    String TAG = "TakeBreathActivity";
    private Button beginBtn;
    private Button inhaleExhaleBtn;
    private TextView headingText,helpText;
    private boolean isThreeSecondRunCallBackPresent = false;
    private boolean isInhaling = true;
    private boolean completeExhale = false;
    private int numBreathLeft = 0;
    ImageView image_Breathe;


    private abstract class State {
        // Empty implementations, so derived classses don't need to
        // overide methods they don't care about
        void handleClickOn() {}
        void handleClickOff() {}
        void handleExit() {}
        void handleEnter() {}
    }
    private final State onState = new OnState();
    private final State offState = new OffState();

    private State currentState = new IdleState();

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
        setupBeginBtn();
        setupInhaleExhaleBtn();

        setState(offState);
    }


    //Code Reference: https://stackoverflow.com/questions/10511423/android-repeat-action-on-pressing-and-holding-a-button
    //Code Reference: https://stackoverflow.com/questions/22606977/how-can-i-get-button-pressed-time-when-i-holding-button-on
    //TODO: make a custom button for blind people -->this is the reason why it's in yellow
    @SuppressLint("ClickableViewAccessibility")
    private void setupInhaleExhaleBtn() {
        inhaleExhaleBtn.setOnClickListener((view) -> currentState.handleClickOff());


        inhaleExhaleBtn.setOnTouchListener(new View.OnTouchListener(){
            Handler handler = new Handler();

            Runnable startRun = () -> {
                //TODO: when the user starts holding --> start animation and sound
                Log.i(TAG, "user starts holding..");
                if(isInhaling){

                }else{

                }
            };

            Runnable threeSecondRun = new Runnable() {
                @Override
                public void run() {
                    //TODO: when the user holds for 3 seconds --> change button to exhale
                    Log.i(TAG, "user holds it for 3 seconds..");
                    isThreeSecondRunCallBackPresent=true;
                    if(isInhaling){
                        setupExhaleButton();

                    }else{
                        Log.i(TAG, "user is not inhaling after 3 seconds");
                        Toast.makeText(TakeBreathActivity.this,"es b",Toast.LENGTH_SHORT).show();
                        //TODO: Update Remaining breath to take
                        if(numBreathLeft>0){
                            Toast.makeText(TakeBreathActivity.this,">0 b",Toast.LENGTH_SHORT).show();
                            updateHeading(numBreathLeft);
                            setupInhaleButton();
                        } else{
                            Toast.makeText(TakeBreathActivity.this,"else elsess b",Toast.LENGTH_SHORT).show();
                            inhaleExhaleBtn.setText("Good Job");
                        }
                    }
                }
            };

            Runnable tenSecondRun = new Runnable() {
                @Override
                public void run() {
                    //TODO: when the user holds for 10 seconds --> stop animation and sound
                    Log.i(TAG, "user holds it for 10 seconds..");;
                    if (isInhaling) {
                        helpText.setText(R.string.msg_release_button_for_inhale);
                        animateExhale(10000);
                    }else{
                        helpText.setText(R.string.msg_release_button_for_exhale);
                    }
                }
            };

            private Handler inflateCircle;

            Runnable myAction = new Runnable(){

                @Override
                public void run() {
                    if (isInhaling) {
                        incrementCircle();
                    }
                    else {
                    }
                    inflateCircle.postDelayed(this,5);
                }
            };

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {

                    // Button is Pressed
                    case MotionEvent.ACTION_DOWN: {
                        if (inflateCircle !=null) {
                            return true;
                        }
                        inhaleSound();
                        inflateCircle = new Handler();
                        inflateCircle.postDelayed(myAction,5);

                        handler.post(startRun);
                        handler.postDelayed(threeSecondRun, 3000);
                        handler.postDelayed(tenSecondRun, 10000);
                        break;
                    }

                    default: {
                        if (!isThreeSecondRunCallBackPresent){
                            //TODO: reset animation and sound
                            Log.i(TAG,"Hold less than 3 seconds!");
                            if(isInhaling){
                                if (inflateCircle == null)
                                {
                                    return true;
                                }
                                inflateCircle.removeCallbacks(myAction);
                                inflateCircle =null;
                                failedInhale();
                            } else {


                            }
                        } else {
                            //TODO: stop animation and sound, move to exhale
                            Log.i(TAG,"Hold more than 3 seconds!");
                            isThreeSecondRunCallBackPresent=false;
                            if (isInhaling) {
                                helpText.setText(R.string.msg_exhale);
                                animateExhale(3000);
                                isInhaling=false;
                            } else {
                                helpText.setText(R.string.msg_done_exhaling);
                                isInhaling=true;
                            }

                        }
                        handler.removeCallbacks(startRun);
                        handler.removeCallbacks(threeSecondRun);
                        handler.removeCallbacks(tenSecondRun);
                        break;
                    }

                }
                return true;
            }
        });

    }

    private void setupExhaleButton() {
        inhaleExhaleBtn.setText("Out!");
        inhaleExhaleBtn.setBackgroundColor(getColor(R.color.exhale_blue));
    }

    private void setupInhaleButton() {
        inhaleExhaleBtn.setText("In");
        inhaleExhaleBtn.setBackgroundColor(getColor(R.color.breathe_green));
    }

    private void updateHeading(int numBreathLeft) {
        headingText = findViewById(R.id.text_heading);
        headingText.setText("Let's take "+numBreathLeft+ "breaths together");
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

    private void setupBeginBtn() {
        beginBtn = findViewById(R.id.button_begin);
        inhaleExhaleBtn = findViewById(R.id.button_inhale_exhale);
        helpText = findViewById(R.id.text_help);
        headingText = findViewById(R.id.text_heading);
        beginBtn.setOnClickListener(v -> {
            //TODO: save the number of breath user wants to breathe

            beginBtn.setVisibility(View.INVISIBLE);
            inhaleExhaleBtn.setVisibility(View.VISIBLE);
            helpText.setText(R.string.msg_inhale);
        });
    }


    private class OnState extends State {
        Handler timerHandler = new Handler();
        Runnable timerRunnable = () -> {
            // When timer expires transition state
            setState(offState);

        };
        @Override
        void handleClickOff() {
            super.handleClickOff();
            Toast.makeText(TakeBreathActivity.this, "From On state, going to Off", Toast.LENGTH_SHORT).show();
            setState(offState);
        }

        @Override
        void handleEnter() {
            super.handleEnter();
            TextView tv =  findViewById(R.id.text_heading);
            tv.setText("In On State");


            timerHandler.postDelayed(timerRunnable,2000);


        }
        // Cleanup timers upon exit
        @Override
        void handleExit() {
            super.handleExit();
            timerHandler.removeCallbacks(timerRunnable);
        }

        @Override
        void handleClickOn() {
            super.handleClickOn();
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable,2000);
        }
    }

    private class OffState extends State {
        int count = 0;
        @Override
        void handleClickOn() {
            super.handleClickOn();
            Toast.makeText(TakeBreathActivity.this, "From off state, clicked On", Toast.LENGTH_SHORT).show();
            setState(onState);
        }

        @Override
        void handleClickOff() {
            super.handleClickOff();
            Toast.makeText(TakeBreathActivity.this, "From off state, clicked off", Toast.LENGTH_SHORT).show();
        }

        @Override
        void handleEnter() {
            super.handleEnter();
            TextView tv =  findViewById(R.id.text_heading);
            tv.setText("In OFF STATE");
        }

        @Override
        void handleExit() {
            super.handleExit();
            Log.i("OffState","Just exited off state");
        }
    }
    // Does nothing, Avoids null checks with Null Object Pattern
    private class IdleState extends State {

    }
}