package com.example.chatapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
     private List<Messages> usermsg;
     private FirebaseAuth mAuth;
     private DatabaseReference usersRef;
     public MessageAdapter (List<Messages> usermsg){

         this.usermsg=usermsg;
     }
    public class  MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView sendermsgText, ReceviermsgText;
        //CircleImageView receiverProfileimg;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            sendermsgText = (TextView) itemView.findViewById(R.id.sender_msg_txt);
            ReceviermsgText = (TextView) itemView.findViewById(R.id.receiver_msg_txt);
       
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

         View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_msg_layout,viewGroup,false);
          mAuth=FirebaseAuth.getInstance();


        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {
         String senderID=mAuth.getCurrentUser().getUid();
         Messages messages=usermsg.get(i);
         String fromUserID=messages.getFrom();
         String FromMessagetype=messages.getType();
         usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
         usersRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.child("image").exists())
            {
                String Receiverimage=dataSnapshot.child("image").getValue().toString();
          
            }


             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
         if(FromMessagetype.equals("text"))
         {messageViewHolder.sendermsgText.setVisibility(View.INVISIBLE);
           
             messageViewHolder.ReceviermsgText.setVisibility(View.INVISIBLE);
             if(fromUserID.equals(senderID))
             {    messageViewHolder.sendermsgText.setVisibility(View.VISIBLE);
                 messageViewHolder.sendermsgText.setBackgroundResource(R.drawable.sendermsglayout);
                  messageViewHolder.sendermsgText.setTextColor(Color.BLACK);
                  messageViewHolder.sendermsgText.setText(messages.getMessage());

             }
             else

             {
                 messageViewHolder.ReceviermsgText.setVisibility(View.VISIBLE);
              
                 messageViewHolder.ReceviermsgText.setBackgroundResource(R.drawable.receivermsglayout);
                 messageViewHolder.ReceviermsgText.setTextColor(Color.BLACK);
                 messageViewHolder.ReceviermsgText.setText(messages.getMessage());

             }
         }



    }

    @Override
    public int getItemCount() {
        return usermsg.size();
    }





}
