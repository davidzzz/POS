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
import com.mobile.pos.adapter.OrderAdapter;
import com.mobile.pos.model.Kategori;
import com.mobile.pos.model.Menu;
import com.mobile.pos.model.Order;
import com.mobile.pos.model.Spec;
import com.mobile.pos.sql.Query;
import com.mobile.pos.view.ExpandableHeightListView;
import com.mobile.pos.view.OnSwipeTouchListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    Button kembali, cancel, order;
    TextView kategori, nomor;
    ExpandableHeightListView listView, listMenu;
    EditText search;
    ArrayList<Menu> list = new ArrayList<>();
    ArrayList<Order> listOrder = new ArrayList<>();
    Query query;
    Spec spec;
    String username, kode, kodeMeja, kategoriMeja, userCode;
    OrderAdapter orderAdapter;

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
        listView = (ExpandableHeightListView) findViewById(R.id.listView);
        listMenu = (ExpandableHeightListView) findViewById(R.id.listMenu);
        kategoriMeja = getIntent().getStringExtra("kategoriMeja");
        kodeMeja = getIntent().getStringExtra("kodeMeja");
        kode = getIntent().getStringExtra("kode");
        userCode = getIntent().getStringExtra("userCode");
        username = getIntent().getStringExtra("username");
        kategori.setText(kategoriMeja);
        nomor.setText(kodeMeja);
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
        orderAdapter = new OrderAdapter(this, userCode, listOrder);
        listView.setAdapter(orderAdapter);
        listView.setExpanded(true);
        listView.setEnabled(false);
        /*listView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                Toast.makeText(MyActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                Toast.makeText(MyActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    public ArrayList<Order> getListOrder(){
        return listOrder;
    }

    public OrderAdapter getOrderAdapter(){
        return orderAdapter;
    }

    public Order getOrder(String kode){
        for (int i = 0; i < listOrder.size(); i++){
            if (listOrder.get(i).getKode().equals(kode)){
                return listOrder.get(i);
            }
        }
        return null;
    }

    public void daftarMenu(){
        list = query.findDaftarMenu(kode);
        MenuAdapter adapter = new MenuAdapter(this, list);
        listMenu.setAdapter(adapter);
        listMenu.setExpanded(true);
        listMenu.setEnabled(false);
    }
}
