package com.abdoo.android.propius;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mSaveBtn;
    private TextInputLayout mStatus;
    private ProgressDialog mProgress;

    private DatabaseReference mStatusDb;
    private FirebaseUser mCrrUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // firebase
        mCrrUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCrrUser.getUid();
        mStatusDb = FirebaseDatabase.getInstance().getReference().child("users").child(uid);


        mSaveBtn = (Button) findViewById(R.id.status_save_btn);
        mStatus = (TextInputLayout) findViewById(R.id.status_input);

        String statusVal = getIntent().getStringExtra("statusVal");
        mStatus.getEditText().setText(statusVal);

        mToolbar = (Toolbar) findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // progress Dlg
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving changes");
                mProgress.setMessage("Please wait while saving the changes..");
                mProgress.show();

                String status = mStatus.getEditText().getText().toString();
                mStatusDb.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(), "There was an error while saving changes :(", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });
    }
}
