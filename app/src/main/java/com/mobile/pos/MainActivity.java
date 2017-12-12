package com.mobile.pos;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.pos.adapter.ConfirmAdapter;
import com.mobile.pos.adapter.KategoriAdapter;
import com.mobile.pos.adapter.MenuAdapter;
import com.mobile.pos.adapter.OrderAdapter;
import com.mobile.pos.model.Kategori;
import com.mobile.pos.model.Menu;
import com.mobile.pos.model.Order;
import com.mobile.pos.sql.Query;
import com.mobile.pos.util.ControlApplication;
import com.mobile.pos.view.ExpandableHeightGridView;
import com.mobile.pos.view.ExpandableHeightListView;
import com.mobile.pos.view.OnSwipeTouchListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button cancel, order, kembali;
    ListView listView;
    ExpandableHeightListView listMenu;
    ExpandableHeightGridView gridView;
    EditText search;
    TextView kategori, nomor;
    ArrayList<Kategori> listKategori = new ArrayList<>();
    ArrayList<Menu> list = new ArrayList<>();
    ArrayList<Order> listOrder = new ArrayList<>();
    OrderAdapter orderAdapter;
    MenuAdapter adapter;
    Query query;
    String userCode, username, kategoriMeja, kodeMeja, date;
    Timer timer = new Timer();
    ControlApplication app;
    int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        query = new Query();
        kembali = (Button) findViewById(R.id.kembali);
        cancel = (Button) findViewById(R.id.cancel);
        order = (Button) findViewById(R.id.order);
        kategori = (TextView) findViewById(R.id.kategori);
        nomor = (TextView) findViewById(R.id.nomor);
        search = (EditText) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.listView);
        listMenu = (ExpandableHeightListView) findViewById(R.id.listMenu);
        gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
        kategoriMeja = getIntent().getStringExtra("kategoriMeja");
        kodeMeja = getIntent().getStringExtra("kodeMeja");
        userCode = getIntent().getStringExtra("userCode");
        username = getIntent().getStringExtra("username");
        date = getIntent().getStringExtra("date");
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
                search.setText("");
                gridView.setVisibility(View.VISIBLE);
                kembali.setVisibility(View.GONE);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.deleteOrderLock(kodeMeja);
                Intent i = new Intent(MainActivity.this, MejaActivity.class);
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
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setTitle("KONFIRMASI ORDERAN");
                dialog.setContentView(R.layout.confirm_order);
                ListView listConfirm = (ListView) dialog.findViewById(R.id.listView);
                TextView teksTotal = (TextView) dialog.findViewById(R.id.total);
                TextView teksTotalItem = (TextView) dialog.findViewById(R.id.totalitem);
                ConfirmAdapter confirmAdapter = new ConfirmAdapter(MainActivity.this, listOrder);
                listConfirm.setAdapter(confirmAdapter);
                teksTotal.setText(format.format(total));
                teksTotalItem.setText(listOrder.size() + "");
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                Button ok = (Button) dialog.findViewById(R.id.ok);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        search.setText("");
                        gridView.setVisibility(View.VISIBLE);
                        kembali.setVisibility(View.GONE);
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
                        Intent intent = new Intent(MainActivity.this, MejaActivity.class);
                        intent.putExtra("userCode", userCode);
                        intent.putExtra("username", username);
                        intent.putExtra("date", date);
                        startActivity(intent);
                        finish();
                        Toast.makeText(MainActivity.this, "Order berhasil dilakukan", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });
        getKategori();
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
                view.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
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
                    if (search.getText().toString().length() > 0) {
                        gridView.setVisibility(View.GONE);
                        kembali.setVisibility(View.VISIBLE);
                    } else {
                        gridView.setVisibility(View.VISIBLE);
                        kembali.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //if (query.isCloseConnection()) {
        //    query = new Query();
        //}
    }

    @Override
    protected void onDestroy() {
        //super.onDestroy();
       // timer.cancel();
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
       // final Timer timer2 = new Timer();
        //timer2.scheduleAtFixedRate(
               // new TimerTask() {
                    //@Override
                   // public void run() {
                       // if (app.isStop()) {
                         //   timer2.cancel();
                         //   Intent i = new Intent(MainActivity.this, LoginActivity.class);
                         //   startActivity(i);
                         //   finish();
                     //   }
             //       }
              //  }, 0, 5000
       // );
    }
}
