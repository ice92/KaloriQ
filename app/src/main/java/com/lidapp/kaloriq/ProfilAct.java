package com.lidapp.kaloriq;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
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
import com.lidapp.kaloriq.Graph.CombinedChartActivity;
import com.lidapp.kaloriq.Model.User;
import com.lidapp.kaloriq.Model.User2;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tooltip.Tooltip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfilAct extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    MaterialEditText edtPhone,edtTb,edtBb,edtTl;
    TextView imtview;
    Button btnSignUp,chaButton;
    RadioGroup rg,sex;
    final Calendar myCalendar=Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();
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
                new DatePickerDialog(ProfilAct.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        imtview=findViewById(R.id.imt);
        rg=findViewById(R.id.rgsu);
        sex=findViewById(R.id.kelamin);
        btnSignUp=findViewById(R.id.btnSignUp);
        chaButton=findViewById(R.id.chngepass);
        chaButton.setVisibility(View.VISIBLE);
        chaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCP();
            }
        });
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");
        DatabaseReference readref = table_user.child(Common.currentUser.getId().toString());
        readref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User2 user=dataSnapshot.getValue(User2.class);
                String tgl=user.TL;
                edtPhone.setText(user.Phone);
                edtTb.setText(user.Tinggi);
                edtBb.setText(user.Berat);
                edtTl.setText(tgl);
                rg.check(user.DB);
                sex.check(user.SEX);
                imtview.setText("IMT : "+(int)user.IMT.doubleValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });



        btnSignUp.setText("EDIT");
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mdialog = new ProgressDialog(ProfilAct.this);
                mdialog.setMessage("Please waiting....");
                mdialog.show();
                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mdialog.dismiss();
                        DatabaseReference hopperRef = table_user.child(Common.currentUser.getId().toString());
                        Map<String, Object> hopperUpdates = new HashMap<>();
                        Log.e("ISI GAES", Common.currentUser.getId().toString()+edtPhone.getText().toString()+edtTb.getText().toString()+edtBb.getText()+"TL"+edtTl.getText().toString());
                        hopperUpdates.put("Phone",edtPhone.getText().toString());
                        hopperUpdates.put("Tinggi",edtTb.getText().toString());
                        hopperUpdates.put("Berat",edtBb.getText().toString());
                        String tgl=edtTl.getText().toString().replace("-","");
                        hopperUpdates.put("TL",tgl);
                        double berat=new Float(edtBb.getText().toString());
                        double tinggi= new Float(edtTb.getText().toString());
                        double tingg2=(double)Math.pow(tinggi,2)/10000;
                        double imt=(double) berat/(double)tingg2;
                        hopperUpdates.put("DB",rg.getCheckedRadioButtonId());
                        hopperUpdates.put("SEX",sex.getCheckedRadioButtonId());
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
                        hopperRef.updateChildren(hopperUpdates);
                        Toast.makeText(ProfilAct.this,"Data Sukses Diubah", Toast.LENGTH_SHORT).show();
                        Intent homeIntent=new Intent(ProfilAct.this,Home.class);
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
    public void onClickCP(){
        Intent homeIntent=new Intent(ProfilAct.this,ChangePass.class);
        startActivity(homeIntent);
        finish();
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            Intent homeIntent=new Intent(ProfilAct.this,Home.class);
            startActivity(homeIntent);

        } else if (id == R.id.nav_cart) {
            Intent DetailIntent=new Intent(ProfilAct.this,HariiniActivity.class);
            startActivity(DetailIntent);

        }  else if (id == R.id.nav_profil) {
            Intent DetailIntent=new Intent(ProfilAct.this,ProfilAct.class);
            startActivity(DetailIntent);

        }else if (id == R.id.nav_act) {
            Intent DetailIntent=new Intent(ProfilAct.this,HariIniAct.class);
            startActivity(DetailIntent);

        }else if (id == R.id.nav_gd) {
            Intent DetailIntent=new Intent(ProfilAct.this,HariIniGD.class);
            startActivity(DetailIntent);

        } else if (id == R.id.nav_ramadhan) {
            if(pref.getBoolean("isfast",true)) {
                editor.putBoolean("isfast", false);
                editor.commit();
//                item.setIcon(R.drawable.ic_action_settings);
                Toast.makeText(ProfilAct.this,"Mode Ramadhan dinonaktifkan",Toast.LENGTH_LONG).show();
            }
            else{
                editor.putBoolean("isfast", true);
                editor.commit();
//              item.setIcon(R.drawable.ic_action_save);
                Toast.makeText(ProfilAct.this,"Mode Ramadhan diaktifkan",Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_logout) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ProfilAct.this);

// Set up the buttons
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    Intent homeIntent=new Intent(ProfilAct.this,MainActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setTitle("Apakah anda yakin akan keluar?");
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
