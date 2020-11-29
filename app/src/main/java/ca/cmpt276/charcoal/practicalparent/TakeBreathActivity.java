package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
    private int numBreathLeft = 0;
    ImageView image_Breathe;


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


    }




    //Code Reference: https://stackoverflow.com/questions/10511423/android-repeat-action-on-pressing-and-holding-a-button
    //Code Reference: https://stackoverflow.com/questions/22606977/how-can-i-get-button-pressed-time-when-i-holding-button-on
    //TODO: make a custom button for blind people -->this is the reason why it's in yellow
    @SuppressLint("ClickableViewAccessibility")
    private void setupInhaleExhaleBtn() {
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
                        inhaleExhaleBtn.setText("Out!");


                    }else{
                        //TODO: Update Remaining breath to take

                        if(numBreathLeft>0){
                            inhaleExhaleBtn.setText("In");
                        } else{
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
                    }else{
                        helpText.setText(R.string.msg_release_button_for_exhale);
                    }
                }
            };

            private Handler myHandler;

            Runnable myAction = new Runnable(){

                @Override
                public void run() {
                    startBreatheAnim();
                    myHandler.postDelayed(this,5);
                }
            };

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN: {
                        if (myHandler!=null)
                        {
                            return true;
                        }
                        myHandler = new Handler();
                        myHandler.postDelayed(myAction,5);


                        handler.post(startRun);
                        handler.postDelayed(threeSecondRun, 3000);
                        handler.postDelayed(tenSecondRun, 10000);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {

                        hideBreathe();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        hideBreathe();

                        if (myHandler == null)
                        {
                            return true;
                        }
                        myHandler.removeCallbacks(myAction);
                        myHandler=null;
                        break;



                    default: {
                        if (!isThreeSecondRunCallBackPresent){
                            //TODO: reset animation and sound
//                            resetBreatheAnim();

                            Log.i(TAG,"Hold less than 3 seconds!");

                            if(isInhaling){

                            }else{

                            }
                        } else {
                            //TODO: stop animation and sound, move to exhale
                            Log.i(TAG,"Hold more than 3 seconds!");
                            isThreeSecondRunCallBackPresent=false;
                            if(isInhaling){
                                helpText.setText(R.string.msg_exhale);
                                isInhaling=false;
                            }else{
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


    private void hideBreathe() {
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
            }
        });


        anim.start();
        resetBreathImage();
    }

    private void resetBreathImage() {
        image_Breathe = findViewById(R.id.image_breathe);
        ViewGroup.LayoutParams params =  image_Breathe.getLayoutParams();
        params.height=4;
        params.width=17;
        image_Breathe.setLayoutParams(params);
    }

    private void startBreatheAnim() {

        image_Breathe = findViewById(R.id.image_breathe);
        image_Breathe.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params =  image_Breathe.getLayoutParams();
        params.height+=20;
        params.width+=20;
        image_Breathe.setLayoutParams(params);

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


}