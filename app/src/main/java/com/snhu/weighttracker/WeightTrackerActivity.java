package com.snhu.weighttracker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.time.Month;
import java.util.Iterator;
import java.util.LinkedHashMap;

/** @noinspection ALL*/
public class WeightTrackerActivity extends AppCompatActivity {
    private OptionRepository optionRepo;
    private DayRepository dayRepo;
    private GridLayout monthlyGridLayout;
    private String currentMonth;
    private String goalType;
    private String goalWeight;
    private int selectedDay;
    private final String GOAL_TYPE_OPTION = "goal_type";
    private final String GOAL_WEIGHT_OPTION = "goal_weight";
    private final String LOOSE_WEIGHT_OPTION_VALUE = "loose_weight";
    private final String GAIN_WEIGHT_OPTION_VALUE = "gain_weight";
    private final String SELECTED_DAY_KEY = "selected_day";
    private final String CURRENT_MONTH_KEY = "current_month";
    private final String NEW_WEIGHT_KEY = "new_weight";
    private final LinkedHashMap<String, Integer> daysInMonthMap = new LinkedHashMap<>();
    private final int enabledGridColor = com.google.android.material.R.color
            .material_dynamic_neutral80;
    private final int disabledGridColor = com.google.android.material.R.color
            .material_dynamic_neutral95;
    @SuppressLint("PrivateResource")
    private final int selectedGridColor = com.google.android.material.R.color
            .mtrl_textinput_hovered_box_stroke_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_tracker);

        // Get the view for the monthly grid of logged weights
        monthlyGridLayout = findViewById(R.id.weight_tracker_monthly_grid);

        // Restore state when rotating screen or restoring app
        if (savedInstanceState != null) {
            selectedDay = savedInstanceState.getInt(SELECTED_DAY_KEY);
            currentMonth = savedInstanceState.getString(CURRENT_MONTH_KEY);
        }
        else {
            selectedDay = R.id.week_one_day_one_button;
            currentMonth = Month.of(Calendar.getInstance().get(Calendar.MONTH) + 1).name();
        }

        // Get the option repository instance
        optionRepo = OptionRepository.getInstance(getApplicationContext());

        // Get option values from database
        goalType = optionRepo.getOption(GOAL_TYPE_OPTION).getValue();
        goalWeight = optionRepo.getOption(GOAL_WEIGHT_OPTION).getValue();

        initDaysInMonthMap(); // Populate map of months to days in the month

        // Set the current month label for the monthly grid
        TextView currentMonthTextView = findViewById(R.id.current_month_text_view);
        currentMonthTextView.setText(currentMonth);

        // Set the current goal weight
        TextView currentGoalWeightTextView = findViewById(R.id.current_goal_weight_text_view);
        currentGoalWeightTextView.setText(goalWeight);

        // Get the day repository instance
        dayRepo = DayRepository.getInstance(getApplicationContext());

        // Initialize the monthly grid cells with the logged weights
        populateMonthlyGrid();

        // Change the selected day's grid color and display the weight for that day
        Button selectedDayButton = findViewById(selectedDay);
        selectDay(selectedDayButton);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the currently selected day
        savedInstanceState.putInt(SELECTED_DAY_KEY, selectedDay);

        // Save the current month for the monthly grid
        savedInstanceState.putString(CURRENT_MONTH_KEY, currentMonth);

        // Call the superclass to save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        // Get the current goal weight from the database
        goalWeight = optionRepo.getOption(GOAL_WEIGHT_OPTION).getValue();
		
		// Get the current goal type from the database
        goalType = optionRepo.getOption(GOAL_TYPE_OPTION).getValue();

        // Show the current goal weight in the UI
        TextView currentGoalWeightTextView = findViewById(R.id.current_goal_weight_text_view);
        currentGoalWeightTextView.setText(goalWeight);
    }

    @Override
    public void onBackPressed() {
        // Minimize the app instead of going to the login screen when the back button is pressed
        moveTaskToBack(true);
    }

    public void previousMonth(View view) {
        // Get the text view for the current month
        TextView currentMonthTextView = findViewById(R.id.current_month_text_view);

        // Create an iterator for the "days in month" linked hash map
        Iterator<String> daysInMonthIterator = daysInMonthMap.keySet().iterator();

        // Set the previous month to "JANUARY" and go to the next element in the iterator
        String previousMonth = daysInMonthIterator.next();
        String currentMonth = previousMonth;

        // While not at the end of the linked hash map iterator
        while(daysInMonthIterator.hasNext()) {
            previousMonth = currentMonth; // Store the previous month
            currentMonth = daysInMonthIterator.next(); // Get the current month

            if (this.currentMonth.equals(currentMonth)) {
                this.currentMonth = previousMonth; // Set the grid to the previous month
                currentMonthTextView.setText(this.currentMonth); // Update the grid month text
                break;
            }
        }

        // Get the weights for the current month
        populateMonthlyGrid();

        // Select the first day of the month
        selectDay(findViewById(R.id.week_one_day_one_button));
    }

    public void nextMonth(View view) {
        // Get the text view for the current month
        TextView currentMonthTextView = findViewById(R.id.current_month_text_view);

        // Create an iterator for the "days in month" linked hash map
        Iterator<String> daysInMonthIterator = daysInMonthMap.keySet().iterator();

        // While not at the end of the linked hash map iterator
        while(daysInMonthIterator.hasNext()) {
            String currentMonth = daysInMonthIterator.next(); // Get the current month

            // If not on the last element in the linked hash map and found the current month
            if (!currentMonth.equals("DECEMBER") && this.currentMonth.equals(currentMonth)) {
                this.currentMonth = daysInMonthIterator.next(); // Set the grid to the next month
                currentMonthTextView.setText(this.currentMonth); // Update the grid month text
                break;
            }
        }

        // Get the weights for the current month
        populateMonthlyGrid();

        // Select the first day of the month
        selectDay(findViewById(R.id.week_one_day_one_button));
    }

    ActivityResultLauncher<Intent> logWeightResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        // Get the new selected day button's weight from the result
                        String newWeight = data.getStringExtra(NEW_WEIGHT_KEY);

                        // Update the currently selected day
                        Button selectedDayButton = findViewById(selectedDay);
                        int selectedDayButtonId = selectedDayButton.getId();
                        selectedDayButton.setText(newWeight);
                        selectDay(selectedDayButton);

                        // Get the key from the day database
                        int dayId = dayRepo.getDayId(currentMonth, selectedDayButtonId);

                        // If the day does not exist in the database
                        if (dayRepo.dayExists(dayId) == 0) {
                            // Add the day to the database
                            dayRepo.addDay(new Day(currentMonth, selectedDayButton.getId(),
                                    newWeight));
                        }
                        else {
                            // Get the day since it exists
                            Day day = dayRepo.getDay(dayId);

                            // Update the weight for the day
                            day.setWeight(newWeight);
                            dayRepo.updateDay(day);
                        }

                        // Check if the newly logged weight makes the user meet their goal
                        checkGoalMet(newWeight);
                    }
                }
            });

    public void logWeight(View view) {
        Intent intent = new Intent(this, LogWeightActivity.class);

        // Update the currently selected day
        selectDay((Button) view);

        // Start the log weight activity and get a result from it
        logWeightResultLauncher.launch(intent);
    }

    public void options(View view) {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }

    public void clearWeek(View view) {
        int weekId = view.getId();
        int weekToClear = 0;
        int currentWeek = 1;
        int currentDay = 0;
        int weekFiveDisabledDays = 35 - daysInMonthMap.get(currentMonth);
        boolean selectedDayInWeekToClear = false;

        // Get the week to clear all days for
        if (weekId == R.id.week_one_clear_button) {
            weekToClear = 1;
        }
        else if (weekId == R.id.week_two_clear_button) {
            weekToClear = 2;
        }
        else if (weekId == R.id.week_three_clear_button) {
            weekToClear = 3;
        }
        else if (weekId == R.id.week_four_clear_button) {
            weekToClear = 4;
        }
        else if (weekId == R.id.week_five_clear_button) {
            weekToClear = 5;
        }

        // Delete each day for the week to clear; skip the first and last columns
        for (int i = 6; i < monthlyGridLayout.getChildCount() - 6; i++) {
            // If on the day header...
            if (i % 6 == 0) {
                currentWeek = 1; // Reset the current week counter for the next column
                currentDay++; // Move to the next column in the monthly grid
            }
            else {
                if (currentWeek == weekToClear) {
                    Button currentDayButton = (Button) monthlyGridLayout.getChildAt(i);

                    if (currentDayButton.getId() == selectedDay)
                        selectedDayInWeekToClear = true; // Found selected day button

                    // If clearing any week other than week 5
                    if (weekToClear != 5) {
                        // Clear the weight from the current day in the week to clear
                        currentDayButton.setText(R.string.empty_cell_text);

                        // Get the day id from the database
                        int dayId = dayRepo.getDayId(currentMonth, currentDayButton.getId());

                        // If the day exists in the database
                        if (dayRepo.dayExists(dayId) != 0) {
                            // Delete the day from the database
                            Day dayToDelete = dayRepo.getDay(dayId);
                            dayRepo.deleteDay(dayToDelete);
                        }
                    }
                    // Only clear the enabled days from week 5
                    else if (currentDay < weekFiveDisabledDays) {
                        // Clear the weight from the current day in the week 5
                        currentDayButton.setText(R.string.empty_cell_text);

                        // Get the day id from the database
                        int dayId = dayRepo.getDayId(currentMonth, currentDayButton.getId());

                        // If the day exists in the database
                        if (dayRepo.dayExists(dayId) != 0) {
                            // Delete the day from the database
                            Day dayToDelete = dayRepo.getDay(dayId);
                            dayRepo.deleteDay(dayToDelete);
                        }
                    }
                }

                currentWeek ++; // Move to the next row in the monthly grid
            }
        }

        if (selectedDayInWeekToClear) {
            // Reset the displayed weight for the selected day under the grid layout
            TextView loggedWeightTextView = findViewById(R.id.logged_weight_text_view);
            loggedWeightTextView.setText(R.string.weight_not_logged);
        }
    }

    private void populateMonthlyGrid () {
        // Populate the current month grid; skip the first and last columns
        for (int i = 6; i < monthlyGridLayout.getChildCount() - 6; i++) {
            // Skip the day header
            if (i % 6 != 0) {
                Button currentDay = (Button) monthlyGridLayout.getChildAt(i);
                currentDay.setText(R.string.empty_cell_text);

                // Get the key from the day database
                int dayId = dayRepo.getDayId(currentMonth, currentDay.getId());

                // If the day exists in the database
                if (dayRepo.dayExists(dayId) != 0) {
                    Day day = dayRepo.getDay(dayId);
                    String weightFromDatabase = day.getWeight();

                    // Set the weight for the grid button to the weight from the database
                    currentDay.setText(weightFromDatabase);
                }

                setGridColor(currentDay, enabledGridColor);
            }
        }

        // Disable buttons in the grid layout that are not needed for the current month
        disableDays();
    }

    private void disableDays() {
        int daysToDisable = 35 - daysInMonthMap.get(currentMonth);
        int disabledDays = 0;
        String disabledCellText = "";

        // Disable week 5 text view and clear week button if all days in the week will be disabled
        if (daysToDisable == 7) {
            monthlyGridLayout.getChildAt(5).setEnabled(false); // Week 5 text view
            monthlyGridLayout.getChildAt(53).setEnabled(false); // Week 5 clear week button
        }
        else {
            // Re-enable the week 5 text view and clear week button
            monthlyGridLayout.getChildAt(5).setEnabled(true); // Week 5 text view
            monthlyGridLayout.getChildAt(53).setEnabled(true); // Week 5 clear week button
        }

        // First re-enable all days
        // Start from the week five day 7 grid cell and move backwards towards week 5 day one
        for (int i = monthlyGridLayout.getChildCount() - 7; i >= 11; i -= 6) {
            Button currentDayButton = (Button) monthlyGridLayout.getChildAt(i);
            currentDayButton.setEnabled(true); // Re-enable the button

            // Reset cell's grid color
            setGridColor(currentDayButton, enabledGridColor);
        }

        // Then disable not needed days for the current month
        // Start from the week five day 7 grid cell and move backwards towards week 5 day one
        for (int i = monthlyGridLayout.getChildCount() - 7; i >= 11; i -= 6) {
            // Disable week five days if they are not needed for the current month
            if (disabledDays < daysToDisable) {
                Button currentDayButton = (Button) monthlyGridLayout.getChildAt(i);
                currentDayButton.setEnabled(false); // Disable the button
                currentDayButton.setText(disabledCellText); // Remove the button text

                // Set the disabled cell's grid color
                setGridColor(currentDayButton, disabledGridColor);

                disabledDays++;
            }
        }
    }

    private void checkGoalMet(String newWeight) {
        // If the user has met the goal weight for loosing weight
        if (goalType.equals(LOOSE_WEIGHT_OPTION_VALUE) &&
                Integer.parseInt(newWeight) <= Integer.parseInt(goalWeight)) {
            Toast.makeText(this, R.string.weight_loss_goal_met_toast,
                    Toast.LENGTH_SHORT).show();

            sendSMS(getString(R.string.weight_loss_goal_met_toast));
        }
        // If the user has met the goal weight for gaining weight
        else if (goalType.equals(GAIN_WEIGHT_OPTION_VALUE)
                && Integer.parseInt(newWeight) >= Integer.parseInt(goalWeight)) {
            Toast.makeText(this, R.string.weight_gain_goal_met_toast,
                    Toast.LENGTH_SHORT).show();

            sendSMS(getString(R.string.weight_gain_goal_met_toast));
        }
    }

    private void sendSMS(String message) {
        // If the user can get the devices phone number and send sms messages
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            // Get the phone number
            TelephonyManager tpm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
            String number = tpm.getLine1Number();

            // Send the text message
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(number,null,message,null,null);
        }
    }

    private void setGridColor(Button gridButton, int color) {
        gridButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                .getColor(WeightTrackerActivity.this, color)));
    }

    private void selectDay(Button dayButton) {
        TextView loggedWeightTextView = findViewById(R.id.logged_weight_text_view);
        String dayButtonText = dayButton.getText().toString();

        // Return the previously selected day grid color back to the default color
        Button prevSelectedDayButton = findViewById(selectedDay);
        setGridColor(prevSelectedDayButton, enabledGridColor);

        // Update the selected day
        selectedDay = dayButton.getId();

        // Change the cell's grid color to a convey a selected grid cell
        setGridColor(dayButton, selectedGridColor);

        // Display a not logged message under the monthly grid if weight not logged
        if (dayButtonText.equals(getString(R.string.empty_cell_text))) {
            loggedWeightTextView.setText(R.string.weight_not_logged);
        }
        else {
            loggedWeightTextView.setText(dayButtonText);
        }
    }

    private void initDaysInMonthMap() {
        daysInMonthMap.put("JANUARY", 31);
        daysInMonthMap.put("FEBRUARY", 28);
        daysInMonthMap.put("MARCH", 31);
        daysInMonthMap.put("APRIL", 30);
        daysInMonthMap.put("MAY", 31);
        daysInMonthMap.put("JUNE", 30);
        daysInMonthMap.put("JULY", 31);
        daysInMonthMap.put("AUGUST", 31);
        daysInMonthMap.put("SEPTEMBER", 30);
        daysInMonthMap.put("OCTOBER", 31);
        daysInMonthMap.put("NOVEMBER", 30);
        daysInMonthMap.put("DECEMBER", 31);
    }
}