package com.abdoo.android.propius;

import android.app.ProgressDialog;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private ImageView mProfileImage;
    private TextView mProfileUsername;
    private TextView mProfilestatus;
    private TextView mProfileFriendsCount;
    private Button mProfileSendReqBtn;
    private Button mProfileDeclineReq;

    private DatabaseReference mUserDb;
    private DatabaseReference mCrrUserDb;
    private DatabaseReference mFriendReqDb;
    private DatabaseReference mFriendsDb;
    private DatabaseReference mNotifDb;
    private FirebaseUser mCrrUser;

    private ProgressDialog mProgress;
    private String mCrrState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileUsername = (TextView) findViewById(R.id.profile_username);
        mProfilestatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_friendsCount);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_sendReq);
        mProfileDeclineReq = (Button) findViewById(R.id.profile_declineReq);

        mCrrState = "not_friends";

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading profile");
        mProgress.setMessage("Please wait..");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        // Firebase
        final String userId = getIntent().getStringExtra("userId");

        mUserDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        mCrrUserDb = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mFriendReqDb = FirebaseDatabase.getInstance().getReference().child("friends_req");
        mFriendsDb = FirebaseDatabase.getInstance().getReference().child("friends");
        mNotifDb = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCrrUser = FirebaseAuth.getInstance().getCurrentUser();


        mUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileUsername.setText(username);
                mProfilestatus.setText(status);

                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.user2).into(mProfileImage);

                // Update friends Reqs

                mFriendReqDb.child(mCrrUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userId)){
                            String req_type = dataSnapshot.child(userId).child("request_type").getValue().toString();
                            if(req_type.equals("received")){
                                mCrrState = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");
                                mProfileDeclineReq.setVisibility(View.VISIBLE);
                                mProfileDeclineReq.setEnabled(true);
                            }else if(req_type.equals("sent")){
                                mCrrState = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                mProfileDeclineReq.setVisibility(View.INVISIBLE);
                                mProfileDeclineReq.setEnabled(false);

                            }
                        } else {
                            mFriendsDb.child(mCrrUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(userId)){
                                        mCrrState = "friends";
                                        mProfileSendReqBtn.setText("Unfriend");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        mProgress.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileSendReqBtn.setEnabled(false);

                // Not Friends Part

                if(mCrrState.equals("not_friends")){
                    mFriendReqDb.child(mCrrUser.getUid()).child(userId).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendReqDb.child(userId).child(mCrrUser.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String, String> notifData = new HashMap<>();
                                        notifData.put("from", mCrrUser.getUid());
                                        notifData.put("type", "request");

                                        mNotifDb.child(userId).push().setValue(notifData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mProfileSendReqBtn.setEnabled(true);
                                                mCrrState = "req_sent";
                                                Toast.makeText(ProfileActivity.this, "Request sent successfully.;)", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }else{
                                Toast.makeText(ProfileActivity.this, "Failed sending request", Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setText("Cancel Friend Request");
                        }
                    });
                }

                // Cancel Request State

                if(mCrrState.equals("req_sent")){
                    mFriendReqDb.child(mCrrUser.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDb.child(userId).child(mCrrUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendReqBtn.setEnabled(true);
                                    mCrrState = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");
                                }
                            });
                        }
                    });
                }

                // Req received state

                if(mCrrState.equals(("req_received"))){
                    Date currentTime = Calendar.getInstance().getTime();
                    final String crrDate = currentTime.toString();
                    mFriendsDb.child(mCrrUser.getUid()).child(userId).child("date").setValue(crrDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDb.child(userId).child(mCrrUser.getUid()).child("date").setValue(crrDate);

                            mFriendReqDb.child(mCrrUser.getUid()).child(userId).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDb.child(userId).child(mCrrUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProfileSendReqBtn.setEnabled(true);
                                            mCrrState = "friends";
                                            mProfileSendReqBtn.setText("unFriend this person");

                                            mProfileDeclineReq.setVisibility(View.INVISIBLE);
                                            mProfileDeclineReq.setEnabled(false);
                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCrrUserDb.child("online").setValue(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mCrrUserDb.child("online").setValue(ServerValue.TIMESTAMP);
    }
}
