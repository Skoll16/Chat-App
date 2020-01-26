package com.example.chatapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {
      private RecyclerView mRecyclerView;
     View contactsView;
     FirebaseAuth mAuth;
     String CurrentUserID;
     DatabaseReference contactsRef,userRef;
     public ContactFragment() {
        // Required empty public constructor
     }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView= inflater.inflate(R.layout.fragment_contact, container, false);
        mRecyclerView=(RecyclerView)contactsView.findViewById(R.id.recycler_contact_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.keepSynced(true);

      mAuth=FirebaseAuth.getInstance();
      CurrentUserID=mAuth.getCurrentUser().getUid();
     contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserID);

        contactsRef.keepSynced(true);


        return contactsView;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(contactsRef,Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,contactsViewHolder> adapter= new FirebaseRecyclerAdapter<Contacts, contactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final contactsViewHolder holder, int position, @NonNull Contacts model) {

                String userID=getRef(position).getKey();
                userRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            if(dataSnapshot.child("image").exists())
                            {
                                final String image=dataSnapshot.child("image").getValue().toString();
                                String name=dataSnapshot.child("name").getValue().toString();
                                String status=dataSnapshot.child("about").getValue().toString();
                                holder.userName.setText(name);
                                holder.userStatus.setText(status);


                                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(holder.userprofileimg, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.userprofileimg);
                                    }
                                });
                            }
                            else{

                                String name=dataSnapshot.child("name").getValue().toString();
                                String status=dataSnapshot.child("about").getValue().toString();
                                holder.userName.setText(name);
                                holder.userStatus.setText(status);
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public contactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

               View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display,viewGroup,false);
               contactsViewHolder holder=new contactsViewHolder(view);
               return  holder;

            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

     }


     public static class contactsViewHolder extends RecyclerView.ViewHolder {
         TextView userName,userStatus;
         CircleImageView userprofileimg;
         public contactsViewHolder(@NonNull View itemView) {
             super(itemView);
         userName=itemView.findViewById(R.id.user_profile_name);
             userStatus=itemView.findViewById(R.id.user_status);
          userprofileimg=itemView.findViewById(R.id.users_profile_image);
         }
     }
}
