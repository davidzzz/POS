package com.mobile.pos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private UserLoginTask mAuthTask = null;
    private EditText mPasswordView;
    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, tv0, delete;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Constant.ip = getPrefs.getString("ip", Constant.ip);
        Constant.username = getPrefs.getString("username", Constant.username);
        Constant.password = getPrefs.getString("password", Constant.password);
        mPasswordView = (EditText) findViewById(R.id.password);
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView mEmailSignInButton = (TextView) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        tv1 = (TextView) findViewById(R.id.satu);
        tv1.setOnClickListener(this);
        tv2 = (TextView) findViewById(R.id.dua);
        tv2.setOnClickListener(this);
        tv3 = (TextView) findViewById(R.id.tiga);
        tv3.setOnClickListener(this);
        tv4 = (TextView) findViewById(R.id.empat);
        tv4.setOnClickListener(this);
        tv5 = (TextView) findViewById(R.id.lima);
        tv5.setOnClickListener(this);
        tv6 = (TextView) findViewById(R.id.enam);
        tv6.setOnClickListener(this);
        tv7 = (TextView) findViewById(R.id.tujuh);
        tv7.setOnClickListener(this);
        tv8 = (TextView) findViewById(R.id.delapan);
        tv8.setOnClickListener(this);
        tv9 = (TextView) findViewById(R.id.sembilan);
        tv9.setOnClickListener(this);
        tv0 = (TextView) findViewById(R.id.nol);
        tv0.setOnClickListener(this);
        delete = (TextView) findViewById(R.id.delete);
        delete.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.satu:
                mPasswordView.setText(mPasswordView.getText() + "1");
                tv1.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv1.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
                break;
            case R.id.dua:
                mPasswordView.setText(mPasswordView.getText() + "2");
                tv2.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv2.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
                break;
            case R.id.tiga:
                mPasswordView.setText(mPasswordView.getText() + "3");
                tv3.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv3.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
                break;
            case R.id.empat:
                mPasswordView.setText(mPasswordView.getText() + "4");
                tv4.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv4.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
                break;
            case R.id.lima:
                mPasswordView.setText(mPasswordView.getText() + "5");
                tv5.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv5.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
                break;
            case R.id.enam:
                mPasswordView.setText(mPasswordView.getText() + "6");
                tv6.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv6.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
                break;
            case R.id.tujuh:
                mPasswordView.setText(mPasswordView.getText() + "7");
                tv7.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv7.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
                break;
            case R.id.delapan:
                mPasswordView.setText(mPasswordView.getText() + "8");
                tv8.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv8.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
                break;
            case R.id.sembilan:
                mPasswordView.setText(mPasswordView.getText() + "9");
                tv9.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv9.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
                break;
            case R.id.nol:
                mPasswordView.setText(mPasswordView.getText() + "0");
                tv0.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv0.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
                break;
            case R.id.delete:
                String pass = mPasswordView.getText().toString();
                if (pass.length() > 0) {
                    mPasswordView.setText(pass.substring(0, pass.length() - 1));
                }
                delete.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        delete.setBackgroundResource(R.drawable.gray);
                    }
                }, 300);
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
                    String pass = mPassword;
                    String temp = "";
                    for (int i = 0; i < pass.length(); i++) {
                        temp = temp + Integer.toString((int)pass.charAt(i), 16);
                    }
                    pass = temp;
                    String query = "select * from User_Pass where Password2 = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, pass);
                    ResultSet rs = stmt.executeQuery();

                    if (!rs.next()) {
                        z = "Password Salah";
                        return false;
                    }
                    userCode = rs.getString("User_Code");
                    username = rs.getString("User_Name");
                    query = "select PublicDate from PublicDate";
                    stmt = con.prepareStatement(query);
                    rs = stmt.executeQuery();
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
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

