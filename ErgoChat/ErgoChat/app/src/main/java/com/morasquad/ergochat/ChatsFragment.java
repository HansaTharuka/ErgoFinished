package com.morasquad.ergochat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mConvList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    public DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    public String mCurrent_user_id;

    private View mMainView;

    private RecyclerView.Adapter mChatsAdapter;

    private List<Users> mChatItemsList;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mConvList = (RecyclerView) mMainView.findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);

        mChatItemsList = new ArrayList<>();
        mChatsAdapter = new ChatsAdapter(mChatItemsList, getContext());
        mConvList.setAdapter(mChatsAdapter);
        loadUsers();

        // Inflate the layout for this fragment
        return mMainView;
    }
    private void loadUsers() {
        //Toast.makeText(getContext(), mCurrent_user_id,Toast.LENGTH_LONG).show();
        mMessageDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Toast.makeText(getContext(), "test",Toast.LENGTH_LONG).show();
                mUsersDatabase.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Users user = dataSnapshot.getValue(Users.class);
                        user.setUserId(dataSnapshot.getKey());
                        //Toast.makeText(getContext(), user.name,Toast.LENGTH_LONG).show();


                        mChatItemsList.add(user);
                        mChatsAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                //Toast.makeText(UsersActivity.this, user.name, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //mRootRef.child("notifications").child("chat").child(mSenderId).child(mReceiverId).child("seen").setValue(true);
    }

}

class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{

    List<Users> userItemsList;
    Context context;
    public ChatsAdapter(List<Users> userItemsList, Context context){
        this.userItemsList = userItemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.users_single_layout_chat,parent,false);
        return new ChatsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Users user = userItemsList.get(position);

        holder.name.setText(user.name);

        Picasso.with(context).load(user.image).into(holder.image);

        FirebaseDatabase.getInstance().getReference().child("messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(user.getUserId())
                .limitToLast(1)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String data = dataSnapshot.child("message").getValue().toString();
                        holder.status.setText(data);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String data = dataSnapshot.child("online").getValue().toString();
                        if(data.equals("true")){
                            holder.online.setVisibility(View.VISIBLE);
                        }else {
                            holder.online.setVisibility(View.INVISIBLE);
                        }
                        Toast.makeText(context,data,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("user_id", user.getUserId());
                chatIntent.putExtra("user_name", user.getName());
                context.startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, status;
        CircleImageView image;
        RelativeLayout relativeLayout;
        ImageView online;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.user_single_name);
            status = (TextView)itemView.findViewById(R.id.user_single_status);
            image = (CircleImageView)itemView.findViewById(R.id.user_single_image);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.user_single_relative_layout);
            online = itemView.findViewById(R.id.user_single_online_icon);
        }
    }
}

