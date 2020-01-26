package com.example.chatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    private Button RegisterButton;
    private EditText RegisterEmail,RegisterPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog Loading;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        RegisterEmail=(EditText)findViewById(R.id.register_email);
        RegisterPassword=(EditText)findViewById(R.id.register_password);
        RegisterButton=(Button)findViewById(R.id.register_btn);
             Loading= new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        rootRef=FirebaseDatabase.getInstance().getReference();
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();

            }
        });
    }

    private void CreateAccount() {
     String email=RegisterEmail.getText().toString();
        String password=RegisterPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter an email!", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter an password!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Loading.setTitle("Creating Account");
            Loading.setMessage("Please Wait");
            Loading.setCanceledOnTouchOutside(true);
            Loading.show();
                 mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        String devicetoken= FirebaseInstanceId.getInstance().getToken();
                        String currentUserID=mAuth.getCurrentUser().getUid();
                        rootRef.child("Users").child(currentUserID).setValue("");

                        rootRef.child("Users").child(currentUserID).child("device_token").setValue(devicetoken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    Loading.dismiss();
                                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }

                            }
                        });

                       // Toast.makeText(RegisterActivity.this, "Account Created Successfully!!", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {    String msg=task.getException().toString();
                        Toast.makeText(RegisterActivity.this, ""+ msg, Toast.LENGTH_LONG).show();
                        Loading.dismiss();

                    }


                     }
                 });
        }
    }


}
