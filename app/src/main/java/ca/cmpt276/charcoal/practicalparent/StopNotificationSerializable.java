package ca.cmpt276.charcoal.practicalparent;

import android.media.Ringtone;
import android.os.Vibrator;

import java.io.Serializable;

public class StopNotificationSerializable implements Serializable {
    private final Ringtone ringtone;
    private final Vibrator vibrator;

    public StopNotificationSerializable(Ringtone ringtone, Vibrator vibrator) {
        this.ringtone = ringtone;
        this.vibrator = vibrator;
    }

    public Ringtone getRingtone() {
        return ringtone;
    }

    public Vibrator getVibrator() {
        return vibrator;
    }
}
