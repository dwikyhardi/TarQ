package root.example.com.tar_q.Guru;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;

public class Form_Tahfizh extends AppCompatActivity {

    private String Lokasi, userID, Sekarang;
    private String Id_Murid, Id_Kelas, Murid, Guru;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private Button Kirim;

    private EditText etSetoran, etMurajaah, etZiadah, etKeterangan;


    private String waktos;

    /*private ArrayList<String> LAPORAN = new ArrayList<>();
    private ArrayList<String> WAKTU= new ArrayList<>();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form__tahfizh);

        Lokasi = getIntent().getStringExtra("Lokasi");
        Id_Murid = getIntent().getStringExtra("Id Murid");
        Id_Kelas = getIntent().getStringExtra("NoKelas");
        Murid = getIntent().getStringExtra("Nama Murid");
        Guru = getIntent().getStringExtra("Nama Guru");

        Kirim = (Button) findViewById(R.id.KirimFormtahfizh);

        etSetoran = (EditText) findViewById(R.id.editTextSetoranTahfizh);
        etMurajaah = (EditText) findViewById(R.id.editTextMurajaahTahfizh);
        etZiadah = (EditText) findViewById(R.id.editTextZiahahTahfizh);
        etKeterangan = (EditText) findViewById(R.id.editTextKeteranganTahfizh);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("TARQ").child("FORM").child(Lokasi).child(Id_Kelas);

        Sekarang = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        Sekarang = Sekarang + "000";


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showForm((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Setoran = etSetoran.getText().toString().trim();
                String Murajaah = etMurajaah.getText().toString().trim();
                String Ziadah = etZiadah.getText().toString().trim();
                String Keterangan = etKeterangan.getText().toString().trim();
                InputTahfizh inn = new InputTahfizh(userID, Id_Murid, Guru, Murid, Sekarang, 1, Setoran, Murajaah, Ziadah, Keterangan);
                myRef.child(waktos).child("MURID").child(Id_Murid).child("LAPORAN").setValue(inn).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent mIntent = new Intent(Form_Tahfizh.this, Main_Guru.class);
                        startActivity(mIntent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMessage("Gagal Upload, Periksa Koneksi Anda");
                    }
                });
            }
        });

    }


    private void showForm(Map<String, Object> dataSnapshot) {

        try{
            final ArrayList<String> WAKTU = new ArrayList<>();
            for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
                Map waktu = (Map) entry.getValue();
                WAKTU.add((String) waktu.get("JADWAL"));
            }
            int i = 0;
            while (WAKTU.size() > i) {
                if (Long.parseLong(Sekarang) - Long.parseLong(WAKTU.get(i)) <= 43200000) {
                    waktos = WAKTU.get(i);
                }
                i++;
            }
        }
        catch(NullPointerException e){
            e.printStackTrace();
            waktos = Sekarang;
            etSetoran.setEnabled(false);
            etZiadah.setEnabled(false);
            etMurajaah.setEnabled(false);
        }
    }


    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Form_Tahfizh.this, Main_Guru.class);
        intent.putExtra("Lokasi", Lokasi);
        startActivity(intent);
    }
}
