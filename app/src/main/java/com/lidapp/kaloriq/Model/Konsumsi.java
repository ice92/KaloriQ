package com.lidapp.kaloriq.Model;

public class Konsumsi {
    private String idmakanan;
    private String nama;
    private String jumlah;
    private String date;

    public Konsumsi(String idmakanan, String nama, String jumlah, String date) {
        this.idmakanan = idmakanan;
        this.nama = nama;
        this.jumlah = jumlah;
        this.date = date;
    }

    public Konsumsi(String idmakanan, String jumlah) {

        this.idmakanan = idmakanan;
        this.jumlah = jumlah;
    }

    public Konsumsi( String idmakanan, String jumlah, String date) {

        this.idmakanan = idmakanan;
        this.jumlah = jumlah;
        this.date = date;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Konsumsi() {
    }


    public String getIdmakanan() {
        return idmakanan;
    }

    public void setIdmakanan(String idmakanan) {
        this.idmakanan = idmakanan;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
