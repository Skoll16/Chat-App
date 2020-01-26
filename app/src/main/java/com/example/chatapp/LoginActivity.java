package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton,PhoneLogin,SignUpButton;
    private EditText userEmail,userPassword;
    private TextView forgetPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog Loading;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton=(Button)findViewById(R.id.login_btn);
        PhoneLogin=(Button)findViewById(R.id.phone_login);
        SignUpButton=(Button)findViewById(R.id.signup_btn);
        mAuth=FirebaseAuth.getInstance();
               userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        userEmail=(EditText)findViewById(R.id.email);
        userPassword=(EditText)findViewById(R.id.password);
        forgetPassword=(TextView)findViewById(R.id.forgetpassword);
        Loading=new ProgressDialog(this);

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

        PhoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(intent);

            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=userEmail.getText().toString();
                String password=userPassword.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(LoginActivity.this, "Please enter an email!", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginActivity.this, "Please enter an password!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Loading.setTitle("Logging In");
                    Loading.setMessage("Please Wait..");
                    Loading.show();
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful())
                           {  String CurrentUserID=mAuth.getCurrentUser().getUid();
                                   String devicetoken= FirebaseInstanceId.getInstance().getToken();
                                   userRef.child(CurrentUserID).child("device_token").setValue(devicetoken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                          if(task.isSuccessful())
                                          {Loading.dismiss();
                                              Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                              startActivity(intent);
                                              finish();

                                          }
                                       }
                                   });


                           }
                           else
                           {Loading.dismiss();
                               String msg=task.getException().toString();
                               Toast.makeText(LoginActivity.this, ""+msg, Toast.LENGTH_LONG).show();
                               Intent intent =new Intent(LoginActivity.this,LoginActivity.class);
                               startActivity(intent);
                           }

                        }
                    });
                }
            }
        });

    }
}
