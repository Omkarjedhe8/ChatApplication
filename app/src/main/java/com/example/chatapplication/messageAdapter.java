package com.example.chatapplication;

import static com.example.chatapplication.Chatwindow.reciverIImg;
import static com.example.chatapplication.Chatwindow.senderImg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<msgModel> messagesAdapterArrayList;
    int ITEM_SEND=1;
    int ITEM_RECIVE=2;

    public messageAdapter(Context context, ArrayList<msgModel> messagesAdapterArrayList) {
        this.context = context;
        this.messagesAdapterArrayList = messagesAdapterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType ==ITEM_SEND)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return  new senderViewHolder(view);

        }else {
            View view =LayoutInflater.from(context).inflate(R.layout.reciver_layout,parent,false);
            return  new reciverViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModel messages = messagesAdapterArrayList.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context).setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                return false;
            }
        });




        if (holder.getClass()==senderViewHolder.class){
            senderViewHolder viewHolder =(senderViewHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            Picasso.get().load(senderImg).into(viewHolder.circleImageView);
        }else {
            reciverViewHolder viewHolder=(reciverViewHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            Picasso.get().load(reciverIImg).into(viewHolder.circleImageView);

        }

    }

    @Override
    public int getItemCount() {
        return messagesAdapterArrayList.size();

    }

    @Override
    public int getItemViewType(int position) {
      //  if (position < messagesAdapterArrayList.size()) {
            msgModel message = messagesAdapterArrayList.get(position);
            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderid())) {
                return ITEM_SEND;
            } else {
                return ITEM_RECIVE;
            }

/*
        return super.getItemViewType(position);
*/
    }

    class senderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;

        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.profilegg);
            msgtxt=itemView.findViewById(R.id.msgsendertyp);

        }


    }

    class reciverViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView msgtxt;
        public reciverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.pro);
            msgtxt=itemView.findViewById(R.id.recivertextset);

        }
    }


}
