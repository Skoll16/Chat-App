package com.example.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAccess tabAccessAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Convo");
        viewPager = (ViewPager) findViewById(R.id.main_tab_pager);
        tabAccessAdapter = new TabAccess(getSupportFragmentManager());
        viewPager.setAdapter(tabAccessAdapter);
        tabLayout = (TabLayout) findViewById(R.id.main_tab);
        tabLayout.setupWithViewPager(viewPager);
        rootRef= FirebaseDatabase.getInstance().getReference();


    }

    @Override
    protected void onStart() {

        super.onStart();
        //checking authentication of the user
        if (currentUser == null) {
            StartPage();
        } else {
            VerifyExistingUser();
        }


    }

    private void VerifyExistingUser() {
        String userID = mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()) {

                } else {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void StartPage() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dropdownoptions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.logout_option) {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (item.getItemId() == R.id.settings_option) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);


        }
        if (item.getItemId() == R.id.grpchat_option) {
            createnewGrp();

        }
        if (item.getItemId() == R.id.main_find_friends) {
            Intent intent = new Intent(MainActivity.this, FindFriendsActivity.class);
           startActivity(intent);
        }

        return true;
    }

    private void createnewGrp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name:");

        final EditText grpname = new EditText(MainActivity.this);
        grpname.setHint("eg. Wolf Pack");
        builder.setView(grpname);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String grpname1 = grpname.getText().toString();
                if (TextUtils.isEmpty(grpname1)) {
                    Toast.makeText(MainActivity.this, "Please input Group Name", Toast.LENGTH_SHORT).show();
                } else {
                    rootRef.child("Groups").child(grpname1).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(MainActivity.this, ""+grpname1 +" is created successfully!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

        });
    builder.show();
    }
    
}