package com.mobile.pos;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.pos.adapter.ConfirmAdapter;
import com.mobile.pos.adapter.MenuAdapter;
import com.mobile.pos.adapter.OrderAdapter;
import com.mobile.pos.model.Menu;
import com.mobile.pos.model.Order;
import com.mobile.pos.sql.Query;
import com.mobile.pos.util.ControlApplication;
import com.mobile.pos.view.ExpandableHeightListView;
import com.mobile.pos.view.OnSwipeTouchListener;

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
    int pos = -1;

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
                Intent i = new Intent(OrderActivity.this, MejaActivity.class);
                i.putExtra("userCode", userCode);
                i.putExtra("username", username);
                i.putExtra("date", date);
                startActivity(i);
                finish();
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DecimalFormat format = new DecimalFormat();
                float total = 0;
                for (int j = 0; j < listOrder.size(); j++) {
                    Order o = listOrder.get(j);
                    total += o.getQty() * o.getHarga();
                }
                final Dialog dialog = new Dialog(OrderActivity.this);
                dialog.setTitle("KONFIRMASI ORDERAN");
                dialog.setContentView(R.layout.confirm_order);
                ListView listConfirm = (ListView) dialog.findViewById(R.id.listView);
                TextView teksTotal = (TextView) dialog.findViewById(R.id.total);
                TextView teksTotalItem = (TextView) dialog.findViewById(R.id.totalitem);
                ConfirmAdapter confirmAdapter = new ConfirmAdapter(OrderActivity.this, listOrder);
                listConfirm.setAdapter(confirmAdapter);
                teksTotal.setText(format.format(total));
                teksTotalItem.setText(listOrder.size() + "");
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                Button ok = (Button) dialog.findViewById(R.id.ok);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        query.closeConnection();
                        setResult(1, getIntent().putParcelableArrayListExtra("orderArray", listOrder));
                        finish();
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        query.insertSellMaster(kodeMeja, userCode, date);
                        for (int j = 0; j < listOrder.size(); j++) {
                            Order o = listOrder.get(j);
                            query.insertSellDetail(kodeMeja, userCode, date, o);
                            query.insertLog(kodeMeja, "POS", userCode, username, "APPS TAMBAH MENU " + o.getKode() + "QTY " + o.getQty());
                        }
                        query.deleteOrderLock(kodeMeja);
                        Intent intent = new Intent(OrderActivity.this, MejaActivity.class);
                        intent.putExtra("userCode", userCode);
                        intent.putExtra("username", username);
                        intent.putExtra("date", date);
                        startActivity(intent);
                        finish();
                        Toast.makeText(OrderActivity.this, "Order berhasil dilakukan", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });
        orderAdapter = new OrderAdapter(this, listOrder, order);
        listView.setAdapter(orderAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Button delete = (Button) view.findViewById(R.id.delete);
                for (int j = 0; j < listOrder.size(); j++) {
                    View v = orderAdapter.getView(j, null, null);
                    Button del = (Button) v.findViewById(R.id.delete);
                    del.setVisibility(View.GONE);
                }
                pos = -1;
                view.setOnTouchListener(new OnSwipeTouchListener(OrderActivity.this) {
                    public void onSwipeLeft() {
                        if (pos != -1) {
                            for (int i = 0; i < listOrder.size(); i++) {
                                View v = orderAdapter.getView(i, null, null);
                                Button delete = (Button) v.findViewById(R.id.delete);
                                delete.setVisibility(View.GONE);
                            }
                        }
                        pos = i;
                        delete.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
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
        final Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (app.isStop()) {
                            timer2.cancel();
                            Intent i = new Intent(OrderActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }, 0, 5000
        );
    }
}
