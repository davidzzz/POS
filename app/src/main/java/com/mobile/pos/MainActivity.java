package com.mobile.pos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobile.pos.model.Kategori;
import com.mobile.pos.model.Spec;
import com.mobile.pos.sql.ConnectionConfig;
import com.mobile.pos.sql.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button confirm, cancel, order;
    Spinner kategori, nomor;
    ListView listView;
    GridView gridView;
    EditText search;
    ArrayList<Kategori> listKategori = new ArrayList<>();
    ArrayList<Spec> listSpec = new ArrayList<>();
    ArrayAdapter<Kategori> adapter1;
    ArrayAdapter<Spec> adapter2;
    Query query;
    Spec spec;
    String userCode, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        query = new Query();
        confirm = (Button) findViewById(R.id.confirm);
        cancel = (Button) findViewById(R.id.cancel);
        order = (Button) findViewById(R.id.order);
        kategori = (Spinner) findViewById(R.id.kategori);
        nomor = (Spinner) findViewById(R.id.nomor);
        search = (EditText) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.listView);
        gridView = (GridView) findViewById(R.id.gridView);
        userCode = getIntent().getStringExtra("userCode");
        username = getIntent().getStringExtra("username");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spec = (Spec) nomor.getSelectedItem();
                if (spec.isKtv()) {
                    if (spec.getStatus().equals("V")) {
                        Toast.makeText(MainActivity.this, "KTV wajib dibuka melalui POS", Toast.LENGTH_SHORT).show();
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
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(i);
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(i);
            }
        });
        getKategoriMeja();
        activeState(true);
    }

    public void activeState(boolean state) {
        kategori.setEnabled(state);
        nomor.setEnabled(state);
        confirm.setEnabled(state);
        listView.setEnabled(!state);
        search.setEnabled(!state);
        gridView.setEnabled(!state);
        cancel.setEnabled(!state);
        order.setEnabled(!state);
    }

    public void orderLock() {
        int status = query.findOrderLock(spec.getKode());
        if (status == 1) {
            int a = query.insertOpenSpec(spec.getKode(), username);
            int b = query.insertOrderLock(spec.getKode());
            int c = query.insertLog(spec.getKode(), "DEP", userCode, username, "BUKA MEJA " + spec.getKode());
            if (a > 0 && b > 0 && c > 0) {
                activeState(false);
            } else {
                Toast.makeText(this, "Terjadi kesalahan saat melakukan konfirmasi", Toast.LENGTH_SHORT).show();
            }
        } else if (status == 0) {
            Toast.makeText(this, "Unit sedang diorder oleh user lain", Toast.LENGTH_SHORT).show();
        }
    }

    public void getKategoriMeja() {
        listKategori = query.findSpecCat();
        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listKategori);
        kategori.setAdapter(adapter1);
        kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Kategori k = (Kategori) adapterView.getItemAtPosition(i);
                getNomorMeja(k.getKode());
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
}
