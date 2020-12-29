package com.example.chatapp;


import android.content.ContentProvider;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View privateChatView;
    private RecyclerView chatsList;
    private DatabaseReference ChatRef,UserRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatView= inflater.inflate(R.layout.fragment_chats, container, false);
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();

        ChatRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");




        chatsList=(RecyclerView)privateChatView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return privateChatView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts>options=
                new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(ChatRef,Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,ChatsViewHolder>adapter=
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(final ChatsViewHolder holder, int position, Contacts model)
                    {
                        final String usersIDS = getRef(position).getKey();
                        final String[] ujjuImage = {"defaut_image"};
                        UserRef.child(usersIDS).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.exists())
                                {

                                    if(dataSnapshot.hasChild("image"))
                                    {
                                          ujjuImage[0] =dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(ujjuImage[0]).into(holder.profileImage);
                                    }

                                    final String ujjuName=dataSnapshot.child("name").getValue().toString();

                                    final String ujjuStatus=dataSnapshot.child("status").getValue().toString();

                                    holder.username.setText(ujjuName);




                                    if(dataSnapshot.child("userState").hasChild("state"))
                                    {
                                        String state=dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date=dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time=dataSnapshot.child("userState").child("time").getValue().toString();

                                                 if(state.equals("online"))
                                                 {
                                                     holder.userStatus.setText("online");

                                                 }
                                                 else if(state.equals("offline"))
                                                 {
                                                     holder.userStatus.setText(date + " " + time );

                                                 }


                                    }
                                    else
                                    {
                                        holder.userStatus.setText("offline");
                                    }




                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id",usersIDS);
                                            chatIntent.putExtra("visit_user_name",ujjuName);
                                            chatIntent.putExtra("visit_image", ujjuImage[0]);

                                            startActivity(chatIntent);
                                        }
                                    });







                                }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);

                        return new ChatsViewHolder(view);
                    }
                };

                     chatsList.setAdapter(adapter);
                      adapter.startListening();
                 }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userStatus,username;

        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            profileImage=itemView.findViewById(R.id.users_profile_image);
            userStatus=itemView.findViewById(R.id.user_status);
            username=itemView.findViewById(R.id.user_profile_name);
        }
    }


}
