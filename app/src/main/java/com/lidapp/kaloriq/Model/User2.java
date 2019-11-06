package com.lidapp.kaloriq.Model;

public class User2 {
    public String Berat,Phone,TL,Tinggi,id,name,password;
    public Double IMT;
    public int DB;
    public int SEX=1;
    public User2() {
    }

    public User2(String berat, Double IMT, String phone, String TL, String tinggi, String id, String name, String password,int DB, int SEX) {
        Berat = berat;
        this.IMT = IMT;
        Phone = phone;
        this.TL = TL;
        Tinggi = tinggi;
        this.id = id;
        this.name = name;
        this.password = password;
        this.DB=DB;
        this.SEX=SEX;
    }
}
