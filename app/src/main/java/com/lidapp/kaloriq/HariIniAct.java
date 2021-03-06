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
import com.lidapp.kaloriq.Graph.CombinedChartActivity;
import com.lidapp.kaloriq.Graph.CombinedChartActivityGD;
import com.lidapp.kaloriq.Interface.ItemClickListener;
import com.lidapp.kaloriq.Model.Aktifitas;
import com.lidapp.kaloriq.Model.Category;
import com.lidapp.kaloriq.Model.Konsumsi;
import com.lidapp.kaloriq.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HariIniAct extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
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
    List<Aktifitas> makanans;
    private static String url = "https://www.caloriecalcmva.com/ApiAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hariini);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Aktifitas Hari Ini");
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);


        new GetContacts().execute();

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
        fab.setText("Tambah Aktivitas");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DetailIntent=new Intent(HariIniAct.this,ActDetail.class);
                HariIniAct.this.finish();
                startActivity(DetailIntent);
            }
        });

    }
    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HariIniAct.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e("RESPONSE", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    // Getting JSON Array node

                    // looping through All Contacts
                    ArrayList<Aktifitas> mockContacts = new ArrayList<>();
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String nama = c.getString("nama");
                        String kalori = c.getString("METS");
                        String durasi = c.getString("detail");
                        mockContacts.add(new Aktifitas(
                                id,
                                nama,
                                durasi,
                                kalori + " METS/Jam"
                        ));
                    }
                    makanans = mockContacts;
                    for (Aktifitas makanan : makanans) {
                        Log.e("Aktifitas", makanan.getKalori() + ":" + makanan.getId());
                    }
                } catch (final JSONException e) {
                    Log.e("RESPONSE", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("RESPONSE", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Tidak dapat mengambil data dari server. periksa koneksi internet anda lalu coba kembali!!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            addData();
            /**
             * Updating parsed JSON data into ListView
             * */
//            ListAdapter adapter = new SimpleAdapter(
//                    HomeDetail.this, contactList,
//                    R.layout.list_item, new String[]{"http://www.erabaru.net/wp-content/uploads/2018/02/Jangan-Lagi-Memberi-Anak-anak-9-Jenis-Makanan-Ini-700x366.jpg","name", "email",
//                    "mobile"}, new int[]{R.id.image,R.id.name,
//                    R.id.email, R.id.mobile});
//            adapter=new AktifitasAdapter(ActDetail.this,makanans);
//            lv.setAdapter(adapter);
//            lv.setLayoutManager(new LinearLayoutManager(ActDetail.this));
        }
    }
    private void addData() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        konsumsiArrayList = new ArrayList<>();


            category=database.getReference("Activity");
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
//                            boolean found=false;
//                            for (int i = 0; i < Common.id.length ; i++) {
//                                if(o.getKey().equals(Common.id[i])){
//                                    nama="Makanan :"+Common.makanan[i];
//                                    jumlah="Jumlah :"+o.getValue()+" Porsi";
//                                    id=Common.id[i];
//                                    found=true;
//                                }
//                            }
//                            if(!found){
                                for (Aktifitas aktifitas:makanans) {
                                    Log.e("action", "The " +aktifitas.getId()+":"+o.getKey());
                                    if(o.getKey().equals(aktifitas.getId())){
                                        nama=""+o.getValue()+" — "+aktifitas.getNama();
                                        jumlah="Durasi :"+o.getValue()+" Menit";
                                        id=aktifitas.getId();
//                                        found=true;
                                    }
                                }
//                            }
                            Log.e(TAG, "The " + o.getKey() + " score is " + o.getValue());
                            konsumsiArrayList.add(new Konsumsi(id,nama,jumlah,"Activity"));
                        }recycler_menu =findViewById(R.id.recycler_menu_hariini);
                        layoutManager = new LinearLayoutManager(HariIniAct.this);
                        adapter = new ActivityHariIniAdapter(konsumsiArrayList);
                        recycler_menu.setHasFixedSize(true);
                        recycler_menu.setAdapter(adapter);
                        recycler_menu.setLayoutManager(layoutManager);}
                    catch (NullPointerException e){
                        Toast.makeText(HariIniAct.this,"Belum ada input",Toast.LENGTH_SHORT).show();
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
    SharedPreferences pref;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            Intent homeIntent=new Intent(HariIniAct.this,Home.class);
            startActivity(homeIntent);
        } else if (id == R.id.nav_cart) {
            Intent DetailIntent=new Intent(HariIniAct.this,HariiniActivity.class);
            startActivity(DetailIntent);
//            this.finish();
        }  else if (id == R.id.nav_profil) {
            Intent DetailIntent=new Intent(HariIniAct.this,ProfilAct.class);
            startActivity(DetailIntent);
//            this.finish();
        }else if (id == R.id.nav_act) {
//            Intent DetailIntent=new Intent(HariiniActivity.this,HariIniAct.class);
//            startActivity(DetailIntent);
//            this.finish();
        }else if (id == R.id.nav_gd) {
            Intent DetailIntent=new Intent(HariIniAct.this,HariIniGD.class);
            startActivity(DetailIntent);
//            this.finish();
        }else if (id == R.id.nav_orders) {
            Intent DetailIntent=new Intent(HariIniAct.this, CombinedChartActivity.class);
            startActivity(DetailIntent);
//            this.finish();
        }else if (id == R.id.nav_orders2) {
            Intent DetailIntent=new Intent(HariIniAct.this, CombinedChartActivityGD.class);
            startActivity(DetailIntent);
//            this.finish();
        }
        else if (id == R.id.nav_ramadhan) {
            if(pref.getBoolean("isfast",true)) {
                editor.putBoolean("isfast", false);
                editor.commit();
                item.setTitle("Mode Puasa (Non Aktif)");
                Toast.makeText(HariIniAct.this,"Mode Puasa dinonaktifkan",Toast.LENGTH_LONG).show();

            }
            else{
                editor.putBoolean("isfast", true);
                editor.commit();
                item.setTitle("Mode Puasa (Aktif)");
//                item.setIcon(R.drawable.ic_action_save);
                Toast.makeText(HariIniAct.this,"Mode Puasa diaktifkan",Toast.LENGTH_LONG).show();
//                this.finish();
            }
        } else if (id == R.id.nav_logout) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(HariIniAct.this);

// Set up the buttons
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    Intent homeIntent=new Intent(HariIniAct.this,MainActivity.class);
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
