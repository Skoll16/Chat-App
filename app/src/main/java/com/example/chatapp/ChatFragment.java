package com.example.chatapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private View chatview;
    private RecyclerView chat_list;
    FirebaseAuth mAuth;
    String CurrentUserID;
   private DatabaseReference chatRef,userRef;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        chatview= inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth=FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();

        chatRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        chatRef.keepSynced(true);
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.keepSynced(true);
        chat_list=(RecyclerView) chatview.findViewById(R.id.chats_list);
        chat_list.setLayoutManager(new LinearLayoutManager(getContext()));

      return chatview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(chatRef.child(CurrentUserID),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ChatViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contacts model) {

                final String usersIDs=getRef(position).getKey();
                final String[] userimage = {"default image"};
                userRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            if(dataSnapshot.child("image").exists())
                            {
                                 userimage[0] =dataSnapshot.child("image").getValue().toString();

                                Picasso.get().load(userimage[0]).placeholder(R.drawable.profile_image).into(holder.profileimg);
                            }
                            final String username=dataSnapshot.child("name").getValue().toString();
                            final String userstatus=dataSnapshot.child("about").getValue().toString();
                            holder.name.setText(username);
                            holder.status.setText("Last Seen: " + "\n" + " Date: "+ " Time:");

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(getContext(),ChatActivity.class);
                                    intent.putExtra("visit_user_id",usersIDs);
                                    intent.putExtra("visit_user_name",username);
                                    intent.putExtra("visit_user_image", userimage[0]);
                                    startActivity(intent);
                                }
                            });
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
              View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display,viewGroup,false);
              ChatViewHolder holder=new ChatViewHolder(view);


             return holder;
            }
        };
      chat_list.setAdapter(adapter);
      adapter.startListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView name , status;
        CircleImageView profileimg;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

          name=itemView.findViewById(R.id.user_profile_name);
          profileimg=itemView.findViewById(R.id.users_profile_image);
          status=itemView.findViewById(R.id.user_status);
        }
    }

}
