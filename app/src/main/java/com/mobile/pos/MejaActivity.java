package com.mobile.pos;

import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.dubu.lockscreenusingservice.Lockscreen;
import com.mobile.pos.adapter.ConfirmAdapter;
import com.mobile.pos.model.Kategori;
import com.mobile.pos.model.Opsi;
import com.mobile.pos.model.Order;
import com.mobile.pos.model.Spec;
import com.mobile.pos.sql.Query;
import com.mobile.pos.util.ControlApplication;
import com.mobile.pos.util.HomeKeyLocker;
import com.mobile.pos.util.HomeWatcher;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MejaActivity extends AppCompatActivity {
    Query query;
    Button confirm, billing, logout;
    Spinner kategori, nomor;
    TextView footer;
    Spec spec;
    String userCode, username, date, kategoriMeja;
    ArrayList<Kategori> listSpecKat = new ArrayList<>();
    ArrayList<Spec> listSpec = new ArrayList<>();
    ArrayAdapter<Kategori> adapter1;
    ArrayAdapter<Spec> adapter2;
    CountDownTimer newtimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meja);
        query = new Query();
        //Lockscreen.getInstance(this).startLockscreenService();
        //HomeKeyLocker hkl = new HomeKeyLocker();
        //hkl.lock(this);
        /*HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                onBackPressed();
            }
            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();*/
        confirm = (Button) findViewById(R.id.confirm);
        billing = (Button) findViewById(R.id.billing);
        logout = (Button) findViewById(R.id.logout);
        kategori = (Spinner) findViewById(R.id.kategori);
        nomor = (Spinner) findViewById(R.id.nomor);
        footer = (TextView) findViewById(R.id.footer);
        userCode = getIntent().getStringExtra("userCode");
        username = getIntent().getStringExtra("username");
        date = getIntent().getStringExtra("date");
        String path = "http://" + Constant.ip + "/FBClub/Help/Pictures/Banner.jpg";
        ImageView banner = (ImageView) findViewById(R.id.banner);
        Glide.with(this)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(banner);
        newtimer = new CountDownTimer(1000000000, 1000) {
            public void onTick(long millisUntilFinished) {
                Calendar c = Calendar.getInstance();
                String hour = (c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "") + c.get(Calendar.HOUR_OF_DAY);
                String minute = (c.get(Calendar.MINUTE) < 10 ? "0" : "") + c.get(Calendar.MINUTE);
                footer.setText(username + ", " + date + " " + hour + ":" + minute);
            }
            public void onFinish() {

            }
        };
        newtimer.start();
        footer.setText(username + ", " + date + " ");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spec = (Spec) nomor.getSelectedItem();
                if (spec != null) {
                    if (spec.isKtv()) {
                        if (spec.getStatus().equals("V")) {
                            SweetAlertDialog alert = new SweetAlertDialog(MejaActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("PESAN KESALAHAN")
                                    .setContentText("KTV wajib dibuka melalui POS");
                            alert.setCancelable(false);
                            alert.show();
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
            }
        });
        billing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spec = (Spec) nomor.getSelectedItem();
                Opsi opsi = query.findOpsi();
                if (opsi != null) {
                    float nilaiTax = opsi.getTax() / 100;
                    float nilaiService = opsi.getService() / 100;
                    final ArrayList<Order> listOrder = query.findTaxService(opsi.getTaxCal(), nilaiTax, nilaiService, spec.getKode());
                    DecimalFormat format = new DecimalFormat();
                    float total = 0, totalTax = 0, totalService = 0;
                    if (listOrder.size() > 0 && !spec.getStatus().equals("V") && !spec.getStatus().equals("X")) {
                        for (int j = 0; j < listOrder.size(); j++) {
                            Order o = listOrder.get(j);
                            total += o.getQty() * o.getHarga();
                            totalTax += o.getTax();
                            totalService += o.getService();
                        }
                        final Dialog dialog = new Dialog(MejaActivity.this);
                        dialog.setTitle("KONFIRMASI BILLING");
                        dialog.setContentView(R.layout.confirm_order);
                        ListView listConfirm = (ListView) dialog.findViewById(R.id.listView);
                        TextView teks1 = (TextView) dialog.findViewById(R.id.teksService);
                        TextView teks2 = (TextView) dialog.findViewById(R.id.teksTax);
                        TextView teks3 = (TextView) dialog.findViewById(R.id.teksGrand);
                        teks1.setVisibility(View.VISIBLE);
                        teks2.setVisibility(View.VISIBLE);
                        teks3.setVisibility(View.VISIBLE);
                        TextView teksTotal = (TextView) dialog.findViewById(R.id.total);
                        TextView teksService = (TextView) dialog.findViewById(R.id.service);
                        TextView teksTax = (TextView) dialog.findViewById(R.id.tax);
                        TextView teksGrandTotal = (TextView) dialog.findViewById(R.id.grandtotal);
                        TextView teksTotalItem = (TextView) dialog.findViewById(R.id.totalitem);
                        teksService.setVisibility(View.VISIBLE);
                        teksTax.setVisibility(View.VISIBLE);
                        teksGrandTotal.setVisibility(View.VISIBLE);
                        ConfirmAdapter confirmAdapter = new ConfirmAdapter(MejaActivity.this, listOrder);
                        listConfirm.setAdapter(confirmAdapter);
                        teksTotal.setText(format.format(total));
                        teksService.setText(format.format(totalService));
                        teksTax.setText(format.format(totalTax));
                        teksGrandTotal.setText(format.format(total + totalService + totalTax));
                        teksTotalItem.setText(listOrder.size() + "");
                        Button cancel = (Button) dialog.findViewById(R.id.cancel);
                        Button ok = (Button) dialog.findViewById(R.id.ok);
                        cancel.setVisibility(View.GONE);
                        /*cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //query.updateStatusSpec(spec.getKode(), "BATAL");
                                //query.updateStatus(spec.getKode(), "HAPUS");
                                //query.updateStatusDetail(spec.getKode(), "HAPUS");
                                Toast.makeText(MejaActivity.this, "Billing telah dibatalkan", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });*/
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                /*Calendar c = Calendar.getInstance();
                                String month = ((c.get(Calendar.MONTH) + 1) < 10 ? "0" : "") + (c.get(Calendar.MONTH) + 1);
                                String year = String.valueOf(c.get(Calendar.YEAR)).substring(2);
                                int n = query.findStatus("POS-" + year + month + "-");
                                String angka = "";
                                if (n < 10) angka = angka + "0";
                                if (n < 100) angka = angka + "0";
                                if (n < 1000) angka = angka + "0";
                                angka = angka + String.valueOf(n);
                                query.updateStatusSpec(spec.getKode(), "POS-" + year + month + "-" + angka);
                                query.updateStatus(spec.getKode(), "POS-" + year + month + "-" + angka);
                                query.updateStatusDetail(spec.getKode(), "POS-" + year + month + "-" + angka);*/
                                //Toast.makeText(MejaActivity.this, "Billing berhasil dilakukan", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else {
                        SweetAlertDialog alert = new SweetAlertDialog(MejaActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("PESAN KESALAHAN")
                                .setContentText("Billing tidak tersedia");
                        alert.setCancelable(false);
                        alert.show();
                    }
                } else {
                    SweetAlertDialog alert = new SweetAlertDialog(MejaActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("PESAN KESALAHAN")
                            .setContentText("Opsi tidak tersedia");
                    alert.setCancelable(false);
                    alert.show();
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MejaActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        getKategoriMeja();
    }

    public void orderLock() {
        int status = query.findOrderLock(spec.getKode());
        boolean isOpenSpec = query.findOpenSpec(spec.getKode());
        if (status == 1) {
            if (spec.isKtv() && !isOpenSpec) {
                SweetAlertDialog alert = new SweetAlertDialog(MejaActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("PESAN KESALAHAN")
                        .setContentText("Konfirmasi unit ditolak");
                alert.setCancelable(false);
                alert.show();
            } else {
                int a = 1;
                if (spec.isKtv() || !isOpenSpec) {
                    a = query.insertOpenSpec(spec.getKode(), username, date);
                }
                int b = query.insertOrderLock(spec.getKode());
                int c = query.insertLog(spec.getKode(), "DEP", userCode, username, "BUKA MEJA " + spec.getKode());
                if (a > 0 && b > 0 && c > 0) {
                    Intent i = new Intent(MejaActivity.this, MainActivity.class);
                    i.putExtra("kategoriMeja", kategoriMeja);
                    i.putExtra("kodeMeja", spec.getKode());
                    i.putExtra("userCode", userCode);
                    i.putExtra("username", username);
                    i.putExtra("date", date);
                    query.closeConnection();
                    startActivity(i);
                } else {
                    SweetAlertDialog alert = new SweetAlertDialog(MejaActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("PESAN KESALAHAN")
                            .setContentText("Terjadi kesalahan saat melakukan konfirmasi");
                    alert.setCancelable(false);
                    alert.show();
                }
            }
        } else if (status == 0) {
            SweetAlertDialog alert = new SweetAlertDialog(MejaActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("PESAN KESALAHAN")
                    .setContentText("Unit sedang diorder oleh user lain");
            alert.setCancelable(false);
            alert.show();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (query.isCloseConnection()) {
            query = new Query();
        }
    }

    @Override
    public void onBackPressed() {
    }

    /*@Override
    protected void onUserLeaveHint() {

    }

    @Override
    public void onAttachedToWindow()
    {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }*/
}
