package ca.cmpt276.charcoal.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import ca.cmpt276.charcoal.practicalparent.R;

public class TimeOutActivity extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 6000;

    private TextView countDownText;
    private Button startButton, pauseButton, cancelButton;

    private boolean isTimerRunning;
    private long timeLeftInMillis = START_TIME_IN_MILLIS;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_out);

        //Reference: https://codinginflow.com/tutorials/android/countdowntimer/part-1-countdown-timer

        countDownText = (TextView) findViewById(R.id.countDownText);
        startButton = (Button) findViewById(R.id.startBtn);
        pauseButton = (Button) findViewById(R.id.pauseBtn);
        cancelButton = (Button) findViewById(R.id.cancelBtn);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(isTimerRunning){
//                    pauseTimer();
//                } else {
//                    startTimer();
//                }
                startTimer();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTimerRunning){
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });
    }

    private void cancelTimer() {
        countDownTimer.cancel();
        timeLeftInMillis= START_TIME_IN_MILLIS;
        updateCountDownText();
        cancelButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        startButton.setText("Start");

    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        countDownText.setText(timeLeftFormatted);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                startButton.setText("Start");
                startButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
            }
        }.start();
        startButton.setVisibility(View.INVISIBLE);
        isTimerRunning = true;
        startButton.setText("Pause");
        cancelButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        pauseButton.setText("Pause");
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        startButton.setText("Start");
        cancelButton.setVisibility(View.VISIBLE);
        pauseButton.setText("Start");
    }


}