package ca.cmpt276.charcoal.practicalparent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Vibrator;

public class NotificationStopBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        StopNotificationSerializable notificationInfo = (StopNotificationSerializable) intent.getSerializableExtra("Notification info");
        Ringtone ringtone = notificationInfo.getRingtone();
        Vibrator vibrator = notificationInfo.getVibrator();

        ringtone.stop();
        vibrator.cancel();
    }
}
