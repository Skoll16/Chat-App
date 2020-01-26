package com.example.chatapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
   private String recevieruserid,CurrentState,SenderUserID;
   private CircleImageView userProfileImage;
   private TextView userProfileName,userProfileStatus;
   private DatabaseReference userRef,chatReqRef,contactRef,NotifyRef;
   private FirebaseAuth mAuth;
   private Button sendMessageRequest,cancelReqBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        recevieruserid=getIntent().getExtras().get("user_id_click").toString();
        mAuth=FirebaseAuth.getInstance();
        SenderUserID=mAuth.getCurrentUser().getUid();
        chatReqRef=FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
         userProfileImage=(CircleImageView)findViewById(R.id.visit_profile_image);
        contactRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotifyRef=FirebaseDatabase.getInstance().getReference().child("Notifications");
        userProfileName=(TextView) findViewById(R.id.visit_user_name);
        userProfileStatus=(TextView) findViewById(R.id.visit_user_about);
        cancelReqBtn=(Button)findViewById(R.id.cancel_message_req);
        CurrentState="new";
           sendMessageRequest=(Button)findViewById(R.id.send_message_req);
           RetriveUserInfo();

    }

    private void RetriveUserInfo() {
    userRef.child(recevieruserid).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         if(dataSnapshot.exists()&&dataSnapshot.child("image").exists())
         { String image=dataSnapshot.child("image").getValue().toString();
             String name=dataSnapshot.child("name").getValue().toString();
             String status=dataSnapshot.child("about").getValue().toString();
             Picasso.get().load(image).placeholder(R.drawable.profile_image).into(userProfileImage);
              userProfileName.setText(name);
              userProfileStatus.setText(status);

              chatRequest();
         }
         else{
             String name=dataSnapshot.child("name").getValue().toString();
             String status=dataSnapshot.child("about").getValue().toString();
             userProfileName.setText(name);
             userProfileStatus.setText(status);
             chatRequest();

         }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    private void chatRequest() {

        chatReqRef.child(SenderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(recevieruserid).exists())
                {
                    String request_type=dataSnapshot.child(recevieruserid).child("request_type")
                            .getValue().toString();
                    if(request_type.equals("sent"))
                    {
                        CurrentState="request_sent";
                        sendMessageRequest.setText("Cancel Request");
                    }
                    else if(request_type.equals("received"))
                    {
                        CurrentState="request_received";
                        sendMessageRequest.setText("Accept Request");
                        cancelReqBtn.setVisibility(View.VISIBLE);
                        cancelReqBtn.setEnabled(true);
                        cancelReqBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CancleChatReq();

                            }
                        });
                    }

                }
                else
                {
                    contactRef.child(SenderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child(recevieruserid).exists())
                            { CurrentState="friends";
                            sendMessageRequest.setText("Remove this Contact");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
      if(!SenderUserID.equals(recevieruserid))
      {
          sendMessageRequest.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  sendMessageRequest.setEnabled(false);

                  if(CurrentState.equals("new"))
                  {
                      sendChatReq();
                  }
                  if(CurrentState.equals("request_sent"))
                  {
                      CancleChatReq();
                  }

                  if(CurrentState.equals("request_received"))
                  {
                      AcceptReq();
                  }
                  if(CurrentState.equals("friends"))
                  {
                      RemoveContact();
                  }

              }
          });
      }
      else
      {
          sendMessageRequest.setVisibility(View.INVISIBLE);
      }



    }

    private void RemoveContact() {
        contactRef.child(SenderUserID).child(recevieruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    contactRef.child(recevieruserid).child(SenderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                sendMessageRequest.setEnabled(true);
                                CurrentState="new";
                                sendMessageRequest.setText("Send Request");
                                cancelReqBtn.setVisibility(View.INVISIBLE);
                                cancelReqBtn.setEnabled(false);
                            }

                        }
                    });
                }

            }
        });




    }

    private void AcceptReq() {
        contactRef.child(SenderUserID).child(recevieruserid).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    contactRef.child(recevieruserid).child(SenderUserID).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                chatReqRef.child(SenderUserID).child(recevieruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    chatReqRef.child(recevieruserid).child(SenderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            sendMessageRequest.setEnabled(true);
                                            CurrentState="friends";
                                            sendMessageRequest.setText("Remove this Contact");
                                            cancelReqBtn.setVisibility(View.INVISIBLE);
                                            cancelReqBtn.setEnabled(false);

                                        }
                                    });


                                    }
                                });

                            }

                        }
                    });

                }

            }
        });


    }

    private void CancleChatReq() {

        chatReqRef.child(SenderUserID).child(recevieruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    chatReqRef.child(recevieruserid).child(SenderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                sendMessageRequest.setEnabled(true);
                                CurrentState="new";
                                sendMessageRequest.setText("Send Request");
                                cancelReqBtn.setVisibility(View.INVISIBLE);
                                cancelReqBtn.setEnabled(false);
                            }

                        }
                    });
                }

            }
        });


    }

    private void sendChatReq() {
    chatReqRef.child(SenderUserID).child(recevieruserid).child("request_type").setValue("sent")
    .addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful())
            {
                chatReqRef.child(recevieruserid).child(SenderUserID).child("request_type").setValue("received")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    HashMap<String,String> chatNotification=new HashMap<>();
                                    chatNotification.put("from",SenderUserID);
                                    chatNotification.put("type","request");
                                     NotifyRef.child(recevieruserid).push().setValue(chatNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {       sendMessageRequest.setEnabled(true);
                                                    CurrentState="request_sent";
                                                    sendMessageRequest.setText("Cancel Request");

                                                }
                                         }
                                     });


                                 }

                            }
                        });
            }
        }
    });


    }
}
