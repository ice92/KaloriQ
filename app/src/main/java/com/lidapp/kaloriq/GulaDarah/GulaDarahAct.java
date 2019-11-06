package com.lidapp.kaloriq.GulaDarah;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Custom.CustomTimePickerDialog;
import com.lidapp.kaloriq.Model.Aktifitas;
import com.lidapp.kaloriq.Model.Contact;
import com.lidapp.kaloriq.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class GulaDarahAct extends AppCompatActivity {
    public static final String EXTRA_CONTACT = "contact";
    Button submitbtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gula_darah);
        final Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        ((TextView)findViewById(R.id.date))
                .setText(df2.format(currentTime));
        ((TextView)findViewById(R.id.waktu))
                .setText("Data Gula");
        // Set up the input

    }
    String jam="";
    public void onClickInput(View v){

        EditText guladrh=findViewById(R.id.inguladarah);
        RadioGroup rg= findViewById(R.id.rg);
        int id=rg.getCheckedRadioButtonId();
        RadioButton rb=findViewById(id);
        String waktu=rb.getText().toString();
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_konsumsitemp=database.getReference("GulaDarah");
        final Date currentTime = Calendar.getInstance().getTime
                ();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        DatabaseReference hopperRef = table_konsumsitemp.child(""+Common.currentUser.getId()+"x"+df.format(currentTime));
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put(waktu , guladrh.getText().toString()+"x"+jam);

        int gd=Integer.parseInt(guladrh.getText().toString());
        if(gd<70){
            Toast toast = Toast.makeText(com.lidapp.kaloriq.GulaDarah.GulaDarahAct.this, " Gula darah Anda Rendah\nSegera Hubungi Dokter !", Toast.LENGTH_LONG);
            View view = toast.getView();
            view.setBackgroundColor(Color.RED);
            view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            TextView text = (TextView) view.findViewById(android.R.id.message);
            /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
            toast.show();
//            Toast.makeText(com.lidapp.kaloriq.GulaDarah.GulaDarahAct.this," Gula darah Anda Rendah, Segera Hubungi Dokter !", Toast.LENGTH_LONG).show();
//            hopperUpdates.put("emergency", guladrh.getText().toString()+"x"+jam);
        }
        hopperRef.updateChildren(hopperUpdates);
        Toast.makeText(com.lidapp.kaloriq.GulaDarah.GulaDarahAct.this," Gula darah hari ini telah ditambahkan", Toast.LENGTH_SHORT).show();
        finish();//
    }
    public void onClockInput(View v){
        final Button input=findViewById(R.id.button);
        Calendar rightNow = Calendar.getInstance();
        final TextView jaam=findViewById(R.id.jam);
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        CustomTimePickerDialog time=new CustomTimePickerDialog(GulaDarahAct.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Log.e("jam",""+i);
                Log.e("minute",""+i1);
                jam=String.format("%02d:%02d", i, i1);//""+i+i1;
                jaam.setText(String.format("%02d:%02d", i, i1));
                input.setEnabled(true);
            }
        }, currentHourIn24Format,0, true);
        time.show();
    }
}
