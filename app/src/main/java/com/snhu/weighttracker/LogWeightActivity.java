package com.snhu.weighttracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LogWeightActivity extends AppCompatActivity {
    private TextView formatErrorTextView;
    private boolean formatErrorVisible;
    private final String FORMAT_ERROR_KEY = "format_error";
    private final String NEW_WEIGHT_KEY = "new_weight";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_weight);

        formatErrorTextView = findViewById(R.id.format_error_text_view);

        // Restore state when rotating screen or restoring app
        if (savedInstanceState != null) {
            formatErrorVisible = savedInstanceState.getBoolean(FORMAT_ERROR_KEY);
        }
        else {
            formatErrorVisible = false;
        }

        // Restore error message visibility state
        if (formatErrorVisible) formatErrorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save error message visibility
        savedInstanceState.putBoolean(FORMAT_ERROR_KEY, formatErrorVisible);

        // Call the superclass to save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void setWeight(View view) {
        formatErrorTextView.setVisibility(View.GONE); // Hide format error message
        formatErrorVisible = false;

        // Get the edit text view that contains the new weight
        EditText enterWeightEditText = findViewById(R.id.enter_weight_edit_text);
        String newWeightText = enterWeightEditText.getText().toString();

        if (newWeightText.length() > 0 && newWeightText.length() < 5) {
            // Display a not logged weight indicator in the monthly grid if nothing is entered
            if (newWeightText.length() == 0) {
                newWeightText = getString(R.string.empty_cell_text);
            }

            // Send the new weight back to the weight tracker activity
            Intent intent = new Intent();
            intent.putExtra(NEW_WEIGHT_KEY, newWeightText);

            setResult(RESULT_OK, intent);
            finish(); // Close this activity and go back to the weight tracker activity
        }
        else {
            // Display an error message if the user is entering a weight that is too large
            formatErrorTextView.setVisibility(View.VISIBLE);
            formatErrorVisible = true;
        }
    }
}