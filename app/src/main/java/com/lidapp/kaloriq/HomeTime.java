package com.lidapp.kaloriq;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Interface.ItemClickListener;
import com.lidapp.kaloriq.Model.Category;
import com.lidapp.kaloriq.Model.User;
import com.lidapp.kaloriq.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

public class HomeTime extends AppCompatActivity{

    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hometime);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pilih Waktu Makan");
        recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        database=FirebaseDatabase.getInstance();

        if(pref.getBoolean("isfast",true)){
            puasa=true;
            category=database.getReference("TimeCategory2");}
        else{
            puasa=false;
            category=database.getReference("TimeCategory");
        }
        loadMenu();
    }
boolean puasa;
    private void loadMenu() {
        FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item2,MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
//                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                if(puasa) {
                    if (position == 0)
                        viewHolder.imageView.setImageResource(R.drawable.makansiang);
                    else if (position == 1)
                        viewHolder.imageView.setImageResource(R.drawable.takjil);
                    else
                        viewHolder.imageView.setImageResource(R.drawable.bukapuasa);
                }
                else{
                    if (position == 0)
                        viewHolder.imageView.setImageResource(R.drawable.makanpagi);
                    else if (position == 1)
                        viewHolder.imageView.setImageResource(R.drawable.camilanpagi);
                    else if (position == 2)
                        viewHolder.imageView.setImageResource(R.drawable.makansiang);
                    else if (position == 3)
                        viewHolder.imageView.setImageResource(R.drawable.camilansiang);
                    else if (position == 4)
                        viewHolder.imageView.setImageResource(R.drawable.bukapuasa);
                    else if (position == 5)
                        viewHolder.imageView.setImageResource(R.drawable.camilanmalam);
                    else{}
//                        viewHolder.imageView.setImageResource(R.drawable.guladarah);
                }
                final Category clickItem=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                            Intent detailIntent=new Intent(HomeTime.this,HomeDetail.class);
                            detailIntent.putExtra("waktu",clickItem.getName());
                            startActivity(detailIntent);
                            finish();
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//
//        return super.onOptionsItemSelected(item);
//    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//
//        SharedPreferences  pref = getApplicationContext().getSharedPreferences("MyPref", 0);
//        SharedPreferences.Editor editor = pref.edit();
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_menu) {
//            Intent homeIntent=new Intent(HomeTime.this,HomeTime.class);
//            startActivity(homeIntent);
//            finish();
//
//        } else if (id == R.id.nav_cart) {
//            Intent DetailIntent=new Intent(HomeTime.this,HariiniActivity.class);
//            startActivity(DetailIntent);
//
//        } else if (id == R.id.nav_orders) {
//        } else if (id == R.id.nav_ramadhan) {
//            if(pref.getBoolean("isfast",true)) {
//                editor.putBoolean("isfast", false);
//                editor.commit();
//                Toast.makeText(HomeTime.this,"Mode ramadhan nonaktif",Toast.LENGTH_SHORT).show();
//            }
//            else{
//                editor.putBoolean("isfast", true);
//                editor.commit();
//                Toast.makeText(HomeTime.this,"Mode ramadhan aktif",Toast.LENGTH_SHORT).show();
//            }
//        } else if (id == R.id.nav_logout) {
//
//            final AlertDialog.Builder builder = new AlertDialog.Builder(HomeTime.this);
//
//// Set up the buttons
//            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.clear();
//                    editor.commit();
//                    Intent homeIntent=new Intent(HomeTime.this,MainActivity.class);
//                    startActivity(homeIntent);
//                    finish();
//                }
//            });
//            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            builder.setTitle("Apakah anda yakin akan keluar?");
//            builder.show();
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}
