package com.lidapp.kaloriq;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Custom.CustomTimePickerDialog;
import com.lidapp.kaloriq.Model.Aktifitas;
import com.lidapp.kaloriq.Model.Contact;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DetailsActivityAct extends AppCompatActivity {
    public static final String EXTRA_CONTACT = "contact";
    Button submitbtn;
    private String jam,jam2="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailsact);
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_konsumsitemp=database.getReference("Activity");
        final Aktifitas contact = getIntent().getExtras().getParcelable(EXTRA_CONTACT);
        final Date currentTime = Calendar.getInstance().getTime();

        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        assert contact != null;
        submitbtn=(Button)findViewById(R.id.submittemp);
        ((TextView)findViewById(R.id.activity_details_name))
                .setText(contact.getNama());
        ((TextView)findViewById(R.id.activity_details_landline))
                .setText(contact.getDurasi());
        ((TextView)findViewById(R.id.activity_details_mobile))
                .setText(contact.getKalori());
        ((TextView)findViewById(R.id.date))
                .setText(df2.format(currentTime));
        final AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivityAct.this);
        // Set up the input


// Set up the buttons
        builder.setPositiveButton("Input", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
                        DatabaseReference hopperRef = table_konsumsitemp.child(""+Common.currentUser.getId()+"x"+df.format(currentTime));
                        Map<String, Object> hopperUpdates = new HashMap<>();
                        hopperUpdates.put(contact.getId(), jam+"-"+jam2);
                        hopperRef.updateChildren(hopperUpdates);
                        Toast.makeText(DetailsActivityAct.this,contact.getNama()+" selama menit telah ditambahkan", Toast.LENGTH_SHORT).show();
                Intent DetailIntent=new Intent(DetailsActivityAct.this,HariIniAct.class);
                startActivity(DetailIntent);
                        finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
            }
        });
        builder.setTitle("Masukkan data?");

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
            }
        });
    }
    public void onClockInput(View v){
        final Button input=findViewById(R.id.submittemp);
        Calendar rightNow = Calendar.getInstance();
        final TextView jaam=findViewById(R.id.jam);
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        CustomTimePickerDialog time=new CustomTimePickerDialog(DetailsActivityAct.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Log.e("jam",""+i);
                Log.e("minute",""+i1);
                jam=String.format("%02d:%02d", i, i1);
                jaam.setText(String.format("%02d:%02d", i, i1));
                input.setEnabled(true);
            }
        }, currentHourIn24Format,0, true);
        time.show();
    }
    public void onClockInput2(View v){
        final Button input=findViewById(R.id.submittemp);
        Calendar rightNow = Calendar.getInstance();
        final TextView jaam2=findViewById(R.id.jam2);
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        CustomTimePickerDialog time=new CustomTimePickerDialog(DetailsActivityAct.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Log.e("jam",""+i);
                Log.e("minute",""+i1);
                jam2=String.format("%02d:%02d", i, i1);
                jaam2.setText(String.format("%02d:%02d", i, i1));
                input.setEnabled(true);
            }
        }, currentHourIn24Format,0, true);
        time.show();
    }


}
