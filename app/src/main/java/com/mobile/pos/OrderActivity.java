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
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.pos.adapter.KategoriAdapter;
import com.mobile.pos.adapter.MenuAdapter;
import com.mobile.pos.model.Kategori;
import com.mobile.pos.model.Menu;
import com.mobile.pos.model.Spec;
import com.mobile.pos.sql.Query;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    Button kembali, cancel, order;
    TextView kategori, nomor;
    ListView listView, listMenu;
    EditText search;
    ArrayList<Menu> list = new ArrayList<>();
    Query query;
    Spec spec;
    String userCode, username, kode, kategoriMeja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        query = new Query();
        kembali = (Button) findViewById(R.id.kembali);
        cancel = (Button) findViewById(R.id.cancel);
        order = (Button) findViewById(R.id.order);
        kategori = (TextView) findViewById(R.id.kategori);
        nomor = (TextView) findViewById(R.id.nomor);
        search = (EditText) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.listView);
        listMenu = (ListView) findViewById(R.id.listMenu);
        kategoriMeja = getIntent().getStringExtra("kategoriMeja");
        kode = getIntent().getStringExtra("kode");
        kategori.setText(kategoriMeja);
        nomor.setText(kode);
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.closeConnection();
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.insertLog(spec.getKode(), "POS", userCode, username, "APPS TAMBAH MENU " + spec.getKode() + "QTY " + spec.getKode());
                query.deleteOrderLock(spec.getKode());
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                builder.setTitle("KONFIRMASI PESANAN");
                builder.setMessage("Apakah pesanan sudah benar?");
                builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        query.deleteOrderLock(spec.getKode());
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
        daftarMenu();
    }

    public void daftarMenu(){
        list = query.findDaftarMenu(kode);
        MenuAdapter adapter = new MenuAdapter(this, list);
        listMenu.setAdapter(adapter);
        listMenu.setEnabled(false);
    }
}
