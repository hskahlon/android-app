package ca.cmpt276.charcoal.practicalparent;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EditChildBottomSheetFragment extends BottomSheetDialogFragment {
    private TextView timeSpeedPercentageTextView;
    private int timeScaleIndex;
    private double[] timeScaleOptions;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        timeScaleIndex = getArguments().getInt(TimeOutActivity.TIME_SCALE_INDEX_TAG);
        timeScaleOptions = getArguments().getDoubleArray(TimeOutActivity.TIME_SCALE_OPTIONS_TAG);
        Log.i("Fragment", "Index raw: " + timeScaleIndex);
        Log.i("Fragment", "Percentage raw: " + timeScaleOptions[timeScaleIndex]);
        double timeScalePercentage = timeScaleOptions[timeScaleIndex] * 100;
        Log.i("Fragment", "timeScalePercentage scaled: " + timeScalePercentage);

        View view = inflater.inflate(R.layout.bottom_sheet_time_out, container, false);

        timeSpeedPercentageTextView = view.findViewById(R.id.text_time_rate);
        timeSpeedPercentageTextView.setText(getString(R.string.msg_timer_speed_percentage, (int)timeScalePercentage));

        setUpButtons(view);

        return view;
    }

    private void setUpButtons(View view) {
        Button plusButton = view.findViewById(R.id.button_speed_up_time);
        plusButton.setOnClickListener(v -> {
            if (timeScaleIndex+1 < timeScaleOptions.length) {
                timeScaleIndex++;
                double timeScalePercentage = timeScaleOptions[timeScaleIndex] * 100;
                timeSpeedPercentageTextView.setText(getString(R.string.msg_timer_speed_percentage, (int)timeScalePercentage));
            }
        });

        Button minusButton = view.findViewById(R.id.button_slow_down_time);
        minusButton.setOnClickListener(v -> {
            if (timeScaleIndex-1 >= 0) {
                timeScaleIndex--;
                double timeScalePercentage = timeScaleOptions[timeScaleIndex] * 100;
                timeSpeedPercentageTextView.setText(getString(R.string.msg_timer_speed_percentage, (int)timeScalePercentage));
            }
        });
    }



}