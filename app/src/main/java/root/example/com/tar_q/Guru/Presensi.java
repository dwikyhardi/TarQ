package root.example.com.tar_q.Guru;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import root.example.com.tar_q.Jamaah.PresensiJamaah;
import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;
import root.example.com.tar_q.ScanBarcode;

public class Presensi extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Guru_Materi";
    private String Lokasi, userID;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ImageView imageProfileGuru;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef, refLatlng, refLatLngPrivate, refLatLngKantor;
    private TextView NamaGuru, EmailGuru;
    private Double LatGuru, LngGuru;
    private Double LatTempatAjarPrivate, LngTempatAjarPrivate, LatTempatAjarKantor, LngTempatAjarKantor;
    private Location tempatAjar = new Location("");
    private Location guru = new Location("");

    private String timeStamp;

    private Button Scan;
    private ImageView BarcodeGuru;

    private ArrayList<String> idAjar = new ArrayList<>();
    private ArrayList<String> PelajaranH = new ArrayList<>();
    private ArrayList<String> Jam = new ArrayList<>();
    private ArrayList<String> Kelas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_presensi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(Presensi.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_guru);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);


        idAjar = getIntent().getStringArrayListExtra("List Ajar");
        PelajaranH = getIntent().getStringArrayListExtra("Pelajaran");
        Jam = getIntent().getStringArrayListExtra("Jam");
        Kelas = getIntent().getStringArrayListExtra("Kelas");
        Lokasi = getIntent().getStringExtra("Lokasi");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("TARQ").child("FORM").child(Lokasi);
        refLatlng = mFirebaseDatabase.getReference();
        refLatLngKantor = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("KANTOR").child(Lokasi);
        refLatLngPrivate = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("PRIVATE").child(Lokasi);

        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        timeStamp = timeStamp + "000";


        //Resource Layout
        BarcodeGuru = (ImageView) findViewById(R.id.BarcodeGuru);
        Scan = (Button) findViewById(R.id.ScanBarcodeGuru);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(userID, BarcodeFormat.QR_CODE, 1000, 1000);//200,200 ukuran barcodenya
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            BarcodeGuru.setImageBitmap(bitmap);
        } catch (WriterException | NullPointerException e) {
            e.printStackTrace();
        }

        //Ambil LatLng
        refLatlng.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ambilLatLngGuru(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        refLatLngPrivate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ambilLatLngTempatAjarPrivate((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        refLatLngKantor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ambilLatLngTempatAjarKantor((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(Presensi.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan Barcode");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setCaptureActivity(ScanBarcode.class);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

    }

    private void ambilLatLngTempatAjarKantor(Map<String, Object> dataSnapshot) {
        try {
            Log.d(TAG, "ambilLatLngTempatAjarKantor() returned: " + Jam);
            final ArrayList<String> IdGuru = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map idGuru = (Map) entry.getValue();
                IdGuru.add((String) idGuru.get("idguru"));
            }

            final ArrayList<String> Lat = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map lat = (Map) entry.getValue();
                Lat.add((String) lat.get("lokasilat"));
            }

            final ArrayList<String> Lng = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map lng = (Map) entry.getValue();
                Lng.add((String) lng.get("lokasilang"));
            }
            int i = 0;
            long jams = 0;
            while (Kelas.size() > i) {
                int j = 1;
                if (IdGuru.get(i).equals(userID)) {
                    String[] a = String.valueOf(Jam.get(i)).split(",");
                    do {
                        jams = (Long.parseLong(a[j]) + 7200000);
                        String jamsa = a[j];
                        if (Long.parseLong(timeStamp) < jams) {
                            long sah = jams - Long.parseLong(timeStamp);
                            if (sah <= 7200000) {
                                LatTempatAjarKantor = Double.parseDouble(Lat.get(i));
                                LngTempatAjarKantor = Double.parseDouble(Lng.get(i));
                                tempatAjar.setLatitude(LatTempatAjarKantor);
                                tempatAjar.setLongitude(LngTempatAjarKantor);
                                Log.d(TAG, "ambilLatLngTempatAjarKantor() returned: Lat " + LatTempatAjarKantor + " Lng " + LngTempatAjarKantor);
                            }
                        } else {
                            Log.d(TAG, "Gagal uyy");
                        }
                        j++;
                    } while (a.length > j);
                }
                i++;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void ambilLatLngTempatAjarPrivate(Map<String, Object> dataSnapshot) {
        try {
            Log.d(TAG, "ambilLatLngTempatAjarPrivate() returned: " + Jam);
            final ArrayList<String> IdGuru = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map idGuru = (Map) entry.getValue();
                IdGuru.add((String) idGuru.get("idguru"));
            }
            final ArrayList<String> Lat = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map lat = (Map) entry.getValue();
                Lat.add((String) lat.get("lokasilat"));
            }

            final ArrayList<String> Lng = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map lng = (Map) entry.getValue();
                Lng.add((String) lng.get("lokasilang"));
            }
            int i = 0;
            long jams = 0;
            while (Kelas.size() > i) {
                int j = 1;
                if (IdGuru.get(i).equals(userID)) {
                    String[] a = String.valueOf(Jam.get(i)).split(",");
                    do {
                        jams = (Long.parseLong(a[j]) + 7200000);
                        String jamsa = a[j];
                        if (Long.parseLong(timeStamp) < jams) {
                            long sah = jams - Long.parseLong(timeStamp);
                            if (sah <= 7200000) {
                                LatTempatAjarPrivate = Double.parseDouble(Lat.get(i));
                                LngTempatAjarPrivate = Double.parseDouble(Lng.get(i));
                                tempatAjar.setLongitude(LngTempatAjarPrivate);
                                tempatAjar.setLatitude(LatTempatAjarPrivate);
                                Log.d(TAG, "ambilLatLngTempatAjarPrivate() returned: Lat " + LatTempatAjarPrivate + " Lng " + LngTempatAjarPrivate);
                            }
                        } else {
                            Log.d(TAG, "Gagal uyy");
                        }
                        j++;
                    } while (a.length > j);
                }
                i++;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void ambilLatLngGuru(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileGuru uInfo = new ProfileGuru();
            Log.d(TAG, "showNama() returned: " + Lokasi);
            uInfo.setNama(ds.child("USER").child("GURU").child(Lokasi).child(userID).getValue(ProfileGuru.class).getNama());
            uInfo.setLatitude(ds.child("USER").child("GURU").child(Lokasi).child(userID).getValue(ProfileGuru.class).getLatitude());
            uInfo.setLongitude(ds.child("USER").child("GURU").child(Lokasi).child(userID).getValue(ProfileGuru.class).getLongitude());
            NamaGuru.setText(uInfo.getNama());
            LatGuru = Double.parseDouble(uInfo.getLatitude());
            LngGuru = Double.parseDouble(uInfo.getLongitude());
            guru.setLatitude(LatGuru);
            guru.setLongitude(LngGuru);
            Log.d(TAG, "onDataChange() returned: " + uInfo.getNama());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent mIntent = new Intent(Presensi.this, Biodata.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Prosensi) {
            Intent mIntent = new Intent(Presensi.this, Presensi.class);
            mIntent.putExtra("Lokasi", Lokasi);
            mIntent.putStringArrayListExtra("List Ajar", idAjar);
            mIntent.putStringArrayListExtra("Pelajaran", PelajaranH);
            mIntent.putStringArrayListExtra("Jam", Jam);
            mIntent.putStringArrayListExtra("Kelas", Kelas);
            startActivity(mIntent);
        } else if (id == R.id.nav_data_jamaah) {
            Intent mIntent = new Intent(Presensi.this, Data_Jamaah.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Progres_report) {
            Intent mIntent = new Intent(Presensi.this, Progres.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_main_guru) {
            Intent mIntent = new Intent(Presensi.this, Main_Guru.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Potensial) {
            Intent mIntent = new Intent(Presensi.this, Potensial.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Realcome) {
            Intent mIntent = new Intent(Presensi.this, Realcome.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Tentang) {
            toastMessage("tentang");
            /*Intent mIntent = new Intent(Data_Jamaah.this, Authors.class);
            startActivity(mIntent);*/
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent mIntent = new Intent(Presensi.this, MainActivity.class);
            startActivity(mIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //disini kode
            String naun = result.getContents();//ambil hasil scan
            int i = 0;
            long jams = 0;
            long jama = 0;
            while (idAjar.size() > i) {
                int j = 1;
                if (idAjar.get(i).contains(naun)) {
                    String[] a = String.valueOf(Jam.get(i)).split(",");
                    do {
                        jams = (Long.parseLong(a[j]) + 7200000);
                        String jamsa = a[j];
                        if (((guru.distanceTo(tempatAjar) / 1000)) <= 0.5) {
                            if (Long.parseLong(timeStamp) < jams) {
                                long sah = jams - Long.parseLong(timeStamp);
                                Log.d(TAG, "sah = " + sah);
                                if (sah <= 7200000) {
                                    myRef.child(Kelas.get(i)).child(jamsa).child("GURU").child(userID).child("ABSEN").setValue("true");
                                    myRef.child(Kelas.get(i)).child(jamsa).child("GURU").child(userID).child("id").setValue(userID);
                                    myRef.child(Kelas.get(i)).child(jamsa).child("MURID").child(naun).child("ABSEN").setValue("true");
                                    myRef.child(Kelas.get(i)).child(jamsa).child("MURID").child(naun).child("id").setValue(naun);
                                    myRef.child(Kelas.get(i)).child(jamsa).child("JADWAL").setValue(jamsa);
                                    myRef.child(Kelas.get(i)).child(jamsa).child("WAKTU ABSEN").setValue(timeStamp);
                                    myRef.child(Kelas.get(i)).child(jamsa).child("PERTEMUAN").setValue(j);
                                    myRef.child(Kelas.get(i)).child(jamsa).child("KELAS").setValue(Kelas.get(i));
                                    myRef.child(Kelas.get(i)).child(jamsa).child("PELAJARAN").setValue(PelajaranH.get(i));
                                    Log.d(TAG, "Berhasil Bray");
                                /*if (PelajaranH.get(i).equals("tahfizh")){
                                    Intent mIntent = new Intent(Presensi_Guru.this, Form_Tahfizh.class);
                                    startActivity(mIntent);
                                }else if (PelajaranH.get(i).equals("pratahsin1")){
                                    Intent mIntent = new Intent(Presensi_Guru.this, Form_Tahsin.class);
                                    startActivity(mIntent);
                                }else if (PelajaranH.get(i).equals("pratahsin2")){
                                    Intent mIntent = new Intent(Presensi_Guru.this, Form_Tahsin.class);
                                    startActivity(mIntent);
                                }else if (PelajaranH.get(i).equals("pratahsin3")){
                                    Intent mIntent = new Intent(Presensi_Guru.this, Form_Tahsin.class);
                                    startActivity(mIntent);
                                }else if (PelajaranH.get(i).equals("tahsin1")){
                                    Intent mIntent = new Intent(Presensi_Guru.this, Form_Tahsin.class);
                                    startActivity(mIntent);
                                }else if (PelajaranH.get(i).equals("tahsin2")){
                                    Intent mIntent = new Intent(Presensi_Guru.this, Form_Tahsin.class);
                                    startActivity(mIntent);
                                }else if (PelajaranH.get(i).equals("tahsin3")){
                                    Intent mIntent = new Intent(Presensi_Guru.this, Form_Tahsin.class);
                                    startActivity(mIntent);
                                }else if (PelajaranH.get(i).equals("tahsin4")){
                                    Intent mIntent = new Intent(Presensi_Guru.this, Form_Tahsin.class);
                                    startActivity(mIntent);
                                }else if (PelajaranH.get(i).equals("bahasaarab")){

                                }*/
                                }
                            }
                        } else {
                            Log.d(TAG, "Gagal uyy");
                        }
                        j++;
                        Log.d(TAG, "Db = " + jams);
                        Log.d(TAG, "Jam = " + timeStamp);
                    } while (a.length > j);
                } else {
                    toastMessage("GAGAL");
                }
                i++;
            }
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
            Intent intent = new Intent(Presensi.this, Main_Guru.class);
            intent.putExtra("Lokasi", Lokasi);
            startActivity(intent);
        }
    }
}