package com.kesa.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kesa.MainActivity;
import com.kesa.R;
import com.kesa.app.KesaApplication;
import com.kesa.util.ResultHandler;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity handling the authentication to the {@link KesaApplication}.
 *
 * @author hongil
 */
public class LoginActivity extends AppCompatActivity {

    @Inject AccountManager accountManager;
    @Bind(R.id.createAccountTextView) TextView createAccountTextView;
    @Bind(R.id.loginButton) Button loginButton;
    @Bind(R.id.emailEditText) EditText emailEditText;
    @Bind(R.id.passwordEditText) EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Dependency Injections
        ((KesaApplication) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);

        // Check if the user has been already authenticated
        if (accountManager.getCurrentUserUid() != null) {
            accountManager.reauthenticate();
            startMainActivity();
        }
    }

    @OnClick(R.id.createAccountTextView)
    void onSignUpClickEvent() {
        Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(signUpIntent);
    }

    @OnClick(R.id.loginButton)
    void onLoginButtonClickEvent() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (!validate(email, password)) {
            return;
        }

        accountManager
            .registerActivity(this)
            .authenticateWithPassword(
                email,
                password,
                new ResultHandler() {
                    @Override
                    public void onComplete() {
                        // Moves to the main page on authentication complete.
                        startMainActivity();
                    }

                    @Override
                    public void onError(Exception exception) {
                        // TODO(hongil): Improves error handling messages
                        // Displaying unexpected error with SnackBar
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "Unexpected error while authenticating...",
                            Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void startMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private boolean validate(String email, String password) {
        // Checking the format of the email
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            return false;
        }

        // Checking the format of the password
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordEditText.setError(
                "Enter a password composed of 4 - 10 alphanumeric characters");
            return false;
        }

        return true;
    }
}
