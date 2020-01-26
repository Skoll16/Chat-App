package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateSettings;
    private EditText userName,userAbout;
    private CircleImageView userProfileImage;
    private ProgressDialog Loading;
    private String currentuserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private static final int jkl=1;
    private Uri imgUri;
    String myUrl="";
    private StorageTask uploadTask;

    private StorageReference userProfileImagesRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.keepSynced(true);
        currentuserID=mAuth.getCurrentUser().getUid();
        Loading= new ProgressDialog(this);

        UpdateSettings=(Button)findViewById(R.id.settings_update_btn);
        userName=(EditText)findViewById(R.id.set_user_name);
        userAbout=(EditText)findViewById(R.id.about_user);
        userProfileImage=(CircleImageView)findViewById(R.id.profile_image);
        userProfileImagesRef=FirebaseStorage.getInstance().getReference().child("Profile Images");

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(imgUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });

        UpdateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSetting();
            }
        });

        RetriveUserInfo();


    }

    private void RetriveUserInfo() {
    rootRef.child("Users").child(currentuserID).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            /*if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))&&(dataSnapshot.hasChild("about"))
                    &&(dataSnapshot.hasChild("images")))
            {  String username=dataSnapshot.child("name").getValue().toString();
                String userabout=dataSnapshot.child("about").getValue().toString();
                String userimage=dataSnapshot.child("images").getValue().toString();
                Picasso.get().load(userimage).into(userProfileImage);
                userName.setText(username);
                userAbout.setText(userabout);

            }
            else if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))){
                String username=dataSnapshot.child("name").getValue().toString();
                String userabout=dataSnapshot.child("about").getValue().toString();
                userName.setText(username);
                userAbout.setText(userabout);
            }*/
            if (dataSnapshot.exists())
            {
                if (dataSnapshot.child("image").exists()&&dataSnapshot.child("name").exists()&&dataSnapshot.child("about").exists())
                {
                    final String image = dataSnapshot.child("image").getValue().toString();
                    String username=dataSnapshot.child("name").getValue().toString();
                    String userabout=dataSnapshot.child("about").getValue().toString();
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(userProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).into(userProfileImage);
                        }
                    });

                    userName.setText(username);
                    userAbout.setText(userabout);


                }
                else if(dataSnapshot.child("name").exists()&&dataSnapshot.child("about").exists()){
                    String username=dataSnapshot.child("name").getValue().toString();
                    String userabout=dataSnapshot.child("about").getValue().toString();
                    userName.setText(username);
                    userAbout.setText(userabout);


                }

           }
            else
            {
                Toast.makeText(SettingsActivity.this, "Update your account info!!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    private void UpdateSetting() {
    String name=userName.getText().toString();
    String about=userAbout.getText().toString();
    if(TextUtils.isEmpty(name))
    {
        Toast.makeText(this, "Write the User Name", Toast.LENGTH_SHORT).show();
    }
    else if(TextUtils.isEmpty(about))
    {
        Toast.makeText(this, "Please fill About field", Toast.LENGTH_SHORT).show();
    }
    else
    {
        Loading.setTitle("Updating Account");
        Loading.setMessage("Please Wait");
        Loading.setCanceledOnTouchOutside(true);
        Loading.show();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid",currentuserID);
        hashMap.put("name",name);
        hashMap.put("about",about);
        rootRef.child("Users").child(currentuserID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
             if(task.isSuccessful())
             {
                Loading.dismiss();
                 Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);
                 finish();
             }
             else
             {
                 String msg=task.getException().toString();
                 Toast.makeText(SettingsActivity.this, "" +msg, Toast.LENGTH_LONG).show();
             }

            }
        });

    }
    uploadImage();
    }

    private void uploadImage() {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        if(imgUri!=null)
        {
             final StorageReference fileref=userProfileImagesRef.child(currentuserID+ ".jpg");
             uploadTask=fileref.putFile(imgUri);

             uploadTask.continueWithTask(new Continuation() {
                 @Override
                 public Object then(@NonNull Task task) throws Exception {
                     if (!task.isSuccessful())
                     {
                         throw task.getException();
                     }

                     return fileref.getDownloadUrl();
                 }
             }).addOnCompleteListener(new OnCompleteListener<  Uri>() {
                 @Override
                 public void onComplete(@NonNull Task<Uri> task)
                 {
                     if (task.isSuccessful())
                     {
                         Uri downloadUrl = task.getResult();
                         myUrl = downloadUrl.toString();

                         DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                         HashMap<String, Object> userMap = new HashMap<>();

                         userMap. put("image", myUrl);
                         ref.child(currentuserID).updateChildren(userMap);

                         progressDialog.dismiss();

                                    finish();
                     }
                     else
                     {
                         progressDialog.dismiss();
                         Toast.makeText(SettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                     }
                 }
             });
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgUri = result.getUri();

            userProfileImage.setImageURI(imgUri);
            uploadImage();
        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

                   finish();
        }
    }


}
