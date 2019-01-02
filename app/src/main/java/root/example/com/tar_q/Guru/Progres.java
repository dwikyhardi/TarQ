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
import android.widget.AdapterView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;

public class Progres extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "Guru_Materi";
    private String Lokasi, userID;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ImageView imageProfileGuru;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef, myRef1;
    private TextView NamaGuru, EmailGuru;
    private String Sekarang;

    private ArrayList<String> listNamaForm = new ArrayList<>();
    private ArrayList<String> listNamasForm = new ArrayList<>();
    private ArrayList<String> listIdForm = new ArrayList<>();
    private ArrayList<String> listPelajaranForm = new ArrayList<>();
    private ArrayList<String> listNoKelasForm = new ArrayList<>();
    private ListView mListViewNamaForm;

    private String HNama, HNamas, HId, HPelajaran, HNokelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_progres);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(Progres.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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


        myRef = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("PRIVATE").child(Lokasi);
        myRef1 = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("KANTOR").child(Lokasi);

        //Resource Layout
        mListViewNamaForm = (ListView) findViewById(R.id.ListLaporan);
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

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showNotification1((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showNotification((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Sekarang = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        Sekarang = Sekarang + "000";

        mListViewNamaForm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HId = listIdForm.get(position);
                HPelajaran = listPelajaranForm.get(position);
                HNokelas = listNoKelasForm.get(position);
                HNama = listNamaForm.get(position);
                HNamas = listNamasForm.get(position);
                if (HPelajaran.equals("tahfizh")) {
                    Intent mIntent = new Intent(Progres.this, Form_Tahfizh.class);
                    mIntent.putExtra("Lokasi", Lokasi);
                    mIntent.putExtra("Id Murid", HId);
                    mIntent.putExtra("NoKelas", HNokelas);
                    mIntent.putExtra("Nama Murid", HNama);
                    mIntent.putExtra("Nama Guru", HNamas);
                    startActivity(mIntent);
                } else if (HPelajaran.equals("pratahsin1")) {
                    Intent mIntent = new Intent(Progres.this, Form_Tahsin.class);
                    mIntent.putExtra("Lokasi", Lokasi);
                    mIntent.putExtra("Id Murid", HId);
                    mIntent.putExtra("Perlajaran", HPelajaran);
                    mIntent.putExtra("NoKelas", HNokelas);
                    mIntent.putExtra("Nama Murid", HNama);
                    mIntent.putExtra("Nama Guru", HNamas);
                    startActivity(mIntent);
                } else if (HPelajaran.equals("pratahsin2")) {
                    Intent mIntent = new Intent(Progres.this, Form_Tahsin.class);
                    mIntent.putExtra("Lokasi", Lokasi);
                    mIntent.putExtra("Id Murid", HId);
                    mIntent.putExtra("Perlajaran", HPelajaran);
                    mIntent.putExtra("NoKelas", HNokelas);
                    mIntent.putExtra("Nama Murid", HNama);
                    mIntent.putExtra("Nama Guru", HNamas);
                    startActivity(mIntent);
                } else if (HPelajaran.equals("pratahsin3")) {
                    Intent mIntent = new Intent(Progres.this, Form_Tahsin.class);
                    mIntent.putExtra("Lokasi", Lokasi);
                    mIntent.putExtra("Id Murid", HId);
                    mIntent.putExtra("NoKelas", HNokelas);
                    mIntent.putExtra("Nama Murid", HNama);
                    mIntent.putExtra("Nama Guru", HNamas);
                    startActivity(mIntent);
                } else if (HPelajaran.equals("tahsin1")) {
                    Intent mIntent = new Intent(Progres.this, Form_Tahsin.class);
                    mIntent.putExtra("Lokasi", Lokasi);
                    mIntent.putExtra("Id Murid", HId);
                    mIntent.putExtra("NoKelas", HNokelas);
                    mIntent.putExtra("Nama Murid", HNama);
                    mIntent.putExtra("Nama Guru", HNamas);
                    startActivity(mIntent);
                } else if (HPelajaran.equals("tahsin2")) {
                    Intent mIntent = new Intent(Progres.this, Form_Tahsin.class);
                    mIntent.putExtra("Lokasi", Lokasi);
                    mIntent.putExtra("Id Murid", HId);
                    mIntent.putExtra("NoKelas", HNokelas);
                    mIntent.putExtra("Nama Murid", HNama);
                    mIntent.putExtra("Nama Guru", HNamas);
                    startActivity(mIntent);
                } else if (HPelajaran.equals("tahsin3")) {
                    Intent mIntent = new Intent(Progres.this, Form_Tahsin.class);
                    mIntent.putExtra("Lokasi", Lokasi);
                    mIntent.putExtra("Id Murid", HId);
                    mIntent.putExtra("NoKelas", HNokelas);
                    mIntent.putExtra("Nama Murid", HNama);
                    mIntent.putExtra("Nama Guru", HNamas);
                    startActivity(mIntent);
                } else if (HPelajaran.equals("tahsin4")) {
                    Intent mIntent = new Intent(Progres.this, Form_Tahsin.class);
                    mIntent.putExtra("Lokasi", Lokasi);
                    mIntent.putExtra("Id Murid", HId);
                    mIntent.putExtra("NoKelas", HNokelas);
                    mIntent.putExtra("Nama Murid", HNama);
                    mIntent.putExtra("Nama Guru", HNamas);
                    startActivity(mIntent);
                } else if (HPelajaran.equals("bahasaarab")) {
                    Intent mIntent = new Intent(Progres.this, Form_Tahsin.class);
                    mIntent.putExtra("Lokasi", Lokasi);
                    mIntent.putExtra("Id Murid", HId);
                    mIntent.putExtra("NoKelas", HNokelas);
                    mIntent.putExtra("Nama Murid", HNama);
                    mIntent.putExtra("Nama Guru", HNamas);
                    startActivity(mIntent);
                }
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
            Intent mIntent = new Intent(Progres.this, Biodata.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Prosensi) {
            Intent mIntent = new Intent(Progres.this, Presensi.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_data_jamaah) {
            Intent mIntent = new Intent(Progres.this, Data_Jamaah.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Progres_report) {
            Intent mIntent = new Intent(Progres.this, Progres.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_main_guru) {
            Intent mIntent = new Intent(Progres.this, Main_Guru.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Tentang) {
            toastMessage("tentang");
            /*Intent mIntent = new Intent(Data_Jamaah.this, Authors.class);
            startActivity(mIntent);*/
        }else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent mIntent = new Intent(Progres.this, MainActivity.class);
            startActivity(mIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showNotification(Map<String, Object> dataSnapshot) {

        final ArrayList<String> Jadwalhari = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map jadwalhari = (Map) entry.getValue();
            Jadwalhari.add((String) jadwalhari.get("jadwalhari"));
        }
        final ArrayList<String> NamaJamaah = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map namajamaah = (Map) entry.getValue();
            NamaJamaah.add((String) namajamaah.get("murid"));
        }
        final ArrayList<String> NamaGuru = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map namaguru = (Map) entry.getValue();
            NamaGuru.add((String) namaguru.get("guru"));
        }
        final ArrayList<String> NoKelas = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map noKelas = (Map) entry.getValue();
            NoKelas.add((String) noKelas.get("nokelas"));
        }
        final ArrayList<String> IdGuru = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map idGuru = (Map) entry.getValue();
            IdGuru.add((String) idGuru.get("idguru"));
        }
        final ArrayList<String> IdMurid = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map idMurid = (Map) entry.getValue();
            IdMurid.add((String) idMurid.get("idmurid"));
        }
        final ArrayList<String> Pelajaran = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map pelajaran = (Map) entry.getValue();
            Pelajaran.add((String) pelajaran.get("pelajaran"));
        }
        if (Jadwalhari != null) {
            int i = 0;
            while (Jadwalhari.size() > i) {
                if (IdGuru.get(i).equals(userID)) {
                    if (Jadwalhari.get(i).equals("proses")) {
                    }
                    else if (Jadwalhari.get(i).equals("request")) {
                    }
                    else if (Jadwalhari.get(i).equals("false")) {
                    } else {
                        int j = 1;
                        String[] a = String.valueOf(Jadwalhari.get(i)).split(",");
                        while (a.length > j) {
                            int k = 0;
                            if (Long.parseLong(a[j]) < Long.parseLong(Sekarang)) {
                                if (Long.parseLong(Sekarang) - Long.parseLong(a[j]) <= 43200000) {
                                    String[] name = NamaJamaah.get(i).split(",");
                                    String[] id = IdMurid.get(i).split(",");
                                    while (name.length > k) {
                                        listIdForm.add(id[k]);
                                        listPelajaranForm.add(Pelajaran.get(i));
                                        listNoKelasForm.add(NoKelas.get(i));
                                        listNamasForm.add(NamaGuru.get(i));
                                        listNamaForm.add(name[k]);
                                        k++;
                                    }
                                }
                            }
                            j++;
                        }
                    }
                }
                i++;
            }
        }
    }

    private void showNotification1(Map<String, Object> dataSnapshot) {

        final ArrayList<String> Jadwalhari = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map jadwalhari = (Map) entry.getValue();
            Jadwalhari.add((String) jadwalhari.get("jadwalhari"));
        }
        final ArrayList<String> NamaJamaah = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map namajamaah = (Map) entry.getValue();
            NamaJamaah.add((String) namajamaah.get("murid"));
        }
        final ArrayList<String> NamaGuru = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map namaguru = (Map) entry.getValue();
            NamaGuru.add((String) namaguru.get("guru"));
        }
        final ArrayList<String> NoKelas = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map noKelas = (Map) entry.getValue();
            NoKelas.add((String) noKelas.get("nokelas"));
        }
        final ArrayList<String> IdGuru = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map idGuru = (Map) entry.getValue();
            IdGuru.add((String) idGuru.get("idguru"));
        }
        final ArrayList<String> IdMurid = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map idMurid = (Map) entry.getValue();
            IdMurid.add((String) idMurid.get("idmurid"));
        }
        final ArrayList<String> Pelajaran = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map pelajaran = (Map) entry.getValue();
            Pelajaran.add((String) pelajaran.get("pelajaran"));
        }
        if (Jadwalhari != null) {
            int i = 0;
            while (Jadwalhari.size() > i) {
                if (IdGuru.get(i).equals(userID)) {
                    Log.d(TAG, "Jadwal Hari samadengan = " + Jadwalhari.get(i));
                    if (Jadwalhari.get(i).equals("proses")) {
                    }
                    else if (Jadwalhari.get(i).equals("request")) {
                    }
                    else if (Jadwalhari.get(i).equals("false")) {
                    } else {
                        int j = 1;
                        String[] a = String.valueOf(Jadwalhari.get(i)).split(",");
                        while (a.length > j) {
                            int k = 0;
                            if (Long.parseLong(a[j]) < Long.parseLong(Sekarang)) {
                                if (Long.parseLong(Sekarang) - Long.parseLong(a[j]) <= 43200000) {
                                    String[] name = NamaJamaah.get(i).split(",");
                                    String[] id = IdMurid.get(i).split(",");
                                    while (name.length > k) {
                                        Log.d(TAG, "Split Regex = " + name[k]);
                                        listIdForm.add(id[k]);
                                        listPelajaranForm.add(Pelajaran.get(i));
                                        listNoKelasForm.add(NoKelas.get(i));
                                        listNamasForm.add(NamaGuru.get(i));
                                        listNamaForm.add(name[k]);
                                        k++;
                                    }
                                }
                            }
                            j++;
                        }
                    }
                }
                i++;
            }
        }

        ArrayAdapter namaForm = new ArrayAdapter(this, R.layout.list_view_style_request, listNamaForm);
        mListViewNamaForm.setAdapter(namaForm);
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
            Intent intent = new Intent(Progres.this, Main_Guru.class);
            intent.putExtra("Lokasi", Lokasi);
            startActivity(intent);
        }
    }
}
