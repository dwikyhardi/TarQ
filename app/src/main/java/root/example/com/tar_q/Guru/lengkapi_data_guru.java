package root.example.com.tar_q.Guru;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

/*import id.zelory.compressor.Compressor;*/
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import root.example.com.tar_q.Berhasil;
import root.example.com.tar_q.R;
import root.example.com.tar_q.services.getLembaga;
import root.example.com.tar_q.services.setLembaga;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;
import static root.example.com.tar_q.Jamaah.lengkapi_data_jamaah.REQUEST_CODE_CAMERA_FOTO;
import static root.example.com.tar_q.Jamaah.lengkapi_data_jamaah.REQUEST_CODE_GPS_FINE;

public class lengkapi_data_guru extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener{

    public static final int REQUEST_CODE_CAMERA_IDENTITAS = 0012;
    public static final int REQUEST_CODE_GALLERY_IDENTITAS = 0013;
    public static final int REQUEST_CODE_CAMERA_STNK = 0014;
    public static final int REQUEST_CODE_GALLERY_STNK = 0015;
    public static final int REQUEST_CODE_CAMERA_SIM = 0016;
    public static final int REQUEST_CODE_GALLERY_SIM = 0017;

    private static final String TAG = "Guru";
    private EditText editTextNama;
    private EditText editTextNohp;
    private EditText editTextAlamat;
    private TextView editTextTanggalLahir;
    private Button btnTambahGuru;
    private Button btnChooseSIM;
    private Spinner PilihLembagaGuru;

    //Checkbox
    private CheckBox cb_Pratahsin1,  cb_Pratahsin2,  cb_Pratahsin3, cb_Tahsin1,  cb_Tahsin2,  cb_Tahsin3,   cb_Tahsin4, cb_Bahasa_arab, cb_Tahfizh;
    private Button Check;

    //Gambar
    private Button btnChooseSTNK, btnChooseIdentitas;
    private ImageView imageViewIdentitas, imageViewSTNK, imageViewSIM;
    private Uri filePath1;
    private Uri filePath2;
    private Uri filePath3;
    private final int PICK_IMAGE_REQUEST_1 = 1;
    private final int PICK_IMAGE_REQUEST_2 = 2;
    private final int PICK_IMAGE_REQUEST_3 = 3;
    private Button btnLoadImage;
    private ImageView ivImage;
    private TextView tvPath,Et_Lembaga;
    private String[] items = {"Camera", "Gallery"},Lembaga;
    private ImageView BtnFotoProfile;
    private String LembagaString;
    /*Compressor mCompressor;*/

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef1;
    FirebaseStorage storage;
    StorageReference storageReference;

