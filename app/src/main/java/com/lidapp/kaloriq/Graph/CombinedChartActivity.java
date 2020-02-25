package com.lidapp.kaloriq.Graph;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Custom.BarComparator;
import com.lidapp.kaloriq.Custom.MyMarkerView;
import com.lidapp.kaloriq.HttpHandler;
import com.lidapp.kaloriq.Model.Aktifitas;
import com.lidapp.kaloriq.Model.Bar;
import com.lidapp.kaloriq.Model.Contact;
import com.lidapp.kaloriq.Model.Konsumsi;
import com.lidapp.kaloriq.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CombinedChartActivity extends DemoBase {

    private CombinedChart chart;
    private final int count = 30;
    private int startDate=335;

    //firebase
    FirebaseDatabase database;
    private String waktu[] ;
    String waktu1[]={"Makan Pagi","Makan Siang","Makan Malam","Camilan Pagi","Camilan Siang","Camilan Malam","Lain - Lain"};
    String waktu2[]={"Sahur","Takjil","Berbuka"};
    float bb;
    private static String url = "https://www.caloriecalcmva.com/ApiAct";
    private static String url2 = "https://www.caloriecalcmva.com/api";
    private ProgressDialog pDialog;
    List<Aktifitas> makanans;
    List<Contact> makanans2;
    ArrayList<Konsumsi> aktivityArrayList;
    ArrayList<Konsumsi> konsumsiArrayList;
    DatabaseReference categorypar;
    ArrayList<Bar> akti;
    ArrayList<Bar> kalo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_combined);
        //spinner tahun
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear-1; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        Spinner spinYear = (Spinner)findViewById(R.id.year_spinner);
        spinYear.setAdapter(adapter);
        //spinner bulan
        Spinner spinner = (Spinner) findViewById(R.id.months_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter2);
        setTitle("CombinedChartActivity");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        bb=pref.getFloat("bb",0);
