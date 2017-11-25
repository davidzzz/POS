package com.mobile.pos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.Timer;
import java.util.TimerTask;

public class OrderActivity extends AppCompatActivity {
    Button kembali, cancel, order;
    TextView kategori, nomor;
    ExpandableHeightListView listView, listMenu;
    EditText search;
    ArrayList<Menu> list = new ArrayList<>();
    ArrayList<Order> listOrder = new ArrayList<>();
    Query query;
    String username, kode, kodeMeja, kategoriMeja, userCode;
    OrderAdapter orderAdapter;
    MenuAdapter adapter;
    Timer timer = new Timer();

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
        listOrder = getIntent().getParcelableArrayListExtra("listOrder");
        kategori.setText(kategoriMeja);
        nomor.setText(kodeMeja);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                daftarMenu();
                            }
                        },
                        500
                );
            }
        });
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.closeConnection();
                setResult(1, getIntent().putParcelableArrayListExtra("orderArray", listOrder));
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.deleteOrderLock(kodeMeja);
                listOrder.clear();
                orderAdapter.notifyDataSetChanged();
                setResult(1, getIntent().putParcelableArrayListExtra("orderArray", listOrder));
                setResult(1, getIntent().putExtra("reset", true));
                finish();
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
                        query.insertSellMaster(kodeMeja, userCode);
                        for (int j = 0; j < listOrder.size(); j++) {
                            Order o = listOrder.get(j);
                            query.insertSellDetail(kodeMeja, userCode, o);
                            query.insertLog(kodeMeja, "POS", userCode, username, "APPS TAMBAH MENU " + o.getKode() + "QTY " + o.getQty());
                        }
                        query.deleteOrderLock(kodeMeja);
                        listOrder.clear();
                        orderAdapter.notifyDataSetChanged();
                        Toast.makeText(OrderActivity.this, "Order berhasil dilakukan", Toast.LENGTH_SHORT).show();
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
        orderAdapter = new OrderAdapter(this, userCode, listOrder);
        listView.setAdapter(orderAdapter);
        listView.setExpanded(true);
        listView.setEnabled(false);
        adapter = new MenuAdapter(this, list, orderAdapter);
        listMenu.setAdapter(adapter);
        listMenu.setExpanded(true);
        listMenu.setEnabled(false);
        daftarMenu();
    }

    public void daftarMenu(){
        String teks = search.getText().toString();
        list.clear();
        ArrayList<Menu> listArray = query.findDaftarMenu("%" + teks + "%", kode);
        for (int i = 0; i < listArray.size(); i++) {
            list.add(listArray.get(i));
        }
        new ReceiverThread().run();
    }

    private class ReceiverThread extends Thread {
        @Override
        public void run() {
            OrderActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
