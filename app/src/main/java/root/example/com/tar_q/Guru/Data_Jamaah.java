package root.example.com.tar_q.Guru;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;

public class Data_Jamaah extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Guru_Materi";
    private String Lokasi, userID;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ImageView imageProfileGuru;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef, myRef1, myRef2;
    private TextView NamaGuru, EmailGuru;

    private ListView ListJamaah;

    private ArrayList<String> Nama = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_data_jamaah);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(Data_Jamaah.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_guru);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        Lokasi = getIntent().getStringExtra("Lokasi");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        myRef1 = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child(Lokasi).child("KANTOR");
        myRef2 = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child(Lokasi).child("PRIVATE");

        //Resource Layout

        ListJamaah = (ListView) findViewById(R.id.ListJamaah);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://lkptarq93.appspot.com");
        imageProfileGuru = (ImageView) header.findViewById(R.id.imageProfileGuru);
        NamaGuru = (TextView) header.findViewById(R.id.textViewNamaGuru);
        EmailGuru = (TextView) header.findViewById(R.id.textViewEmailGuru);
        final String userID = user.getUid();
        storageRef.child("Guru/IdentitasGuru/" + userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(imageProfileGuru);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        EmailGuru.setText(user.getEmail());
        //tambahan Ieu
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showNama(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showMuridPrivate((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showMuridKantor((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showNama(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileGuru uInfo = new ProfileGuru();
            Log.d(TAG, "showNama() returned: " + Lokasi);
            uInfo.setNama(ds.child("USER").child("GURU").child(Lokasi).child(userID).getValue(ProfileGuru.class).getNama());
            NamaGuru.setText(uInfo.getNama());
            Log.d(TAG, "onDataChange() returned: " + uInfo.getNama());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent mIntent = new Intent(Data_Jamaah.this, Biodata.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Prosensi) {
            Intent mIntent = new Intent(Data_Jamaah.this, Presensi.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_data_jamaah) {
            Intent mIntent = new Intent(Data_Jamaah.this, Data_Jamaah.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Progres_report) {
            Intent mIntent = new Intent(Data_Jamaah.this, Progres.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_main_guru) {
            Intent mIntent = new Intent(Data_Jamaah.this, Main_Guru.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Potensial) {
            Intent mIntent = new Intent(Data_Jamaah.this, Potensial.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Realcome) {
            Intent mIntent = new Intent(Data_Jamaah.this, Realcome.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Tentang) {
            toastMessage("tentang");
            /*Intent mIntent = new Intent(Data_Jamaah.this, Authors.class);
            startActivity(mIntent);*/
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent mIntent = new Intent(Data_Jamaah.this, MainActivity.class);
            startActivity(mIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showMuridPrivate(Map<String, Object> dataSnapshot) {
        try {
            final ArrayList<String> Id_Murid = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map murid = (Map) entry.getValue();
                Id_Murid.add((String) murid.get("murid"));
            }
            final ArrayList<String> Id_Guru = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map guru = (Map) entry.getValue();
                Id_Guru.add((String) guru.get("idguru"));
            }
            Log.d(TAG, "showMuridPrivate() returned: " + Id_Murid);
            int i = 0;
            while (Id_Guru.size() > i) {
                if (Id_Guru.get(i).equals(userID)) {
                    Nama.add(Id_Murid.get(i));
                }
                i++;
            }
            ArrayAdapter namaJamaah = new ArrayAdapter(this, R.layout.list_view_style_request, Nama);
            ListJamaah.setAdapter(namaJamaah);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void showMuridKantor(Map<String, Object> dataSnapshot) {
        try {
            final ArrayList<String> Id_Murid = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map murid = (Map) entry.getValue();
                Id_Murid.add((String) murid.get("murid"));
            }
            final ArrayList<String> Id_Guru = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map guru = (Map) entry.getValue();
                Id_Guru.add((String) guru.get("idguru"));
            }
            Log.d(TAG, "showMuridKantor() returned: " + Id_Murid);
            int i = 0;
            while (Id_Guru.size() > i) {
                if (Id_Guru.get(i).equals(userID)) {
                    Nama.add(Id_Murid.get(i));
                }
                i++;
            }
            ArrayAdapter namaJamaah = new ArrayAdapter(this, R.layout.list_view_style_request, Nama);
            ListJamaah.setAdapter(namaJamaah);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent intent = new Intent(Data_Jamaah.this, Main_Guru.class);
            intent.putExtra("Lokasi", Lokasi);
            startActivity(intent);
        }
    }

}
