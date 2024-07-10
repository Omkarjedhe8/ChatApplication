package com.example.chatapplication;

import static java.lang.Boolean.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends Activity {
FirebaseAuth auth;
ImageView chat,logout,camera,setting;
RecyclerView mainUserRecycleerView;
//UserAdapter adapter;
FirebaseDatabase database;
CircleImageView userimg;
TextView username,userstatus;


ArrayList<Users> usersArrayList;
UserAdapter adapter; /*= new UserAdapter(MainActivity.this,usersArrayList);*/


//Toolbar navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        auth= FirebaseAuth.getInstance();
        mainUserRecycleerView=findViewById(R.id.mainUserRecycleerView);
     ///   UserAdapter adapter= new UserAdapter(this,usersArrayList);
        mainUserRecycleerView.setLayoutManager( new  LinearLayoutManager(this));
        adapter = new UserAdapter(this, new ArrayList<>());
        mainUserRecycleerView.setAdapter(adapter);
        logout=findViewById(R.id.mainlogout);


       /* chat= findViewById(R.id.mainchat);
        logout=findViewById(R.id.mainlogout);
        camera=findViewById(R.id.main_camera);
        setting=findViewById(R.id.main_setting);
       */
        database=FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();
        DatabaseReference reference= database.getReference().child("user");
        usersArrayList= new ArrayList<>();


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
//                adapter = new UserAdapter(MainActivity.this, usersArrayList);
             //error sloving code for recycle view......
                adapter.updateData(usersArrayList);

                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog =new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_layout);
                Button yes,no;
                yes=dialog.findViewById(R.id.logoutyes);
                no=dialog.findViewById(R.id.logoutno);

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(MainActivity.this, Register.class);
                        startActivity(intent);
                        finish();
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });



        if(auth.getCurrentUser() == null)
        {
            Intent intent;
            intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }

    }


}
