package com.snhu.weighttracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private UserRepository userRepo;
    private User user;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView loginErrorTextView;
    private TextView usernameTakenErrorTextView;
    private TextView fieldErrorTextView;
    private TextView usernameErrorTextView;
    private TextView passwordErrorTextView;
    private boolean userInstantiated;
    boolean loginErrorVisible;
    boolean usernameTakenErrorVisible;
    boolean fieldErrorVisible;
    boolean usernameErrorVisible;
    boolean passwordErrorVisible;
    private final int VALID_FIELD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the edit text views
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);

        // Get the text views for errors
        loginErrorTextView = findViewById(R.id.login_error_text_view);
        usernameTakenErrorTextView = findViewById(R.id.username_taken_error_text_view);
        fieldErrorTextView = findViewById(R.id.field_length_error_text_view);
        usernameErrorTextView = findViewById(R.id.username_length_error_text_view);
        passwordErrorTextView = findViewById(R.id.password_length_error_text_view);

        // Restore state when rotating screen or restoring app
        if (savedInstanceState != null) {
            // Restore fields
            userInstantiated = savedInstanceState.getBoolean("userInstantiated");
            loginErrorVisible = savedInstanceState.getBoolean("loginErrorVisible");
            usernameTakenErrorVisible = savedInstanceState.getBoolean("usernameTakenErrorVisible");
            fieldErrorVisible = savedInstanceState.getBoolean("fieldErrorVisible");
            usernameErrorVisible = savedInstanceState.getBoolean("usernameErrorVisible");
            passwordErrorVisible = savedInstanceState.getBoolean("passwordErrorVisible");
        }
        else {
            // Initialize fields
            userInstantiated = false;
            loginErrorVisible = false;
            usernameTakenErrorVisible = false;
            fieldErrorVisible = false;
            usernameErrorVisible = false;
            passwordErrorVisible = false;
        }

        // Restore error message visibility states
        if (loginErrorVisible) loginErrorTextView.setVisibility(View.VISIBLE);
        if (usernameTakenErrorVisible) usernameTakenErrorTextView.setVisibility(View.VISIBLE);
        if (fieldErrorVisible) fieldErrorTextView.setVisibility(View.VISIBLE);
        if (usernameErrorVisible) usernameErrorTextView.setVisibility(View.VISIBLE);
        if (passwordErrorVisible) passwordErrorTextView.setVisibility(View.VISIBLE);

        // Get the user repository instance
        userRepo = UserRepository.getInstance(getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save error message visibility
        savedInstanceState.putBoolean("loginErrorVisible", loginErrorVisible);
        savedInstanceState.putBoolean("usernameTakenErrorVisible", usernameTakenErrorVisible);
        savedInstanceState.putBoolean("fieldErrorVisible", fieldErrorVisible);
        savedInstanceState.putBoolean("usernameErrorVisible", usernameErrorVisible);
        savedInstanceState.putBoolean("passwordErrorVisible", passwordErrorVisible);

        // Call the superclass to save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void login(View view) {
        hideErrors();

        // Try to create the user object
        String username = createUser();

        // If the user object has not been created or does not exist in the database
        if (!userInstantiated || userRepo.userExists(username) == 0) {
            // Show the login error message
            loginErrorTextView.setVisibility(View.VISIBLE);
            loginErrorVisible = true;
        }
        else {
            // Go to the weight tracker activity
            Intent intent = new Intent(this, WeightTrackerActivity.class);
            startActivity(intent);
        }
    }

    public void register(View view) {
        hideErrors();

        // Try to create the user object
        String username = createUser();

        // If the user object has been created and does not exist in the database
        if (userInstantiated && userRepo.userExists(username) == 0) {
            // Insert the user into the user database
            userRepo.addUser(user);
        } else {
            usernameTakenErrorTextView.setVisibility(View.VISIBLE);
            usernameTakenErrorVisible = true;
        }
    }

    private String createUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        userInstantiated = false;

        // Check username and password length and display an error if needed
        if (username.length() < VALID_FIELD_LENGTH && password.length() < VALID_FIELD_LENGTH) {
            fieldErrorTextView.setVisibility(View.VISIBLE);
            fieldErrorVisible = true;
        }
        else if (username.length() < VALID_FIELD_LENGTH) {
            usernameErrorTextView.setVisibility(View.VISIBLE);
            usernameErrorVisible = true;
        }
        else if (password.length() < VALID_FIELD_LENGTH) {
            passwordErrorTextView.setVisibility(View.VISIBLE);
            passwordErrorVisible = true;
        }
        else {
            // Instantiate the user object
            user = new User(username, password);
            userInstantiated = true;
        }

        return username;
    }

    private void hideErrors() {
        // Hide all error text views
        loginErrorTextView.setVisibility(View.GONE);
        loginErrorVisible = false;
        usernameTakenErrorTextView.setVisibility(View.GONE);
        usernameTakenErrorVisible = false;
        usernameErrorTextView.setVisibility(View.GONE);
        usernameErrorVisible = false;
        passwordErrorTextView.setVisibility(View.GONE);
        passwordErrorVisible = false;
        fieldErrorTextView.setVisibility(View.GONE);
        fieldErrorVisible = false;
    }
}