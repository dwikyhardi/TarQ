package root.example.com.tar_q.Jamaah;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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


import root.example.com.tar_q.R;

public class Find_Guru extends AppCompatActivity {
    private final String TAG = "Find_guru";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef1;
    private String userID;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    private Spinner PilihKelas;
    private String Kelas_Atas = "",IdGuru;
    private Dialog dGuru;
    private Button btnRequest;


    private String NamaGuru, NamaJamaah, AlamatGuru, NoTelpGuru, LembagaGuru;


    private ImageView ivFotoGuruPopup;
    private TextView etNamaGuruPopup, etNoTelpGuruPopup, etAlamatGuruPopup, etLembagaGuruPopup;


    private ListView mListViewGuru;
    private TextView TV_ATAS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__guru);

        NamaJamaah = getIntent().getStringExtra("NamaJamaah");

        mListViewGuru = (ListView) findViewById(R.id.listViewGuru);
        PilihKelas = (Spinner) findViewById(R.id.PilihKelas);
        TV_ATAS = (TextView) findViewById(R.id.TV_ATAS);
        dGuru = new Dialog(this);
        String[] Kelas = getResources().getStringArray(R.array.PemilihanGuru);
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_style, Kelas);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PilihKelas.setAdapter(mArrayAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("TARQ").child("USER").child("GURU");
        myRef1 = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("PRIVATE");
        userID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        PilihKelas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected() called with: adapterView = [" + adapterView + "], view = [" + view + "], i = [" + i + "], l = [" + l + "]");
                Kelas_Atas = PilihKelas.getSelectedItem().toString().trim();
                Log.d(TAG, "onItemSelected() Awal returned: " + Kelas_Atas);

                switch (Kelas_Atas) {
                    case "Pra Tahsin 3": {
                        Kelas_Atas = "pratahsin3";
                        break;
                    }
                    case "Pra Tahsin 2": {
                        Kelas_Atas = "pratahsin2";
                        break;
                    }
                    case "Pra Tahsin 1": {
                        Kelas_Atas = "pratahsin1";
                        break;
                    }
                    case "Tahsin 1": {
                        Kelas_Atas = "tahsin1";
                        break;
                    }
                    case "Tahsin 2": {
                        Kelas_Atas = "tahsin2";
                        break;
                    }
                    case "Tahsin 3": {
                        Kelas_Atas = "tahsin3";
                        break;
                    }
                    case "Tahsin 4": {
                        Kelas_Atas = "tahsin4";
                        break;
                    }
                    case "Tahfizh": {
                        Kelas_Atas = "tahfizh";
                        break;
                    }
                    case "Bahasa Arab": {
                        Kelas_Atas = "bahasaarab";
                        break;
                    }
                }
                Log.d(TAG, "onItemSelected() Akhir returned: " + Kelas_Atas);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        showData((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showData(Map<String, Object> dataSnapshot) {

        final ArrayList<String> Nama = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nama = (Map) entry.getValue();
            Nama.add((String) nama.get("nama"));
        }

        final ArrayList<String> Id_Guru = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_guru = (Map) entry.getValue();
            Id_Guru.add((String) id_guru.get("id_user"));
        }

        final ArrayList<String> Alamat = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map alamat = (Map) entry.getValue();
            Alamat.add((String) alamat.get("alamat"));
        }

        final ArrayList<String> Latitude = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map latitude = (Map) entry.getValue();
            Latitude.add((String) latitude.get("latitude"));
        }
        final ArrayList<String> Longitude = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map longitude = (Map) entry.getValue();
            Longitude.add((String) longitude.get("longitude"));
        }
        final ArrayList<String> NoHp = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nohp = (Map) entry.getValue();
            NoHp.add((String) nohp.get("nohp"));
        }
        final ArrayList<String> Verifikasi = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map verifikasi = (Map) entry.getValue();
            Verifikasi.add((String) verifikasi.get("verifikasi"));
        }
        final ArrayList<String> Lembaga = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lembaga = (Map) entry.getValue();
            Lembaga.add((String) lembaga.get("lembaga"));
        }
        final ArrayList<String> Kelas = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map kelas = (Map) entry.getValue();
            Kelas.add((String) kelas.get(Kelas_Atas));
        }
        Log.d(TAG, "showData() returned: NAMAMU" + Nama);
        Log.d(TAG, "showData() returned: NAMAMU" + Nama);

        Log.d(TAG, "showData() returned: " + Kelas_Atas);
        final ArrayList<String> listNama = new ArrayList<>();
        final ArrayList<String> listId = new ArrayList<>();
        final ArrayList<String> listNoTelp = new ArrayList<>();
        final ArrayList<String> listLembaga = new ArrayList<>();
        final ArrayList<String> listAlamat = new ArrayList<>();

        try {
            int i = 0;
            while (Nama.size() > i) {
                if(Nama.get(i) != null){
                    if (Verifikasi.get(i).equals("true")) {
                        if (Kelas.get(i).equals("true")) {
                            listId.add(Id_Guru.get(i));
                            listNama.add(Nama.get(i));
                            listNoTelp.add(NoHp.get(i));
                            listLembaga.add(Lembaga.get(i));
                            listAlamat.add(Alamat.get(i));
                        }
                    }
                }
                i++;
            }
            Log.d(TAG, "onItemClick() returnee " + listNama);
        } catch (NullPointerException e) {
        }

        mListViewGuru.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NamaGuru = listNama.get(position);
                AlamatGuru = listAlamat.get(position);
                NoTelpGuru = listNoTelp.get(position);
                LembagaGuru = listLembaga.get(position);
                IdGuru = listId.get(position);
                ShowPopupGuru(view);
            }
        });
        ArrayAdapter namaGuru = new ArrayAdapter(this, R.layout.list_view_style, listNama);
        mListViewGuru.setAdapter(namaGuru);
    }


    public void ShowPopupGuru(View v) {
        TextView txtclose;
        dGuru.setContentView(R.layout.popup_detail_guru);
        ivFotoGuruPopup = (ImageView) dGuru.findViewById(R.id.imageViewFotoGuruPopup);
        etNamaGuruPopup = (TextView) dGuru.findViewById(R.id.editTextNamaGuruPopup);
        etNoTelpGuruPopup = (TextView) dGuru.findViewById(R.id.editTextNoTelpPopup);
        etAlamatGuruPopup = (TextView) dGuru.findViewById(R.id.editTextAlamatGuruPopup);
        etLembagaGuruPopup = (TextView) dGuru.findViewById(R.id.editTextLembagaPopup);
        btnRequest = (Button) dGuru.findViewById(R.id.btn_Pilih_Guru_popup);


        storageReference.child("Guru/IdentitasGuru/" + IdGuru).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(ivFotoGuruPopup);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        txtclose =(TextView) dGuru.findViewById(R.id.txtclose);
        txtclose.setText("X");

        etNamaGuruPopup.setText("Nama : " + NamaGuru);
        etNoTelpGuruPopup.setText("No Telepon : " + NoTelpGuru);
        etAlamatGuruPopup.setText("Alamat : "+AlamatGuru);
        etLembagaGuruPopup.setText("Lembaga : " + LembagaGuru);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dGuru.dismiss();
            }
        });
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = myRef1.push().getKey();
                myRef1.child(key).child("idguru").setValue(IdGuru);
                myRef1.child(key).child("guru").setValue(NamaGuru);
                myRef1.child(key).child("idmurid").setValue(userID);
                myRef1.child(key).child("murid").setValue(NamaJamaah);
                myRef1.child(key).child("jadwalhari").setValue("proses");
                myRef1.child(key).child("nokelas").setValue(key);
                dGuru.dismiss();
                Intent mIntent = new Intent(Find_Guru.this, Main_Jamaah.class);
                startActivity(mIntent);
            }
        });

        dGuru.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dGuru.show();
    }

    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
