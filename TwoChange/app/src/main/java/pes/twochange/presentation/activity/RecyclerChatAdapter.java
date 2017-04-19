package pes.twochange.presentation.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ThrowOnExtraProperties;

import java.util.ArrayList;


import pes.twochange.R;

/**
 * Created by Adrian on 07/04/2017.
 */

public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerChatAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> usersChat;

    public RecyclerChatAdapter(Context context, ArrayList<String> users) {
        inflater=LayoutInflater.from(context);
        usersChat = users;
        this.context = context;

    }

    @Override
    public int getItemCount() {
        return usersChat.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chat_active,parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder (MyViewHolder holder, int position){
        String current = usersChat.get(position);
        holder.user.setText(String.valueOf(current));
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView user;
        private ImageView photo;

        public MyViewHolder(View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.userChat);
            photo = (ImageView) itemView.findViewById(R.id.profChat);
        }
    }
}
