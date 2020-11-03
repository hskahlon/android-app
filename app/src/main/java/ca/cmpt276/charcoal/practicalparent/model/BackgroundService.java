package ca.cmpt276.charcoal.practicalparent.model;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import ca.cmpt276.charcoal.practicalparent.TimeOutActivity;

public class BackgroundService extends Service {
//reference: https://www.youtube.com/watch?v=BbXuumYactY

    private final static String TAG = "BroadcastService";
    private static final String EXTRA_TIME = "ca.cmpt276.charcoal.practicalparent.model - timeLeftinMillis";
    private long timeLeftInMillis;
    public static final String COUNTDOWN_BR = "ca.cmpt276.charcoal.practicalparent.model";
    Intent intent = new Intent(COUNTDOWN_BR);
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning;

    public static Intent makeLaunchIntent(Context context, long timeLeftInMillis) {
        Intent intent = new Intent(context, BackgroundService.class);
        intent.putExtra(EXTRA_TIME, timeLeftInMillis);
        return intent;
    }

    private void startTimer() {
        Log.i(TAG, "timeLeftinMillis" + timeLeftInMillis);

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isTimerRunning = true;
                timeLeftInMillis = millisUntilFinished;

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                intent.putExtra("countDown",timeLeftInMillis);
                intent.putExtra("isTimerRunning",isTimerRunning);

                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                intent.putExtra("isTimerRunning",isTimerRunning);
                sendBroadcast(intent);
                Log.i(TAG, "Timer finished");
            }
        }.start();


    }


    @Override
    public void onCreate() {
        super.onCreate();


    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timeLeftInMillis = intent.getLongExtra(EXTRA_TIME , 1000);
        Log.i(TAG, "timeleftin Millis in onstart method" + timeLeftInMillis);
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        countDownTimer.cancel();
        isTimerRunning = false;
        intent.putExtra("isTimerRunning",isTimerRunning);
        sendBroadcast(intent);

    }
}
