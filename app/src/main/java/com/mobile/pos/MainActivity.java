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

import java.sql.Connection;
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
    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectionConfig connectionConfig = new ConnectionConfig();
        con = connectionConfig.CONN("FBMaster");
        confirm = (Button) findViewById(R.id.confirm);
        cancel = (Button) findViewById(R.id.cancel);
        order = (Button) findViewById(R.id.order);
        kategori = (Spinner) findViewById(R.id.kategori);
        nomor = (Spinner) findViewById(R.id.nomor);
        search = (EditText) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.listView);
        gridView = (GridView) findViewById(R.id.gridView);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(i);
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

    public void getKategoriMeja() {
        try {
            String query = "Select Spec_CatDesc,Spec_CatCode from SpecCat Order By Spec_CatDesc";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Kategori k = new Kategori();
                k.setNama(rs.getString("Spec_CatDesc"));
                k.setKode(rs.getString("Spec_CatCode"));
                listKategori.add(k);
            }
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
        } catch (Exception e) {
        }
    }

    public void getNomorMeja(String kode) {
        try {
            listSpec.clear();
            String query = "Select Spec_Code, ISKtv From Spec Where Spec_CatCode = '" + kode + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Spec s = new Spec();
                s.setKtv(rs.getBoolean("ISKtv"));
                s.setKode(rs.getString("Spec_Code"));
                listSpec.add(s);
            }
            adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listSpec);
            nomor.setAdapter(adapter2);
        } catch (Exception e) {
        }
    }
}
