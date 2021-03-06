package root.example.com.tar_q;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import root.example.com.tar_q.Guru.lengkapi_data_guru;
import root.example.com.tar_q.Jamaah.lengkapi_data_jamaah;

public class register extends AppCompatActivity {

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editReTextPassword;
    private Button buttonSignup;
    private ProgressDialog progressDialog;
    private Spinner spPilih;

    //defining firebaseauth object
    private String userID;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editReTextPassword = (EditText) findViewById(R.id.editTextRePassword);

        progressDialog = new ProgressDialog(this);
        TextView textViewSignIn = (TextView) findViewById(R.id.textViewSignin);
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(register.this, MainActivity.class);
                startActivity(mIntent);
            }
        });

        spPilih = (Spinner) findViewById(R.id.sp_Pilih);
        String[] Kelas = getResources().getStringArray(R.array.PemilihanUser);
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_style, Kelas);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPilih.setAdapter(mArrayAdapter);
    }

    public void registerUser(View view) {

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String repassword = editReTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password) && TextUtils.isEmpty(repassword)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.equals(repassword)) {
        }
        else{
            Toast.makeText(this, "Password Tidak Sesuai", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Tunggu Sebentar...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            userID = user.getUid();
                            //display some message here
                            Toast.makeText(register.this, "Successfully registered "+spPilih.getSelectedItem(), Toast.LENGTH_LONG).show();
                            if (spPilih.getSelectedItem().toString().equals("Jamaah")) {
                                myRef.child("TARQ").child("USER").child("JAMAAH").child(userID).child("level").setValue(2);
                                Intent register = new Intent(register.this, lengkapi_data_jamaah.class);
                                startActivity(register);
                            }
                            else if(spPilih.getSelectedItem().toString().equals("Guru")){
                                myRef.child("TARQ").child("USER").child("GURU").child(userID).child("level").setValue(3);
                                Intent register = new Intent(register.this, lengkapi_data_guru.class);
                                startActivity(register);
                            }
                            else{
                                Toast.makeText(register.this,"Pilih Level",Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            //display some message here
                            Toast.makeText(register.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(register.this,"Registration Error",Toast.LENGTH_LONG).show();
            }
        });
    }
}