package com.mobile.pos.sql;

import android.util.Log;

import com.mobile.pos.model.Kategori;
import com.mobile.pos.model.Menu;
import com.mobile.pos.model.Opsi;
import com.mobile.pos.model.Order;
import com.mobile.pos.model.Spec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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

    public ArrayList<Menu> findDaftarMenu(String search, String kode) {
        ArrayList<Menu> list = new ArrayList<>();
        String kategori = "";
        if (!kode.equals("")) {
            kategori = " And Category_Code = ?";
        }
        try {
            query = "Select Stock_Code,Stock_Name,Stock_UOM,Stock_SellPrice1,WH_No,PrintCode From Allocation " +
                    "Where (Stock_Code LIKE ? OR Stock_Name LIKE ?)" + kategori + " Order By Stock_Name";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, search);
            stmt.setString(2, search);
            if (!kode.equals("")) {
                stmt.setString(3, kode);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Menu m = new Menu();
                m.setNama(rs.getString("Stock_Name"));
                m.setKode(rs.getString("Stock_Code"));
                m.setUom(rs.getString("Stock_UOM"));
                m.setWh(rs.getString("WH_No"));
                m.setPrintCode(rs.getString("PrintCode"));
                m.setHarga(rs.getFloat("Stock_SellPrice1"));
                list.add(m);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public Opsi findOpsi() {
        try {
            query = "Select * from Opsi Where Dep_Code = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, "POS");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Opsi o = new Opsi();
                o.setTaxCal(rs.getString("TaxCal"));
                o.setTax(rs.getFloat("TaxAmount"));
                o.setService(rs.getFloat("ServiceAmount"));
                return o;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public ArrayList<Order> findTaxService(String taxCal, float tax, float service, String kode) {
        ArrayList<Order> list = new ArrayList<>();
        if (taxCal.equals("Tax dan Service")) {
            service *= (1 + tax);
        }
        if (taxCal.equals("Service dan Tax")) {
            tax *= (1 + service);
        }
        try {
            query = "SELECT STOCK_CODE,STOCK_NAME,SELL_QTY,SELL_PRICE," +
                    "(CASE WHEN ISNULL((SELECT TaxType FROM Allocation Where Stock_Code = Sell_Detail.Stock_Code),1) = 1 THEN SELL_QTY * SELL_PRICE * ?" +
                    "ELSE 0 END) As SellTax,(CASE WHEN ISNULL((SELECT ServiceType FROM Allocation Where Stock_Code = Sell_Detail.Stock_Code),1) = 1 THEN SELL_QTY * SELL_PRICE * ?" +
                    "ELSE 0 END) As SellService" +
                    " FROM SELL_DETAIL WHERE STATUS = '' AND SPEC_CODE = ?";
            stmt = conn.prepareStatement(query);
            stmt.setFloat(1, tax);
            stmt.setFloat(2, service);
            stmt.setString(3, kode);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setNama(rs.getString("Stock_Name"));
                o.setKode(rs.getString("Stock_Code"));
                o.setQty(rs.getInt("Sell_Qty"));
                o.setHarga(rs.getFloat("Sell_Price"));
                o.setService(rs.getFloat("SellService"));
                o.setTax(rs.getFloat("SellTax"));
                list.add(o);
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

    public boolean findOpenSpec(String kode) {
        try {
            query = "Select * From OpenSpec Where Spec_Code = ? And Status = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            stmt.setString(2, "");
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
        }
        return false;
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

    public int insertOpenSpec(String kode, String username, String date){
        try {
            Calendar c = Calendar.getInstance();
            String hour = (c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "") + c.get(Calendar.HOUR_OF_DAY);
            String minute = (c.get(Calendar.MINUTE) < 10 ? "0" : "") + c.get(Calendar.MINUTE);
            query = "INSERT INTO OpenSpec(Spec_Code,Dep_Code,Open_Date,Open_Time,Spec_MinCharge,Guest_Name,Status,Recept_Name,Spec_ChargeType,Close_Time,LengthTime)" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            stmt.setString(2, "POS");
            stmt.setString(3, date);
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
            String day = (c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + c.get(Calendar.DAY_OF_MONTH);
            String month = ((c.get(Calendar.MONTH) + 1) < 10 ? "0" : "") + (c.get(Calendar.MONTH) + 1);
            String year = String.valueOf(c.get(Calendar.YEAR));
            String hour = (c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "") + c.get(Calendar.HOUR_OF_DAY);
            String minute = (c.get(Calendar.MINUTE) < 10 ? "0" : "") + c.get(Calendar.MINUTE);

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

    public int findStatus(String kode) {
        try {
            query = "Select top 1 SUBSTRING(Status,10,4) as angka From OpenSpec Where SUBSTRING(Status,1,9) = ? order by Status desc";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("angka") + 1 : 1;
        } catch (Exception e) {
        }
        return 0;
    }

    private int findEmptyStatus(String kode) {
        try {
            query = "Select count(*) as jumlah From Sell_Master Where Spec_Code = ? AND Status = ''";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("jumlah") : 0;
        } catch (Exception e) {
        }
        return 0;
    }

    private int findNomorMeja(String kode) {
        try {
            query = "Select count(*) as jumlah From Sell_Detail Where Spec_Code = ? AND Status = ''";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("jumlah") + 1 : 1;
        } catch (Exception e) {
        }
        return 0;
    }

    private int findQueryNumber(String tanggal) {
        try {
            query = "Select count(*) as jumlah From Sell_Master Where Sell_Date = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, tanggal);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("jumlah") : 0;
        } catch (Exception e) {
        }
        return 0;
    }

    public ResultSet cekStok(String tanggal, String kode) {
        try {
            query = "Select Food_Code,Food_Qty,ISNULL((Select Sum(Sell_Qty) As Total From Sell_Detail " +
                    "Where Stock_Code=FoodAdmin.Food_Code And Sell_Date=?),0) as Food_Sales from FoodAdmin Where Food_Date=? and Food_Code=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, tanggal);
            stmt.setString(2, tanggal);
            stmt.setString(3, kode);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs : null;
        } catch (Exception e) {
        }
        return null;
    }

    private String getFormatString(int n) {
        if (n < 10) return "00" + String.valueOf(n);
        else if (n > 9 && n < 100) return "0" + String.valueOf(n);
        return String.valueOf(n);
    }

    public void updateStatusSpec(String kode, String status){
        try {
            Calendar c = Calendar.getInstance();
            String hour = (c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "") + c.get(Calendar.HOUR_OF_DAY);
            String minute = (c.get(Calendar.MINUTE) < 10 ? "0" : "") + c.get(Calendar.MINUTE);
            query = "UPDATE OpenSpec SET Status = ?, Close_Time = ? WHERE Status = '' AND Spec_Code = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, hour + ":" + minute);
            stmt.setString(3, kode);
            stmt.executeUpdate();
        } catch (Exception e) {
        }
    }

    public void updateStatus(String kode, String status){
        try {
            query = "UPDATE Sell_Master SET Status = ? WHERE Status = '' AND Spec_Code = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, kode);
            stmt.executeUpdate();
        } catch (Exception e) {
        }
    }

    public void updateStatusDetail(String kode, String status){
        try {
            query = "UPDATE Sell_Detail SET Status = ? WHERE Status = '' AND Spec_Code = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setString(2, kode);
            stmt.executeUpdate();
        } catch (Exception e) {
        }
    }

    public void insertSellMaster(String kode, String kodeUser, String date){
        try {
            Calendar c = Calendar.getInstance();
            String month = ((c.get(Calendar.MONTH) + 1) < 10 ? "0" : "") + (c.get(Calendar.MONTH) + 1);
            String year = String.valueOf(c.get(Calendar.YEAR));
            String teks = kode + "-" + getFormatString(findEmptyStatus(kode) + 1);

            query = "INSERT INTO Sell_Master(Spec_Code,SpecO_Code,Dep_Code,Guest_Name,Sell_Date,Status,User_Code,Period,Guest_No)" +
                    "VALUES (?,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            stmt.setString(2, teks);
            stmt.setString(3, "POS");
            stmt.setString(4, "CASH");
            stmt.setString(5, date);
            stmt.setString(6, "");
            stmt.setString(7, kodeUser);
            stmt.setString(8, year + month);
            stmt.setString(9, "");
            stmt.executeUpdate();
        } catch (Exception e) {
        }
    }

    public void insertSellDetail(String kode, String kodeUser, String date, Order o){
        try {
            Calendar c = Calendar.getInstance();
            String day = (c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + c.get(Calendar.DAY_OF_MONTH);
            String month = ((c.get(Calendar.MONTH) + 1) < 10 ? "0" : "") + (c.get(Calendar.MONTH) + 1);
            String year = String.valueOf(c.get(Calendar.YEAR));
            String hour = (c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "") + c.get(Calendar.HOUR_OF_DAY);
            String minute = (c.get(Calendar.MINUTE) < 10 ? "0" : "") + c.get(Calendar.MINUTE);
            String teks = kode + "-" + getFormatString(findEmptyStatus(kode));
            String nomorMeja = String.valueOf(findNomorMeja(kode));
            int nomor = findQueryNumber(day + "-" + month + "-" + year);

            query = "INSERT INTO Sell_Detail(Spec_Code,SpecO_Code,Dep_Code,Status,Stock_Code,Stock_Name,Stock_Brand,Stock_UOM," +
                    "Sell_Qty,Sell_Price,WH_No,Sell_Disc,DescX,PrintCode,Sell_Tax,User_Code,Pax,Sell_Date,Sell_Time,BillS_Code," +
                    "User_Del,Del_Time,HR_Code,Guest_No,Queue_No,Reason) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, kode);
            stmt.setString(2, teks);
            stmt.setString(3, "POS");
            stmt.setString(4, "");
            stmt.setString(5, o.getKode());
            stmt.setString(6, o.getNama());
            stmt.setString(7, nomorMeja);
            stmt.setString(8, o.getUom());
            stmt.setFloat(9, o.getQty());
            stmt.setFloat(10, o.getHarga());
            stmt.setString(11, o.getWh());
            stmt.setFloat(12, 0);
            stmt.setString(13, o.getKeterangan() == null ? "" : o.getKeterangan());
            stmt.setString(14, o.getPrintCode());
            stmt.setFloat(15, 0);
            stmt.setString(16, kodeUser);
            stmt.setString(17, "1");
            stmt.setString(18, date);
            stmt.setString(19, hour + ":" + minute);
            stmt.setString(20, "");
            stmt.setString(21, "");
            stmt.setString(22, "");
            stmt.setString(23, "");
            stmt.setString(24, "");
            stmt.setInt(25, nomor);
            stmt.setString(26, "");
            stmt.executeUpdate();
        } catch (Exception e) {
        }
    }
}
