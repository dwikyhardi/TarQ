package root.example.com.tar_q.Guru;

import android.content.Intent;
import android.graphics.Bitmap;
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

import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;
import root.example.com.tar_q.ScanBarcode;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class Presensi_Guru extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "Guru_Materi";
    private String Lokasi,userID;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ImageView imageProfileGuru;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private TextView NamaGuru, EmailGuru;

    private Button Scan;
    private ImageView BarcodeGuru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_presensi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(Presensi_Guru.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(Presensi_Guru.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Judul");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setCaptureActivity(ScanBarcode.class);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
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
            Intent mIntent = new Intent(Presensi_Guru.this, Biodata_Guru.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Prosensi) {
            Intent mIntent = new Intent(Presensi_Guru.this, Presensi_Guru.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_data_jamaah) {
            Intent mIntent = new Intent(Presensi_Guru.this, Data_Jamaah.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_jadwal_mengajar) {
            Intent mIntent = new Intent(Presensi_Guru.this, Jadwal_Guru.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Materi_pengajaran) {
            Intent mIntent = new Intent(Presensi_Guru.this, Guru_Materi.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Potential_pendapatan) {
            Intent mIntent = new Intent(Presensi_Guru.this, Guru_Potensi.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Progres_report) {
            Intent mIntent = new Intent(Presensi_Guru.this, Guru_Progres.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Pendapatan) {
            Intent mIntent = new Intent(Presensi_Guru.this, Guru_Pendapatan.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_main_guru) {
            Intent mIntent = new Intent(Presensi_Guru.this, Main_Guru.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Tentang) {
            toastMessage("tentang");
            /*Intent mIntent = new Intent(Data_Jamaah.this, Authors.class);
            startActivity(mIntent);*/
        }else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent mIntent = new Intent(Presensi_Guru.this, MainActivity.class);
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
            Log.d(TAG, "onActivityResult() returned: " + naun);
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
            Intent intent = new Intent(Presensi_Guru.this, Main_Guru.class);
            intent.putExtra("Lokasi", Lokasi);
            startActivity(intent);
        }
    }
}