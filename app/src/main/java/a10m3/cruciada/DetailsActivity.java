package a10m3.cruciada;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DetailsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference,mCurrentComp,mCurrentStatus;
    private TextView name,desc,creator,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        name = (TextView) findViewById(R.id.concurs);
        desc = (TextView) findViewById(R.id.descriere);
        creator = (TextView) findViewById(R.id.creator);
        status = (TextView) findViewById(R.id.status);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Comps");
        mCurrentComp = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCurrentComp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String comp = dataSnapshot.child("CurrentComp").getValue().toString().trim();
                final String stat = dataSnapshot.child("CurrentStatus").getValue().toString().trim();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        creator.setText(dataSnapshot.child(comp).child("Creator").getValue().toString().trim());
                        if(!(dataSnapshot.child(comp).child("Desc").getValue().toString().trim().compareTo("")==0))desc.setText(dataSnapshot.child(comp).child("Desc").getValue().toString().trim());
                        name.setText(dataSnapshot.child(comp).child("Name").getValue().toString().trim());
                        status.setText(stat);
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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent no = new Intent(DetailsActivity.this, CruciadaHome.class);
        no.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(no);
    }
}
