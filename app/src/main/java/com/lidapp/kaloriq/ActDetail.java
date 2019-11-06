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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Model.Aktifitas;
import com.lidapp.kaloriq.Model.Contact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ActDetail extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private String TAG = ActDetail.class.getSimpleName();
    ArrayList<HashMap<String, String>> contactList;
    private List<Aktifitas> makanans;
    private ProgressDialog pDialog;
    private RecyclerView lv;
    AktifitasAdapter adapter;
    TextView txtFullName;
    private static String url = "https://ordsofttest.000webhostapp.com/ApiAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_detail);
        Button sortBT=(Button)findViewById(R.id.sortBt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cari Aktivitas");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DetailIntent=new Intent(ActDetail.this,HariIniAct.class);
                startActivity(DetailIntent);

            }
        });
        contactList = new ArrayList<>();


        lv = (RecyclerView) findViewById(R.id.recycler_menudetail);
        sortBT.setText("Urutkan Aktifitas");
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
        List<Aktifitas> newlist=new ArrayList<>();
        for(Aktifitas makan:makanans){
            if(makan.getNama().toLowerCase().contains(userinput)||makan.getDurasi().toLowerCase().contains(userinput)){
                newlist.add(makan);
            }
        }
        adapter.updateList(newlist);
        return true;
    }
    boolean ascending=false;
    public void onClickSort(View v){

        List<Aktifitas> newlist = new ArrayList<>();
        if(!ascending) {
            Collections.sort(makanans, new Comparator<Aktifitas>() {
                @Override
                public int compare(Aktifitas contact, Aktifitas t1) {
                    return String.valueOf(contact.getNama()).compareTo(t1.getNama());
                }
            });
            ascending=true;
        }
        else{
            Collections.sort(makanans, new Comparator<Aktifitas>() {
                @Override
                public int compare(Aktifitas contact, Aktifitas t1) {
                    return String.valueOf(t1.getNama()).compareTo(contact.getNama());
                }
            });
            ascending=false;
        }
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
            pDialog = new ProgressDialog(ActDetail.this);
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
                    ArrayList<Aktifitas> mockContacts = new ArrayList<>();
                    String namas[]=new String[contacts.length()];
                    String ids[]=new String[contacts.length()];
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("id");
                        String nama = c.getString("nama");
                        String kalori = c.getString("METS");
                        String durasi = c.getString("detail");
                        Log.e(TAG, "isi"+jsonStr+id+nama+kalori+durasi);

                        // tmp hash map for single contact
//                        HashMap<String, String> contact = new HashMap<>();
//
//                        // adding each child node to HashMap key => value
//                        contact.put("id", id);
//                        namas[i]=nama;
//                        ids[i]=id;
//                        contact.put("name", nama);
//                        contact.put("email", address);
//                        contact.put("mobile", gender+" Cal");

                        // adding contact to contact list
                        mockContacts.add(new Aktifitas(
                                id,
                                nama,
                                durasi,
                                kalori+" METS/Jam"
                        ));
                    }
                    makanans=mockContacts;
                    Common.act=mockContacts;
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
            /**
             * Updating parsed JSON data into ListView
             * */
//            ListAdapter adapter = new SimpleAdapter(
//                    HomeDetail.this, contactList,
//                    R.layout.list_item, new String[]{"http://www.erabaru.net/wp-content/uploads/2018/02/Jangan-Lagi-Memberi-Anak-anak-9-Jenis-Makanan-Ini-700x366.jpg","name", "email",
//                    "mobile"}, new int[]{R.id.image,R.id.name,
//                    R.id.email, R.id.mobile});
            adapter=new AktifitasAdapter(ActDetail.this,makanans);
            lv.setAdapter(adapter);
            lv.setLayoutManager(new LinearLayoutManager(ActDetail.this));
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
