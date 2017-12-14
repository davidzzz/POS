package com.mobile.pos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    EditText teksIP, teksUser, teksPass;
    SharedPreferences getPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        teksIP = (EditText) findViewById(R.id.ip);
        teksUser = (EditText) findViewById(R.id.username);
        teksPass = (EditText) findViewById(R.id.password);
        teksIP.setText(Constant.ip);
        teksUser.setText(Constant.username);
        teksPass.setText(Constant.password);
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Button button = (Button) findViewById(R.id.ok);
        Button kembali = (Button) findViewById(R.id.kembali);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.ip = teksIP.getText().toString();
                Constant.username = teksUser.getText().toString();
                Constant.password = teksPass.getText().toString();
                SharedPreferences.Editor e = getPrefs.edit();
                e.putString("ip", Constant.ip);
                e.putString("username", Constant.username);
                e.putString("password", Constant.password);
                e.apply();
                Toast.makeText(SettingActivity.this, "Setting berhasil diubah", Toast.LENGTH_SHORT).show();
            }
        });
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(teksIP.getWindowToken(), 0);
                InputMethodManager imm1 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(teksUser.getWindowToken(), 0);
                InputMethodManager imm2 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(teksPass.getWindowToken(), 0);
                Intent i = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
