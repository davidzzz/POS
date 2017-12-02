package com.mobile.pos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.pos.adapter.MenuAdapter;
import com.mobile.pos.adapter.OrderAdapter;
import com.mobile.pos.model.Menu;
import com.mobile.pos.model.Order;
import com.mobile.pos.sql.Query;
import com.mobile.pos.util.ControlApplication;
import com.mobile.pos.view.ExpandableHeightListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class OrderActivity extends AppCompatActivity {
    Button kembali, cancel, order;
    TextView kategori, nomor;
    ListView listView;
    ExpandableHeightListView listMenu;
    ArrayList<Menu> list = new ArrayList<>();
    ArrayList<Order> listOrder = new ArrayList<>();
    Query query;
    String username, kode, kodeMeja, kategoriMeja, userCode, date;
    OrderAdapter orderAdapter;
    MenuAdapter adapter;
    ControlApplication app;

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
        listView = (ListView) findViewById(R.id.listView);
        listMenu = (ExpandableHeightListView) findViewById(R.id.listMenu);
        kategoriMeja = getIntent().getStringExtra("kategoriMeja");
        kodeMeja = getIntent().getStringExtra("kodeMeja");
        kode = getIntent().getStringExtra("kode");
        userCode = getIntent().getStringExtra("userCode");
        username = getIntent().getStringExtra("username");
        listOrder = getIntent().getParcelableArrayListExtra("listOrder");
        date = getIntent().getStringExtra("date");
        kategori.setText(kategoriMeja);
        nomor.setText(kodeMeja);
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
                String teks = "";
                DecimalFormat format = new DecimalFormat();
                float total = 0;
                for (int j = 0; j < listOrder.size(); j++) {
                    Order o = listOrder.get(j);
                    total += o.getQty() * o.getHarga();
                    teks += o.getQty() + " " + o.getNama() + " @ Rp." + format.format(o.getHarga()) + "\n";
                }
                teks += "Total = Rp." + format.format(total);
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                builder.setTitle("KONFIRMASI PESANAN");
                builder.setMessage("Apakah pesanan sudah benar?\n" + teks);
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
        orderAdapter = new OrderAdapter(this, listOrder, order);
        listView.setAdapter(orderAdapter);
        listView.setEnabled(false);
        adapter = new MenuAdapter(this, list, date, orderAdapter, query, order);
        listMenu.setAdapter(adapter);
        listMenu.setExpanded(true);
        listMenu.setEnabled(false);
        daftarMenu();
    }

    public void daftarMenu(){
        list.clear();
        ArrayList<Menu> listArray = query.findDaftarMenu("%", kode);
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
                            Intent i = new Intent(OrderActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }, 0, 5000
        );
    }
}
