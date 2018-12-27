package root.example.com.tar_q;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import root.example.com.tar_q.Guru.lengkapi_data_guru;

public class GantiPassword extends AppCompatActivity {

    private final String TAG = "GantiPassword";
    private EditText
            Et_PassLama,
            Et_PassBaru,
            Et_RePassBaru;

    private Button
            confirmPass;

    private String
            Lokasi,
            newPass;
    private String email;
    private FirebaseUser user;
    private AuthCredential credential;
    private ProgressDialog progressDialog;
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);
        Lokasi = getIntent().getStringExtra("Lokasi");
        Log.d(TAG, "onCreate() returned: " + Lokasi);
        Et_PassLama = (EditText) findViewById(R.id.Et_PassLama);
        Et_PassBaru = (EditText) findViewById(R.id.Et_PassBaru);
        Et_RePassBaru = (EditText) findViewById(R.id.Et_RePassBaru);
        confirmPass = (Button) findViewById(R.id.confirmPass);

        progressDialog = new ProgressDialog(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();

        confirmPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passLama = Et_PassLama.getText().toString();
                String passBaru = Et_PassBaru.getText().toString();
                newPass = passBaru;
                String rePassBaru = Et_RePassBaru.getText().toString();
                credential = EmailAuthProvider.getCredential(email, passLama);
                if (passBaru.equals(rePassBaru) && !passBaru.equals("") && !rePassBaru.equals("")) {
                    user.reauthenticate(credential).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GantiPassword.this, "Password Sebelumnya Salah", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(GantiPassword.this, "Process", Toast.LENGTH_SHORT).show();
                            gantiPassword();
                        }
                    });
                }
            }
        });
    }

    private void gantiPassword() {
        progressDialog.setMax(100);
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.setTitle("Login");

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        progressStatus = 0;
        final Handler handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressDialog.incrementProgressBy(1);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handle.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setProgress(progressStatus);
                            if (progressStatus == 100) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        }).start();
        user.updatePassword(newPass).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GantiPassword.this, "Gagal Mengganti Password", Toast.LENGTH_LONG).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Intent mIntent = new Intent(GantiPassword.this, lengkapi_data_guru.class);
                    mIntent.putExtra("Lokasi",Lokasi);
                    mIntent.putExtra("Verifikasi","true");
                    startActivity(mIntent);
                }
            }
        });
    }
}
