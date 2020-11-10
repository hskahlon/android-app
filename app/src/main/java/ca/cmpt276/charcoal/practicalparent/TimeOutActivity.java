package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.cmpt276.charcoal.practicalparent.model.BackgroundService;
import ca.cmpt276.charcoal.practicalparent.model.GetRandomBackgroundImage;
import ca.cmpt276.charcoal.practicalparent.model.PresetTimeCustomSpinner;

/**
 *  Creates TimeOut activity and setups up buttons
 */
public class TimeOutActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String TAG = "TimeOut";
    private long startTimeInMillis;
    private long timeLeftInMillis;

    private View loadingView;
    private TextView countDownText;
    private Button startButton, pauseButton, resetButton, setButton;
    private EditText setTimeText;

    private boolean isTimerRunning;
    private boolean isTimerReset;


    private PresetTimeCustomSpinner preSetTimeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_out);

        //Reference: https://codinginflow.com/tutorials/android/countdowntimer/part-1-countdown-timer
        setupSetButton();
        setupStartButton();
        setupResetButton();
        setupPauseButton();
        setupSpinner();

        setLoadingScreen();
        setRandomBackgroundImage();

        //TODO: Delete logs when submitting
        //TODO: Vibration and sound when the alarm is finished
        //TODO: REFACTOR

        //Enable "up" on toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    private void setLoadingScreen() {
        countDownText = (TextView) findViewById(R.id.countDownText);


        loadingView = (ProgressBar) findViewById(R.id.loading_spinner);
        loadingView.animate()
                .alpha(0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingView.setVisibility(View.GONE);
                    }
                });
        setupFirstTimeOutUI();
    }

    private void setupFirstTimeOutUI(){
        fadeInView(startButton);
        fadeInView(setButton);
        fadeInView(setTimeText);
        fadeInView(countDownText);
        fadeInView(preSetTimeSpinner);
    }

    private void fadeInView(View view){
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity
        view.animate()
                .alpha(1f)
                .setDuration(6000)
                .setListener(null);
    }


    private void setRandomBackgroundImage() {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.timeoutLayout);
        layout.setBackground(ContextCompat.getDrawable(this, GetRandomBackgroundImage.getId()));
    }

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, TimeOutActivity.class);
    }

    private void setupSpinner() {
        preSetTimeSpinner = (PresetTimeCustomSpinner) findViewById(R.id.preSetTimeSpinner);
       // preSetTimeSpinner.setVisibility(View.INVISIBLE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.preSetTimes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preSetTimeSpinner.setAdapter(adapter);
        preSetTimeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String preSetTime = parent.getItemAtPosition(position).toString();
        long millisInput = Long.parseLong(preSetTime) * 60000;
        Log.i(TAG,"Selected drop down time : " + millisInput/1000);
        setTime(millisInput);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void updateUI() {
        if (isTimerRunning) {
            resetButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            pauseButton.setText(R.string.Pause);
            startButton.setVisibility(View.INVISIBLE);

            preSetTimeSpinner.setVisibility(View.INVISIBLE);

            setButton.setVisibility(View.INVISIBLE);
            setTimeText.setVisibility(View.INVISIBLE);

        } else {
            //If the timer is done
            if (timeLeftInMillis < 1000) {
                resetButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                pauseButton.setText(R.string.Pause);
                startButton.setVisibility(View.VISIBLE);

                preSetTimeSpinner.setVisibility(View.VISIBLE);

                setButton.setVisibility(View.VISIBLE);
                setTimeText.setVisibility(View.VISIBLE);
            }
            //if user press cancel when the timer is running
            else if(isTimerReset){
                resetButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                setButton.setVisibility(View.VISIBLE);
                setTimeText.setVisibility(View.VISIBLE);
                preSetTimeSpinner.setVisibility(View.VISIBLE);

            }
            //if the timer is paused
            else if (timeLeftInMillis < startTimeInMillis) {
                resetButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                pauseButton.setText(R.string.Resume);
                startButton.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void setupPauseButton() {
        pauseButton = (Button) findViewById(R.id.pauseBtn);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });
    }

    private void setupResetButton() {
        resetButton = (Button) findViewById(R.id.resetBtn);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    private void setupStartButton() {
        startButton = (Button) findViewById(R.id.startBtn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeLeftInMillis < 1000) {
                    Toast.makeText(TimeOutActivity.this, "No Timer Made", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(TimeOutActivity.this, "Started", Toast.LENGTH_SHORT).show();
                startTimer();
            }

        });
    }

    private void setupSetButton() {
        setButton = (Button) findViewById(R.id.setBtn);
        setTimeText = (EditText) findViewById(R.id.setTimeText);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = setTimeText.getText().toString();

                //If input is empty
                if (input.length() == 0) {
                    Toast.makeText(TimeOutActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input) * 60000;
                //If entered 0
                if (millisInput == 0) {
                    Toast.makeText(TimeOutActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisInput);
                setTimeText.setText("");
            }
        });
    }

    private void setTime(long milliseconds) {
        startTimeInMillis = milliseconds;
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
        updateUI();
        closeKeyboard();
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //TODO: Refactor updating buttons to updateUI()
    private void resetTimer() {
        isTimerReset = true;
        stopService(new Intent(this,BackgroundService.class));
        Log.i(TAG,"Reset service");
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();

        updateUI();
    }

    private void updateCountDownText() {

        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        Log.i(TAG, "Countdown seconds remaining:" + timeLeftInMillis/1000);
        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
         timeLeftFormatted = String.format(Locale.getDefault(),
                 "%02d:%02d", minutes, seconds);
        }
        countDownText.setText(timeLeftFormatted);
    }

    private void startTimer() {
        isTimerReset = false;
        Intent intent = BackgroundService.makeLaunchIntent(this, timeLeftInMillis);
        startService(intent);
    }

    private void pauseTimer() {
        isTimerReset = false;
        stopService(new Intent(this,BackgroundService.class));
        Log.i(TAG,"Paused service");
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //update timeLeftinmillis
            if(intent.getExtras() != null) {
                timeLeftInMillis = intent.getLongExtra("countDown", 1000);
                isTimerRunning = intent.getBooleanExtra("isTimerRunning", false);
                Log.i(TAG, "timeleftinMillis passed from service: " + timeLeftInMillis / 1000);
                Log.i(TAG, "isTimerRunning passed from service: " + isTimerRunning);
            }
            if(isTimerReset){
                timeLeftInMillis = startTimeInMillis;
            }
            updateCountDownText();
            updateUI();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BackgroundService.COUNTDOWN_BR));
        Log.i(TAG, "Registered broadcast receiver");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
