package a10m3.cruciada;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import a10m3.cruciada.Clase.Comp;

public class StartActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
             Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseComps;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseCurrentUser;
    private DatabaseReference mDatabaseCod;
    private FirebaseUser mCurrentUser;
    private CoordinatorLayout layout;
    private RecyclerView mCompList;
    private Query mQueryCrt;
    private ImageView back;

    private String userName;

    private LinearLayoutManager mLayoutManager;

    private boolean mProcess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();
    }

    private void initialize()
    {
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        layout = (CoordinatorLayout)findViewById(R.id.bigLayout);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseCod = FirebaseDatabase.getInstance().getReference().child("Comps");
        mDatabaseComps = mDatabase.child("Comps");
        mDatabaseUsers = mDatabase.child("Users").child(mCurrentUser.getUid());
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Comps");
        mQueryCrt = mDatabaseCurrentUser.orderByChild(mAuth.getCurrentUser().getUid()).equalTo("Acces");


        back = (ImageView) findViewById(R.id.background);

//        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if(!dataSnapshot.hasChild("CurrentComp"))
//                {
//                    layout.setBackground(getDrawable(R.drawable.textconcurs));
//                }
//                else layout.setBackground(getDrawable(R.color.light_grey));
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        mDatabaseUsers.child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue().toString().trim();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCompList = (RecyclerView) findViewById(R.id.comp_list);
        mCompList.setHasFixedSize(true);
        mCompList.setLayoutManager(new LinearLayoutManager(this));
        mLayoutManager = new LinearLayoutManager(StartActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mCompList.setLayoutManager(mLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(StartActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_create,null);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder. create();

                final EditText mName = (EditText) mView.findViewById(R.id.CompName);
                final EditText mDesc = (EditText) mView.findViewById(R.id.CompDescription);
                final TextView createBtn = (TextView) mView.findViewById(R.id.CreateCompButton);

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String name = mName.getText().toString().trim();
                        final String desc = mDesc.getText().toString().trim();

                        if(!TextUtils.isEmpty(name))
                        {
                            Toast.makeText(StartActivity.this,"Se creeaza concursul",Toast.LENGTH_SHORT).show();
                            final DatabaseReference newComp = mDatabaseComps.push();
                            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                    newComp.child("Name").setValue(name);
                                    newComp.child("Desc").setValue(desc);
                                    newComp.child(mAuth.getCurrentUser().getUid()).setValue("Acces");
                                    newComp.child("Admin").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                    mDatabaseUsers.child("CurrentComp").setValue(newComp.getKey());
                                    newComp.child("Creator").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                dialog.dismiss();
                                                startActivity(new Intent(StartActivity.this,CruciadaHome.class));
                                            }

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(StartActivity.this,"Completeaza toate spatiile obligatorii",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.show();
            }
        });

        final FirebaseRecyclerAdapter<Comp,CompViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comp, CompViewHolder>(
                Comp.class,
                R.layout.comp_row,
                CompViewHolder.class,
                mQueryCrt
        ) {
            @Override
            protected void populateViewHolder(CompViewHolder viewHolder, Comp model, int position) {

                final String comp_key = getRef(position).getKey();
                viewHolder.setTitle(model.getName());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcess = true;

                        mDatabase.child("Comps").child(comp_key).child("Members").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mProcess) {

                                    mProcess = false;

                                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                        final String tip = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("Type").getValue().toString().trim();

                                        if (tip.compareTo("Concurent") == 0) {
                                            mDatabaseUsers.child("CurrentStatus").setValue("Concurent");
                                            mDatabaseUsers.child("CurrentComp").setValue(comp_key);
                                            Intent a = new Intent(StartActivity.this, CruciadaHome.class);
                                            startActivity(a);
                                        } else if (tip.compareTo("Jurat") == 0) {
                                            mDatabaseUsers.child("CurrentStatus").setValue("Jurat");
                                            mDatabaseUsers.child("CurrentComp").setValue(comp_key);
                                            Intent a = new Intent(StartActivity.this, CruciadaHome.class);
                                            startActivity(a);
                                        } else
                                            Toast.makeText(StartActivity.this, tip, Toast.LENGTH_SHORT).show();


                                    } else {
                                        mDatabaseUsers.child("CurrentStatus").setValue("Admin");
                                        mDatabaseUsers.child("CurrentComp").setValue(comp_key);
                                        Intent a = new Intent(StartActivity.this, CruciadaHome.class);
                                        startActivity(a);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }
        };
        mCompList.setAdapter(firebaseRecyclerAdapter);

//        if(firebaseRecyclerAdapter.getItemCount()!=0)
//            back.setVisibility(View.GONE);


        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if(firebaseRecyclerAdapter.getItemCount()!=0)
                    back.setVisibility(View.GONE);
            }
        });

    }

    public static class CompViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public CompViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String title){

            TextView post_title = (TextView) mView.findViewById(R.id.compTitle);
            post_title.setText(title);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        /*if (id == R.id.help) {
            Intent intent3 = new Intent(StartActivity.this, GetHelp.class);
            startActivity(intent3);
        }
        else */if(id == R.id.logout){
            mAuth.signOut();
            Intent intent2 = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent2);
        }
        else if(id == R.id.join)
        {
            final AlertDialog.Builder mBuilde = new AlertDialog.Builder(StartActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.cod_concurs,null);

            final EditText mCod = (EditText) mView.findViewById(R.id.CompCod);
            TextView addBtn = (TextView) mView.findViewById(R.id.AddCompButton);

            mBuilde.setView(mView);
            final AlertDialog dialog = mBuilde.create();

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String cod = mCod.getText().toString().trim();

                    if(!TextUtils.isEmpty(cod)) {

                        mDatabaseCod.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(cod))
                                {
                                    if(!dataSnapshot.child(cod).hasChild(mAuth.getCurrentUser().getUid()))
                                    {
                                        mDatabaseCod.child(cod).child(mAuth.getCurrentUser().getUid()).setValue("Acces");
                                        mDatabaseCod.child(cod).child("Concurent").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        mDatabaseCod.child(cod).child("Members").child(mAuth.getCurrentUser().getUid()).child("Name").setValue(userName);
                                        mDatabaseCod.child(cod).child("Members").child(mAuth.getCurrentUser().getUid()).child("Type").setValue("Concurent");
                                        Toast.makeText(StartActivity.this,"Acum aveti acces la acest concurs",Toast.LENGTH_SHORT).show();
                                        //mDatabaseUsers.child("CurrentComp").setValue(cod);
                                        //mDatabaseUsers.child("CurrentStatus").setValue("Concurent");
                                        //startActivity(new Intent(StartActivity.this,CruciadaHome.class));
                                        dialog.dismiss();
                                    }
                                    else
                                    {
                                        //Toast.makeText(StartActivity.this,"Aveti deja acces la acest concurs",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(StartActivity.this,"Concurs inexistent",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(StartActivity.this,"Nu ai scris nimic ",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS, grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
        finish();
    }
}
