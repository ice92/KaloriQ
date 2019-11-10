package com.lidapp.kaloriq.Graph;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lidapp.kaloriq.ActivityHariIniAdapter;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Custom.BarComparator;
import com.lidapp.kaloriq.Custom.MyMarkerView;
import com.lidapp.kaloriq.HariIniAct;
import com.lidapp.kaloriq.HariiniActivity;
import com.lidapp.kaloriq.HttpHandler;
import com.lidapp.kaloriq.MakananHariIniAdapter;
import com.lidapp.kaloriq.Model.Aktifitas;
import com.lidapp.kaloriq.Model.Bar;
import com.lidapp.kaloriq.Model.Contact;
import com.lidapp.kaloriq.Model.Konsumsi;
import com.lidapp.kaloriq.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BarChartActivityMultiDataset extends DemoBase implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {
    private String waktu[] ;
    String waktu1[]={"Makan Pagi","Makan Siang","Makan Malam","Camilan Pagi","Camilan Siang","Camilan Malam","Lain - Lain"};
    String waktu2[]={"Berbuka","Takjil","Sahur"};
    private BarChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;
    private ProgressDialog pDialog;
    private static String url = "https://www.caloriecalcmva.com/ApiAct";
    private static String url2 = "https://www.caloriecalcmva.com/api";
     List<Aktifitas> makanans;
     List<Contact> makanans2;
    private ProgressDialog pDialog2;
     ArrayList<Konsumsi> aktivityArrayList;
     ArrayList<Konsumsi> konsumsiArrayList;
    FirebaseDatabase database;
    DatabaseReference category;
    DatabaseReference categorypar;
    float bb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);

        setTitle("BarChartActivityMultiDataset");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        bb=pref.getFloat("bb",0);
        if (pref.getBoolean("isfast", true)) {
            waktu = waktu2;
        } else {
            waktu = waktu1;
        }
        new GetContacts().execute();
        database = FirebaseDatabase.getInstance();
    }

    private void showGraph(){
        tvX = findViewById(R.id.tvXMax);
        tvX.setTextSize(10);
        tvY = findViewById(R.id.tvYMax);

        seekBarX = findViewById(R.id.seekBar1);
        seekBarX.setMax(50);
        seekBarX.setOnSeekBarChangeListener(this);

        seekBarY = findViewById(R.id.seekBar2);
        seekBarY.setOnSeekBarChangeListener(this);

        chart = findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);
        chart.getDescription().setEnabled(false);

//        chart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);

        chart.setDrawGridBackground(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        seekBarX.setProgress(3);
        seekBarY.setProgress(10);
        ValueFormatter custom = new MyValueFormatter("KCal");
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setTypeface(tfLight);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTypeface(tfLight);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(
//                new DayAxisValueFormatter(chart));
                new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if(tgl!=null && tgl.length>0){
//                    return tgl[((int) value)];
                    Log.e("angka",String.valueOf((int) value)+":"+tgl.length);
                    return ""+tgl[((int) value)];
                }else
                    return String.valueOf((int) value);

            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setValueFormatter(custom);
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        chart.animateY(1500);
    }

    ArrayList<Bar> akti;
    private void addData() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        aktivityArrayList = new ArrayList<>();
        akti=new ArrayList<>();
        categorypar=database.getReference("Activity");
        categorypar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    String temp = childDataSnapshot.getKey();

                    float kalori=0;
                    if(temp.contains(Common.currentUser.getId())) {


                        for(DataSnapshot data:childDataSnapshot.getChildren()){
                            String start=data.getValue().toString().split("-")[0];
                            String end=data.getValue().toString().split("-")[1];
                            int hour=Integer.parseInt(end.split(":")[0])-Integer.parseInt(start.split(":")[0]);
                            int minutes=hour*60+(Integer.parseInt(end.split(":")[1])-Integer.parseInt(start.split(":")[1]));
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

                            for(Aktifitas aktifitas:makanans){
                                if(aktifitas.getId().equals(data.getKey())){
                                    kalori+=bb*(float)minutes/60*Float.parseFloat(aktifitas.getKalori().split(" ")[0]);
//                                    Log.e("RESPONSE2", "The " +aktifitas.getId()+":"+kalori);
                                }
                            }
                        }

                        int t=Integer.parseInt(temp.split("x")[1].substring(0,2));
                        int b=Integer.parseInt(temp.split("x")[1].substring(2,4));
                        int y=Integer.parseInt(temp.split("x")[1].substring(4));
                        akti.add(new Bar(kalori,t,b,y));
//                        Log.e("RESPONSE", "The " +temp.split("x")[1]+":"+t+":"+b+":"+y);
                    }

                }
                Collections.sort(akti,new BarComparator());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }
    ArrayList<Bar> kalo;
    private void addData2() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        kalo=new ArrayList<>();
        konsumsiArrayList = new ArrayList<>();
            categorypar=database.getReference("Konsumsi");
            categorypar.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    float kalori=0;
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        String temp = childDataSnapshot.getKey();

                        if(temp.contains(Common.currentUser.getId()))
                        {
                            for(DataSnapshot data:childDataSnapshot.getChildren()){
                                for(Contact makanan:makanans2){
                                    if(makanan.getFirstName().equals(data.getKey())){
                                        kalori+=Float.parseFloat(data.getValue().toString().split("x")[0])*Float.parseFloat(makanan.getLandline().split(" ")[0]);
                                    }
                                }
                            }
                            int t=Integer.parseInt(temp.split("x")[1].substring(0,2));
                            int b=Integer.parseInt(temp.split("x")[1].substring(2,4));
                            int y=Integer.parseInt(temp.split("x")[1].substring(4));
                            kalo.add(new Bar(kalori,t,b,y));

                            kalori=0;
                        }
                    }
                    ArrayList<Bar> kalofil=new ArrayList<>();


                    Collections.sort(kalo,new BarComparator());
