package com.kesa.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kesa.R;
import com.kesa.app.KesaApplication;
import com.kesa.profile.EditProfileActivity;
import com.kesa.util.ResultHandler;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity handling the registration of the {@link KesaApplication}.
 *
 * @author hongil
 */
public class SignUpActivity extends AppCompatActivity {

    @Inject AccountManager accountManager;
    @Bind(R.id.nameEditText) EditText nameEditText;
    @Bind(R.id.emailEditText) EditText emailEditText;
    @Bind(R.id.passwordEditText) EditText passwordEditText;
    @Bind(R.id.signInTextView) TextView signInTextView;
    @Bind(R.id.createAccountButton) Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Dependency Injections
        ((KesaApplication) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.signInTextView)
    void onSignInClickEvent() {
        // Going back to LoginActivity
        finish();
    }

    @OnClick(R.id.createAccountButton)
    void onCreateAccountClickEvent() {
        final String name = nameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        if (!validate(name, email, password)) {
            return;
        }

        accountManager
            .registerActivity(this)
            .createAccount(email, password, new ResultHandler() {
                @Override
                public void onComplete() {
                    Intent editProfileIntent =
                        new Intent(getApplicationContext(), EditProfileActivity.class);

                    // Removing the activity stacked on top (Removing LoginActivity from the stack).
                    editProfileIntent.setFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    editProfileIntent.putExtra(EditProfileActivity.USER_NAME, name);
                    startActivity(editProfileIntent);
                    finish();
                }

                @Override
                public void onError(Exception exception) {
                    // TODO(hongil): Improves error handling messages
                    // Displaying unexpected error with SnackBar
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Unexpected error while creating the account...",
                        Snackbar.LENGTH_LONG).show();
                }
            });
    }

    private boolean validate(String name, String email, String password) {
        // Checking the format of the name
        if (name.isEmpty()) {
            nameEditText.setError("Enter a valid name");
            return false;
        }

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
