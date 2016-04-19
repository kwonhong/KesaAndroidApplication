package com.kesa.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kesa.R;
import com.kesa.app.KesaApplication;
import com.kesa.profile.EditProfileActivity;
import com.kesa.util.OnCompleteListener;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity handling the registration of the {@link KesaApplication}.
 */
public class SignUpActivity extends AppCompatActivity {

    @Inject AccountManager accountManager;

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
        Intent loginIntent =
            new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @OnClick(R.id.createAccountButton)
    void onCreateAccountClickEvent() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (!validate(email, password)) {
            return;
        }

        accountManager
            .registerActivity(this)
            .createAccount(email, password, new OnCompleteListener() {
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
