package root.example.com.tar_q;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by User on 2/8/2017.
 */

public class Berhasil extends AppCompatActivity {
    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    private long backPressedTime;
    private Toast backToast;
    private String re;

    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berhasil);


        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        System.out.println("User = " + userID);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    showData(dataSnapshot);
                    showData1(dataSnapshot);
                    // do process 1
                } else {
                    myRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    private NullPointerException showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            System.out.println("INI SHOWDATA 1");
            ProfileJamaah uInfo = new ProfileJamaah();
            try {
                uInfo.setLevel(ds.child("USER").child("JAMAAH").child(userID).getValue(ProfileJamaah.class).getLevel());
                if (uInfo.getLevel() == 2) {
                    try {
                        uInfo.setNama(ds.child("USER").child("JAMAAH").child(userID).getValue(ProfileJamaah.class).getNama());
                        System.out.println("HAHA JAMAAH " + uInfo.getNama());
                        if(uInfo.getNama() == null){
                            Intent sIntent = new Intent(Berhasil.this, lengkapi_data_jamaah.class);
                            startActivity(sIntent);
                        }
                        else{
                            Intent mIntent = new Intent(Berhasil.this, Main_Jamaah.class);
                            startActivity(mIntent);
                        }
                    } catch (NullPointerException e) {
                        Intent sIntent = new Intent(Berhasil.this, lengkapi_data_jamaah.class);
                        startActivity(sIntent);
                    }
                }
            } catch (NullPointerException e) {
                return e;
            }
        }
        return null;
    }

    private NullPointerException showData1(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            System.out.println("INI SHOWDATA 2");
            ProfileGuru kuInfo = new ProfileGuru();
            try {

                kuInfo.setLevel(ds.child("USER").child("GURU").child(userID).getValue(ProfileGuru.class).getLevel());
                System.out.println("kuInfo = " + kuInfo.getLevel());

                if (kuInfo.getLevel() == 3) {
                    try {
                        kuInfo.setVerifikasi(ds.child("USER").child("GURU").child(userID).getValue(ProfileGuru.class).getVerifikasi());
                        if (kuInfo.getVerifikasi().equals("true")) {
                            System.out.println("HAHA GURU");
                            Intent mIntent = new Intent(Berhasil.this, Main_Guru.class);
                            startActivity(mIntent);
                        } else {
                            toastMessage("Akun Anda Belum Ter-Verifikasi");
                        }
                    } catch (NullPointerException e) {
                        Intent sIntent = new Intent(Berhasil.this, lengkapi_data_guru.class);
                        startActivity(sIntent);
                    }
                }


            } catch (NullPointerException e) {
                return e;
            }
        }
        return null;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            Intent intent = new Intent(Berhasil.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            backToast = Toast.makeText(getBaseContext(), "Tekan Lagi Untuk Keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
