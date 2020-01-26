package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private Button sendverifiactioncode,verifybutton;
    private EditText inputPhone, inputVerficationCode;
    private ProgressDialog Loading;
    private String mVerificationId;
    private ImageView phoneicon;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_phone_login);
     mAuth=FirebaseAuth.getInstance();
    sendverifiactioncode=(Button)findViewById(R.id.phone_otp_generate_btn);
        verifybutton=(Button)findViewById(R.id.verify_phone_otp_generate_btn);
        inputPhone=(EditText)findViewById(R.id.input_phone);
        phoneicon=(ImageView)findViewById(R.id.phone_icon);
        inputVerficationCode=(EditText)findViewById(R.id.verification_code);
        Loading=new ProgressDialog(this);

         sendverifiactioncode.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String phoneno=inputPhone.getText().toString();
                 if(TextUtils.isEmpty(phoneno))
                 {
                     Toast.makeText(PhoneLoginActivity.this, "Please enter your phone number!!", Toast.LENGTH_SHORT).show();
                 }
                 else
                 {   Loading.setTitle("Phone Verifcation");
                      Loading.setMessage("Please wait, while we are authenticating your phone number");
                     Loading.setCanceledOnTouchOutside(false);
                      Loading.show();
                      PhoneAuthProvider.getInstance().verifyPhoneNumber(
                             phoneno,        // Phone number to verify
                             60,                 // Timeout duration
                             TimeUnit.SECONDS,   // Unit of timeout
                             PhoneLoginActivity.this,               // Activity (for callback binding)
                             callbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java

                 }
             }
         });
         verifybutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String veriycode=inputVerficationCode.getText().toString();
                 if(TextUtils.isEmpty(veriycode))
                 {
                     Toast.makeText(PhoneLoginActivity.this, "Enter the code ", Toast.LENGTH_SHORT).show();
                 }
                 else
                 {
                     Loading.setTitle("Code Verifcation");
                     Loading.setMessage("Please wait, while we are verifying code your phone number");
                     Loading.setCanceledOnTouchOutside(false);
                     Loading.show();
                     PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, veriycode);
                      signInWithPhoneAuthCredential(credential);

                 }
             }
         });
         callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
             @Override
             public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                               signInWithPhoneAuthCredential(phoneAuthCredential);
             }

             @Override
             public void onVerificationFailed(FirebaseException e) {
                 Loading.dismiss();
                 Toast.makeText(PhoneLoginActivity.this, "" +e, Toast.LENGTH_LONG).show();

                 sendverifiactioncode.setVisibility(View.VISIBLE);
                 inputPhone.setVisibility(View.VISIBLE);
                   phoneicon.setVisibility(View.VISIBLE);

                 verifybutton.setVisibility(View.INVISIBLE);
                 inputVerficationCode.setVisibility(View.INVISIBLE);

             }



             public void onCodeSent(String verificationId,
                                    PhoneAuthProvider.ForceResendingToken token) {
                 // The SMS verification code has been sent to the provided phone number, we
                 // now need to ask the user to enter the code and then construct a credential
                 // by combining the code with a verification ID.


                 // Save verification ID and resending token so we can use them later

                 mVerificationId = verificationId;
                 mResendToken = token;
                 Loading.dismiss();
                 sendverifiactioncode.setVisibility(View.INVISIBLE);
                 inputPhone.setVisibility(View.INVISIBLE);
                 phoneicon.setVisibility(View.INVISIBLE);


                 verifybutton.setVisibility(View.VISIBLE);
                 inputVerficationCode.setVisibility(View.VISIBLE);

                 Toast.makeText(PhoneLoginActivity.this, "Verification code has been sent", Toast.LENGTH_SHORT).show();
             }
         };



    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                                 Loading.dismiss();
                            Intent intent=new Intent(PhoneLoginActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}
