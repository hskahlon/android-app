package ca.cmpt276.charcoal.practicalparent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Vibrator;
import androidx.core.app.NotificationManagerCompat;

import ca.cmpt276.charcoal.practicalparent.model.AlarmInfo;


public class NotificationStopBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra(context.getString(R.string.NotificationID_intentNametag), 0);

        AlarmInfo alarmInfo = AlarmInfo.getInstance();
        Ringtone ringtone = alarmInfo.getRingtone();
        Vibrator vibrator = alarmInfo.getVibrator();

        ringtone.stop();
        vibrator.cancel();

        NotificationManagerCompat.from(context).cancel(notificationId);
    }
}
