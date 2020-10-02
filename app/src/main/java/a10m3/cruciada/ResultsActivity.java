package a10m3.cruciada;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultsActivity extends AppCompatActivity {

    TextView comp,firstW,secondW,thirdW,lucrare1,lucrare2,lucrare3;
    String postKey,numeCOmp;

    private DatabaseReference mDatabaseUsers, mDatabaseComps;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        comp = (TextView)findViewById(R.id.competitie);
        firstW = (TextView)findViewById(R.id.winner1);
        secondW = (TextView)findViewById(R.id.winner2);
        thirdW = (TextView)findViewById(R.id.winner3);
        lucrare1 = (TextView)findViewById(R.id.lucrare1);
        lucrare2 = (TextView)findViewById(R.id.lucrare2);
        lucrare3 = (TextView)findViewById(R.id.lucrare3);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseComps = FirebaseDatabase.getInstance().getReference().child("Comps");

        mDatabaseUsers.child(mCurrentUser.getUid()).child("CurrentComp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postKey = dataSnapshot.getValue().toString().trim();
                mDatabaseComps.child(postKey).child("Name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        numeCOmp = dataSnapshot.getValue().toString().trim();
                        comp.setText(numeCOmp);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        Intent no = new Intent(ResultsActivity.this, CruciadaHome.class);
        no.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(no);
    }
}
