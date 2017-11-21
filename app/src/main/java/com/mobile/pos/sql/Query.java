package com.mobile.pos.sql;

import android.util.Log;

import com.mobile.pos.model.Kategori;
import com.mobile.pos.model.Menu;
import com.mobile.pos.model.Spec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

public class Query {
    private ConnectionConfig connConfig;
    private Connection conn;
    private String query;
    private PreparedStatement stmt;

    public Query(){
        connConfig = new ConnectionConfig();
        conn = connConfig.CONN("FBMaster");
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (Exception e) {
        }
    }

    public boolean isCloseConnection() {
        try {
            return conn.isClosed();
        } catch (Exception e) {
            return true;
        }
    }

    public ArrayList<Kategori> findSpecCat() {
        ArrayList<Kategori> list = new ArrayList<>();
        try {
            query = "Select Spec_CatDesc,Spec_CatCode from SpecCat Order By Spec_CatDesc";
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Kategori k = new Kategori();
                k.setNama(rs.getString("Spec_CatDesc"));
                k.setKode(rs.getString("Spec_CatCode"));
                list.add(k);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public ArrayList<Spec> findSpec(String kode) {
        ArrayList<Spec> list = new ArrayList<>();
        try {
            query = "Select Spec_Code, Status, ISKtv From Spec Where Spec_CatCode = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Spec s = new Spec();
                s.setKtv(rs.getBoolean("ISKtv"));
                s.setKode(rs.getString("Spec_Code"));
                s.setStatus(rs.getString("Status"));
                list.add(s);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public ArrayList<Kategori> findKategori() {
        ArrayList<Kategori> list = new ArrayList<>();
        try {
            query = "Select Category_Code, Stock_Category From Allocation Group By Category_Code, Stock_Category Order By Stock_Category";
            stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Kategori k = new Kategori();
                k.setNama(rs.getString("Stock_Category"));
                k.setKode(rs.getString("Category_Code"));
                list.add(k);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public ArrayList<Menu> findDaftarMenu(String kode) {
        ArrayList<Menu> list = new ArrayList<>();
        try {
            query = "Select Stock_Code,Stock_Name,Stock_SellPrice1 From Allocation Where Category_Code = ? Order By Stock_Name";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Menu m = new Menu();
                m.setNama(rs.getString("Stock_Name"));
                m.setKode(rs.getString("Stock_Code"));
                m.setHarga(rs.getDouble("Stock_SellPrice1"));
                list.add(m);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public int findOrderLock(String kode) {
        try {
            query = "Select * From OrderLock Where Spec_Code = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? 0 : 1;
        } catch (Exception e) {
        }
        return -1;
    }

    public int updateSpec(String kode){
        try {
            query = "UPDATE Spec SET Status = ? WHERE Spec_Code = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, "O");
            stmt.setString(2, kode);
            return stmt.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }

    public int insertOpenSpec(String kode, String username){
        try {
            Calendar c = Calendar.getInstance();
            String day = c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "" + c.get(Calendar.DAY_OF_MONTH);
            String month = (c.get(Calendar.MONTH) + 1) < 10 ? "0" : "" + (c.get(Calendar.MONTH) + 1);
            String year = String.valueOf(c.get(Calendar.YEAR));
            String hour = c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "" + c.get(Calendar.HOUR_OF_DAY);
            String minute = c.get(Calendar.MINUTE) < 10 ? "0" : "" + c.get(Calendar.MINUTE);
            query = "INSERT INTO OpenSpec(Spec_Code,Dep_Code,Open_Date,Open_Time,Spec_MinCharge,Guest_Name,Status,Recept_Name,Spec_ChargeType,Close_Time,LengthTime)" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            stmt.setString(2, "POS");
            stmt.setString(3, day + "-" + month + "-" + year);
            stmt.setString(4, hour + ":" + minute);
            stmt.setInt(5, 0);
            stmt.setString(6, "CASH");
            stmt.setString(7, "");
            stmt.setString(8, username);
            stmt.setString(9, "NO CHARGES");
            stmt.setString(10, "");
            stmt.setFloat(11, 0);
            return stmt.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }

    public int insertOrderLock(String kode){
        try {
            query = "INSERT INTO OrderLock(Spec_Code) VALUES(?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            return stmt.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }

    public int insertLog(String kode, String depCode, String kodeUser, String namaUser, String desc){
        try {
            Calendar c = Calendar.getInstance();
            String day = c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "" + c.get(Calendar.DAY_OF_MONTH);
            String month = (c.get(Calendar.MONTH) + 1) < 10 ? "0" : "" + (c.get(Calendar.MONTH) + 1);
            String year = String.valueOf(c.get(Calendar.YEAR));
            String hour = c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "" + c.get(Calendar.HOUR_OF_DAY);
            String minute = c.get(Calendar.MINUTE) < 10 ? "0" : "" + c.get(Calendar.MINUTE);

            query = "INSERT INTO Logging2(Spec_Code,Dep_Code,Date,Time,User_Code,User_Name,Period,Description,Sell_Code)" +
                    "VALUES (?,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            stmt.setString(2, depCode);
            stmt.setString(3, day + "-" + month + "-" + year);
            stmt.setString(4, hour + ":" + minute);
            stmt.setString(5, kodeUser);
            stmt.setString(6, namaUser);
            stmt.setString(7, year + month);
            stmt.setString(8, desc);
            stmt.setString(9, "");
            return stmt.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }

    public int deleteOrderLock(String kode) {
        try {
            query = "DELETE FROM OrderLock WHERE Spec_Code = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            return stmt.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }
}
