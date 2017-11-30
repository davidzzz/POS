package com.mobile.pos.sql;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import com.mobile.pos.Constant;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionConfig {
    private String ip = Constant.ip;
    private String classs = "net.sourceforge.jtds.jdbc.Driver";
    private String db = "FBMain";
    private String un = Constant.username;
    private String password = Constant.password;

    @SuppressLint("NewApi")
    public Connection CONN(String db) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL;
        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ":1433;"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
        return conn;
    }
}