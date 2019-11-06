package com.lidapp.kaloriq;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lidapp.kaloriq.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone,edtName,edtPassword;
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edtName=(MaterialEditText) findViewById(R.id.edtName);
        edtPhone=(MaterialEditText) findViewById(R.id.edtPhone);
        edtPassword=(MaterialEditText) findViewById(R.id.edtPassword);

        btnSignUp=(Button)findViewById(R.id.btnSignUp);

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mdialog = new ProgressDialog(SignUp.this);
                mdialog.setMessage("Please waiting....");
                mdialog.show();
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check if user exist
                        if(dataSnapshot.child(encodeString(edtPhone.getText().toString())).exists()){
                            mdialog.dismiss();
                            Toast.makeText(SignUp.this,"Nomor Hp/Email Sudah Terdaftar", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mdialog.dismiss();
                            User user = new User(edtName.getText().toString(),edtPassword.getText().toString(),encodeString(edtPhone.getText().toString()));
                            table_user.child(encodeString(edtPhone.getText().toString())).setValue(user);
                            Toast.makeText(SignUp.this,"Pendaftaran Sukses", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    public static String encodeString(String string) {
        return string.replace(".", ",");
    }

    public static String decodeString(String string) {
        return string.replace(",", ".");
    }
}
