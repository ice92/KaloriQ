package com.lidapp.kaloriq;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Graph.BarChartActivityMultiDataset;
import com.lidapp.kaloriq.GulaDarah.GulaDarahAct;
import com.lidapp.kaloriq.Interface.ItemClickListener;
import com.lidapp.kaloriq.Model.Aktifitas;
import com.lidapp.kaloriq.Model.Category;
import com.lidapp.kaloriq.Model.Konsumsi;
import com.lidapp.kaloriq.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HariIniGD extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private String TAG = HariIniAct.class.getSimpleName();
    private String waktu[] ;

    FirebaseDatabase database;
    DatabaseReference category;
    private ProgressDialog pDialog;
    TextView txtFullName,txtcal;
    private ArrayList<Konsumsi> konsumsiArrayList;
    private ActivityHariIniAdapter adapter;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hariini);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gula darah Hari Ini");
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);


        addData();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtFullName=(TextView) headerView.findViewById(R.id.txtFullName);
        txtcal=(TextView) headerView.findViewById(R.id.txtcal);
        try {
            txtFullName.setText("Selamat Datang " + Common.currentUser.getName());
            txtcal.setText("Rekomendasi kalori harian anda :"+pref.getFloat("cal",2500)+" KCal");
        }
        catch (NullPointerException e){
        }

//            recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);

//            layoutManager=new LinearLayoutManager(this);
//            recycler_menu.setLayoutManager(layoutManager);
        Button fab = findViewById(R.id.fab2);
        fab.setText("Tambah Gula Darah");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DetailIntent=new Intent(HariIniGD.this, GulaDarahAct.class);
                HariIniGD.this.finish();
                startActivity(DetailIntent);
            }
        });


    }

    private void addData() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        konsumsiArrayList = new ArrayList<>();


        category=database.getReference("GulaDarah");
        category = category.child("" + Common.currentUser.getId() + "x" + df.format(currentTime) );//+"x03032019")


        category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{

                    for(DataSnapshot o : dataSnapshot.getChildren()){
                        System.out.println(o.toString());
                        String nama="";
                        String id="";
                        String jumlah="";

                                    nama=o.getValue().toString().split("x")[1]+" — "+o.getValue().toString().split("x")[0]+" mg/dL";
                                    jumlah="Waktu :"+o.getKey()+" Pukul :"+o.getValue().toString().split("x")[1];


                        Log.e(TAG, "The " + o.getKey() + " score is " + o.getValue());
                        konsumsiArrayList.add(new Konsumsi(id,nama,jumlah,"GulaDarah"));
                    }recycler_menu =findViewById(R.id.recycler_menu_hariini);
                    layoutManager = new LinearLayoutManager(HariIniGD.this);
                    adapter = new ActivityHariIniAdapter(konsumsiArrayList);
                    recycler_menu.setHasFixedSize(true);
                    recycler_menu.setAdapter(adapter);
                    recycler_menu.setLayoutManager(layoutManager);}
                catch (NullPointerException e){
                    Toast.makeText(HariIniGD.this,"Belum ada input",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            Intent homeIntent=new Intent(HariIniGD.this,Home.class);
            startActivity(homeIntent);
        } else if (id == R.id.nav_cart) {
            Intent DetailIntent=new Intent(HariIniGD.this,HariiniActivity.class);
            startActivity(DetailIntent);
        }  else if (id == R.id.nav_profil) {
            Intent DetailIntent=new Intent(HariIniGD.this,ProfilAct.class);
            startActivity(DetailIntent);
        }else if (id == R.id.nav_act) {
            Intent DetailIntent=new Intent(HariIniGD.this,HariIniAct.class);
            startActivity(DetailIntent);
        }else if (id == R.id.nav_gd) {
//            Intent DetailIntent=new Intent(HariIniGD.this,HariIniGD.class);
//            startActivity(DetailIntent);
        }else if (id == R.id.nav_orders) {
            Intent DetailIntent=new Intent(HariIniGD.this, BarChartActivityMultiDataset.class);
            startActivity(DetailIntent);}
        else if (id == R.id.nav_ramadhan) {
            if(pref.getBoolean("isfast",true)) {
                editor.putBoolean("isfast", false);
                editor.commit();
                item.setTitle("Mode Puasa (Non Aktif)");
                Toast.makeText(HariIniGD.this,"Mode Puasa dinonaktifkan",Toast.LENGTH_LONG).show();
            }
            else{
                editor.putBoolean("isfast", true);
                editor.commit();
                item.setTitle("Mode Puasa (Aktif)");
//                item.setIcon(R.drawable.ic_action_save);
                Toast.makeText(HariIniGD.this,"Mode Puasa diaktifkan",Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_logout) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(HariIniGD.this);

// Set up the buttons
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    Intent homeIntent=new Intent(HariIniGD.this,MainActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setTitle("Apakah anda yakin akan keluar?");
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
