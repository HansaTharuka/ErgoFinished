package com.morasquad.ergochat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    private RecyclerView.Adapter mFrindsAdapter;

    private List<Users> mFriendItemsList;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mFriendItemsList = new ArrayList<>();

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mFrindsAdapter = new FriendsAdapter(mFriendItemsList, getContext());
        mFriendsList.setAdapter(mFrindsAdapter);
        loadUsers();
        // Inflate the layout for this fragment
        return mMainView;
    }

    private void loadUsers() {
        //Toast.makeText(getContext(), mCurrent_user_id,Toast.LENGTH_LONG).show();
        mFriendsDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Toast.makeText(getContext(), "test",Toast.LENGTH_LONG).show();
                mUsersDatabase.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Users user = dataSnapshot.getValue(Users.class);
                        user.setUserId(dataSnapshot.getKey());
                        //Toast.makeText(getContext(), user.name,Toast.LENGTH_LONG).show();

                        mFriendItemsList.add(user);
                        mFrindsAdapter.notifyDataSetChanged();

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

class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{

    List<Users> userItemsList;
    Context context;
    public FriendsAdapter(List<Users> userItemsList, Context context){
        this.userItemsList = userItemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.users_single_layout_chat,parent,false);
        return new FriendsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users user = userItemsList.get(position);

        holder.name.setText(user.name);
        holder.status.setText(user.status);
        Picasso.with(context).load(user.image).into(holder.image);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Click Event for each item.
                        if(i == 0){

                            Intent profileIntent = new Intent(context, ProfileActivity.class);
                            profileIntent.putExtra("user_id", user.getUserId());
                            context.startActivity(profileIntent);

                        }

                        if(i == 1){

                            Intent chatIntent = new Intent(context, ChatActivity.class);
                            chatIntent.putExtra("user_id", user.getUserId());
                            chatIntent.putExtra("user_name", user.getName());
                            context.startActivity(chatIntent);

                        }

                    }
                });

                builder.show();

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

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.user_single_name);
            status = (TextView)itemView.findViewById(R.id.user_single_status);
            image = (CircleImageView)itemView.findViewById(R.id.user_single_image);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.user_single_relative_layout);
        }
    }
}
