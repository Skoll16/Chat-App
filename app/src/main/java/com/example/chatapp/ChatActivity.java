package com.example.chatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String messagereceiverID,messageReceiverName,mesaagerecevierimage,messagesenderID;
   TextView userName,userLastSeen;
   CircleImageView userImage;
   Toolbar chatToolbar;
   ImageButton sendmsgBtn;
   private RecyclerView userMessageList;
   EditText msgInput;
   FirebaseAuth mAuth;
   DatabaseReference RootRef;
   private List<Messages> msglist=new ArrayList<>();
   LinearLayoutManager linearLayoutManager;
   MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messagereceiverID=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
        mesaagerecevierimage=getIntent().getExtras().get("visit_user_image").toString();
        mAuth=FirebaseAuth.getInstance();
        messagesenderID=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
      
        userName=(TextView)findViewById(R.id.custom_profile_name);
        userLastSeen=(TextView)findViewById(R.id.custom_profile_lastseen);
        //userImage=(CircleImageView)findViewById(R.id.custom_profile_image);
        userName.setText(messageReceiverName);
        //Picasso.get().load(mesaagerecevierimage).placeholder(R.drawable.profile_image).into(userImage);
        msgInput=(EditText)findViewById(R.id.input_msg12);
        sendmsgBtn=(ImageButton)findViewById(R.id.send_pvt_msg_btn);
        messageAdapter=new MessageAdapter(msglist);
        userMessageList=(RecyclerView)findViewById(R.id.private_msg);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);

        sendmsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMSg();
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        RootRef.child("Messages").child(messagesenderID).child(messagereceiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                msglist.add(messages);
                messageAdapter.notifyDataSetChanged();

                userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());//scroll to latest msg
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMSg(){

        String msgTxt=msgInput.getText().toString();
        if(TextUtils.isEmpty(msgTxt))
        {
            Toast.makeText(this, "Enter a msg first!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String msgsenderRef="Messages/" + messagesenderID + "/" + messagereceiverID;
            String msgreceiverRef="Messages/" + messagereceiverID + "/" + messagesenderID;

            DatabaseReference userMsgKEyRef=RootRef.child("Messages").child(messagesenderID).child(messagereceiverID).push();
            String messagePushID=userMsgKEyRef.getKey();

            Map msgtxtbody=new HashMap();
            msgtxtbody.put("message",msgTxt);
            msgtxtbody.put("type","text");
            msgtxtbody.put("from",messagesenderID);

            Map messageDetail=new HashMap();
            messageDetail.put(msgsenderRef + "/" + messagePushID, msgtxtbody);
            messageDetail.put(msgreceiverRef + "/" + messagePushID, msgtxtbody);

        RootRef.updateChildren(messageDetail).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {

                }
                else
                {
                    Toast.makeText(ChatActivity.this, "Error: Sending msg failed!!", Toast.LENGTH_SHORT).show();
                }
                msgInput.setText("");
            }
        });
        }

    }
}