//                    for(Bar item:kalo) {
//                        Log.e("RESPONSE2", "The " + item.getTgl() + ":" + item.getBulan()+ ":" + item.getTahun()+ ":" + item.getNilai()+":"+item.getall());
//                    }
                    kalori=0;
                    for(int i=0;i<kalo.size()-1;i++) {
                        kalori+=kalo.get(i).getNilai();
//                        Log.e("RES", "The " +kalo.get(i).getall()+":"+kalo.get(i+1).getall()+":"+(kalo.get(i).getall().equals(kalo.get(i+1).getall())));
                        if(!kalo.get(i).getall().equals(kalo.get(i+1).getall())){
                            kalofil.add(new Bar(kalori,kalo.get(i).getTgl(),kalo.get(i).getBulan(),kalo.get(i).getTahun()));
                            kalori=0;
                            if(i==kalo.size()-2){
                                kalofil.add(new Bar(kalo.get(i+1).getNilai(),kalo.get(i+1).getTgl(),kalo.get(i+1).getBulan(),kalo.get(i+1).getTahun()));
                                break;
                            }
                        }
                        if(i==kalo.size()-2){
                            kalofil.add(new Bar(kalori+kalo.get(i+1).getNilai(),kalo.get(i+1).getTgl(),kalo.get(i+1).getBulan(),kalo.get(i+1).getTahun()));
                        }
                    }
//                    for(Bar item:kalofil){
////                        Log.e("RESPONSE3", "The " + item.getTgl() + ":" + item.getBulan()+ ":" + item.getTahun()+ ":" + item.getNilai());
//                    }
                    kalo=kalofil;
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });

        showGraph();
    }
    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(BarChartActivityMultiDataset.this);
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
//                        Log.e("RESPONSE2", "isi"+jsonStr+name+address+gender);
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
//                    for(Contact makanan:makanans2)
//                    {Log.e("Makanan",makanan.getFirstName()+":"+makanan.getLandline());}
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
            addData2();
//            adapter=new MakananAdapter(HomeDetail.this,makanans);
//            lv.setAdapter(adapter);
//            lv.setLayoutManager(new LinearLayoutManager(HomeDetail.this));
        }

    }
    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(BarChartActivityMultiDataset.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

//            Log.e("RESPONSE", "Response from url: " + jsonStr);

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
                                kalori+" METS/Jam"
                        ));
                    }
                    makanans=mockContacts;
