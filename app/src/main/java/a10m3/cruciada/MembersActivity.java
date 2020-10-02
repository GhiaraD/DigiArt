package a10m3.cruciada;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import a10m3.cruciada.Clase.person;

public class MembersActivity extends AppCompatActivity {

    private RecyclerView mPersonList;
    private DatabaseReference mDatabaseNames;
    private String comp_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        mPersonList = (RecyclerView) findViewById(R.id.person_list);
        mPersonList.setHasFixedSize(true);
        mPersonList.setLayoutManager(new LinearLayoutManager(this));
        comp_key = getIntent().getExtras().getString("id");
        mDatabaseNames = FirebaseDatabase.getInstance().getReference().child("Comps").child(comp_key).child("Members");

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<person,PersonViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<person, PersonViewHolder>(
                person.class,
                R.layout.person_row,
                PersonViewHolder.class,
                mDatabaseNames
        ) {
            @Override
            protected void populateViewHolder(PersonViewHolder viewHolder, person model, int position) {

                final String person_key = getRef(position).getKey();
                final String person_type = model.getType();
                viewHolder.setTitle(model.getName());
                viewHolder.setType(person_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(person_type.compareTo("Concurent")==0)
                        {

                            mDatabaseNames.child(person_key).child("Type").setValue("Jurat");
                            FirebaseDatabase.getInstance().getReference().child("Comps").child(comp_key).child("Concurent").child(person_key).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Comps").child(comp_key).child("Jurat").child(person_key).setValue(person_key);

                        }
                        else if(person_type.compareTo("Jurat")==0)
                        {

                            mDatabaseNames.child(person_key).child("Type").setValue("Concurent");
                            FirebaseDatabase.getInstance().getReference().child("Comps").child(comp_key).child("Jurat").child(person_key).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Comps").child(comp_key).child("Concurent").child(person_key).setValue(person_key);

                        }

                    }
                });


            }
        };

        mPersonList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView post_type;

        DatabaseReference mDatabase,mDatabaseCurrentComp;
        FirebaseAuth mAuth;

        public PersonViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            post_type = (TextView) mView.findViewById(R.id.typeAccount);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            mDatabaseCurrentComp = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("CurrentComp");
            mDatabase.keepSynced(true);

        }

        public void setTitle(String title){

            TextView post_title = (TextView) mView.findViewById(R.id.nameAccount);
            post_title.setText(title);

        }

        public void setType(final String post_key){

            mDatabaseCurrentComp.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String competition_key = dataSnapshot.getValue().toString().trim();
                    mDatabase.child("Comps").child(competition_key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child("Concurent").hasChild(post_key))
                            {
                                post_type.setText("Concurent");
                            }
                            else if(dataSnapshot.child("Jurat").hasChild(post_key))
                            {
                                post_type.setText("Jurat");
                            }

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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent no = new Intent(MembersActivity.this, CruciadaHome.class);
        no.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(no);
    }

}
