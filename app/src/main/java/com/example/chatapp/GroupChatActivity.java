package com.example.chatapp;

import android.content.Intent;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.ToLongBiFunction;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton sendMessage;
    private EditText usermsg;
    private ScrollView scrollView;
    private TextView displaymsg;
    private String currentgrpName,CurrentuserID,CurrentuserName,currentdate,currenttime;
    private FirebaseAuth mAuth;
    private DatabaseReference grpref,grpNameRef,grpMessageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentgrpName=getIntent().getExtras().get("groupName").toString();
        mAuth=FirebaseAuth.getInstance();
        CurrentuserID=mAuth.getCurrentUser().getUid();
        grpref=FirebaseDatabase.getInstance().getReference().child("Users");
        grpNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentgrpName);
        setContentView(R.layout.activity_group_chat);

        mToolbar=(Toolbar)findViewById(R.id.grpchatlayout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentgrpName);
        sendMessage=(ImageButton)findViewById(R.id.send_msg_grp_btn);
        usermsg=(EditText)findViewById(R.id.grp_message);
        scrollView=(ScrollView)findViewById(R.id.grp_scroll);
        displaymsg=(TextView)findViewById(R.id.grpchat_txt_display);




        getUserInfo();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=usermsg.getText().toString();
                String msgkey=grpNameRef.push().getKey();
                if(TextUtils.isEmpty(message))
                {
                    Toast.makeText(GroupChatActivity.this, "Enter a message", Toast.LENGTH_SHORT).show();
                }
                else {
                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat currentDateformat=new SimpleDateFormat("MMM dd,yyyy");
                    currentdate=currentDateformat.format(calendar.getTime());
                    SimpleDateFormat currentTimeformat=new SimpleDateFormat("hh:mm:ss a");
                    currenttime=currentTimeformat.format(calendar.getTime());

                    /*HashMap<String,Object> hashMap=new HashMap<>();
                    grpMessageRef.updateChildren(hashMap);*/
                    grpMessageRef=grpNameRef.child(msgkey);

                    HashMap<String,Object> msginfo=new HashMap<>();
                    msginfo.put("name",CurrentuserName);
                    msginfo.put("message",message);
                    msginfo.put("date",currentdate);
                    msginfo.put("time",currenttime);
                    grpMessageRef.updateChildren(msginfo);


                }
                usermsg.setText("");
              scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    private void getUserInfo() {

    grpref.child(CurrentuserID).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            {
                CurrentuserName=dataSnapshot.child("name").getValue().toString();

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

    @Override
    protected void onStart() {
        super.onStart();

    grpNameRef.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(dataSnapshot.exists())
           {
              DisplayMsg(dataSnapshot);
           }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(dataSnapshot.exists())
            {
                DisplayMsg(dataSnapshot);
            }
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

    private void DisplayMsg(DataSnapshot dataSnapshot) {

        Iterator iterator=dataSnapshot.getChildren().iterator();
        while (iterator.hasNext())
        {
           String chatdate=(String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String)((DataSnapshot)iterator.next()).getValue();
            String chatName=(String)((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String)((DataSnapshot)iterator.next()).getValue();

            displaymsg.append(chatName + ":\n" + chatMessage +"\n" +chatdate + "   " +chatTime +"\n\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);


        }

    }

}
