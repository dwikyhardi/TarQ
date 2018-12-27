package root.example.com.tar_q.Jamaah;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
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

import root.example.com.tar_q.R;

public class Biodata_Jamaah extends AppCompatActivity {

    private ImageButton ImgBtnBack;
    private ImageView fotoProfile;
    private TextView namaProfile,alamatProfile,noTelp,tanggalLahir;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String Lokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jamaah_biodata);

        ImgBtnBack = findViewById(R.id.imageButtonBackBiodataJamaah);
        Lokasi = getIntent().getStringExtra("Lokasi");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://lkptarq93.appspot.com");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(root.example.com.tar_q.Jamaah.Biodata_Jamaah.this, Main_Jamaah.class);
                startActivity(mIntent);
            }
        });


        namaProfile = (TextView) findViewById(R.id.textViewNamaProfileJamaah);
        alamatProfile = (TextView) findViewById(R.id.textViewAlamatProfileJamaah);
        noTelp = (TextView) findViewById(R.id.textViewNoTelpJamaah);
        tanggalLahir = (TextView) findViewById(R.id.textViewTanggalLahirJamaah);
        fotoProfile = (ImageView) findViewById(R.id.fotoProfileBiodataJamaah);
        final String userID = user.getUid();
        storageRef.child("Jamaah/FotoProfil/" + userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(fotoProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            ProfileJamaah uInfo = new ProfileJamaah();
            uInfo.setNama(ds.child("USER").child("JAMAAH").child(Lokasi).child(userID).getValue(ProfileJamaah.class).getNama());
            uInfo.setAlamat(ds.child("USER").child("JAMAAH").child(Lokasi).child(userID).getValue(ProfileJamaah.class).getAlamat());
            uInfo.setTanggallahir(ds.child("USER").child("JAMAAH").child(Lokasi).child(userID).getValue(ProfileJamaah.class).getTanggallahir());
            uInfo.setNohp(ds.child("USER").child("JAMAAH").child(Lokasi).child(userID).getValue(ProfileJamaah.class).getNohp());

            namaProfile.setText("Nama : "+uInfo.getNama());
            alamatProfile.setText("Alamat : "+uInfo.getAlamat());
            noTelp.setText("Nomor Telepon : "+uInfo.getNohp());
            tanggalLahir.setText("Tanggal Lahir : "+uInfo.getTanggallahir());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Biodata_Jamaah.this, Main_Jamaah.class);
        intent.putExtra("Lokasi",Lokasi);
        startActivity(intent);
    }
}

