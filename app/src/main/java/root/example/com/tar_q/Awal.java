package root.example.com.tar_q;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Awal extends AppCompatActivity {

    private Button BtnSignInAwal, BtnSignUpAwal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awal);

        BtnSignInAwal = (Button) findViewById(R.id.btnSignInAwal);
        BtnSignUpAwal = (Button) findViewById(R.id.btnSignUpAwal);

        BtnSignUpAwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Awal.this, register.class);
                startActivity(mIntent);
            }
        });

        BtnSignInAwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Awal.this, MainActivity.class);
                startActivity(mIntent);
            }
        });
    }
}
