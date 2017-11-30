package com.mobile.pos;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobile.pos.model.Kategori;
import com.mobile.pos.model.Spec;
import com.mobile.pos.sql.Query;
import com.mobile.pos.util.ControlApplication;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MejaActivity extends AppCompatActivity {
    Query query;
    Button confirm, billing;
    Spinner kategori, nomor;
    Spec spec;
    String userCode, username, date, kategoriMeja;
    ArrayList<Kategori> listSpecKat = new ArrayList<>();
    ArrayList<Spec> listSpec = new ArrayList<>();
    ArrayAdapter<Kategori> adapter1;
    ArrayAdapter<Spec> adapter2;
    ControlApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meja);
        query = new Query();
        confirm = (Button) findViewById(R.id.confirm);
        billing = (Button) findViewById(R.id.billing);
        kategori = (Spinner) findViewById(R.id.kategori);
        nomor = (Spinner) findViewById(R.id.nomor);
        userCode = getIntent().getStringExtra("userCode");
        username = getIntent().getStringExtra("username");
        date = getIntent().getStringExtra("date");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spec = (Spec) nomor.getSelectedItem();
                if (spec.isKtv()) {
                    if (spec.getStatus().equals("V")) {
                        Toast.makeText(MejaActivity.this, "KTV wajib dibuka melalui POS", Toast.LENGTH_SHORT).show();
                    } else {
                        orderLock();
                    }
                } else {
                    if (spec.getStatus().equals("V")) {
                        query.updateSpec(spec.getKode());
                    }
                    orderLock();
                }
            }
        });
        billing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // to be continued
            }
        });
        getKategoriMeja();
    }

    public void orderLock() {
        int status = query.findOrderLock(spec.getKode());
        if (status == 1) {
            int a = query.insertOpenSpec(spec.getKode(), username, date);
            int b = query.insertOrderLock(spec.getKode());
            int c = query.insertLog(spec.getKode(), "DEP", userCode, username, "BUKA MEJA " + spec.getKode());
            if (a > 0 && b > 0 && c > 0) {
                Intent i = new Intent(MejaActivity.this, MainActivity.class);
                i.putExtra("kategoriMeja", kategoriMeja);
                i.putExtra("kodeMeja", spec.getKode());
                i.putExtra("userCode", userCode);
                i.putExtra("username", username);
                i.putExtra("date", date);
                query.closeConnection();
                startActivity(i);
            } else {
                Toast.makeText(this, "Terjadi kesalahan saat melakukan konfirmasi", Toast.LENGTH_SHORT).show();
            }
        } else if (status == 0) {
            Toast.makeText(this, "Unit sedang diorder oleh user lain", Toast.LENGTH_SHORT).show();
        }
    }

    public void getKategoriMeja() {
        listSpecKat = query.findSpecCat();
        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listSpecKat);
        kategori.setAdapter(adapter1);
        kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Kategori k = (Kategori) adapterView.getItemAtPosition(i);
                getNomorMeja(k.getKode());
                kategoriMeja = k.getNama();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getNomorMeja(String kode) {
        listSpec.clear();
        listSpec = query.findSpec(kode);
        adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listSpec);
        nomor.setAdapter(adapter2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (query.isCloseConnection()) {
            query = new Query();
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onUserInteraction()
    {
        super.onUserInteraction();
        app = new ControlApplication();
        app.touch();
        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                        if (myKM.inKeyguardRestrictedInputMode() || app.isStop()) {
                            Intent i = new Intent(MejaActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }, 0, 5000
        );
    }
}
