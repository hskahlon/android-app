package ca.cmpt276.charcoal.practicalparent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.cmpt276.charcoal.practicalparent.model.BackgroundService;
import ca.cmpt276.charcoal.practicalparent.model.GetRandomBackgroundImage;
import ca.cmpt276.charcoal.practicalparent.model.PresetTimeCustomSpinner;

public class TimeOutActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String TAG = "TimeOut";
    private long startTimeInMillis;

    private TextView countDownText;
    private Button startButton, pauseButton, cancelButton, setButton;

    private EditText setTimeText;

    private boolean isTimerRunning;
    private boolean isTimerCanceled;

    private long timeLeftInMillis;


    private PresetTimeCustomSpinner preSetTimeSpinner;

    private final long[] pattern = {0, 400, 300};
    private final int NOTIFICATION_ID = 0;



    private BackgroundService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_out);

        //Reference: https://codinginflow.com/tutorials/android/countdowntimer/part-1-countdown-timer
        setupSetButton();
        setupStartButton();
        setupCancelButton();
        setupPauseButton();

        setupSpinner();
        setRandomBackgroundImage();

        //TODO: Delete logs when submitting
        //TODO: Bug- when you come out of timeoutActivity and then go in again, there is a delay on display UI
        //TODO: Vibration and sound when the alarm is finished
        //TODO: REFACTOR

        //Enable "up" on toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

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
            cancelButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            pauseButton.setText("Pause");
            startButton.setVisibility(View.INVISIBLE);

            preSetTimeSpinner.setVisibility(View.INVISIBLE);

            setButton.setVisibility(View.INVISIBLE);
            setTimeText.setVisibility(View.INVISIBLE);

        } else {
            //If the timer is done
            if (timeLeftInMillis < 1000) {
                cancelButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                pauseButton.setText("Pause");
                startButton.setVisibility(View.VISIBLE);

                preSetTimeSpinner.setVisibility(View.VISIBLE);

                setButton.setVisibility(View.VISIBLE);
                setTimeText.setVisibility(View.VISIBLE);

                notifyTimerDone();
            }
            //if user press cancel when the timer is running
            else if(isTimerCanceled){
                cancelButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                setButton.setVisibility(View.VISIBLE);
                setTimeText.setVisibility(View.VISIBLE);
                preSetTimeSpinner.setVisibility(View.VISIBLE);

            }
            //if the timer is paused
            else if (timeLeftInMillis < startTimeInMillis) {
                cancelButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                pauseButton.setText("Resume");
                startButton.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void notifyTimerDone() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ringtone.play();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            // source https://stackoverflow.com/questions/60466695/android-vibration-app-doesnt-work-anymore-after-android-10-api-29-update
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0)
            , new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            );

        } else {
            vibrator.vibrate(pattern, 0);
        }

        createNotification(ringtone, vibrator);
    }

    private void createNotification(Ringtone ringtone, Vibrator vibrator) {
        Intent intent = makeLaunchIntent(this);
        PendingIntent pendingLaunchIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent stopTimerIntent = new Intent(this, NotificationStopBroadcastReceiver.class);
        stopTimerIntent.putExtra(getString(R.string.NotificationID_intentNametag), NOTIFICATION_ID);
        PendingIntent pendingStopTimerIntent = PendingIntent.getBroadcast(this, 0, stopTimerIntent, 0);

        AlarmInfo alarmInfo = AlarmInfo.getInstance();
        alarmInfo.setRingtone(ringtone);
        alarmInfo.setVibrator(vibrator);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.timout_alarm_notification_ID))
                .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                .setContentTitle(getString(R.string.timeout_notification_title))
                .setContentText(getString(R.string.timeout_notification_body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(pendingLaunchIntent, true)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_CALL)
                .setAutoCancel(true)

                .addAction(R.drawable.ic_baseline_alarm_24, "Stop", pendingStopTimerIntent);

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build());
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

    private void setupCancelButton() {
        cancelButton = (Button) findViewById(R.id.cancelBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
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
    private void cancelTimer() {

        isTimerCanceled = true;
        stopService(new Intent(this,BackgroundService.class));
        Log.i(TAG,"Canceled service");
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
        countDownText = (TextView) findViewById(R.id.countDownText);
        countDownText.setText(timeLeftFormatted);
    }

    private void startTimer() {
        isTimerCanceled = false;
        Intent intent = BackgroundService.makeLaunchIntent(this, timeLeftInMillis);
        startService(intent);
    }

    private void pauseTimer() {
        isTimerCanceled = false;
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
            if(isTimerCanceled){
                timeLeftInMillis = startTimeInMillis;
            }
            updateCountDownText();
            updateUI();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(BackgroundService.COUNTDOWN_BR));
        Log.i(TAG,"Registered broadcast receiver");
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
    }


}
