package pes.twochange.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.callback.BlockedResponse;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.themes.SettingsTheme;
import pes.twochange.presentation.controller.ChatActivity;
import pes.twochange.services.ImageManager;

/**
 * Created by Adrian on 07/04/2017.
 */

public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerChatAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> usersChat;
    private DatabaseReference mFirebaseChats;
    private String user;


    public RecyclerChatAdapter(Context context, ArrayList<String> users, final String user) {
        inflater=LayoutInflater.from(context);
        usersChat = users;
        this.context = context;
        this.user = user;

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseChats = mFirebaseDatabase.getReference().child("chats").child(user);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> useraux = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String userReciver = ds.getKey().toString();

                    //Callback a arreglar
                    SettingsTheme.getInstance(user).userIsBlocked(userReciver, new BlockedResponse() {
                        @Override
                        public void isBlocked(boolean blocked,String userblock) {
                        //si el usuario está bloqueado, no aparecerá en la lista de chats
                            if(!blocked) {
                                useraux.add(userblock);
                                notifyDataSetChanged();
                            }
                        }
                    });

                }

                usersChat = useraux;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mFirebaseChats.addValueEventListener(valueEventListener);

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
    public void onBindViewHolder (MyViewHolder holder, final int position){
        final String current = usersChat.get(position);
        holder.view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent chatIntent = new Intent(context,ChatActivity.class);
                        Chat chat = new Chat(user,usersChat.get(position));
                        chatIntent.putExtra("chat",chat);
                        context.startActivity(chatIntent);

                    }
                }
        );
        String imagePath = String.format("profiles/%s.jpg", current);
        ImageManager.getInstance().putImageIntoView(imagePath, context, holder.photo);
        holder.user.setText(String.valueOf(current));
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        private TextView user;
        private ImageView photo;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            user = (TextView) itemView.findViewById(R.id.userChat);
            photo = (ImageView) itemView.findViewById(R.id.profChat);
            photo.setImageResource(R.mipmap.prof_default);
        }
    }
}
