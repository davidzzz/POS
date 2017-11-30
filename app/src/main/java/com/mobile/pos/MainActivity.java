package com.mobile.pos;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.mobile.pos.util.ControlApplication;
import com.mobile.pos.view.ExpandableHeightGridView;
import com.mobile.pos.view.ExpandableHeightListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button cancel, order;
    ExpandableHeightListView listView, listMenu;
    ExpandableHeightGridView gridView;
    EditText search;
    TextView tanggal, kategori, nomor;
    ArrayList<Kategori> listKategori = new ArrayList<>();
    ArrayList<Menu> list = new ArrayList<>();
    ArrayList<Order> listOrder = new ArrayList<>();
    OrderAdapter orderAdapter;
    MenuAdapter adapter;
    Query query;
    String userCode, username, kategoriMeja, kodeMeja, date;
    Timer timer = new Timer();
    ControlApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        query = new Query();
        cancel = (Button) findViewById(R.id.cancel);
        order = (Button) findViewById(R.id.order);
        kategori = (TextView) findViewById(R.id.kategori);
        nomor = (TextView) findViewById(R.id.nomor);
        search = (EditText) findViewById(R.id.search);
        tanggal = (TextView) findViewById(R.id.tanggal);
        listView = (ExpandableHeightListView) findViewById(R.id.listView);
        listMenu = (ExpandableHeightListView) findViewById(R.id.listMenu);
        gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
        kategoriMeja = getIntent().getStringExtra("kategoriMeja");
        kodeMeja = getIntent().getStringExtra("kodeMeja");
        userCode = getIntent().getStringExtra("userCode");
        username = getIntent().getStringExtra("username");
        date = getIntent().getStringExtra("date");
        tanggal.setText(date);
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
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.deleteOrderLock(kodeMeja);
                listOrder.clear();
                orderAdapter.notifyDataSetChanged();
                search.setText("");
                list.clear();
                adapter.notifyDataSetChanged();
                //activeState(true);
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teks = "";
                DecimalFormat format = new DecimalFormat();
                float total = 0;
                for (int j = 0; j < listOrder.size(); j++) {
                    Order o = listOrder.get(j);
                    total += o.getQty() * o.getHarga();
                    teks += o.getQty() + " " + o.getNama() + " @ Rp." + format.format(o.getHarga()) + "\n";
                }
                teks += "Total = Rp." + format.format(total);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("KONFIRMASI PESANAN");
                builder.setMessage("Apakah pesanan ini sudah benar?\n" + teks);
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
                        search.setText("");
                        list.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Order berhasil dilakukan", Toast.LENGTH_SHORT).show();
                        //activeState(true);
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
        getKategori();
        orderAdapter = new OrderAdapter(this, userCode, listOrder);
        listView.setAdapter(orderAdapter);
        listView.setExpanded(true);
        listView.setEnabled(false);
        adapter = new MenuAdapter(this, list, date, orderAdapter, query);
        listMenu.setAdapter(adapter);
        listMenu.setExpanded(true);
        listMenu.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            ArrayList<Order> listArray = data.getParcelableArrayListExtra("orderArray");
            listOrder.clear();
            for (int i = 0; i < listArray.size(); i++) {
                listOrder.add(listArray.get(i));
            }
            orderAdapter.notifyDataSetChanged();
        }
    }

    public void getKategori() {
        listKategori = query.findKategori();
        KategoriAdapter adapter = new KategoriAdapter(this, listKategori);
        gridView.setAdapter(adapter);
        gridView.setExpanded(true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Kategori k = (Kategori)adapterView.getItemAtPosition(position);
                Intent i = new Intent(MainActivity.this, OrderActivity.class);
                i.putExtra("kategoriMeja", kategoriMeja);
                i.putExtra("kodeMeja", kodeMeja);
                i.putExtra("kode", k.getKode());
                i.putExtra("userCode", userCode);
                i.putExtra("username", username);
                i.putExtra("date", date);
                i.putExtra("listOrder", listOrder);
                query.closeConnection();
                startActivityForResult(i, 1);
            }
        });
    }

    public void daftarMenu(){
        String teks = search.getText().toString();
        list.clear();
        ArrayList<Menu> listArray = query.findDaftarMenu(teks.equals("") ? "" : "%" + teks + "%", "");
        for (int i = 0; i < listArray.size(); i++) {
            list.add(listArray.get(i));
        }
        new ReceiverThread().run();
    }

    private class ReceiverThread extends Thread {
        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (query.isCloseConnection()) {
            query = new Query();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
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
                        if (app.isStop()) {
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }, 0, 5000
        );
    }
}
