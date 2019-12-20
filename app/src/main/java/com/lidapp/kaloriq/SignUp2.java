package com.lidapp.kaloriq;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tooltip.Tooltip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUp2 extends AppCompatActivity {

    MaterialEditText edtPhone,edtTb,edtBb,edtTl;
    Button btnSignUp,imtbt;
    RadioGroup rg,sex;
    TextView imtview;
    final Calendar myCalendar=Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        edtPhone= findViewById(R.id.edtNohp);
        edtTb=findViewById(R.id.edtTinggi);
        edtBb=findViewById(R.id.edtBb);
        edtTl= findViewById(R.id.edtTl);
        final DatePickerDialog.OnDateSetListener date=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        edtTl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SignUp2.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        imtview=findViewById(R.id.imt);
        imtbt=findViewById(R.id.imtbt);
        imtview.setVisibility(View.GONE);
        imtbt.setVisibility(View.GONE);
        rg=findViewById(R.id.rgsu);
        sex=findViewById(R.id.kelamin);
        btnSignUp=findViewById(R.id.btnSignUp);




        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mdialog = new ProgressDialog(SignUp2.this);
                mdialog.setMessage("Please waiting....");
                mdialog.show();
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            mdialog.dismiss();
//                            table_user.child(Common.currentUser.getId()).setValue("Phone",edtPhone.getText());
//                        table_user.child(Common.currentUser.getId()).setValue("Tinggi",edtTb.getText());
//                        table_user.child(Common.currentUser.getId()).setValue("Berat",edtBb.getText());
//                        table_user.child(Common.currentUser.getId()).setValue("TL",edtTl.getText());

                        DatabaseReference hopperRef = table_user.child(Common.currentUser.getId().toString());
                        Map<String, Object> hopperUpdates = new HashMap<>();
                        Log.e("ISI GAES", Common.currentUser.getId().toString()+edtPhone.getText().toString()+edtTb.getText().toString()+edtBb.getText()+"TL"+edtTl.getText().toString());
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        final SharedPreferences.Editor editor = pref.edit();

                        hopperUpdates.put("Phone",edtPhone.getText().toString());
                        hopperUpdates.put("Tinggi",edtTb.getText().toString());
                        hopperUpdates.put("Berat",edtBb.getText().toString());
                        hopperUpdates.put("TL",edtTl.getText().toString());
                        double berat=new Float(edtBb.getText().toString());
                        double tinggi= new Float(edtTb.getText().toString());
                        double tingg2=(double)Math.pow(tinggi,2)/10000;
                        double imt=(double) berat/(double)tingg2;
                        double cal;
                        if((int) sex.getCheckedRadioButtonId()==2131230918) {
                            if(tinggi<160){
                                cal = (tinggi - 100)*30;
                            }
                            else {

                                cal = (tinggi - 100)*0.9*30;
                            }
                        }
                        else{
                            if(tinggi<150){
                                cal = (tinggi - 100)*25;
                            }
                            else {

                                cal = (tinggi - 100)*0.9*25;
                            }
                        }
                        editor.putFloat("cal",(float) cal);
                        editor.putFloat("bb",(float)berat);
                        editor.commit();
                        Log.e("IMT",""+Math.pow(tinggi,2)+","+imt);
                        hopperUpdates.put("IMT",imt);
                        hopperUpdates.put("CAL",cal);
                        hopperUpdates.put("DB",rg.getCheckedRadioButtonId());
                        hopperUpdates.put("SEX",sex.getCheckedRadioButtonId());
                        hopperRef.updateChildren(hopperUpdates);
                        Toast.makeText(SignUp2.this,"Data Sukses Ditambahkan", Toast.LENGTH_SHORT).show();
                        Intent homeIntent=new Intent(SignUp2.this,Home.class);
                        startActivity(homeIntent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    private void updateLabel(){
        String myFormat="dd-MM-yyyy";
        SimpleDateFormat sdf=new SimpleDateFormat(myFormat, Locale.US);
        edtTl.setText(sdf.format(myCalendar.getTime()));
    }
    public void onClickTooltip(View v){
        showTooltip(v, Gravity.TOP);
    }

    public void showTooltip(View v, int gravity) {
        Button btn=(Button) v;
        new Tooltip.Builder(btn).setText("Indeks Massa Tubuh")
                .setTextColor(Color.BLACK)
                .setBackgroundColor(Color.WHITE)
                .setGravity(gravity)
                .setCornerRadius(8f)
                .setDismissOnClick(true)
                .show();
    }
}
