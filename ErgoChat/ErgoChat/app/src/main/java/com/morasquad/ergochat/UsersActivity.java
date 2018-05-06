package com.morasquad.ergochat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolBar = null;
    private RecyclerView mUsersList;

    private DatabaseReference mUsersdatabase;
    private FirebaseRecyclerOptions<Users> options;
    private List<Users> mUserItemsList;
    private RecyclerView.Adapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mToolBar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersdatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mUserItemsList = new ArrayList<>();
        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        mUserAdapter = new UserAdapter(mUserItemsList, getApplicationContext());
        mUsersList.setAdapter(mUserAdapter);

        loadUsers();
    }


    private void loadUsers() {
        mUsersdatabase.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Users user = dataSnapshot.getValue(Users.class);
                        user.setUserId(dataSnapshot.getKey());

                        Toast.makeText(UsersActivity.this, s, Toast.LENGTH_LONG).show();
                        mUserItemsList.add(user);
                        mUserAdapter.notifyDataSetChanged();

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

class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    List<Users> userItemsList;
    Context context;
    public UserAdapter(List<Users> userItemsList, Context context){
        this.userItemsList = userItemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.users_single_layout_chat,parent,false);
        return new UserAdapter.ViewHolder(v);
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
                Intent profileIntent=new Intent(context,ProfileActivity.class);
                profileIntent.putExtra("user_id",user.getUserId());
                context.startActivity(profileIntent);
                Toast.makeText(context,user.getUserId(),Toast.LENGTH_LONG).show();
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

/*
class UsersViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public UsersViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

    }


    public void setName(String name) {

        TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
        userNameView.setText(name);

    }

    public void setUserImage(String thumb_image, Context ctx) {

        CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
        Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

    }

    public void setUserOnline(String online_status) {

        ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

        if (online_status.equals("true")) {

            userOnlineView.setVisibility(View.VISIBLE);

        } else {

            userOnlineView.setVisibility(View.INVISIBLE);

        }

    }


}


class UsersAdapter extends FirebaseRecyclerAdapter<Users, UsersViewHolder> {

    */
/**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     *//*

    public UsersAdapter(@NonNull FirebaseRecyclerOptions<Users> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final UsersViewHolder usersViewHolder, int i, @NonNull Users users) {
        usersViewHolder.setName(users.getName());
        Toast.makeText(UsersActivity.this, "hhhh", Toast.LENGTH_LONG).show();

        final String list_user_id = getRef(i).getKey();

        mUsersdatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String userName = dataSnapshot.child("name").getValue().toString();
                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                if (dataSnapshot.hasChild("online")) {

                    String userOnline = dataSnapshot.child("online").getValue().toString();
                    usersViewHolder.setUserOnline(userOnline);

                }

                usersViewHolder.setName(userName);
                usersViewHolder.setUserImage(userThumb, getApplicationContext());

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Click Event for each item.
                                if (i == 0) {

                                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                                    profileIntent.putExtra("user_id", list_user_id);
                                    startActivity(profileIntent);

                                }

                                if (i == 1) {

                                    Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                                    chatIntent.putExtra("user_id", list_user_id);
                                    chatIntent.putExtra("user_name", userName);
                                    startActivity(chatIntent);

                                }

                            }
                        });

                        builder.show();

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_single_layout_chat, parent, false);

        return new UsersViewHolder(view);
    }
}
*/




