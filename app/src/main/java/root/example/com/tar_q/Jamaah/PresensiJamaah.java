package root.example.com.tar_q.Jamaah;

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

import root.example.com.tar_q.Awal;
import root.example.com.tar_q.Guru.ProfileGuru;
import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;
import root.example.com.tar_q.ScanBarcode;
import root.example.com.tar_q.Tentang;

public class PresensiJamaah extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "PresensiJamaah";
    private String Lokasi,userID;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private ImageView imageProfileJamaah;
    private TextView NamaJamaah, EmailJamaah;

    private Button Scan;
    private Button btn_awal;
    private ImageView BarcodeJamaah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jamaah_presensi);

        Toolbar toolbar = findViewById(R.id.toolbar_jamaah);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(PresensiJamaah.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_jamaah);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        Lokasi = getIntent().getStringExtra("Lokasi");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        Scan = (Button) findViewById(R.id.ScanBarcodeJamaah);
        btn_awal = (Button) findViewById(R.id.btn_awal);
        BarcodeJamaah = (ImageView) findViewById(R.id.BarcodeJamaah);

        //Resource Layout
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://lkptarq93.appspot.com");
        imageProfileJamaah = (ImageView) header.findViewById(R.id.imageProfileJamaah);
        NamaJamaah = (TextView) header.findViewById(R.id.textViewNamaJamaah);
        EmailJamaah = (TextView) header.findViewById(R.id.textViewEmailJamaah);
        final String userID = user.getUid();
        storageRef.child("Jamaah/FotoProfil/" + userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(imageProfileJamaah);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        EmailJamaah.setText(user.getEmail());
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
                IntentIntegrator integrator = new IntentIntegrator(PresensiJamaah.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan Barcode");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setCaptureActivity(ScanBarcode.class);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        btn_awal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(PresensiJamaah.this, Awal.class);
                startActivity(mIntent);
            }
        });

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(userID, BarcodeFormat.QR_CODE, 1000, 1000);//200,200 ukuran barcodenya
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            BarcodeJamaah.setImageBitmap(bitmap);
        } catch (WriterException | NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void showNama(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileJamaah uInfo = new ProfileJamaah();
            Log.d(TAG, "showNama() returned: " + Lokasi);
            uInfo.setNama(ds.child("USER").child("JAMAAH").child(Lokasi).child(userID).getValue(ProfileGuru.class).getNama());
            NamaJamaah.setText(uInfo.getNama());
            Log.d(TAG, "onDataChange() returned: " + uInfo.getNama());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (id == R.id.nav_logout){
            mAuth.signOut();
            Intent mIntent = new Intent(PresensiJamaah.this,MainActivity.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Prosensi_jamaah){
            Intent mIntent = new Intent(PresensiJamaah.this,PresensiJamaah.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Tentang){
            Intent mIntent = new Intent(PresensiJamaah.this,Tentang.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Account){
            Intent mIntent = new Intent(PresensiJamaah.this,Biodata_Jamaah.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }else if (id == R.id.nav_Home){
            Intent mIntent = new Intent(PresensiJamaah.this,Main_Jamaah.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        }
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent intent = new Intent(PresensiJamaah.this, Main_Jamaah.class);
            intent.putExtra("Lokasi", Lokasi);
            startActivity(intent);
        }
    }
}