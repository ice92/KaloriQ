package com.lidapp.kaloriq;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Custom.CustomTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomInput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_input);

    }
    String jam;
//    public void onClickInput(View v){
//        final FirebaseDatabase database=FirebaseDatabase.getInstance();
//        final DatabaseReference table_konsumsitemp=database.getReference("Konsumsi");
//        final String waktu=getIntent().getStringExtra("waktu");
//        final Date currentTime = Calendar.getInstance().getTime();
//        String jumlah = input.getText().toString();
//        String nama;
//        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
//        DatabaseReference hopperRef = table_konsumsitemp.child(""+ Common.currentUser.getId()+"x"+df.format(currentTime)+"x"+waktu);
//        Map<String, Object> hopperUpdates = new HashMap<>();
//        hopperUpdates.put(contact.getFirstName(), jumlah+"x"+jam);
//        hopperRef.updateChildren(hopperUpdates);
//    }
//    public void onClockInput(View v){
//        final Button input=findViewById(R.id.submittemp);
//        Calendar rightNow = Calendar.getInstance();
//        final TextView jaam=findViewById(R.id.jam);
//        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
//        CustomTimePickerDialog time=new CustomTimePickerDialog(CustomInput.this, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int i, int i1) {
//                Log.e("jam",""+i);
//                Log.e("minute",""+i1);
//                jam=String.format("%02d:%02d", i, i1);
//                jaam.setText(""+i+":"+i1);
//                input.setEnabled(true);
//            }
//        }, currentHourIn24Format,0, true);
//        time.show();
//    }
}
