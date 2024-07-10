package com.example.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {

    MainActivity mainActivity;
    ArrayList<Users> usersArrayList;

    public UserAdapter(MainActivity mainActivity, ArrayList<Users> usersArrayList) {
        this.mainActivity = mainActivity;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {
        Users users = usersArrayList.get(position);
        holder.username.setText(users.userName);
        holder.userstatus.setText(users.status);
        Picasso.get().load(users.profilepic).into(holder.circleImageView);

        //chat window code
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity,Chatwindow.class);
                intent.putExtra("nameee",users.getUserName());
                intent.putExtra("reciverImg",users.getProfilepic());
                intent.putExtra("uid",users.getUserId());
                mainActivity.startActivity(intent);



            }
        });

    }

    @Override
    public int getItemCount() {
        if (usersArrayList != null) {
            return usersArrayList.size();
        } else {
            return 0;
        }
    }

    public void updateData(ArrayList<Users> usersArrayList) {
        this.usersArrayList = usersArrayList;
        notifyDataSetChanged();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView username, userstatus;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
        }
    }
}

