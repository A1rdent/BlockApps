package com.yary.blockapps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class TimePickerDialog extends DialogFragment {

    public interface TimePickerListener {
        void onTimeSet(int hours, int minutes, int seconds);
    }

    private TimePickerListener listener;
    private NumberPicker hourPicker, minutePicker, secondPicker;

    public TimePickerDialog() {
    }

    public void setTimePickerListener(TimePickerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
        
        initializePickers(view);
        setupButtons(view);
        
        builder.setView(view)
               .setTitle("Установите время блокировки");
        
        return builder.create();
    }

    private void initializePickers(View view) {
        hourPicker = view.findViewById(R.id.hourPicker);
        minutePicker = view.findViewById(R.id.minutePicker);
        secondPicker = view.findViewById(R.id.secondPicker);
        
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(0);
        
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(5);
        
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        secondPicker.setValue(0);
        
        // FIXED: Compatible solution for all API levels
        setupPickerDisplayedValues(hourPicker, "ч");
        setupPickerDisplayedValues(minutePicker, "мин");
        setupPickerDisplayedValues(secondPicker, "сек");
    }

    private void setupPickerDisplayedValues(NumberPicker picker, String suffix) {
        int minValue = picker.getMinValue();
        int maxValue = picker.getMaxValue();
        String[] displayedValues = new String[maxValue - minValue + 1];
        
        for (int i = minValue; i <= maxValue; i++) {
            displayedValues[i - minValue] = String.format("%02d %s", i, suffix);
        }
        
        picker.setDisplayedValues(displayedValues);
    }

    private void setupButtons(View view) {
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSet = view.findViewById(R.id.btnSet);
        TextView tvTotalTime = view.findViewById(R.id.tvTotalTime);
        
        NumberPicker.OnValueChangeListener changeListener = (picker, oldVal, newVal) -> updateTotalTime(tvTotalTime);
        
        hourPicker.setOnValueChangedListener(changeListener);
        minutePicker.setOnValueChangedListener(changeListener);
        secondPicker.setOnValueChangedListener(changeListener);
        
        updateTotalTime(tvTotalTime);
        
        btnCancel.setOnClickListener(v -> dismiss());
        
        btnSet.setOnClickListener(v -> {
            if (listener != null) {
                int hours = hourPicker.getValue();
                int minutes = minutePicker.getValue();
                int seconds = secondPicker.getValue();
                
                if (hours > 0 || minutes > 0 || seconds > 0) {
                    listener.onTimeSet(hours, minutes, seconds);
                    dismiss();
                } else {
                    android.widget.Toast.makeText(requireContext(), "Установите время больше 0", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTotalTime(TextView tvTotalTime) {
        int hours = hourPicker.getValue();
        int minutes = minutePicker.getValue();
        int seconds = secondPicker.getValue();
        
        long totalSeconds = hours * 3600L + minutes * 60L + seconds;
        
        if (totalSeconds == 0) {
            tvTotalTime.setText("Общее время: 0 секунд");
        } else {
            String timeText = "Общее время: ";
            if (hours > 0) timeText += hours + " ч ";
            if (minutes > 0) timeText += minutes + " мин ";
            if (seconds > 0) timeText += seconds + " сек";
            tvTotalTime.setText(timeText.trim());
        }
    }
}