package a10m3.cruciada;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class SinglePost extends AppCompatActivity {

    DatabaseReference mDatabase;
    DatabaseReference mCurrentComp;
    PhotoView zoomImage;
    String key;
    ActionBar actionBar;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_single_post);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        //actionBar.hide();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Comps");
        mCurrentComp = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CurrentComp");
        key = getIntent().getExtras().getString("id");
        zoomImage = (PhotoView) findViewById(R.id.imagine);

        mCurrentComp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String code = dataSnapshot.getValue().toString().trim();

                mDatabase.child(code).child("Lucrari").child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String postImage = dataSnapshot.child("image").getValue(String.class);
                        Picasso.get()
                                .load(postImage)
                                .into(zoomImage);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //mAttacher = new PhotoViewAttacher(zoomImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent no = new Intent(SinglePost.this, CruciadaHome.class);
        no.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(no);
    }

}
