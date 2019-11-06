package com.lidapp.kaloriq.Model;

public class Bar {
    private float nilai;
    private int tgl;
    private int bulan;
    private int tahun;

    public Bar(float nilai, int tgl, int bulan, int tahun) {
        this.nilai = nilai;
        this.tgl = tgl;
        this.bulan = bulan;
        this.tahun = tahun;
    }

    public void setNilai(float nilai) {
        this.nilai = nilai;
    }

    public Bar(int tgl, int bulan, int tahun) {
        this.tgl = tgl;
        this.bulan = bulan;
        this.tahun = tahun;
    }

    public float getNilai() {
        return nilai;
    }

    public int getTgl() {
        return tgl;
    }

    public int getBulan() {
        return bulan;
    }

    public int getTahun() {
        return tahun;
    }
    public String getall(){
        int total=(tahun*365)+(bulan*30)+tgl;

        return ""+total;
    }
    public int getAllint(){
        int total=(tahun*365)+(bulan*30)+tgl;

        return total;
    }
}
