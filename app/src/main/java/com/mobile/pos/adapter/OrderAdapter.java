package com.mobile.pos.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.pos.OrderActivity;
import com.mobile.pos.R;
import com.mobile.pos.model.Order;
import com.mobile.pos.view.OnSwipeTouchListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Order> list;
    private String userCode;

    public OrderAdapter(Context context, String userCode, ArrayList<Order> list) {
        this.context = context;
        this.list = list;
        this.userCode = userCode;
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int location) { return list.get(location); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_order, null);
        final Order o = list.get(position);
        DecimalFormat format = new DecimalFormat();
        TextView teks = (TextView) convertView.findViewById(R.id.nama);
        teks.setText(o.getNama());
        TextView teksHarga = (TextView) convertView.findViewById(R.id.harga);
        teksHarga.setText(format.format(o.getHarga()));
        TextView teksQty = (TextView) convertView.findViewById(R.id.qty);
        teksQty.setText(String.valueOf(o.getQty()));
        TextView teksUser = (TextView) convertView.findViewById(R.id.user);
        teksUser.setText(userCode);
        final Button delete = (Button) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("HAPUS PESANAN");
                builder.setMessage("Apakah pesanan ini akan dihapus?");
                builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete.setVisibility(View.GONE);
                        list.remove(position);
                        notifyDataSetChanged();
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
        convertView.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeRight() {
                delete.setVisibility(View.GONE);
            }

            public void onSwipeLeft() {
                delete.setVisibility(View.VISIBLE);
            }
        });

        return convertView;
    }
}
