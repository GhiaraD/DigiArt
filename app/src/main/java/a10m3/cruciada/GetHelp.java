package a10m3.cruciada;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GetHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent no = new Intent(GetHelp.this, StartActivity.class);
        no.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(no);
    }

}
