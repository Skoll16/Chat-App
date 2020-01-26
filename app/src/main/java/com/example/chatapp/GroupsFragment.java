package com.example.chatapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

     private View grpFragment;
     private ListView listview;
     private ArrayAdapter<String> arrayAdapter;
     ArrayList<String> arrayListofGrps=new ArrayList<>();
     private DatabaseReference rootref;
    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootref= FirebaseDatabase.getInstance().getReference().child("Groups");
        grpFragment= inflater.inflate(R.layout.fragment_groups, container, false);
        listview=(ListView)grpFragment.findViewById(R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,arrayListofGrps);
        listview.setAdapter(arrayAdapter);
        RetriveGrps();

          listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                  String CurrentgrpName=adapterView.getItemAtPosition(i).toString();

                  Intent intent=new Intent(getContext(),GroupChatActivity.class);
                    intent.putExtra("groupName",CurrentgrpName);
                    startActivity(intent);

              }
          });


    return grpFragment;
    }

    private void RetriveGrps() {

    rootref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Set<String> set=new HashSet<>();
            Iterator iterator=dataSnapshot.getChildren().iterator();
            while(iterator.hasNext())
            {
                set.add(((DataSnapshot)iterator.next()).getKey());
            }

            arrayListofGrps.clear();
            arrayListofGrps.addAll(set);
            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

}
