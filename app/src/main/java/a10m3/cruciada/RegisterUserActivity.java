package a10m3.cruciada;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterUserActivity extends AppCompatActivity {

    EditText emailRegister, passRegister, username, passConfirm;
    TextView CreateAccount;

    ProgressDialog mProgressDialog;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        emailRegister = (EditText) findViewById(R.id.emailRegister);
        passRegister = (EditText) findViewById(R.id.passRegister);
        username = (EditText) findViewById(R.id.usernameET);
        passConfirm = (EditText) findViewById(R.id.passRegisterConfirm);

        CreateAccount = (TextView) findViewById(R.id.createAccount);

        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {

                FirebaseUser user= firebaseAuth.getCurrentUser();
                if(user!=null)
                {
                    Intent moveToHome = new Intent(RegisterUserActivity.this, StartActivity.class);
                    moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(moveToHome);
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);

        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setTitle("Se creeaza contul");
                mProgressDialog.setMessage("Va rugam așteptați...");
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                try {
                    createUserAccount();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void createUserAccount() throws NoSuchAlgorithmException {

        final String emailUser, passUser, user, passConf;
        emailUser = emailRegister.getText().toString().trim();
        passUser = passRegister.getText().toString().trim();
        user = username.getText().toString().trim();
        passConf = passConfirm.getText().toString().trim();


        if( !TextUtils.isEmpty(emailUser) && !TextUtils.isEmpty(passUser) && !TextUtils.isEmpty(user) && passConf.compareTo(passUser)==0 && passConf.length()>=6)
        {
            mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if( task.isSuccessful())
                    {
                        String userid = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = databaseReference.child(userid);
                        current_user_db.child("Name").setValue(user);
                        Toast.makeText(RegisterUserActivity.this, "Contul a fost creat cu succes", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        startActivity(new Intent(RegisterUserActivity.this, StartActivity.class));
                    }
                    else
                    {
                        Toast.makeText(RegisterUserActivity.this, "Email invalid", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
        else
        {
            if(TextUtils.isEmpty(emailUser)||TextUtils.isEmpty(passUser)||TextUtils.isEmpty(user)||TextUtils.isEmpty(passConf))
            {
                Toast.makeText(RegisterUserActivity.this, "Completează toate spațiile", Toast.LENGTH_SHORT).show();
            }
            else if(passConf.compareTo(passUser)!=0)
            {
                Toast.makeText(RegisterUserActivity.this, "Cele doua parole sunt diferite", Toast.LENGTH_SHORT).show();
            }
            else if(passConf.compareTo(passUser)==0 && passConf.length()<6)
            {
                Toast.makeText(RegisterUserActivity.this, "Parola trebuie să conțină cel puțin 6 caractere", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(RegisterUserActivity.this, "Contul nu a putut fi creat", Toast.LENGTH_SHORT).show();
            }
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent no = new Intent(RegisterUserActivity.this, MainActivity.class);
        no.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(no);
    }
}
