package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

/**
 *  Sets up TimeOut activity and timer
 */
public class TimeOutActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String PREFS_NAME = "Saved data";
    private static final String START_TIME_IN_MILLIS = "startTimeInMillis";
    String TAG = "TimeOut";
    private long startTimeInMillis;
    private long timeLeftInMillis;

    private View loadingView;
    private TextView countDownText;
    private Button startButton, pauseButton, resetButton, setButton;
    private EditText setTimeText;

    private boolean isTimerRunning;
    private boolean isTimerReset;
    private boolean needToFetch = true;

    private CustomSpinner preSetTimeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_out);

        setupSetButton();
        setupStartButton();
        setupResetButton();
        setupPauseButton();
        setupSpinner();
        setLoadingScreen();
        setRandomBackgroundImage();

        // Enable "up" on toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setLoadingScreen() {
        countDownText = (TextView) findViewById(R.id.text_count_down);
        loadingView = (ProgressBar) findViewById(R.id.spinner_loading);
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
        ProgressBar pieTimer = findViewById(R.id.timer_progress);
        fadeInView(startButton);
        fadeInView(setButton);
        fadeInView(setTimeText);
        fadeInView(countDownText);
        fadeInView(preSetTimeSpinner);
        fadeInView(pieTimer);
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
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout_timeout);
        layout.setBackground(ContextCompat.getDrawable(this, GetRandomBackgroundImage.getId()));
    }

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, TimeOutActivity.class);
    }

    private void setupSpinner() {
        preSetTimeSpinner = (CustomSpinner) findViewById(R.id.spinner_preset_time);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.msg_preset_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preSetTimeSpinner.setAdapter(adapter);
        preSetTimeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String preSetTime = parent.getItemAtPosition(position).toString();
        long millisInput = Long.parseLong(preSetTime) * 60000;
        Log.i(TAG,"Selected drop down time : " + millisInput/1000);
        if(!isTimerRunning){
            setTime(millisInput);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void updateUI() {
        if (isTimerRunning) {
            resetButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            pauseButton.setText(R.string.action_pause);
            startButton.setVisibility(View.INVISIBLE);
            preSetTimeSpinner.setVisibility(View.INVISIBLE);
            setButton.setVisibility(View.INVISIBLE);
            setTimeText.setVisibility(View.INVISIBLE);
        } else {
            if (timeLeftInMillis < 1000) {
                // If the timer is done:
                resetButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                pauseButton.setText(R.string.action_pause);
                startButton.setVisibility(View.VISIBLE);
                preSetTimeSpinner.setVisibility(View.VISIBLE);
                setButton.setVisibility(View.VISIBLE);
                setTimeText.setVisibility(View.VISIBLE);
            } else if (isTimerReset) {
                // If user press cancel when the timer is running:
                resetButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                setButton.setVisibility(View.VISIBLE);
                setTimeText.setVisibility(View.VISIBLE);
                preSetTimeSpinner.setVisibility(View.VISIBLE);
            } else if (timeLeftInMillis < startTimeInMillis) {
                // If the timer is paused:
                resetButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                pauseButton.setText(R.string.action_resume);
                startButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setupPauseButton() {
        pauseButton = (Button) findViewById(R.id.button_pause);
        pauseButton.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });
    }

    private void setupResetButton() {
        resetButton = (Button) findViewById(R.id.button_reset);
        resetButton.setOnClickListener(v -> resetTimer());
    }

    private void setupStartButton() {
        startButton = (Button) findViewById(R.id.button_start);
        startButton.setOnClickListener(v -> {

            if (timeLeftInMillis < 1000) {
                Toast.makeText(TimeOutActivity.this, "No Timer Made", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(TimeOutActivity.this, "Started", Toast.LENGTH_SHORT).show();
            startTimer();
        });
    }

    private void setupSetButton() {
        setButton = (Button) findViewById(R.id.button_set);
        setTimeText = (EditText) findViewById(R.id.text_set_time);
        setButton.setOnClickListener(v -> {
            String input = setTimeText.getText().toString();

            // If input is empty
            if (input.length() == 0) {
                Toast.makeText(TimeOutActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            long millisInput = Long.parseLong(input) * 60000;
            // If entered 0
            if (millisInput == 0) {
                Toast.makeText(TimeOutActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                return;
            }
            setTime(millisInput);
            setTimeText.setText("");
        });
    }

    private void setTime(long milliseconds) {
        startTimeInMillis = milliseconds;
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
        updateUI();
        closeKeyboard();
    }

    private void saveStartTimeInMillisInSharedPrefs(long startTimeInMillis) {
        Log.i(TAG, "Save StartTimeINMillis" + startTimeInMillis);
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(START_TIME_IN_MILLIS, Long.toString(startTimeInMillis));
        editor.apply();
    }

    private long getSavedStartTimeInMillisFromSharedPrefs(){
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String serializedStartTimeInMillis = prefs.getString(START_TIME_IN_MILLIS, "0");
        Log.i(TAG, "serialized StartTimeinMillis: " + serializedStartTimeInMillis);
        return Long.parseLong(serializedStartTimeInMillis);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void resetTimer() {
        isTimerReset = true;
        stopService(new Intent(this,BackgroundService.class));
        Log.i(TAG,"Reset service");
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
        updateUI();
        updatePieTimer(timeLeftInMillis, true);
    }

    private void updateCountDownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        long formattedMillis = (hours * 1000 * 3600) + (minutes * 60 * 1000) + (seconds * 1000);
        updatePieTimer(formattedMillis, false);

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
        needToFetch = false;
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
            // Update timeLeftinmillis
            if (intent.getExtras() != null) {
                timeLeftInMillis = intent.getLongExtra("countDown", 1000);
                isTimerRunning = intent.getBooleanExtra("isTimerRunning", false);
                Log.i(TAG, "timeleftinMillis passed from service: " + timeLeftInMillis / 1000);
                Log.i(TAG, "isTimerRunning passed from service: " + isTimerRunning);
            }
            if (isTimerReset){
                timeLeftInMillis = startTimeInMillis;
            }

            if(needToFetch  && getSavedStartTimeInMillisFromSharedPrefs() != 0){
                startTimeInMillis = getSavedStartTimeInMillisFromSharedPrefs();
                needToFetch = false;
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
        Log.i(TAG, "on Destory" + startTimeInMillis);
        saveStartTimeInMillisInSharedPrefs(startTimeInMillis);
        unregisterReceiver(broadcastReceiver);
    }

    //Code Reference: https://www.youtube.com/watch?v=YsHHXg1vbcc&ab_channel=CodinginFlow
    private void updatePieTimer(long timeLeftInMillis, boolean reset){
        ProgressBar pieTimer = findViewById(R.id.timer_progress);
        float pieProgressFloat;
        int pieProgressInt;
        if (reset) {
            pieTimer.setProgress(0);
            return;
        }

        Log.i(TAG,"startTimeInMilllis in updatePieTimer before updating"+startTimeInMillis);

        Log.i(TAG,"startTimeInMilllis in updatePieTimer after updating"+startTimeInMillis);
        if (startTimeInMillis  - 500 > startTimeInMillis - timeLeftInMillis) {
            pieProgressFloat = startTimeInMillis - timeLeftInMillis;
            pieProgressFloat = (pieProgressFloat/(startTimeInMillis)) * 100;
            pieProgressFloat = Math.round(pieProgressFloat);
            pieProgressInt = ((int) pieProgressFloat);
        } else {
            pieProgressInt = 100;
        }

        pieTimer.setProgress(pieProgressInt);
    }
}
