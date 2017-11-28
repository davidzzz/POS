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
    Button confirm, cancel, order;
    Spinner kategori, nomor;
    ExpandableHeightListView listView, listMenu;
    ExpandableHeightGridView gridView;
    EditText search;
    TextView tanggal;
    ArrayList<Kategori> listSpecKat = new ArrayList<>();
    ArrayList<Kategori> listKategori = new ArrayList<>();
    ArrayList<Spec> listSpec = new ArrayList<>();
    ArrayList<Menu> list = new ArrayList<>();
    ArrayList<Order> listOrder = new ArrayList<>();
    ArrayAdapter<Kategori> adapter1;
    ArrayAdapter<Spec> adapter2;
    OrderAdapter orderAdapter;
    MenuAdapter adapter;
    Query query;
    Spec spec;
    String userCode, username, kategoriMeja, date;
    Timer timer = new Timer();
    ControlApplication app;

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
        tanggal = (TextView) findViewById(R.id.tanggal);
        listView = (ExpandableHeightListView) findViewById(R.id.listView);
        listMenu = (ExpandableHeightListView) findViewById(R.id.listMenu);
        gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
        userCode = getIntent().getStringExtra("userCode");
        username = getIntent().getStringExtra("username");
        date = getIntent().getStringExtra("date");
        tanggal.setText(date);
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
                query.deleteOrderLock(spec.getKode());
                listOrder.clear();
                orderAdapter.notifyDataSetChanged();
                search.setText("");
                list.clear();
                adapter.notifyDataSetChanged();
                activeState(true);
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
                    total += o.getHarga();
                    teks += o.getQty() + " " + o.getNama() + " @ Rp." + format.format(o.getHarga()) + "\n";
                }
                teks += "Total = Rp." + format.format(total);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("KONFIRMASI PESANAN");
                builder.setMessage("Apakah pesanan ini sudah benar?\n" + teks);
                builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        query.insertSellMaster(spec.getKode(), userCode);
                        for (int j = 0; j < listOrder.size(); j++) {
                            Order o = listOrder.get(j);
                            query.insertSellDetail(spec.getKode(), userCode, o);
                            query.insertLog(spec.getKode(), "POS", userCode, username, "APPS TAMBAH MENU " + o.getKode() + "QTY " + o.getQty());
                        }
                        query.deleteOrderLock(spec.getKode());
                        listOrder.clear();
                        orderAdapter.notifyDataSetChanged();
                        search.setText("");
                        list.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Order berhasil dilakukan", Toast.LENGTH_SHORT).show();
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
        orderAdapter = new OrderAdapter(this, userCode, listOrder);
        listView.setAdapter(orderAdapter);
        listView.setExpanded(true);
        listView.setEnabled(false);
        adapter = new MenuAdapter(this, list, orderAdapter);
        listMenu.setAdapter(adapter);
        listMenu.setExpanded(true);
        listMenu.setEnabled(false);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            ArrayList<Order> listArray = data.getParcelableArrayListExtra("orderArray");
            boolean reset = data.getBooleanExtra("reset", false);
            listOrder.clear();
            for (int i = 0; i < listArray.size(); i++) {
                listOrder.add(listArray.get(i));
            }
            orderAdapter.notifyDataSetChanged();
            if (reset) {
                activeState(true);
            }
        }
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
        gridView.setExpanded(true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Kategori k = (Kategori)adapterView.getItemAtPosition(position);
                Intent i = new Intent(MainActivity.this, OrderActivity.class);
                i.putExtra("kategoriMeja", kategoriMeja);
                i.putExtra("kodeMeja", spec.getKode());
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
                        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                        if (myKM.inKeyguardRestrictedInputMode() && confirm.isEnabled()) {
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
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
