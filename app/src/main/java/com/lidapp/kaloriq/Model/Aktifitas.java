package com.lidapp.kaloriq.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Aktifitas implements Parcelable {
    private String id;
    private String nama;
    private String durasi;
    private String kalori;

    public Aktifitas(String id, String nama, String durasi, String kalori) {
        this.id = id;
        this.nama = nama;
        this.durasi = durasi;
        this.kalori = kalori;
    }

    protected Aktifitas(Parcel in) {
        id = in.readString();
        nama = in.readString();
        durasi = in.readString();
        kalori = in.readString();
    }

    public static final Creator<Aktifitas> CREATOR = new Creator<Aktifitas>() {
        @Override
        public Aktifitas createFromParcel(Parcel in) {
            return new Aktifitas(in);
        }

        @Override
        public Aktifitas[] newArray(int size) {
            return new Aktifitas[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public String getKalori() {
        return kalori;
    }

    public void setKalori(String kalori) {
        this.kalori = kalori;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(nama);
        parcel.writeString(durasi);
        parcel.writeString(kalori);
    }
}
