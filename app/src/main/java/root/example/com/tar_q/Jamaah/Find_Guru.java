package root.example.com.tar_q.Jamaah;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import root.example.com.tar_q.services.getLembaga;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static root.example.com.tar_q.Jamaah.lengkapi_data_jamaah.REQUEST_CODE_CAMERA_FOTO;
import static root.example.com.tar_q.Jamaah.lengkapi_data_jamaah.REQUEST_CODE_GPS_FINE;

public class Find_Guru extends AppCompatActivity
        implements
        GoogleMap.OnMapLongClickListener,
        Dialog.OnDismissListener {
    private final String TAG = "Find_guru";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef1, myRef2, myRef3, myRef4;
    private String userID;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    //maps
    private GoogleMap mMap;
    ArrayList<LatLng> lispoints;
    public LatLng alamatLatLng = null;
    public Double alamatLatitude, alamatLongitude;
    public LatLng indonesia;
    private static final int LOCATION_REQUEST = 500;


    private Spinner PilihKelas, PilihLembaga;
    private String Kelas_Atas = "", IdGuru, LembagaString;
    private Dialog dGuru;
    private Button btnRequest;
    private String[] Lembaga;
    private Spinner PilihPertemuan, lokasiBelajar;


    private String NamaGuru, NamaJamaah, AlamatGuru, NoTelpGuru, LembagaGuru, Lokasi;


    private ImageView ivFotoGuruPopup;
    private TextView etNamaGuruPopup, etNoTelpGuruPopup, etAlamatGuruPopup, etLembagaGuruPopup;


    private ListView mListViewGuru;
    private TextView TV_ATAS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_guru);

        NamaJamaah = getIntent().getStringExtra("NamaJamaah");
        Lokasi = getIntent().getStringExtra("Lokasi");
        mListViewGuru = (ListView) findViewById(R.id.listViewGuru);
        PilihKelas = (Spinner) findViewById(R.id.PilihKelas);
        TV_ATAS = (TextView) findViewById(R.id.TV_ATAS);
        dGuru = new Dialog(this);
        String[] Kelas = getResources().getStringArray(R.array.PemilihanGuru);
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_style, Kelas);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PilihKelas.setAdapter(mArrayAdapter);
        PilihLembaga = (Spinner) findViewById(R.id.PilihLembaga);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("TARQ").child("USER").child("GURU").child(Lokasi);
        myRef1 = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("PRIVATE").child(Lokasi);
        myRef4 = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("KANTOR").child(Lokasi);
        myRef2 = mFirebaseDatabase.getReference();
        myRef3 = mFirebaseDatabase.getReference();
        userID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        lispoints = new ArrayList<>();

        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData2(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showdata1(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                PilihLembaga.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        LembagaString = PilihLembaga.getSelectedItem().toString().trim();

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
                    public void onNothingSelected(AdapterView<?> parent) {
                        LembagaString = "TARQ";
                    }
                });

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
                myRef.orderByKey().startAt("A").endAt("Z");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }

    private void showData2(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileJamaah uInfo = new ProfileJamaah();
            uInfo.setLatitude(ds.child("USER").child("JAMAAH").child(Lokasi).child(userID).getValue(ProfileJamaah.class).getLatitude());
            uInfo.setLongitude(ds.child("USER").child("JAMAAH").child(Lokasi).child(userID).getValue(ProfileJamaah.class).getLongitude());

            String lat = uInfo.getLatitude();
            String lng = uInfo.getLongitude();
            alamatLatitude = Double.parseDouble(lat);
            alamatLongitude = Double.parseDouble(lng);
            Log.d(TAG, "showData2(LatLng) returned: " + alamatLatitude + "," + alamatLongitude);
        }
    }

    private void showdata1(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            getLembaga uInfo = new getLembaga();
            uInfo.setLembaga(ds.child("Lembaga").getValue(getLembaga.class).getLembaga());
            Lembaga = uInfo.getLembaga().split(",");
            LembagaString = uInfo.getLembaga();
            Log.d(TAG, "showdata1() returned: " + Lembaga);
        }
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(Find_Guru.this, R.layout.spinner_style, Lembaga);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PilihLembaga.setAdapter(mArrayAdapter);
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
        final ArrayList<String> mLembaga = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map lembaga = (Map) entry.getValue();
            mLembaga.add((String) lembaga.get("lembaga"));
        }
        final ArrayList<String> Kelas = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map kelas = (Map) entry.getValue();
            Kelas.add((String) kelas.get(Kelas_Atas));
        }
        final ArrayList<String> listNama = new ArrayList<>();
        final ArrayList<String> listId = new ArrayList<>();
        final ArrayList<String> listNoTelp = new ArrayList<>();
        final ArrayList<String> listLembaga = new ArrayList<>();
        final ArrayList<String> listAlamat = new ArrayList<>();

        try {
            int i = 0;
            while (Nama.size() > i) {
                if (Nama.get(i) != null) {
                    if (Verifikasi.get(i).equals("true")) {
                        if (Kelas.get(i).equals("true")) {
                            if (mLembaga.get(i).equals(LembagaString)) {
                                listId.add(Id_Guru.get(i));
                                listNama.add(Nama.get(i));
                                listNoTelp.add(NoHp.get(i));
                                listLembaga.add(mLembaga.get(i));
                                listAlamat.add(Alamat.get(i));
                            }
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
        dGuru.setContentView(R.layout.popup_detail_guru);
        TextView txtclose;
        GoogleMap googleMap;

        MapView mMapView = (MapView) dGuru.findViewById(R.id.map);
        mMapView.onCreate(dGuru.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "onMapReady(OnMapReadyCallback) returned: " + googleMap);
                mMap = googleMap;
                //minta permission
                if (ActivityCompat.checkSelfPermission(Find_Guru.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Find_Guru.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                int currentApiVersion = Build.VERSION.SDK_INT;
                if (currentApiVersion >= Build.VERSION_CODES.M) {
                    if (checkPermissionLocation()) {
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.getUiSettings().setAllGesturesEnabled(true);
                        mMap.getUiSettings().setCompassEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        LatLng imah = new LatLng(alamatLatitude, alamatLongitude);
                        mMap.addMarker(new MarkerOptions().position(imah));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(imah));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(imah, 16));
                        mMap.setOnMapLongClickListener(Find_Guru.this);
                    } else {
                        requestPermissionLocation();
                        return;
                    }
                } else {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                    mMap.getUiSettings().setCompassEnabled(true);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    LatLng imah = new LatLng(alamatLatitude, alamatLongitude);
                    mMap.addMarker(new MarkerOptions().position(imah));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(imah));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(imah, 16));
                    mMap.setOnMapLongClickListener(Find_Guru.this);
                }
            }
        });

        PilihPertemuan = (Spinner) dGuru.findViewById(R.id.SpinnerJumlahPertemuan);
        ivFotoGuruPopup = (ImageView) dGuru.findViewById(R.id.imageViewFotoGuruPopup);
        etNamaGuruPopup = (TextView) dGuru.findViewById(R.id.editTextNamaGuruPopup);
        etNoTelpGuruPopup = (TextView) dGuru.findViewById(R.id.editTextNoTelpPopup);
        etAlamatGuruPopup = (TextView) dGuru.findViewById(R.id.editTextAlamatGuruPopup);
        etLembagaGuruPopup = (TextView) dGuru.findViewById(R.id.editTextLembagaPopup);
        btnRequest = (Button) dGuru.findViewById(R.id.btn_Pilih_Guru_popup);
        lokasiBelajar = (Spinner) dGuru.findViewById(R.id.lokasiBelajar);

        String[] lokasi = new String[]{"Rumah", "Kantor", "Lainnya"};
        ArrayAdapter<String> mStringArrayAdapter = new ArrayAdapter<String>(Find_Guru.this, R.layout.spinner_style_mantap, lokasi);
        mStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lokasiBelajar.setAdapter(mStringArrayAdapter);
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
        txtclose = (TextView) dGuru.findViewById(R.id.txtclose);
        txtclose.setText("X");

        lokasiBelajar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String lokasi = lokasiBelajar.getSelectedItem().toString();
                switch (lokasi) {
                    case "Kantor": {
                        lispoints.clear();
                        mMap.clear();
                        LatLng imah = new LatLng(-6.894144, 107.629769);
                        mMap.addMarker(new MarkerOptions().position(imah));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(imah));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(imah, 16));

                        lispoints.add(imah);
                        break;
                    }
                    case "Rumah": {
                        lispoints.clear();
                        mMap.clear();
                        LatLng imah = new LatLng(alamatLatitude, alamatLongitude);
                        mMap.addMarker(new MarkerOptions().position(imah));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(imah));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(imah, 16));
                        lispoints.add(imah);
                        break;
                    }
                    case "Lainnya": {
                        lispoints.clear();
                        mMap.clear();
                        Snackbar snackbar = Snackbar.make(view, "Tekan Maps Selama 5 Detik Untuk Menandai Tempat Belajar", 5000);
                        snackbar.show();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etNamaGuruPopup.setText("Nama : " + NamaGuru);
        etNoTelpGuruPopup.setText("No Telepon : " + NoTelpGuru);
        etAlamatGuruPopup.setText("Alamat : " + AlamatGuru);
        etLembagaGuruPopup.setText("Lembaga : " + LembagaGuru);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dGuru.dismiss();
            }
        });
        String[] Pertemuan = new String[]{"4", "8", "12", "16"};
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(Find_Guru.this, R.layout.spinner_style_mantap, Pertemuan);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PilihPertemuan.setAdapter(mArrayAdapter);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = myRef1.push().getKey();
                String lat = alamatLatitude.toString();
                String lng = alamatLongitude.toString();

                switch (lokasiBelajar.getSelectedItem().toString()) {
                    case "Kantor": {
                        myRef4.child(key).child("idguru").setValue(IdGuru);
                        myRef4.child(key).child("guru").setValue(NamaGuru);
                        myRef4.child(key).child("idmurid").setValue(userID);
                        myRef4.child(key).child("murid").setValue(NamaJamaah);
                        myRef4.child(key).child("jadwalhari").setValue("proses");
                        myRef4.child(key).child("jmlpertemuan").setValue(PilihPertemuan.getSelectedItem().toString());
                        myRef4.child(key).child("nokelas").setValue(key);
                        myRef4.child(key).child("pelajaran").setValue(Kelas_Atas);
                        myRef4.child(key).child("lokasilat").setValue(lat);
                        myRef4.child(key).child("lokasilang").setValue(lng);
                        dGuru.dismiss();
                        Intent mIntent = new Intent(Find_Guru.this, Main_Jamaah.class);
                        mIntent.putExtra("Lokasi", Lokasi);
                        startActivity(mIntent);
                        break;
                    }
                    case "Rumah": {
                        myRef1.child(key).child("idguru").setValue(IdGuru);
                        myRef1.child(key).child("guru").setValue(NamaGuru);
                        myRef1.child(key).child("idmurid").setValue(userID);
                        myRef1.child(key).child("murid").setValue(NamaJamaah);
                        myRef1.child(key).child("jadwalhari").setValue("proses");
                        myRef1.child(key).child("jmlpertemuan").setValue(PilihPertemuan.getSelectedItem().toString());
                        myRef1.child(key).child("nokelas").setValue(key);
                        myRef1.child(key).child("pelajaran").setValue(Kelas_Atas);
                        myRef1.child(key).child("lokasilat").setValue(lat);
                        myRef1.child(key).child("lokasilang").setValue(lng);
                        dGuru.dismiss();
                        Intent mIntent = new Intent(Find_Guru.this, Main_Jamaah.class);
                        mIntent.putExtra("Lokasi", Lokasi);
                        startActivity(mIntent);
                        break;
                    }
                    case "Lainnya": {
                        myRef1.child(key).child("idguru").setValue(IdGuru);
                        myRef1.child(key).child("guru").setValue(NamaGuru);
                        myRef1.child(key).child("idmurid").setValue(userID);
                        myRef1.child(key).child("murid").setValue(NamaJamaah);
                        myRef1.child(key).child("jadwalhari").setValue("proses");
                        myRef1.child(key).child("jmlpertemuan").setValue(PilihPertemuan.getSelectedItem().toString());
                        myRef1.child(key).child("nokelas").setValue(key);
                        myRef1.child(key).child("pelajaran").setValue(Kelas_Atas);
                        myRef1.child(key).child("lokasilat").setValue(lat);
                        myRef1.child(key).child("lokasilang").setValue(lng);
                        dGuru.dismiss();
                        Intent mIntent = new Intent(Find_Guru.this, Main_Jamaah.class);
                        mIntent.putExtra("Lokasi", Lokasi);
                        startActivity(mIntent);
                        break;
                    }
                }
            }
        });

        dGuru.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dGuru.show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (lispoints.size() >= 1) {
            mMap.clear();
            lispoints.clear();
        } else {
            lispoints.add(latLng);
            MarkerOptions mMarkerOptions = new MarkerOptions();
            mMarkerOptions.position(latLng);
            mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
            alamatLatitude = latLng.latitude;
            alamatLongitude = latLng.longitude;
        }

        Log.d("Latitude = ", alamatLatitude.toString());
        Log.d("Longitude = ", alamatLongitude.toString());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mMap.clear();
    }

    private boolean checkPermissionLocation() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissionLocation() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_CODE_GPS_FINE);
    }

    private boolean checkPermissionCamera() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissionCamera() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CODE_CAMERA_FOTO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermissionCamera()) {
            } else {
                requestPermissionCamera();
            }
            if (checkPermissionLocation()) {
            } else {
                requestPermissionLocation();
            }
        }
    }

    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Find_Guru.this, Main_Jamaah.class);
        intent.putExtra("Lokasi", Lokasi);
        startActivity(intent);
    }
}
