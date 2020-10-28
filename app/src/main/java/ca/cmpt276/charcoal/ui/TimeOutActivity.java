package ca.cmpt276.charcoal.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.cmpt276.charcoal.practicalparent.R;

public class TimeOutActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private long startTimeInMillis;

    private TextView countDownText;
    private Button startButton, pauseButton, cancelButton, setButton;

    private EditText setTimeText;

    private boolean isTimerRunning;
    private long timeLeftInMillis = startTimeInMillis;
    private CountDownTimer countDownTimer;

    private Spinner preSetTimeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_out);

        //Reference: https://codinginflow.com/tutorials/android/countdowntimer/part-1-countdown-timer

        countDownText = (TextView) findViewById(R.id.countDownText);

        startButton = (Button) findViewById(R.id.startBtn);
        pauseButton = (Button) findViewById(R.id.pauseBtn);
        cancelButton = (Button) findViewById(R.id.cancelBtn);
        setButton = (Button) findViewById(R.id.setBtn);

        setTimeText = (EditText) findViewById(R.id.setTimeText);

        preSetTimeSpinner = (Spinner) findViewById(R.id.preSetTimeSpinner);

        setupSetButton();
        setupStartButton();
        setupCancelButton();
        setupPauseButton();

        setupSpinner();
    }

    private void setupSpinner() {
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

    private void setupPauseButton() {
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
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
            }
        });
    }

    private void setupStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeLeftInMillis < 1000) {
                    Toast.makeText(TimeOutActivity.this, "No Timer Made", Toast.LENGTH_SHORT).show();
                    return;
                }
                startTimer();
            }
        });
    }

    private void setupSetButton() {
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
        cancelTimer();
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();

        cancelButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        setButton.setVisibility(View.VISIBLE);
        setTimeText.setVisibility(View.VISIBLE);
        preSetTimeSpinner.setVisibility(View.VISIBLE);
//      updateUI();
    }

    private void updateCountDownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

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
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                updateUI();
            }
        }.start();

        isTimerRunning = true;
        updateUI();
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        updateUI();

    }
}
