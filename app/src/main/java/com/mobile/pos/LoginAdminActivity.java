package com.mobile.pos;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobile.pos.sql.ConnectionConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginAdminActivity extends AppCompatActivity {
    private LoginActivity.UserLoginTask mAuthTask = null;
    private EditText mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        Button kembali = (Button) findViewById(R.id.kembali);
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
                InputMethodManager imm2 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                Intent i = new Intent(LoginAdminActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            String pass = password.trim();
            String temp = "";
            for (int i = 0; i < pass.length(); i++) {
                temp = temp + Integer.toString((int)pass.charAt(i), 16);
            }
            pass = temp.trim();
            boolean user1 = email.equalsIgnoreCase("admin") && pass.equalsIgnoreCase("61646D696E");
            boolean user2 = email.equalsIgnoreCase("endy") && pass.equalsIgnoreCase("54464F5341");
            if (user1 || user2) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
                InputMethodManager imm2 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                Intent i = new Intent(this, SettingActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Tidak bisa masuk", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onBackPressed() {
    }
}
