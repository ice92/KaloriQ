package com.lidapp.kaloriq;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Graph.AnotherBarActivity;
import com.lidapp.kaloriq.Graph.BarChartActivityMultiDataset;
import com.lidapp.kaloriq.Graph.CombinedChartActivity;
import com.lidapp.kaloriq.Graph.CombinedChartActivityGD;
import com.lidapp.kaloriq.GulaDarah.GulaDarahAct;
import com.lidapp.kaloriq.Interface.ItemClickListener;
import com.lidapp.kaloriq.Model.Category;
import com.lidapp.kaloriq.Model.User;
import com.lidapp.kaloriq.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName,txtcal;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        if(pref.getBoolean("islogin",false)){
            User user=new User();
            user.setId(pref.getString("id",null));
            user.setName(pref.getString("username", null));
            Common.currentUser=user;
        }
        else{
            Intent homeIntent=new Intent(this,MainActivity.class);
            startActivity(homeIntent);
            finish();

        }
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Beranda");
        setSupportActionBar(toolbar);
        final Date currentTime = Calendar.getInstance().getTime();
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category");
        //Tombol emergency gula darah
        Button fab = findViewById(R.id.fab2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                // Set up the input
                final EditText input = new EditText(Home.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                input.setHint("Angka Gula darah/dll");
                builder.setView(input);
                final DatabaseReference table_konsumsitemp=database.getReference("GulaDarah");
                // Set up the buttons
                builder.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String jumlah = input.getText().toString();
                        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
                        DatabaseReference hopperRef = table_konsumsitemp.child(""+Common.currentUser.getId()+"x"+df.format(currentTime)+"x");
                        Map<String, Object> hopperUpdates = new HashMap<>();
                        hopperUpdates.put("emergency", jumlah);
                        hopperRef.updateChildren(hopperUpdates);
                        Toast.makeText(Home.this," Emergency data telah ditambahkan", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
                    }
                });
                builder.setTitle("Masukkan data darurat/penting");

                        builder.show();

            }
        });
        //end tombol emergeny gula darah
        //Side menu objek
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menuNav=navigationView.getMenu();
        MenuItem item=menuNav.findItem(R.id.nav_ramadhan);
        if(pref.getBoolean("isfast",false)) {
            item.setTitle("Mode Puasa (Aktif)");
        }
        else{
            item.setTitle("Mode Puasa (Non Aktif)");
        }
        View headerView=navigationView.getHeaderView(0);
        txtFullName=(TextView) headerView.findViewById(R.id.txtFullName);
        txtcal=(TextView) headerView.findViewById(R.id.txtcal);
        try {
            txtFullName.setText("Selamat Datang " + Common.currentUser.getName());
            txtcal.setText("Rekomendasi kalori harian anda :"+pref.getFloat("cal",0)+" KCal");
        }
        catch (NullPointerException e){
        }
        //end side menu


        //menu utama
        recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        loadMenu();
        //end menu utama
    }
    int i=0;
    private void loadMenu() {
        FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText("\n  "+model.getName());
//                Picasso.with(getBaseContext()).load(model.getImage()).placeholder().into(viewHolder.imageView);
//                if(position==0)
//                    viewHolder.imageView.setImageResource(R.drawable.konsumsi);
//                    else if(position==1)
//                        viewHolder.imageView.setImageResource(R.drawable.aktifitas);
//                        else
//                    viewHolder.imageView.setImageResource(R.drawable.guladarah);
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                final Category clickItem=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(clickItem.getName().equals("Diary Konsumsi")){
//                            Intent DetailIntent =new Intent(Home.this,HomeTime.class);
//                            startActivity(DetailIntent);
                            Intent DetailIntent=new Intent(Home.this,HariiniActivity.class);
                            startActivity(DetailIntent);
                        }
                        else if(clickItem.getName().equals("Diary Aktivitas")){
//                            Intent DetailIntent=new Intent(Home.this,ActDetail.class);
//                            startActivity(DetailIntent);
                            Intent DetailIntent=new Intent(Home.this,HariIniAct.class);
                            startActivity(DetailIntent);
                        }
                        else{
//                            Intent DetailIntent=new Intent(Home.this, GulaDarahAct.class);
//                            startActivity(DetailIntent);
                            Intent DetailIntent=new Intent(Home.this,HariIniGD.class);
                            startActivity(DetailIntent);
                        }
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
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
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
//            Intent homeIntent=new Intent(Home.this,Home.class);
//            startActivity(homeIntent);
        } else if (id == R.id.nav_cart) {
            Intent DetailIntent=new Intent(Home.this,HariiniActivity.class);
            startActivity(DetailIntent);
//            this.finish();
        }  else if (id == R.id.nav_profil) {
            Intent DetailIntent=new Intent(Home.this,ProfilAct.class);
            startActivity(DetailIntent);
//            this.finish();
        }else if (id == R.id.nav_act) {
            Intent DetailIntent=new Intent(Home.this,HariIniAct.class);
            startActivity(DetailIntent);
//            this.finish();
        }else if (id == R.id.nav_gd) {
            Intent DetailIntent=new Intent(Home.this,HariIniGD.class);
            startActivity(DetailIntent);
//            this.finish();
        }else if (id == R.id.nav_orders) {
        Intent DetailIntent=new Intent(Home.this, CombinedChartActivity.class);
        startActivity(DetailIntent);
//            this.finish();
        }else if (id == R.id.nav_orders2) {
            Intent DetailIntent=new Intent(Home.this, CombinedChartActivityGD.class);
            startActivity(DetailIntent);
//            this.finish();
        }
        else if (id == R.id.nav_ramadhan) {
            if(pref.getBoolean("isfast",true)) {
                editor.putBoolean("isfast", false);
                editor.commit();
                item.setTitle("Mode Puasa (Non Aktif)");
                Toast.makeText(Home.this,"Mode Puasa dinonaktifkan",Toast.LENGTH_LONG).show();

            }
            else{
                editor.putBoolean("isfast", true);
                editor.commit();
                item.setTitle("Mode Puasa (Aktif)");
//                item.setIcon(R.drawable.ic_action_save);
                Toast.makeText(Home.this,"Mode Puasa diaktifkan",Toast.LENGTH_LONG).show();
//                this.finish();
            }
        } else if (id == R.id.nav_logout) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);

// Set up the buttons
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    Intent homeIntent=new Intent(Home.this,MainActivity.class);
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
