package com.lidapp.kaloriq.Graph;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

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
import com.lidapp.kaloriq.Custom.MyMarkerView;
import com.lidapp.kaloriq.R;

import java.util.ArrayList;

public class CombinedChartActivityGD extends DemoBase {

    private CombinedChart chart;
    private final int count = 30;
    private int startDate=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_combined);
        setTitle("CombinedChartActivity");

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

//        Legend l = chart.getLegend();
//        l.setWordWrapEnabled(true);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
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
        leftAxis.setAxisMaximum(400f);
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(startDate);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(xAxisFormatter);
        CombinedData data = new CombinedData();

        data.setData(generateLineData());
//        data.setData(generateBarData());
        data.setValueTypeface(tfLight);

        xAxis.setAxisMaximum(data.getXMax() + 1f);
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control

        chart.setMarker(mv); // Set the marker to the chart
        chart.setData(data);
        chart.getAxisRight().setEnabled(false);
        chart.animateY(1500);
        chart.invalidate();
    }

    private LineData generateLineData() {

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> entries2 = new ArrayList<>();
        ArrayList<Entry> entries3 = new ArrayList<>();
        ArrayList<Entry> entries4 = new ArrayList<>();

        for (int index = startDate; index < startDate+count; index++) {
            entries.add(new Entry(index + 0.5f, getRandom(250, 50)));
            entries2.add(new Entry(index + 0.5f, getRandom(250, 50)));
            entries3.add(new Entry(index + 0.5f, getRandom(250, 50)));
            entries4.add(new Entry(index + 0.5f, 70));
        }

        LineDataSet set = new LineDataSet(entries, "Gula darah pagi");
        set.setColor(Color.rgb(0, 240, 0));
        set.setLineWidth(0f);
        set.setCircleColor(Color.rgb(0, 0, 0));
        set.setCircleRadius(3f);
        set.setFillColor(Color.rgb(240, 0, 0));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(false);
        set.setValueTextSize(3f);
        set.setValueTextColor(Color.rgb(0, 0, 0));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineDataSet set1 = new LineDataSet(entries2, "Gula darah siang");
        set1.setColor(Color.rgb(0, 0, 240));
        set1.setLineWidth(0f);
        set1.setCircleColor(Color.rgb(0, 0, 0));
        set1.setCircleRadius(3f);
        set1.setFillColor(Color.rgb(240, 0, 0));
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setDrawValues(false);
        set1.setValueTextSize(3f);
        set1.setValueTextColor(Color.rgb(0, 0, 0));
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineDataSet set2 = new LineDataSet(entries3, "Gula darah malam");
        set2.setColor(Color.rgb(240, 240, 0));
        set2.setLineWidth(0f);
        set2.setCircleColor(Color.rgb(0, 0, 0));
        set2.setCircleRadius(3f);
        set2.setFillColor(Color.rgb(240, 0, 0));
        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set2.setDrawValues(false);
        set2.setValueTextSize(3f);
        set2.setValueTextColor(Color.rgb(0, 0, 0));
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineDataSet set3 = new LineDataSet(entries4, "Batas bawah");
        set3.setColor(Color.rgb(240, 0, 0));
        set3.setLineWidth(0f);

        set3.setCircleColor(Color.rgb(240, 0, 0));
        set3.setCircleRadius(0.5f);
        set3.setFillColor(Color.rgb(240, 0, 0));
        set3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set3.setDrawValues(false);
        set3.setValueTextSize(3f);
        set3.setValueTextColor(Color.rgb(0, 0, 0));
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);


        LineData d = new LineData(set,set1,set2,set3);
//        d.addDataSet();

        return d;
    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();

        for (int index = startDate; index < startDate+count; index++) {
            entries1.add(new BarEntry(index, getRandom(500, 2000)));
            entries2.add(new BarEntry(index, getRandom(1500, 1000)));
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
}
