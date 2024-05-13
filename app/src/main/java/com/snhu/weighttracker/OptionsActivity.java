package com.snhu.weighttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity {
    private OptionRepository optionRepo;
    private Option goalTypeOption;
    private Option goalWeightOption;
    private RadioButton looseWeightRadioButton;
    private RadioButton gainWeightRadioButton;
    private TextView goalWeightFormatErrorTextView;
    boolean goalWeightFormatErrorVisible;
    private final int SMS_REQUEST_CODE = 100;
    private final String GOAL_TYPE_OPTION = "goal_type";
    private final String GOAL_WEIGHT_OPTION = "goal_weight";
    private final String LOOSE_WEIGHT_OPTION_VALUE = "loose_weight";
    private final String GAIN_WEIGHT_OPTION_VALUE = "gain_weight";
    private final String GOAL_WEIGHT_ERROR_KEY = "goal_weight_error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        looseWeightRadioButton = findViewById(R.id.radio_loose_weight);
        gainWeightRadioButton = findViewById(R.id.radio_gain_weight);
        goalWeightFormatErrorTextView = findViewById(R.id.goal_weight_format_error_text_view);

        // Get the option repository instance
        optionRepo = OptionRepository.getInstance(getApplicationContext());

        // Initialize options from database
        goalTypeOption = optionRepo.getOption(GOAL_TYPE_OPTION);
        goalWeightOption = optionRepo.getOption(GOAL_WEIGHT_OPTION);

        // Set the checked goal type radio button corresponding to the option value
        if (goalTypeOption.getValue().equals(LOOSE_WEIGHT_OPTION_VALUE)) {
            looseWeightRadioButton.setChecked(true);
            gainWeightRadioButton.setChecked(false);
        }
        else if (goalTypeOption.getValue().equals(GAIN_WEIGHT_OPTION_VALUE)) {
            gainWeightRadioButton.setChecked(true);
            looseWeightRadioButton.setChecked(false);
        }

        // Restore state when rotating screen or restoring app
        if (savedInstanceState != null) {
            goalWeightFormatErrorVisible = savedInstanceState.getBoolean(GOAL_WEIGHT_ERROR_KEY);
        }
        else {
            goalWeightFormatErrorVisible = false;
        }

        // Restore error message visibility state
        if (goalWeightFormatErrorVisible) goalWeightFormatErrorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save error message visibility
        savedInstanceState.putBoolean(GOAL_WEIGHT_ERROR_KEY, goalWeightFormatErrorVisible);

        // Call the superclass to save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void allowSms(View view) {
        // Request SMS permissions if the user has not already denied it twice
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { android.Manifest.permission.SEND_SMS,
                            android.Manifest.permission.READ_SMS,
                            android.Manifest.permission.READ_PHONE_NUMBERS,
                            android.Manifest.permission.READ_PHONE_STATE}, SMS_REQUEST_CODE);
        }
        else {
            // Show a toast notification if SMS permission is already granted
            Toast.makeText(this, R.string.sms_permission_already_granted_toast,
                            Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        // Show a toast notification for the current SMS permission state
        if (requestCode == SMS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.sms_permission_granted_toast,
                                Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, R.string.sms_permission_denied_toast,
                                Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void changeGoalType(View view) {
        int radioButtonId = view.getId();

        // Change the goal type option value depending on which radio button is clicked
        if (radioButtonId == R.id.radio_loose_weight) {
            goalTypeOption.setValue(LOOSE_WEIGHT_OPTION_VALUE);
        }
        else if (radioButtonId == R.id.radio_gain_weight) {
            goalTypeOption.setValue(GAIN_WEIGHT_OPTION_VALUE);
        }

        // Update the option's value in the database
        optionRepo.updateOption(goalTypeOption);
    }

    public void setGoalWeight(View view) {
        EditText goalWeightEditText = findViewById(R.id.goal_weight_edit_text);
        String newGoalWeight = goalWeightEditText.getText().toString();
        goalWeightFormatErrorTextView.setVisibility(View.GONE); // Hide the error message
        goalWeightFormatErrorVisible = false;

        // Goal weight must be less than 5 digits
        if (newGoalWeight.length() > 0 && newGoalWeight.length() < 5) {
            // Set the value for the goal weight option to the text from the goal weight edit text
            goalWeightOption.setValue(newGoalWeight);

            // Update the option's value in the database
            optionRepo.updateOption(goalWeightOption);
        }
        else {
            // Display the format error message
            goalWeightFormatErrorTextView.setVisibility(View.VISIBLE);
            goalWeightFormatErrorVisible = true;
        }
    }
}