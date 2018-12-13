package root.example.com.tar_q.Guru;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.LocationManager;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Calendar;

/*import id.zelory.compressor.Compressor;*/
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import root.example.com.tar_q.Berhasil;
import root.example.com.tar_q.Jamaah.lengkapi_data_jamaah;
import root.example.com.tar_q.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;
import static root.example.com.tar_q.Jamaah.lengkapi_data_jamaah.ALAMAT;
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
    private EditText editTextNoPlat;
    private Button btnTambahGuru;
    private Button btnChooseSIM;
    private TextView EditTexttanggallahir;

    //Checkbox
    private CheckBox cb_Pratahsin, cb_Tahsin, cb_Bahasa_arab, cb_SKI, cb_Tahfizh, cb_Lanjutan;
    private Button Check;

    //Lokasi
    private GoogleMap mMap;
    ArrayList<LatLng> lispoints;
    public LatLng alamatLatLng = null;
    public Double alamatLatitude, alamatLongitude;
    public LatLng indonesia;
    private static final int LOCATION_REQUEST = 500;

    //Date
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

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
    private TextView tvPath;
    private String[] items = {"Camera", "Gallery"};
    private ImageView BtnFotoProfile;
    /*Compressor mCompressor;*/

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static final int ALAMAT = 1;
    private static int REQUEST_CODE = 0;

    LocationManager locationManager;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lengkapi_data_guru);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        editTextNama = (EditText) findViewById(R.id.EditTextnama);
        editTextAlamat = (EditText) findViewById(R.id.EditTextalamat);
        editTextNohp = (EditText) findViewById(R.id.EditTextnohp);
        editTextNoPlat = (EditText) findViewById(R.id.EditTextNoPlat);
        mDisplayDate = (TextView) findViewById(R.id.TextViewtanggallahir);

        //Initialize Views
        btnChooseSTNK = (Button) findViewById(R.id.btnChooseSTNK);
        imageViewSTNK = (ImageView) findViewById(R.id.imgViewSTNK);
        btnChooseSIM = (Button) findViewById(R.id.btnChooseSIM);
        imageViewSIM = (ImageView) findViewById(R.id.imgViewSIM);
        BtnFotoProfile = (ImageView) findViewById(R.id.BtnFotoProfile);
        btnTambahGuru = (Button) findViewById(R.id.btnTambahGuru);

        //Checkbox
        cb_Pratahsin = (CheckBox) findViewById(R.id.cb_Pratahsin);
        cb_Tahsin = (CheckBox) findViewById(R.id.cb_Tahsin);
        cb_Bahasa_arab = (CheckBox) findViewById(R.id.cb_Bahasa_arab);
        cb_SKI = (CheckBox) findViewById(R.id.cb_SKI);
        cb_Tahfizh = (CheckBox) findViewById(R.id.cb_Tahfizh);
        cb_Lanjutan = (CheckBox) findViewById(R.id.cb_Lanjutan);
        Check = (Button) findViewById(R.id.check);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Maps

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
                String tanggallahir = mDisplayDate.getText().toString().trim();
                String noPlat = editTextNoPlat.getText().toString().trim();
                String latitude = "";
                String longitude = "";
                if (alamatLatitude != null) {
                    latitude = alamatLatitude.toString().trim();
                    longitude = alamatLongitude.toString().trim();
                }
                Log.d("ISI ====", nama + " , " + nohp + " , " + alamat + " , " + tanggallahir + " , " + " , " + noPlat);

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

                if (nama.equals("")&& nohp.equals("") && alamat.equals("") && tanggallahir.equals("")
                        && noPlat.equals("")) {
                    showSnackbar(v, "Harap Lengkapi Semua Kolom", 3000);
                    return;
                }else{
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    UserGuru newUser = new UserGuru(userID, nama, nohp, alamat, tanggallahir, latitude, longitude);
                    myRef.child("TARQ").child("USER").child("GURU").child(userID).setValue(newUser);
                    Intent i = new Intent(lengkapi_data_guru.this, Berhasil.class);
                    startActivity(i);
                }

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editTextAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaceAutoComplete(ALAMAT);
            }
        });
        lispoints = new ArrayList<>();

        BtnFotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageIdentitas();
            }
        });

        Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = "Pratahsin check " + cb_Pratahsin.isChecked()
                        + "\n Tahsin check " + cb_Tahsin.isChecked()
                        + "\n Bahasa Arab check " + cb_Bahasa_arab.isChecked()
                        + "\n SKI check " + cb_SKI.isChecked()
                        + "\n Tahfizh check " + cb_Tahfizh.isChecked()
                        + "\n Lanjutan check " + cb_Lanjutan.isChecked();

                Toast.makeText(lengkapi_data_guru.this,status, Toast.LENGTH_LONG).show();
            }
        });

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        lengkapi_data_guru.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);

                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };

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
}