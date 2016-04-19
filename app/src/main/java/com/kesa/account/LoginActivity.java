package com.kesa.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kesa.MainActivity;
import com.kesa.R;
import com.kesa.app.KesaApplication;
import com.kesa.profile.EditProfileActivity;
import com.kesa.util.OnCompleteListener;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/** An activity handling the authentication to the {@link KesaApplication}. */
public class LoginActivity extends AppCompatActivity {

    @Inject AccountManager accountManager;

    @Bind(R.id.createAccountTextView) TextView createAccountTextView;
    @Bind(R.id.loginButton) Button loginButton;
    @Bind(R.id.skipButton) Button skipButton;
    @Bind(R.id.emailEditText) EditText emailEditText;
    @Bind(R.id.passwordEditText) EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Dependency Injections
        ((KesaApplication) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.createAccountTextView)
    void onSignUpClickEvent() {
        Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(signUpIntent);
        finish();
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
                new OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Intent mainIntent =
                            new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                });
    }

    @OnClick(R.id.skipButton)
    void onSkipButtonClickEvent() {
        String testingEmail = "testing@gmail.com";
        String testingPassword = "testing";
        accountManager
            .registerActivity(this)
            .authenticateWithPassword(
                testingEmail,
                testingPassword,
                new OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Intent editProfileIntent =
                            new Intent(getApplicationContext(), EditProfileActivity.class);
                        startActivity(editProfileIntent);
                        finish();
                    }
                });
    }

    private boolean validate(String email, String password) {
        // Checking the format of the email
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            return false;
        }

        // Checking the format of the password
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordEditText.setError("Between 4 and 10 alphanumeric characters");
            return false;
        }

        return true;
    }
}
