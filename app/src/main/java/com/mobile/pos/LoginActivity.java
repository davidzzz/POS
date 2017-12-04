package com.mobile.pos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.pos.sql.ConnectionConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private UserLoginTask mAuthTask = null;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Constant.ip = getPrefs.getString("ip", Constant.ip);
        Constant.username = getPrefs.getString("username", Constant.username);
        Constant.password = getPrefs.getString("password", Constant.password);
        mPasswordView = (EditText) findViewById(R.id.password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView tv1 = (TextView) findViewById(R.id.satu);
        tv1.setOnClickListener(this);
        TextView tv2 = (TextView) findViewById(R.id.dua);
        tv2.setOnClickListener(this);
        TextView tv3 = (TextView) findViewById(R.id.tiga);
        tv3.setOnClickListener(this);
        TextView tv4 = (TextView) findViewById(R.id.empat);
        tv4.setOnClickListener(this);
        TextView tv5 = (TextView) findViewById(R.id.lima);
        tv5.setOnClickListener(this);
        TextView tv6 = (TextView) findViewById(R.id.enam);
        tv6.setOnClickListener(this);
        TextView tv7 = (TextView) findViewById(R.id.tujuh);
        tv7.setOnClickListener(this);
        TextView tv8 = (TextView) findViewById(R.id.delapan);
        tv8.setOnClickListener(this);
        TextView tv9 = (TextView) findViewById(R.id.sembilan);
        tv9.setOnClickListener(this);
        TextView tv0 = (TextView) findViewById(R.id.nol);
        tv0.setOnClickListener(this);
        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.satu:
                mPasswordView.setText(mPasswordView.getText() + "1");
                break;
            case R.id.dua:
                mPasswordView.setText(mPasswordView.getText() + "2");
                break;
            case R.id.tiga:
                mPasswordView.setText(mPasswordView.getText() + "3");
                break;
            case R.id.empat:
                mPasswordView.setText(mPasswordView.getText() + "4");
                break;
            case R.id.lima:
                mPasswordView.setText(mPasswordView.getText() + "5");
                break;
            case R.id.enam:
                mPasswordView.setText(mPasswordView.getText() + "6");
                break;
            case R.id.tujuh:
                mPasswordView.setText(mPasswordView.getText() + "7");
                break;
            case R.id.delapan:
                mPasswordView.setText(mPasswordView.getText() + "8");
                break;
            case R.id.sembilan:
                mPasswordView.setText(mPasswordView.getText() + "9");
                break;
            case R.id.nol:
                mPasswordView.setText(mPasswordView.getText() + "0");
                break;
            case R.id.delete:
                String pass = mPasswordView.getText().toString();
                mPasswordView.setText(pass.substring(0, pass.length() - 1));
                break;
            default:
                break;
        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPassword;
        private String z, userCode, username, date;

        UserLoginTask(String password) {
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ConnectionConfig connectionConfig = new ConnectionConfig();
                Connection con = connectionConfig.CONN("FBMain");
                if (con == null) {
                    z = "Error in connection with SQL server";
                    return false;
                } else {
                    String pass = mPassword.trim();
                    String temp = "";
                    for (int i = 0; i < pass.length(); i++) {
                        temp = temp + Integer.toString((int)pass.charAt(i), 16);
                    }
                    pass = temp.trim();
                    String query = "select * from User_Pass where Password2='" + pass + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (!rs.next()) {
                        z = "Password Salah";
                        return false;
                    }
                    userCode = rs.getString("User_Code");
                    username = rs.getString("User_Name");
                    query = "select PublicDate from PublicDate";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    rs.next();
                    date = rs.getString("PublicDate");
                }
                con.close();
            } catch (Exception ex) {
                z = ex.toString();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                Intent i = new Intent(LoginActivity.this, MejaActivity.class);
                i.putExtra("userCode", userCode);
                i.putExtra("username", username);
                i.putExtra("date", date);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, z, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    @Override
    public void onBackPressed() {
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.login_admin) {
            Intent i = new Intent(LoginActivity.this, LoginAdminActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

