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
import com.lidapp.kaloriq.GulaDarah.GulaDarahAct;
import com.lidapp.kaloriq.Model.Contact;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;


public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_CONTACT = "contact";
    Button submitbtn;
    private String jumlah = "";
    private String jam="";
    private AlertDialog.Builder builder;
    private EditText input;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_konsumsitemp=database.getReference("Konsumsi");
        final Contact contact = getIntent().getExtras().getParcelable(EXTRA_CONTACT);
        final String waktu=getIntent().getStringExtra("waktu");
        final Date currentTime = Calendar.getInstance().getTime();

        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        assert contact != null;
        submitbtn=(Button)findViewById(R.id.submittemp);
        ((TextView)findViewById(R.id.activity_details_name))
                .setText(contact.getLastName());
        ((TextView)findViewById(R.id.activity_details_landline))
                .setText(contact.getLandline());
        ((TextView)findViewById(R.id.date))
                .setText(df2.format(currentTime));
        ((TextView)findViewById(R.id.waktu))
                .setText(waktu);
        ((TextView)findViewById(R.id.activity_details_mobile))
                .setText(contact.getMobile());
        builder = new AlertDialog.Builder(DetailsActivity.this);
        // Set up the input

        


// Set up the buttons
        builder.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Button in=findViewById(R.id.submittemp);
                final TextView porsi=findViewById(R.id.porsi);
                in.setEnabled(true);
                jumlah = input.getText().toString();
                porsi.setText(jumlah);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setTitle("Masukkan Jumlah Porsi");
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
                DatabaseReference hopperRef = table_konsumsitemp.child(""+Common.currentUser.getId()+"x"+df.format(currentTime)+"x"+waktu);
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put(contact.getFirstName(), jumlah+"x"+jam);
                hopperRef.updateChildren(hopperUpdates);
                Toast.makeText(DetailsActivity.this,contact.getLastName()+" Sejumlah "+jumlah+" telah ditambahkan", Toast.LENGTH_SHORT).show();
                Intent DetailIntent=new Intent(DetailsActivity.this,HariiniActivity.class);
                startActivity(DetailIntent);
            }
        });
    }
    public void onClockInput(View v){
        Calendar rightNow = Calendar.getInstance();
        final TextView jaam=findViewById(R.id.jam);
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        CustomTimePickerDialog time=new CustomTimePickerDialog(DetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Log.e("jam",""+i);
                Log.e("minute",""+i1);
                jam=String.format("%02d:%02d", i, i1);
                jaam.setText(String.format("%02d:%02d", i, i1));
            }
        }, currentHourIn24Format,0, true);
        time.show();
    }
    public void onPorsiInput(View v){
        input = new EditText(DetailsActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        input.setHint("Porsi");

        builder.setView(input);
        builder.show();
    }
}
