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
import com.kesa.user.EditProfileActivity;
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
    @Bind(R.id.firstNameEditText) EditText firstNameEditText;
    @Bind(R.id.lastNameEditText) EditText lastNameEditText;
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
        final String firstName = firstNameEditText.getText().toString();
        final String lastName = lastNameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        if (!validate(firstName, lastName, email, password)) {
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
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // Passing profile information through an Intent object.
                    editProfileIntent.putExtra(EditProfileActivity.USER_FIRST_NAME, firstName);
                    editProfileIntent.putExtra(EditProfileActivity.USER_LAST_NAME, lastName);
                    editProfileIntent.putExtra(EditProfileActivity.USER_EMAIL, email);
                    startActivity(editProfileIntent);
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

    private boolean validate(String firstName, String lastName, String email, String password) {
        // TODO(hongil): More input validation.
        // Checking the format of the name
        if (firstName.isEmpty()) {
            firstNameEditText.setError("Enter a valid first name");
            return false;
        }

        if (lastName.isEmpty()) {
            lastNameEditText.setError("Enter a valid last name");
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
