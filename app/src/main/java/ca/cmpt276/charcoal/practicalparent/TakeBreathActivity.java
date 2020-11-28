package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TakeBreathActivity extends AppCompatActivity {
    String TAG = "TakeBreathAcitivty";
    private Button beginBtn;
    private Button inhaleExhaleBtn;
    private TextView headingText,helpText;
    private boolean isThreeSecondRunCallBackPresent = false;
    private boolean isInhaling = true;
    private int numBreathLeft = 0;

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


    //Code Reference: https://stackoverflow.com/questions/22606977/how-can-i-get-button-pressed-time-when-i-holding-button-on
    //TODO: make a custom button for blind people -->this is the reason why it's in yellow
    @SuppressLint("ClickableViewAccessibility")
    private void setupInhaleExhaleBtn() {
        inhaleExhaleBtn.setOnTouchListener(new View.OnTouchListener(){
            Handler handler = new Handler();

            Runnable startRun = new Runnable() {
                @Override
                public void run() {
                    //TODO: when the user starts holding --> start animation and sound
                    Log.i(TAG, "user starts holding..");
                    if(isInhaling){

                    }else{

                    }
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


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
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

    private void setupBeginBtn() {
        beginBtn = findViewById(R.id.button_begin);
        inhaleExhaleBtn = findViewById(R.id.button_inhale_exhale);
        helpText = findViewById(R.id.text_help);
        headingText = findViewById(R.id.text_heading);
        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: save the number of breath user wants to breathe

                beginBtn.setVisibility(View.INVISIBLE);
                inhaleExhaleBtn.setVisibility(View.VISIBLE);
                helpText.setText(R.string.msg_inhale);
            }
        });
    }


}