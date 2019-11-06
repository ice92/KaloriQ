package com.lidapp.kaloriq;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lidapp.kaloriq.Common.Common;
import com.lidapp.kaloriq.Model.User;
import com.lidapp.kaloriq.Model.User2;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {
    EditText edtPhone,edtPassword;
    Button btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword= (MaterialEditText) findViewById(R.id.edtPassword);
        edtPhone=(MaterialEditText) findViewById(R.id.edtPhone);

        btnSignIn=(Button) findViewById(R.id.btnSignIn);

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog mdialog = new ProgressDialog(SignIn.this);
                mdialog.setMessage("Please waiting....");
                mdialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check if user not exist in database
                        if(dataSnapshot.child(SignUp.encodeString(edtPhone.getText().toString())).exists()){
                        //get User Information
                        mdialog.dismiss();
                        User user=dataSnapshot.child(SignUp.encodeString(edtPhone.getText().toString())).getValue(User.class);
                        if(user.getPassword().equals(edtPassword.getText().toString())){
                            editor.putBoolean("islogin", true); // Storing boolean - true/false
                            editor.putBoolean("isfast", true);
                            editor.putString("username", user.getName());
                            editor.putString("id",user.getId());
                            editor.commit();



                            Common.currentUser=user;
                            isProfiled();
                        }
                        else{
                            Toast.makeText(SignIn.this,"Password Salah !", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        mdialog.dismiss();
                        Toast.makeText(SignIn.this,"Pengguna belum terdaftar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    public void isProfiled() {

        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");
        DatabaseReference readref = table_user.child(Common.currentUser.getId().toString());
        readref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User2 user=dataSnapshot.getValue(User2.class);
                try{
                if((user.SEX==1)){
                    Intent homeIntent=new Intent(SignIn.this,SignUp2.class);
                    startActivity(homeIntent);
                    finish();
                }
                else{
                    Intent homeIntent=new Intent(SignIn.this,Home.class);
                    startActivity(homeIntent);
                    finish();
                }
                }
                catch (NullPointerException e){
                    Intent homeIntent=new Intent(SignIn.this,SignUp2.class);
                    startActivity(homeIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }
}
