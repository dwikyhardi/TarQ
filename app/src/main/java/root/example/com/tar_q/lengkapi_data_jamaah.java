package root.example.com.tar_q;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.location.places.Place;
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

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class lengkapi_data_jamaah extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "Jamaah";

    EditText editTextNama;
    EditText editTextEmail;
    EditText editTextNohp;
    TextView editTextAlamat;
    EditText editTextIdentitas;
    Button btnTambah;
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
    private Button btnChooseIdentitasJamaah;
    private Button btnChooseFotoJamaah;
    private ImageView imageViewIdentitasJamaah, imgViewFotoJamaah;
    private Uri filePath1;
    private Uri filePath2;
    public static final int REQUEST_CODE_CAMERA_IDENTITAS = 0022;
    public static final int REQUEST_CODE_GALLERY_IDENTITAS = 0023;
    public static final int REQUEST_CODE_CAMERA_FOTO = 0020;
    public static final int REQUEST_CODE_GALLERY_FOTO = 0021;
    private String[] items = {"Camera", "Gallery"};
    private ImageView BtnFotoProfile;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    public static final int ALAMAT = 1;
    private static int REQUEST_CODE = 0;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lengkapi_data_jamaah);

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            editTextNama = (EditText) findViewById(R.id.EditTextnama);
            editTextNohp = (EditText) findViewById(R.id.EditTextnohp);
            editTextAlamat = (TextView) findViewById(R.id.EditTextalamat);
            btnTambah = (Button) findViewById(R.id.tambahJamaah);
            editTextIdentitas = (EditText) findViewById(R.id.EditTextidentitas);
            mDisplayDate = (TextView) findViewById(R.id.EditTexttanggallahir);

            //Initialize Views
            btnChooseIdentitasJamaah = (Button) findViewById(R.id.btnChooseIdentitasJamaah);
            imageViewIdentitasJamaah = (ImageView) findViewById(R.id.imgViewIdentitasJamaah);
            imgViewFotoJamaah = (ImageView) findViewById(R.id.BtnFotoProfile);


            //declare the database reference object. This is what we use to access the database.
            //NOTE: Unless you are signed in, this will not be useable.
            mAuth = FirebaseAuth.getInstance();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();

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

            btnChooseIdentitasJamaah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseImageIdentitasJamaah();
                }
            });

            btnChooseFotoJamaah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseImageFotoJamaah();
                }
            });

            btnTambah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Attempting to add object to database.");
                    String nama = editTextNama.getText().toString().toUpperCase().trim();
                    String nohp = editTextNohp.getText().toString().trim();
                    String alamat = editTextAlamat.getText().toString().toUpperCase().trim();
                    String tanggallahir = mDisplayDate.getText().toString().trim();
                    String noIdentitas = editTextIdentitas.getText().toString().trim();
                    String latitude = "";
                    String longitude = "";
                    if (alamatLatitude != null){
                        latitude = alamatLatitude.toString().trim();
                        longitude = alamatLongitude.toString().trim();
                    }

                    Log.d("ISI", nama + " , " + nohp + " , " + alamat + " , " + tanggallahir + " , " + noIdentitas);

                    if (!nama.equals("") && !nohp.equals("") && !alamat.equals("") && !tanggallahir.equals("")
                            && !noIdentitas.equals("")&& !latitude.equals("") && !longitude.equals("")) {
                        if (filePath1 == null && filePath2 == null) {
                            showSnackbar(v, "Harap Lengkapi Foto", 3000);
                            return;
                        } else if (filePath1 == null) {
                            showSnackbar(v, "Harap Lengkapi KTP", 3000);
                            return;
                        } else if (filePath2 == null) {
                            showSnackbar(v, "Harap Lengkapi Foto Profil", 3000);
                            return;
                        } else {
                            uploadImageIdentitasJamaah();
                            uploadImageFotoJamaah();
                        }
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userID = user.getUid();
                        UserJamaah newUser = new UserJamaah(userID, nama, noIdentitas, nohp, alamat, tanggallahir, latitude, longitude, 2);
                        myRef.child("SHAFOOD").child("USER").child("DONATUR").child(userID).setValue(newUser);
                        Intent i = new Intent(lengkapi_data_jamaah.this, berhasil.class);
                        startActivity(i);
                    }else {
                        showSnackbar(v, "Harap Lengkapi Semua Kolom", 3000);
                        return;
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

            mDisplayDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            lengkapi_data_jamaah.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListener,
                            year,month,day);
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

            imgViewFotoJamaah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseImageFotoJamaah();

                }
            });
        }

        public void chooseImageIdentitasJamaah() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Options");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (items[i].equals("Camera")) {
                        EasyImage.openCamera(lengkapi_data_jamaah.this, REQUEST_CODE_CAMERA_IDENTITAS);
                    } else if (items[i].equals("Gallery")) {
                        EasyImage.openGallery(lengkapi_data_jamaah.this, REQUEST_CODE_GALLERY_IDENTITAS);
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void chooseImageFotoJamaah() {
            AlertDialog.Builder builder = new AlertDialog.Builder(lengkapi_data_jamaah.this);
            builder.setTitle("Options");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (items[i].equals("Camera")) {
                        EasyImage.openCamera(lengkapi_data_jamaah.this, REQUEST_CODE_CAMERA_FOTO);
                    } else if (items[i].equals("Gallery")) {
                        EasyImage.openGallery(lengkapi_data_jamaah.this, REQUEST_CODE_GALLERY_FOTO);
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
/*
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST_1) {
                filePath1 = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath1);
                    imageViewIdentitasJamaah.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                filePath2 = data.getData();
                try {
                    Bitmap bitmip = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath2);
                    imgViewFotoJamaah.setImageBitmap(bitmip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                    switch (type) {
                        case REQUEST_CODE_CAMERA_IDENTITAS:
                            Glide.with(lengkapi_data_jamaah.this)
                                    .load(imageFile)
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageViewIdentitasJamaah);
                            filePath1 = Uri.fromFile(imageFile);
                            System.out.println("PATH ============== " + filePath1);
                            System.out.println("PATH ============== " + DiskCacheStrategy.ALL.toString());
                            break;
                        case REQUEST_CODE_GALLERY_IDENTITAS:
                            Glide.with(lengkapi_data_jamaah.this)
                                    .load(imageFile)
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageViewIdentitasJamaah);
                            filePath1 = Uri.fromFile(imageFile);
                            break;
                        case REQUEST_CODE_CAMERA_FOTO:
                            Glide.with(lengkapi_data_jamaah.this)
                                    .load(imageFile)
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imgViewFotoJamaah);
                            filePath2 = Uri.fromFile(imageFile);
                            break;
                        case REQUEST_CODE_GALLERY_FOTO:
                            Glide.with(lengkapi_data_jamaah.this)
                                    .load(imageFile)
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imgViewFotoJamaah);
                            filePath2 = Uri.fromFile(imageFile);
                            break;
                    }
                }
            });
            if (resultCode == RESULT_OK) {
                //Toast.makeText(this, "Sini Gaes2", Toast.LENGTH_SHORT).show();
                // Tampung Data tempat ke variable
                try {
                    Place placeData = PlaceAutocomplete.getPlace(this, data);
                    if (placeData.isDataValid()) {
                        // Show in Log Cat
                        Log.d("autoCompletePlace Data", placeData.toString());

                        // Dapatkan Detail
                        String placeAddress = placeData.getAddress().toString();
                        LatLng placeLatLng = placeData.getLatLng();
                        String placeName = placeData.getName().toString();

                        // Cek user milih titik jemput atau titik tujuan
                        switch (REQUEST_CODE) {
                            case ALAMAT:
                                // Set ke widget lokasi asal
                                editTextAlamat.setText(placeAddress);
                                alamatLatLng = placeData.getLatLng();
                                break;
                        }
                        if (alamatLatLng != null) {
                            onMapLongClick(alamatLatLng);
                            lispoints.add(alamatLatLng);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(alamatLatLng));
                            CameraUpdateFactory.newLatLng(alamatLatLng);
                            CameraUpdateFactory.newLatLngZoom(alamatLatLng, 16);
                            mMap.addMarker(new MarkerOptions().position(alamatLatLng).title(placeAddress));
                        }

                    } else {
                        // Data tempat tidak valid
                        Toast.makeText(this, "Invalid Place !", Toast.LENGTH_SHORT).show();
                    }
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
            }

        }

        private void uploadImageIdentitasJamaah() {

            if (filePath1 != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                FirebaseUser user = mAuth.getCurrentUser();
                String userID = user.getUid();
                StorageReference ref = storageReference.child("Jamaah/KTP/" + userID);
                ref.putFile(filePath1)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(lengkapi_data_jamaah.this, "Uploaded Berhasil", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(lengkapi_data_jamaah.this, "Upload Gagal " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            } else {
                Toast.makeText(lengkapi_data_jamaah.this, "Gagal uyyyyyy", Toast.LENGTH_SHORT).show();
            }
        }

        private void uploadImageFotoJamaah() {

            if (filePath2 != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                FirebaseUser user = mAuth.getCurrentUser();
                String userID = user.getUid();
                StorageReference ref = storageReference.child("Jamaah/FotoProfil/" + userID);
                ref.putFile(filePath2)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(lengkapi_data_jamaah.this, "Uploaded Berhasil", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(lengkapi_data_jamaah.this, "Upload Gagal " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            } else {
                Toast.makeText(lengkapi_data_jamaah.this, "Gagal uyyyyyy", Toast.LENGTH_SHORT).show();
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

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            //minta permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
                return;
            }
            //deklarasi widget
            //setting UI MAPS
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
        //add a toast to show when successfully signed in

        /**
         * customizable toast
         *
         * @param message
         */
        public void showSnackbar(View v, String message, int duration) {
            Snackbar.make(v, message, duration).show();
        }
    }