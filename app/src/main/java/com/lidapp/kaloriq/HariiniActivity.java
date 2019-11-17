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
import com.lidapp.kaloriq.Model.Contact;
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

public class HariiniActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private String TAG = HariiniActivity.class.getSimpleName();
        private String waktu[] ;
        String waktu1[]={"Makan Pagi","Makan Siang","Makan Malam","Camilan Pagi","Camilan Siang","Camilan Malam","Lain - Lain"};
        String waktu2[]={"Sahur","Takjil","Berbuka"};
        FirebaseDatabase database;
        DatabaseReference category;
        private ProgressDialog pDialog;
        TextView txtFullName,txtcal;
        private ArrayList<Konsumsi> konsumsiArrayList;
        private MakananHariIniAdapter adapter;
        RecyclerView recycler_menu;
        RecyclerView.LayoutManager layoutManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_hariini);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle("Konsumsi Hari Ini");
                setSupportActionBar(toolbar);

                database = FirebaseDatabase.getInstance();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

                if (pref.getBoolean("isfast", true)) {
                    waktu = waktu2;
                } else {
                    waktu = waktu1;
                }
            new GetContacts2().execute();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
////        fab.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
////            }
////        });

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
            fab.setText("Tambah Kalori");
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent DetailIntent =new Intent(HariiniActivity.this,HomeTime.class);
                    HariiniActivity.this.finish();
                    startActivity(DetailIntent);
                }
            });

        }
    List<Contact> makanans2;
    private static String url2 = "https://www.caloriecalcmva.com/api";
    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HariiniActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url2);

            Log.e("RESPONSE2", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    // Getting JSON Array node

                    // looping through All Contacts
                    ArrayList<Contact> mockContacts = new ArrayList<>();
                    String namas[]=new String[contacts.length()];
                    String ids[]=new String[contacts.length()];
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("idmakanan");
                        String name = c.getString("nama");
                        String address = c.getString("takaran");
                        String gender = c.getString("kaloripertakaran");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        namas[i]=name;
                        ids[i]=id;
                        contact.put("name", name);
                        contact.put("email", address);
                        contact.put("mobile", gender+" KCal");
                        Log.e("RESPONSE2", "isi"+jsonStr+name+address+gender);
                        // adding contact to contact list
                        mockContacts.add(new Contact(
                                id,
                                name,
                                address,
                                gender+" KCal"
                        ));
                    }
                    makanans2=mockContacts;
                    Common.id=ids;
                    Common.makanan=namas;
                    for(Contact makanan:makanans2)
                    {Log.e("Makanan",makanan.getFirstName()+":"+makanan.getLandline());}
                } catch (final JSONException e) {
                    Log.e("RESPONSE2", "Json parsing error: " + e.getMessage());
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
                Log.e("RESPONSE2", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Tidak dapat mengambil data dari server. periksa koneksi internet anda lalu coba kembali!",
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

//            adapter=new MakananAdapter(HomeDetail.this,makanans);
//            lv.setAdapter(adapter);
//            lv.setLayoutManager(new LinearLayoutManager(HomeDetail.this));
        }

    }
    private void addData() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        konsumsiArrayList = new ArrayList<>();

        for (final String w:waktu) {
            category=database.getReference("Konsumsi");
            category = category.child("" + Common.currentUser.getId() + "x" + df.format(currentTime) + "x" + w);//+"x03032019")
            Log.e(TAG, "The " + w);

        category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    konsumsiArrayList.add(new Konsumsi("",w,"",""));
                    for(DataSnapshot o : dataSnapshot.getChildren()){
                        System.out.println(o.toString());
                        String nama="";
                        String id="";
                        String jumlah="";

                        for (Contact makanan:makanans2) {
                            if(o.getKey().equals(makanan.getFirstName())){
                                nama=o.getValue().toString().split("x")[1]+" - "+makanan.getName().split(" ")[1]+" - "+o.getValue().toString().split("x")[0]+" Porsi";
                                id="";//makanan.getFirstName();

                            }
                        }
                        Log.e(TAG, "The " + o.getKey() + " score is " + nama);

                        konsumsiArrayList.add(new Konsumsi(id,nama,jumlah,w));

                    }recycler_menu =findViewById(R.id.recycler_menu_hariini);
                    layoutManager = new LinearLayoutManager(HariiniActivity.this);
                    adapter = new MakananHariIniAdapter(konsumsiArrayList);
                    recycler_menu.setHasFixedSize(true);
                    recycler_menu.setAdapter(adapter);
                    recycler_menu.setLayoutManager(layoutManager);}
                     catch (NullPointerException e){
                        Toast.makeText(HariiniActivity.this,"Belum ada input",Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }
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
            Intent homeIntent=new Intent(HariiniActivity.this,Home.class);
            startActivity(homeIntent);
        } else if (id == R.id.nav_cart) {
//            Intent DetailIntent=new Intent(HariiniActivity.this,HariiniActivity.class);
//            startActivity(DetailIntent);
        }  else if (id == R.id.nav_profil) {
            Intent DetailIntent=new Intent(HariiniActivity.this,ProfilAct.class);
            startActivity(DetailIntent);
        }else if (id == R.id.nav_act) {
            Intent DetailIntent=new Intent(HariiniActivity.this,HariIniAct.class);
            startActivity(DetailIntent);
        }else if (id == R.id.nav_gd) {
            Intent DetailIntent=new Intent(HariiniActivity.this,HariIniGD.class);
            startActivity(DetailIntent);
        }else if (id == R.id.nav_orders) {
            Intent DetailIntent=new Intent(HariiniActivity.this, BarChartActivityMultiDataset.class);
            startActivity(DetailIntent);}
        else if (id == R.id.nav_ramadhan) {
            if(pref.getBoolean("isfast",true)) {
                editor.putBoolean("isfast", false);
                editor.commit();
                item.setTitle("Mode Puasa (Non Aktif)");
                Toast.makeText(HariiniActivity.this,"Mode Puasa dinonaktifkan",Toast.LENGTH_LONG).show();
            }
            else{
                editor.putBoolean("isfast", true);
                editor.commit();
                item.setTitle("Mode Puasa (Aktif)");
//                item.setIcon(R.drawable.ic_action_save);
                Toast.makeText(HariiniActivity.this,"Mode Puasa diaktifkan",Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_logout) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(HariiniActivity.this);

// Set up the buttons
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    Intent homeIntent=new Intent(HariiniActivity.this,MainActivity.class);
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
