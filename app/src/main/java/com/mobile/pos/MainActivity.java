package com.mobile.pos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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

import com.mobile.pos.adapter.KategoriAdapter;
import com.mobile.pos.model.Kategori;
import com.mobile.pos.model.Spec;
import com.mobile.pos.sql.Query;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button confirm, cancel, order;
    Spinner kategori, nomor;
    ListView listView;
    GridView gridView;
    EditText search;
    ArrayList<Kategori> listSpecKat = new ArrayList<>();
    ArrayList<Kategori> listKategori = new ArrayList<>();
    ArrayList<Spec> listSpec = new ArrayList<>();
    ArrayAdapter<Kategori> adapter1;
    ArrayAdapter<Spec> adapter2;
    Query query;
    Spec spec;
    String userCode, username, kategoriMeja;

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
                query.insertLog(spec.getKode(), "POS", userCode, username, "APPS TAMBAH MENU " + spec.getKode() + "QTY " + spec.getKode());
                query.deleteOrderLock(spec.getKode());
                activeState(true);
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("KONFIRMASI PESANAN");
                builder.setMessage("Apakah pesanan sudah benar?");
                builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        query.deleteOrderLock(spec.getKode());
                        activeState(true);
                    }
                });
                builder.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        getKategoriMeja();
        getKategori();
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

    public void getKategori() {
        listKategori = query.findKategori();
        KategoriAdapter adapter = new KategoriAdapter(this, listKategori);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Kategori k = (Kategori)adapterView.getItemAtPosition(position);
                Intent i = new Intent(MainActivity.this, OrderActivity.class);
                i.putExtra("kategoriMeja", kategoriMeja);
                i.putExtra("kode", k.getKode());
                query.closeConnection();
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (query.isCloseConnection()) {
            query = new Query();
        }
    }
}
