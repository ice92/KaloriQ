package com.lidapp.kaloriq;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Model.Contact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class HomeDetail extends AppCompatActivity implements SearchView.OnQueryTextListener{
    public static String EXTRA_TIME = "";
    private String TAG = HomeDetail.class.getSimpleName();
    ArrayList<HashMap<String, String>> contactList;
    private List<Contact> makanans;
    private ProgressDialog pDialog;
    private RecyclerView lv;
    MakananAdapter adapter;
    TextView txtFullName;
    private static String url = "https://www.caloriecalcmva.com/api";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_detail);
        EXTRA_TIME=getIntent().getStringExtra("waktu");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Cari Makanan");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DetailIntent=new Intent(HomeDetail.this,HariiniActivity.class);
                startActivity(DetailIntent);
                finish();
            }
        });
        contactList = new ArrayList<>();

        lv = (RecyclerView) findViewById(R.id.recycler_menudetail);

        new GetContacts().execute();

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newtext) {
        String userinput=newtext.toLowerCase();
        List<Contact> newlist=new ArrayList<>();
        for(Contact makan:makanans){
            if(makan.getLastName().toLowerCase().contains(userinput)){
                newlist.add(makan);
            }
        }
        adapter.updateList(newlist);

        return true;
    }
    boolean ascending=false;
    public void onClickSort(View v){

        List<Contact> newlist = new ArrayList<>();
        if(!ascending) {
            Collections.sort(makanans, new Comparator<Contact>() {
                @Override
                public int compare(Contact contact, Contact t1) {
                    return String.valueOf(contact.getLastName()).compareTo(t1.getLastName());
                }
            });
            ascending=true;
        }
        else{
            Collections.sort(makanans, new Comparator<Contact>() {
                @Override
                public int compare(Contact contact, Contact t1) {
                    return String.valueOf(t1.getLastName()).compareTo(contact.getLastName());
                }
            });
            ascending=false;
        }
//        for(Contact makan:makanans){
//            if(makan.getLastName().toLowerCase().contains(userinput)){
//                newlist.add(makan);
//            }
//        }
        adapter.updateList(makanans);

    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HomeDetail.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

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
                        Log.e(TAG, "isi"+jsonStr+name+address+gender);
                        // adding contact to contact list
                        mockContacts.add(new Contact(
                                id,
                                name,
                                address,
                                gender+" KCal"
                        ));
                    }
                    makanans=mockContacts;
                    Common.id=ids;
                    Common.makanan=namas;
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
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
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
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
            adapter=new MakananAdapter(HomeDetail.this,makanans);
            lv.setAdapter(adapter);
            lv.setLayoutManager(new LinearLayoutManager(HomeDetail.this));
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
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}