//        Log.e("berat badan",""+bb);
        if (pref.getBoolean("isfast", true)) {
            waktu = waktu2;
        } else {
            waktu = waktu1;
        }
        new GetContacts().execute();
        database = FirebaseDatabase.getInstance();
    }

    private LineData generateLineData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();

        for (int index = startDate; index < startDate+count; index++)
            entries.add(new Entry(index + 0.5f, pref.getFloat("cal",2350)));

        LineDataSet set = new LineDataSet(entries, "Kalori Basal");
        set.setColor(Color.rgb(0, 240, 0));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(0, 0, 0));
        set.setCircleRadius(3f);
        set.setFillColor(Color.rgb(240, 0, 0));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(false);
        set.setValueTextSize(3f);
        set.setValueTextColor(Color.rgb(0, 0, 0));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }
    private float dateToDays(Date endDateValue, Date startDateValue){

        long diff = endDateValue.getTime() - startDateValue.getTime();
        float days= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return 0;
    }
    private BarData generateBarData() throws ParseException {
        String dateStr = "2/3/2017";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse(dateStr);


        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();
        int ba=0;
        Log.e("besar",""+kalo.size());
        for (int i=0;i<31&&ba<kalo.size();i++) {
            if(i+1==kalo.get(ba).getTgl()){
                int h=kalo.get(ba).getTgl();
                int t=kalo.get(ba).getTahun();
//                int thisYear = Calendar.getInstance().get(Calendar.YEAR);
//                int tahun=thisYear-2020;
                int bulan=kalo.get(ba).getBulan();
//                int to=getDaysForMonth(bulan-1,t);
                entries1.add(new BarEntry(kalo.get(ba).getTgl(), kalo.get(ba).getNilai()));
                ba++;
            }
            else {
                entries1.add(new BarEntry(i+1, 0));
            }
        }
        ba=0;

        for(int i=0;i<31&&ba<akti.size();i++){
            if(i+1==akti.get(ba).getTgl()){
//                int t=akti.get(ba).getTahun();
//                int thisYear = Calendar.getInstance().get(Calendar.YEAR);
//                int tahun=thisYear-2019;
//                int bulan=akti.get(ba).getBulan();
//                int to=getDaysForMonth(bulan-1,t);
                entries2.add(new BarEntry(akti.get(ba).getTgl(), akti.get(ba).getNilai()));
                ba++;
            }
            else {
                entries2.add(new BarEntry(i+1, 0));
            }
        }

        BarDataSet set1 = new BarDataSet(entries1, "Konsumsi");
        set1.setColor(Color.rgb(220, 100, 78));
        set1.setValueTextColor(Color.rgb(0, 0, 0));
        set1.setValueTextSize(5f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set2 = new BarDataSet(entries2, "Aktifitas");
        set2.setColor(Color.rgb(23, 197, 255));
        set2.setValueTextColor(Color.rgb(0, 0, 0));
        set2.setValueTextSize(5f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        float groupSpace = 0.10f;
        float barSpace = 0.15f; // x2 dataset
        float barWidth = 0.30f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1, set2);
        d.setBarWidth(barWidth);

        // make this BarData object grouped
        d.groupBars(startDate, groupSpace, barSpace); // start at x = 0

        return d;
    }
    public int getDaysForMonth(int month, int year) {

        // month is 0-based

        if (month == 1) {
            boolean is29Feb = false;

            if (year < 1582)
                is29Feb = (year < 1 ? year + 1 : year) % 4 == 0;
            else if (year > 1582)
                is29Feb = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);

            return is29Feb ? 29 : 28;
        }

        if (month == 3 || month == 5 || month == 8 || month == 10)
            return 30;
        else
            return 31;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.combined, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewGithub: {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/CombinedChartActivity.java"));
                startActivity(i);
                break;
            }
            case R.id.actionToggleLineValues: {
                for (IDataSet set : chart.getData().getDataSets()) {
                    if (set instanceof LineDataSet)
                        set.setDrawValues(!set.isDrawValuesEnabled());
                }

                chart.invalidate();
                break;
            }
            case R.id.actionToggleBarValues: {
                for (IDataSet set : chart.getData().getDataSets()) {
                    if (set instanceof BarDataSet)
                        set.setDrawValues(!set.isDrawValuesEnabled());
                }

                chart.invalidate();
                break;
            }
            case R.id.actionRemoveDataSet: {
                int rnd = (int) getRandom(chart.getData().getDataSetCount(), 0);
                chart.getData().removeDataSet(chart.getData().getDataSetByIndex(rnd));
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
                chart.invalidate();
                break;
            }
        }
        return true;
    }

    @Override
    public void saveToGallery() { /* Intentionally left empty */ }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CombinedChartActivity.this);
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
//            Log.e("Aktifitas","Halooo");
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

        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CombinedChartActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url2);

//            Log.e("RESPONSE2", "Response from url: " + jsonStr);

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
//                    Log.e("RESPONSE2", "Json parsing error: " + e.getMessage());
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
//                Log.e("RESPONSE2", "Couldn't get json from server.");
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

        }

    }


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
//                        Log.e("RESPONSE", "The " +kalori+":"+t+":"+b+":"+y);
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
//
                try {
                    showGraph();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

    private void showGraph() throws ParseException {
//        for(Bar item:akti) {
//            Log.e("BARUUU", "The " + item.getTgl() + ":" + item.getBulan()+ ":" + item.getTahun()+ ":" + item.getNilai()+":"+item.getall());
//        }
//        Log.e("BARUUU",""+kalo.size());
        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(true);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        chart.setDrawOrder(new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.LINE,
        });

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
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(1f); // this replaces setStartAtZero(true)


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(3000f);
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(startDate);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setCenterAxisLabels(true);
        CombinedData data = new CombinedData();

        data.setData(generateLineData());
        data.setData(generateBarData());
        data.setValueTypeface(tfLight);

        xAxis.setAxisMaximum(data.getXMax() + 1f);
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control

        chart.setMarker(mv); // Set the marker to the chart
        chart.setData(data);
        chart.getAxisRight().setEnabled(false);
        chart.animateY(1500);
        chart.animateX(2500);
        chart.invalidate();
    }
}
