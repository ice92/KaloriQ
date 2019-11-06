package com.lidapp.kaloriq;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class ChangePass extends AppCompatActivity {
    EditText oldpass,newpass,newpass2;
    Button submit;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        oldpass=findViewById(R.id.oldpass);
        newpass=findViewById(R.id.newpass);
        newpass2=findViewById(R.id.confirm);
        submit=findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog mdialog = new ProgressDialog(ChangePass.this);
                mdialog.setMessage("Please waiting....");
                mdialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //get User Information
                            mdialog.dismiss();
                            User user=dataSnapshot.child(Common.currentUser.getId().toString()).getValue(User.class);
                            if(user.getPassword().equals(oldpass.getText().toString())){
                                if(newpass.getText().toString().equals(newpass2.getText().toString())) {
                                    DatabaseReference readref = table_user.child(Common.currentUser.getId().toString());
                                    Map<String, Object> hopperUpdates = new HashMap<>();
                                    hopperUpdates.put("password",newpass.getText().toString());
                                    readref.updateChildren(hopperUpdates);
                                    Toast.makeText(ChangePass.this,"Password sukses diubah !", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else{
                                    Toast.makeText(ChangePass.this,"Konfirmasi Password tidak cocok !", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(ChangePass.this,"Password lama Salah !", Toast.LENGTH_SHORT).show();
                            }
                        }                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ChangePass.this,"Tidak ada koneksi, Mohon periksa koneksi internet anda !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
