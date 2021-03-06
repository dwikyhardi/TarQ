package root.example.com.tar_q;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import root.example.com.tar_q.Guru.Main_Guru;
import root.example.com.tar_q.Guru.ProfileGuru;
import root.example.com.tar_q.Guru.lengkapi_data_guru;
import root.example.com.tar_q.Jamaah.Main_Jamaah;
import root.example.com.tar_q.Jamaah.ProfileJamaah;
import root.example.com.tar_q.Jamaah.lengkapi_data_jamaah;

/**
 * Created by User on 2/8/2017.
 */

public class Berhasil extends AppCompatActivity {
    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef1;
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
        myRef1 = mFirebaseDatabase.getReference().child("TARQ").child("USER").child("GURU");
        System.out.println("User = " + userID);


        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showLokasi((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    private void showLokasi(Map<String, Object> dataSnapshot) {
        final ArrayList<String> Lokasi = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lokasi = (Map) entry.getValue();
            Lokasi.add((String) lokasi.get("GURU"));
        }
        final ArrayList<String> listLokasi = new ArrayList<>();
        int i = 0;
        while (Lokasi.size() > i) {
            listLokasi.add(Lokasi.get(i));
            i++;
        }
        Log.d(TAG, "showLokasi() returned: " + listLokasi);
    }

    private NullPointerException showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            System.out.println("INI SHOWDATA 1");
            ProfileJamaah uInfo = new ProfileJamaah();
            try {
                uInfo.setLevel(ds.child("USER").child("JAMAAH").child("BANDUNG").child(userID).getValue(ProfileJamaah.class).getLevel());
                if (uInfo.getLevel() == 2) {
                    try {
                        uInfo.setNama(ds.child("USER").child("JAMAAH").child("BANDUNG").child(userID).getValue(ProfileJamaah.class).getNama());
                        System.out.println("HAHA JAMAAH " + uInfo.getNama());
                        if (uInfo.getNama() == null) {
                            Intent sIntent = new Intent(Berhasil.this, lengkapi_data_jamaah.class);
                            startActivity(sIntent);
                        } else {
                            Intent mIntent = new Intent(Berhasil.this, Main_Jamaah.class);
                            mIntent.putExtra("Lokasi", "BANDUNG");
                            startActivity(mIntent);
                        }
                    } catch (NullPointerException e) {
                        Intent sIntent = new Intent(Berhasil.this, lengkapi_data_jamaah.class);
                        startActivity(sIntent);
                    }
                }
            } catch (NullPointerException e) {
                try {
                    uInfo.setLevel(ds.child("USER").child("JAMAAH").child("JAKARTA").child(userID).getValue(ProfileJamaah.class).getLevel());
                    if (uInfo.getLevel() == 2) {
                        try {
                            uInfo.setNama(ds.child("USER").child("JAMAAH").child("JAKARTA").child(userID).getValue(ProfileJamaah.class).getNama());
                            System.out.println("HAHA JAMAAH " + uInfo.getNama());
                            if (uInfo.getNama() == null) {
                                Intent sIntent = new Intent(Berhasil.this, lengkapi_data_jamaah.class);
                                startActivity(sIntent);
                            } else {
                                Intent mIntent = new Intent(Berhasil.this, Main_Jamaah.class);
                                mIntent.putExtra("Lokasi", "JAKARTA");
                                startActivity(mIntent);
                            }
                        } catch (NullPointerException d) {
                            Intent sIntent = new Intent(Berhasil.this, lengkapi_data_jamaah.class);
                            startActivity(sIntent);
                        }
                    }
                }catch (NullPointerException m){
                    try{
                        uInfo.setLevel(ds.child("USER").child("JAMAAH").child("PADANG").child(userID).getValue(ProfileJamaah.class).getLevel());
                        if (uInfo.getLevel() == 2) {
                            try {
                                uInfo.setNama(ds.child("USER").child("JAMAAH").child("PADANG").child(userID).getValue(ProfileJamaah.class).getNama());
                                System.out.println("HAHA JAMAAH " + uInfo.getNama());
                                if (uInfo.getNama() == null) {
                                    Intent sIntent = new Intent(Berhasil.this, lengkapi_data_jamaah.class);
                                    startActivity(sIntent);
                                } else {
                                    Intent mIntent = new Intent(Berhasil.this, Main_Jamaah.class);
                                    mIntent.putExtra("Lokasi", "PADANG");
                                    startActivity(mIntent);
                                }
                            } catch (NullPointerException d) {
                                Intent sIntent = new Intent(Berhasil.this, lengkapi_data_jamaah.class);
                                startActivity(sIntent);
                            }
                        }
                    }catch (NullPointerException l){
                        l.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private NullPointerException showData1(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            System.out.println("INI SHOWDATA 2");
            ProfileGuru kuInfo = new ProfileGuru();
            try {
                try {
                    kuInfo.setLatitudeRumah(ds.child("USER").child("GURU").child("BANDUNG").child(userID).getValue(ProfileGuru.class).getLatitudeRumah());
                    kuInfo.setLongitudeRumah(ds.child("USER").child("GURU").child("BANDUNG").child(userID).getValue(ProfileGuru.class).getLongitudeRumah());

                    if (kuInfo.getLatitudeRumah().equals("0") && kuInfo.getLongitudeRumah().equals("0")) {
                        Intent mIntent = new Intent(Berhasil.this, GantiPassword.class);
                        mIntent.putExtra("Lokasi", "BANDUNG");
                        startActivity(mIntent);
                    } else {
                        kuInfo.setLevel(ds.child("USER").child("GURU").child("BANDUNG").child(userID).getValue(ProfileGuru.class).getLevel());
                        System.out.println("kuInfo = " + kuInfo.getLevel());

                        if (kuInfo.getLevel() == 3) {
                            try {
                                kuInfo.setVerifikasi(ds.child("USER").child("GURU").child("BANDUNG").child(userID).getValue(ProfileGuru.class).getVerifikasi());

                                if (kuInfo.getVerifikasi().equals("true")) {
                                    System.out.println("Bandung");
                                    Intent mIntent = new Intent(Berhasil.this, Main_Guru.class);
                                    mIntent.putExtra("Lokasi", "BANDUNG");
                                    startActivity(mIntent);

                                } else {
                                    toastMessage("Akun Anda Belum Ter-Verifikasi");
                                }
                            } catch (NullPointerException e) {
                                Intent sIntent = new Intent(Berhasil.this, lengkapi_data_guru.class);
                                startActivity(sIntent);
                            }
                        }
                    }
                } catch (NullPointerException e) {

                    try {
                        kuInfo.setLatitudeRumah(ds.child("USER").child("GURU").child("JAKARTA").child(userID).getValue(ProfileGuru.class).getLatitudeRumah());
                        kuInfo.setLongitudeRumah(ds.child("USER").child("GURU").child("JAKARTA").child(userID).getValue(ProfileGuru.class).getLongitudeRumah());

                        if (kuInfo.getLatitudeRumah().equals("0") && kuInfo.getLongitudeRumah().equals("0")) {
                            Log.d(TAG, "showData1(LngRumah) returned: " + kuInfo.getLongitudeRumah());
                            Intent mIntent = new Intent(Berhasil.this, GantiPassword.class);
                            mIntent.putExtra("Lokasi", "JAKARTA");
                            startActivity(mIntent);
                        } else {
                            kuInfo.setLevel(ds.child("USER").child("GURU").child("JAKARTA").child(userID).getValue(ProfileGuru.class).getLevel());
                            if (kuInfo.getLevel() == 3) {
                                try {
                                    kuInfo.setVerifikasi(ds.child("USER").child("GURU").child("JAKARTA").child(userID).getValue(ProfileGuru.class).getVerifikasi());
                                    if (kuInfo.getVerifikasi().equals("true")) {
                                        System.out.println("Jakarta");
                                        Intent mIntent = new Intent(Berhasil.this, Main_Guru.class);
                                        mIntent.putExtra("Lokasi", "JAKARTA");
                                        startActivity(mIntent);
                                    } else {
                                        toastMessage("Akun Anda Belum Ter-Verifikasi");
                                    }
                                } catch (NullPointerException c) {
                                    Intent sIntent = new Intent(Berhasil.this, lengkapi_data_guru.class);
                                    startActivity(sIntent);
                                }
                            }
                        }
                    } catch (NullPointerException d) {
                        try {
                            kuInfo.setLatitudeRumah(ds.child("USER").child("GURU").child("PADANG").child(userID).getValue(ProfileGuru.class).getLatitudeRumah());
                            kuInfo.setLongitudeRumah(ds.child("USER").child("GURU").child("PADANG").child(userID).getValue(ProfileGuru.class).getLongitudeRumah());

                            if (kuInfo.getLatitudeRumah().equals("0") && kuInfo.getLongitudeRumah().equals("0")) {
                                Log.d(TAG, "showData1(LngRumah) returned: " + kuInfo.getLongitudeRumah());
                                Intent mIntent = new Intent(Berhasil.this, GantiPassword.class);
                                mIntent.putExtra("Lokasi", "PADANG");
                                startActivity(mIntent);
                            } else {
                                kuInfo.setLevel(ds.child("USER").child("GURU").child("PADANG").child(userID).getValue(ProfileGuru.class).getLevel());
                                if (kuInfo.getLevel() == 3) {
                                    try {
                                        kuInfo.setVerifikasi(ds.child("USER").child("GURU").child("PADANG").child(userID).getValue(ProfileGuru.class).getVerifikasi());
                                        if (kuInfo.getVerifikasi().equals("true")) {
                                            System.out.println("Jakarta");
                                            Intent mIntent = new Intent(Berhasil.this, Main_Guru.class);
                                            mIntent.putExtra("Lokasi", "PADANG");
                                            startActivity(mIntent);
                                        } else {
                                            toastMessage("Akun Anda Belum Ter-Verifikasi");
                                        }
                                    } catch (NullPointerException c) {
                                        Intent sIntent = new Intent(Berhasil.this, lengkapi_data_guru.class);
                                        startActivity(sIntent);
                                    }
                                }
                            }
                        }catch (NullPointerException l){
                            kuInfo.setLevel(ds.child("USER").child("GURU").child(userID).getValue(ProfileGuru.class).getLevel());
                            if (kuInfo.getLevel() == 3) {
                                Intent sIntent = new Intent(Berhasil.this, lengkapi_data_guru.class);
                                startActivity(sIntent);
                            }
                        }
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