    //Lokasi
    private GoogleMap mMap;
    ArrayList<LatLng> lispoints;
    public LatLng alamatLatLng = null;
    public Double alamatLatitude, alamatLongitude;
    public LatLng indonesia;
    private static final int LOCATION_REQUEST = 500;
    public static final int ALAMAT = 1;
    private static int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lengkapi_data_guru);

        editTextNama = (EditText) findViewById(R.id.EditTextnama);
        editTextAlamat = (EditText) findViewById(R.id.EditTextalamat);
        editTextNohp = (EditText) findViewById(R.id.EditTextnohp);
        editTextTanggalLahir = (TextView) findViewById(R.id.EditTexttanggallahir);
        Et_Lembaga = (EditText) findViewById(R.id.Et_Lembaga);

        //Initialize Views
        btnChooseSTNK = (Button) findViewById(R.id.btnChooseSTNK);
        imageViewSTNK = (ImageView) findViewById(R.id.imgViewSTNK);
        btnChooseSIM = (Button) findViewById(R.id.btnChooseSIM);
        imageViewSIM = (ImageView) findViewById(R.id.imgViewSIM);
        BtnFotoProfile = (ImageView) findViewById(R.id.BtnFotoProfile);
        btnTambahGuru = (Button) findViewById(R.id.btnTambahGuru);
        PilihLembagaGuru = (Spinner) findViewById(R.id.PilihLembagaGuru);

        //Checkbox
        cb_Pratahsin1 = (CheckBox) findViewById(R.id.cb_Pratahsin1);
        cb_Pratahsin2 = (CheckBox) findViewById(R.id.cb_Pratahsin2);
        cb_Pratahsin3 = (CheckBox) findViewById(R.id.cb_Pratahsin3);
        cb_Tahsin1 = (CheckBox) findViewById(R.id.cb_Tahsin1);
        cb_Tahsin2 = (CheckBox) findViewById(R.id.cb_Tahsin2);
        cb_Tahsin3 = (CheckBox) findViewById(R.id.cb_Tahsin3);
        cb_Tahsin4 = (CheckBox) findViewById(R.id.cb_Tahsin4);
        cb_Bahasa_arab = (CheckBox) findViewById(R.id.cb_Bahasa_arab);
        cb_Tahfizh = (CheckBox) findViewById(R.id.cb_Tahfizh);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        myRef1 = mFirebaseDatabase.getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_IDENTITAS);

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
                // ...
            }
        };

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showdata(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editTextAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaceAutoComplete(ALAMAT);
            }
        });
        lispoints = new ArrayList<>();

        btnChooseSTNK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageSTNK();
            }
        });

        btnChooseSIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageSIM();
            }
        });

        btnTambahGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Attempting to add object to database.");
                String nama = editTextNama.getText().toString().toUpperCase().trim();
                String nohp = editTextNohp.getText().toString().trim();
                String alamat = editTextAlamat.getText().toString().toUpperCase().trim();
                String tanggallahir = editTextTanggalLahir.getText().toString().trim();
                String lembaga = PilihLembagaGuru.getSelectedItem().toString().trim();
                String lembagatxt = Et_Lembaga.getText().toString().trim();

                if (filePath1 == null && filePath2 == null && filePath3 == null){
                    showSnackbar(v, "Harap Lengkapi Foto", 3000);
                    return;
                }else if (filePath1 == null){
                    showSnackbar(v, "Harap Lengkapi Foto Profil", 3000);
                    return;
                }else if(filePath2 == null) {
                    showSnackbar(v, "Harap Lengkapi Foto STNK", 3000);
                    return;
                }else if(filePath3 == null){
                    showSnackbar(v, "Harap Lengkapi Foto SIM", 3000);
                    return;
                }else {
                    uploadImageIdentitas();
                    uploadImageSTNK();
                    uploadImageSIM();
                }

                if (nama.equals("")&& nohp.equals("") && alamat.equals("") && tanggallahir.equals("")) {
                    showSnackbar(v, "Harap Lengkapi Semua Kolom", 3000);
                    return;
                }else{
                    if (PilihLembagaGuru.getSelectedItem().equals(" ")){
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userID = user.getUid();
                        String praTahsin1 = "" + cb_Pratahsin1.isChecked();
                        String praTahsin2 = "" + cb_Pratahsin2.isChecked();
                        String praTahsin3 = "" + cb_Pratahsin3.isChecked();
                        String tahsin1 = "" + cb_Tahsin1.isChecked();
                        String tahsin2 = "" + cb_Tahsin2.isChecked();
                        String tahsin3 = "" + cb_Tahsin3.isChecked();
                        String tahsin4 = "" + cb_Tahsin4.isChecked();
                        String bahasaArab = "" + cb_Bahasa_arab.isChecked();
                        String tahfizh = "" + cb_Tahfizh.isChecked();
                        UserGuru newUser = new UserGuru(userID, nama, nohp, alamat, tanggallahir, lembagatxt, praTahsin1, praTahsin2, praTahsin3, tahsin1, tahsin2, tahsin3, tahsin4, bahasaArab, tahfizh, "0.0", "0.0");
                        myRef.child("TARQ").child("USER").child("GURU").child(userID).setValue(newUser);
                        String addLembaga = LembagaString+","+lembagatxt;
                        setLembaga mSetLembaga = new setLembaga(addLembaga);
                        Log.d(TAG, "onClick(addLembaga) returned: " + addLembaga);
                        myRef.child("TARQ").child("Lembaga").child("lembaga").setValue(mSetLembaga);
                        Intent i = new Intent(lengkapi_data_guru.this, Berhasil.class);
                        startActivity(i);
                    }else {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userID = user.getUid();
                        String praTahsin1 = "" + cb_Pratahsin1.isChecked();
                        String praTahsin2 = "" + cb_Pratahsin2.isChecked();
                        String praTahsin3 = "" + cb_Pratahsin3.isChecked();
                        String tahsin1 = "" + cb_Tahsin1.isChecked();
                        String tahsin2 = "" + cb_Tahsin2.isChecked();
                        String tahsin3 = "" + cb_Tahsin3.isChecked();
                        String tahsin4 = "" + cb_Tahsin4.isChecked();
                        String bahasaArab = "" + cb_Bahasa_arab.isChecked();
                        String tahfizh = "" + cb_Tahfizh.isChecked();
                        UserGuru newUser = new UserGuru(userID, nama, nohp, alamat, tanggallahir, lembaga, praTahsin1, praTahsin2, praTahsin3, tahsin1, tahsin2, tahsin3, tahsin4, bahasaArab, tahfizh, "0.0", "0.0");
                        myRef.child("TARQ").child("USER").child("GURU").child(userID).setValue(newUser);
                        Intent i = new Intent(lengkapi_data_guru.this, Berhasil.class);
                        startActivity(i);
                    }
                }

            }
        });

        BtnFotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageIdentitas();
            }
        });
    }

    private void showdata(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            getLembaga uInfo = new getLembaga();
            uInfo.setLembaga(ds.child("Lembaga").getValue(getLembaga.class).getLembaga());
            Lembaga = uInfo.getLembaga().split(",");
            LembagaString = uInfo.getLembaga();

        }
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Lembaga);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PilihLembagaGuru.setAdapter(mArrayAdapter);
    }


    private void chooseImageIdentitas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    EasyImage.openCamera(lengkapi_data_guru.this, REQUEST_CODE_CAMERA_IDENTITAS);
                } else if (items[i].equals("Gallery")) {
                    EasyImage.openGallery(lengkapi_data_guru.this, REQUEST_CODE_GALLERY_IDENTITAS);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void chooseImageSTNK() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    EasyImage.openCamera(lengkapi_data_guru.this, REQUEST_CODE_CAMERA_STNK);
                } else if (items[i].equals("Gallery")) {
                    EasyImage.openGallery(lengkapi_data_guru.this, REQUEST_CODE_GALLERY_STNK);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void chooseImageSIM() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    EasyImage.openCamera(lengkapi_data_guru.this, REQUEST_CODE_CAMERA_SIM);
                } else if (items[i].equals("Gallery")) {
                    EasyImage.openGallery(lengkapi_data_guru.this, REQUEST_CODE_GALLERY_SIM);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (resultCode == RESULT_OK && data != null && data.getData() != null) {

            if (requestCode == PICK_IMAGE_REQUEST_1) {
                filePath1 = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath1);
                    imageViewIdentitas.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == PICK_IMAGE_REQUEST_2){
                filePath2 = data.getData();
                Bitmap bitmip = null;
                try {
                    bitmip = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath2);
                    imageViewSTNK.setImageBitmap(bitmip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                filePath3 = data.getData();
                Bitmap bitmup = null;
                try{
                    bitmup = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath3);
                    imageViewSIM.setImageBitmap(bitmup);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }*/
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                switch (type) {
                    case REQUEST_CODE_CAMERA_IDENTITAS:
                        Glide.with(lengkapi_data_guru.this)
                                .load(imageFile)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(BtnFotoProfile);

                        filePath1 = Uri.fromFile(imageFile);
                        System.out.println("PATH ============== "+filePath1);
                        System.out.println("PATH ============== "+DiskCacheStrategy.ALL.toString());
                        break;
                    case REQUEST_CODE_GALLERY_IDENTITAS:
                        Glide.with(lengkapi_data_guru.this)
                                .load(imageFile)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(BtnFotoProfile);
                        filePath1 = Uri.fromFile(imageFile);
                        break;
                    case REQUEST_CODE_CAMERA_STNK:
                        Glide.with(lengkapi_data_guru.this)
                                .load(imageFile)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageViewSTNK);
                        filePath2 = Uri.fromFile(imageFile);
                        break;
                    case REQUEST_CODE_GALLERY_STNK:
                        Glide.with(lengkapi_data_guru.this)
                                .load(imageFile)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageViewSTNK);
                        filePath2 = Uri.fromFile(imageFile);
                        break;
                    case REQUEST_CODE_CAMERA_SIM:
                        Glide.with(lengkapi_data_guru.this)
                                .load(imageFile)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageViewSIM);
                        filePath3 = Uri.fromFile(imageFile);
                        break;
                    case REQUEST_CODE_GALLERY_SIM:
                        Glide.with(lengkapi_data_guru.this)
                                .load(imageFile)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageViewSIM);
                        filePath3 = Uri.fromFile(imageFile);
                        break;
                }
            }
        });
    }

    private void uploadImageIdentitas() {

        if (filePath1 != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userID = user.getUid();
            StorageReference ref = storageReference.child("Guru/IdentitasGuru/" + userID);
            ref.putFile(filePath1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(lengkapi_data_guru.this, "Uploaded Berhasil", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(lengkapi_data_guru.this, "Upload Gagal " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void uploadImageSTNK() {

        if (filePath2 != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userID = user.getUid();
            StorageReference ref = storageReference.child("Guru/STNK/" + userID);
            ref.putFile(filePath2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(lengkapi_data_guru.this, "Uploaded Berhasil", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(lengkapi_data_guru.this, "Upload Gagal " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void uploadImageSIM() {

        if (filePath3 != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            String userID = user.getUid();
            StorageReference ref = storageReference.child("Guru/SIM/" + userID);
            ref.putFile(filePath3)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(lengkapi_data_guru.this, "Uploaded Berhasil", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(lengkapi_data_guru.this, "Upload Gagal " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //minta permission
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermissionLocation()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setAllGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                String[] indo = "-6.175 , 106.828333".split(",");
                Double lat = Double.parseDouble(indo[0]);
                Double lng = Double.parseDouble(indo[1]);
                indonesia = new LatLng(lat, lng);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 16));
                mMap.setOnMapLongClickListener(this);
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
            String[] indo = "-6.175 , 106.828333".split(",");
            Double lat = Double.parseDouble(indo[0]);
            Double lng = Double.parseDouble(indo[1]);
            indonesia = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 16));
            mMap.setOnMapLongClickListener(this);
        }

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

    public void showPlaceAutoComplete(int typeLocation) {
        REQUEST_CODE = typeLocation;
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("ID").build();
        try {
            Intent mIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(this);
            startActivityForResult(mIntent, REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Layanan Tidak Tersedia", Toast.LENGTH_SHORT).show();
        }
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

    //add a toast to show when successfully signed in
    /**
     * customizable toast
     * @param message
     */
    public void showSnackbar(View v, String message, int duration) {
        Snackbar.make(v, message, duration).show();
    }
}