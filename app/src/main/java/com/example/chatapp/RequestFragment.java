package com.example.chatapp;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Button;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class RequestFragment extends Fragment {

     View requestFragmentView;
     private RecyclerView myReqList;
     private DatabaseReference reqRef,userRef,contactsRef;
     private FirebaseAuth mAuth;
     private String CurrentUserID;
    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestFragmentView= inflater.inflate(R.layout.fragment_request, container, false);
        mAuth=FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.keepSynced(true);
        reqRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        reqRef.keepSynced(true);
             contactsRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        myReqList=(RecyclerView)requestFragmentView.findViewById(R.id.chat_req_list);
        myReqList.setLayoutManager(new LinearLayoutManager(getContext()));
        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(reqRef.child(CurrentUserID),Contacts.class).build();
        final FirebaseRecyclerAdapter<Contacts,reqViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, reqViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final reqViewHolder holder, int position, @NonNull final Contacts model) {
                holder.itemView.findViewById(R.id.req_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.req_cancel_btn).setVisibility(View.VISIBLE);

                final String  user_list=getRef(position).getKey();
                DatabaseReference typeRef=getRef(position).child("request_type").getRef();
                typeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                          String type=dataSnapshot.getValue().toString();
                          if(type.equals("received"))
                          {
                              userRef.child(user_list).addValueEventListener(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                             if(dataSnapshot.child("image").exists())
                                             {  final String reqprofileimg=dataSnapshot.child("image").getValue().toString();
                                                  Picasso.get().load(reqprofileimg).networkPolicy(NetworkPolicy.OFFLINE).into(holder.profileimg, new Callback() {
                                                     @Override
                                                     public void onSuccess() {

                                                     }

                                                     @Override
                                                     public void onError(Exception e) {
                                                         Picasso.get().load(reqprofileimg).placeholder(R.drawable.profile_image).into(holder.profileimg);
                                                     }
                                                 });
                                             }
                                                       final String requsername=dataSnapshot.child("name").getValue().toString();
                                                       final String requsetstatus=dataSnapshot.child("about").getValue().toString();
                                                       holder.name.setText(requsername);
                                                        holder.status.setText(requsername + " wants to be your friend");
                                             holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     CharSequence options[]=new CharSequence[]
                                                             {
                                                                     "Accept",
                                                                     "Decline"
                                                             };

                                                     final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                     builder.setTitle(requsername + "Chat Request");
                                                     builder.setItems(options, new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialogInterface, int i) {

                                                             if(i==0)
                                                             {
                                                                 contactsRef.child(CurrentUserID).child(user_list).child("Contact").setValue("Saved").
                                                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful())
                                                                        {
                                                                            contactsRef.child(user_list).child(CurrentUserID).child("Contact").setValue("Saved").
                                                                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                            if(task.isSuccessful())
                                                                                             {
                                                                                                   reqRef.child(CurrentUserID).child(user_list).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                       @Override
                                                                                                       public void onComplete(@NonNull Task<Void> task) {

                                                                                                           if(task.isSuccessful())
                                                                                                           {
                                                                                                               reqRef.child(user_list).child(CurrentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                   @Override
                                                                                                                   public void onComplete(@NonNull Task<Void> task) {

                                                                                                                       if(task.isSuccessful())
                                                                                                                       {
                                                                                                                           Toast.makeText(getContext(), "New Friend Added!!", Toast.LENGTH_SHORT).show();



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
                                                                    });
                                                             }
                                                             if(i==1)
                                                             {
                                                                 reqRef.child(CurrentUserID).child(user_list).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                     @Override
                                                                     public void onComplete(@NonNull Task<Void> task) {

                                                                         if(task.isSuccessful())
                                                                         {
                                                                             reqRef.child(user_list).child(CurrentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                 @Override
                                                                                 public void onComplete(@NonNull Task<Void> task) {

                                                                                     if(task.isSuccessful())
                                                                                     {
                                                                                         Toast.makeText(getContext(), "Request Deleted!!", Toast.LENGTH_SHORT).show();



                                                                                     }
                                                                                 }
                                                                             });
                                                                         }
                                                                     }
                                                                 });
                                                             }



                                                         }
                                                     });

                                                     builder.show();
                                                 }
                                             });

                                  }

                                  @Override
                                  public void onCancelled(@NonNull DatabaseError databaseError) {

                                  }
                              });

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
            public reqViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

              View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display,viewGroup,false);
              reqViewHolder holder=new reqViewHolder(view);
              return holder;
            }
        };
         myReqList.setAdapter(adapter);
         adapter.startListening();
    }

    public static  class reqViewHolder extends RecyclerView.ViewHolder {
        TextView name,status;
        CircleImageView profileimg;
        Button Accept,Cancel;
        public reqViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.user_profile_name);
            status=itemView.findViewById(R.id.user_status);
            profileimg=itemView.findViewById(R.id.users_profile_image);
            Accept=itemView.findViewById(R.id.req_accept_btn);
            Cancel=itemView.findViewById(R.id.req_cancel_btn);
        }
    }
}