//                    for(Aktifitas makanan:makanans)
//                    {Log.e("Aktifitas",makanan.getKalori()+":"+makanan.getId());}
                } catch (final JSONException e) {
//                    Log.e("RESPONSE", "Json parsing error: " + e.getMessage());
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
//                Log.e("RESPONSE", "Couldn't get json from server.");
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
            new GetContacts2().execute();
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
    String tgl[];
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tgl=new String[400];
        for(int i=0;i<400;i++){
            tgl[i]="-";
        }
        for(int i=0;i<akti.size();i++){
           tgl[i]=""+akti.get(i).getTgl()+"/"+akti.get(i).getBulan()+"/"+akti.get(i).getTahun();
        }
//        for(Bar b:akti){
//            Log.e("Bar", "The " +b.getNilai()+":"+b.getTgl()+":"+b.getBulan()+":"+b.getTahun());
//        }
        float groupSpace = 0.1f;
        float barSpace = 0.06f; // x2 DataSet
        float barWidth = 0.24f; // x2 DataSet
        // (0.4 + 0.06) * 2 + 0.08 = 1.00 -> interval per "group"

        int groupCount = seekBarX.getProgress() + 2;
        int startYear = 1;
        int endYear = startYear + groupCount;
        Log.e("Bar",""+seekBarX.getProgress());
        tvX.setText(String.format(Locale.ENGLISH, "%d-%d", startYear, endYear));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        ArrayList<BarEntry> values3 = new ArrayList<>();

        float randomMultiplier = seekBarY.getProgress() ;

        for (int i = 0; i < akti.size(); i++) {
            values3.add(new BarEntry(i,  2500));
        }
//        int sizemax;
//        if(akti.size()>kalo.size()){
//            sizemax=akti.size();
//        }
//        else{
//            sizemax=kalo.size();
//        }
        for (Bar b:akti) {
            values1.add(new BarEntry(b.getAllint(),b.getNilai()));
            boolean couple=false;
            for(Bar c:kalo){
                if(!b.getall().equals(c.getall())){
                }
                else{
                    values2.add(new BarEntry(b.getAllint(),c.getNilai()));
//                    kalo.remove(c);
                    couple=true;
                    break;
                }
            }
            if(!couple){
                values2.add(new BarEntry(b.getAllint(),0));
            }
        }
        for (Bar b:kalo) {
            boolean couple=false;
            for(Bar c:akti) {
                if(b.getall().equals(c.getall())){
                    couple=true;
                }
            }
            if(!couple)
                values2.add(new BarEntry(b.getAllint(), b.getNilai()));
        }

        BarDataSet set1, set2,set3;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);
            set3 = (BarDataSet) chart.getData().getDataSetByIndex(2);
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            // create 2 DataSets
            set1 = new BarDataSet(values1, "Aktifitas");
            set1.setColor(Color.rgb(200, 0, 0));
            set2 = new BarDataSet(values2, "Konsumsi");
            set2.setColor(Color.rgb(0, 200, 0));
            set3 = new BarDataSet(values2, "Ideal");
            set3.setColor(Color.rgb(0, 0, 200));
            BarData data = new BarData(set1, set2,set3);
            data.setValueFormatter(new LargeValueFormatter());
            data.setValueTypeface(tfLight);

            chart.setData(data);
        }

        // specify the width each bar should have
        chart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        chart.getXAxis().setAxisMinimum(startYear);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        chart.getXAxis().setAxisMaximum(startYear + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chart.groupBars(startYear, groupSpace, barSpace);
        chart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.viewGithub: {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartActivityMultiDataset.java"));
                startActivity(i);
                break;
            }
            case R.id.actionToggleValues: {
                for (IBarDataSet set : chart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                chart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (chart.isPinchZoomEnabled())
                    chart.setPinchZoom(false);
                else
                    chart.setPinchZoom(true);

                chart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
                chart.notifyDataSetChanged();
                break;
            }
            case R.id.actionToggleBarBorders: {
                for (IBarDataSet set : chart.getData().getDataSets())
                    ((BarDataSet) set).setBarBorderWidth(set.getBarBorderWidth() == 1.f ? 0.f : 1.f);

                chart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (chart.getData() != null) {
                    chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
                    chart.invalidate();
                }
                break;
            }
            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                } else {
                    requestStoragePermission(chart);
                }
                break;
            }
            case R.id.animateX: {
                chart.animateX(2000);
                break;
            }
            case R.id.animateY: {
                chart.animateY(2000);
                break;
            }
            case R.id.animateXY: {
                chart.animateXY(2000, 2000);
                break;
            }
        }
        return true;
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "BarChartActivityMultiDataset");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Activity", "Selected: " + e.toString() + ", dataSet: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Activity", "Nothing selected.");
    }
}
