package root.example.com.tar_q.Guru;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;
import root.example.com.tar_q.services.LocationUpdate;

import static root.example.com.tar_q.MainActivity.ERROR_DIALOG_REQUEST;
import static root.example.com.tar_q.MainActivity.PERMISSIONS_REQUEST_ENABLE_GPS;

public class Main_Guru extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Main_Guru";
    //Add Firebase Function
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef, myRef1, myRef2, myRef3, myRef4;
    //Storage
    private FirebaseStorage storage;
    private StorageReference storageRef;


    //Lokasi Update
    private FusedLocationProviderClient mLastLocation;

    private long backPressedTime;
    private Toast backToast;
    private String userID, IdJamaah;
    private String lat, lng;
    public String publicNamaGuru;
    private String NamaMurid, NomorKelas, JumlahPertemuan;
    private String listNamaEvent;
    private Long listWaktuEvent;
    private String Lokasi;


    private CompactCalendarView kalenderGuru;
    private SimpleDateFormat dateFormatMonth;

    //resource Layout
    private ImageView imageProfileGuru, ivFotoMuridPopup;
    private TextView NamaGuru, EmailGuru, Month, SaldoGuru;
    private Button test, btnTerimaPopup, btnTolakPopup;
    private Dialog NotifikasiGuru;
    private TextView etNamaPenerimaPopup, Tv_Kantor, Tv_Private;
    private LinearLayout LL;
    private ListView mListViewRequestKantor, mListViewRequestPrivate, mListViewDataAjar;


    private ArrayList<String> idAjar = new ArrayList<>();
    private ArrayList<String> PelajaranH = new ArrayList<>();
    private ArrayList<String> Jam = new ArrayList<>();
    private ArrayList<String> Kelas = new ArrayList<>();


    public static final int PERMISSIONS_REQUEST_WRITE_CALENDAR = 9005;
    public static final int PERMISSIONS_REQUEST_READ_CALENDAR = 9006;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__guru);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(Main_Guru.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_guru);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        NotifikasiGuru = new Dialog(this);
        mListViewRequestKantor = (ListView) findViewById(R.id.listRequestGuruKantor);
        mListViewRequestPrivate = (ListView) findViewById(R.id.listRequestGuruPrivate);
        mListViewDataAjar = (ListView) findViewById(R.id.listMengajarGuru);
        LL = (LinearLayout) findViewById(R.id.LL);
        Tv_Kantor = (TextView) findViewById(R.id.Tv_Kantor);
        Tv_Private = (TextView) findViewById(R.id.Tv_Private);
        Lokasi = getIntent().getStringExtra("Lokasi");
        Log.d(TAG, "Lokasi = " + Lokasi);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        myRef1 = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("KANTOR").child(Lokasi);
        myRef2 = mFirebaseDatabase.getReference().child("TARQ").child("USER");
        myRef4 = mFirebaseDatabase.getReference();
        myRef3 = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("PRIVATE").child(Lokasi);
        mLastLocation = LocationServices.getFusedLocationProviderClient(this);
        userID = user.getUid();


        //Resource Layout
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://lkptarq93.appspot.com");
        imageProfileGuru = (ImageView) header.findViewById(R.id.imageProfileGuru);
        NamaGuru = (TextView) header.findViewById(R.id.textViewNamaGuru);
        EmailGuru = (TextView) header.findViewById(R.id.textViewEmailGuru);
        SaldoGuru = (TextView) header.findViewById(R.id.textViewSaldoGuru);
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
        kalenderGuru = (CompactCalendarView) findViewById(R.id.calenderGuru);

        Month = (TextView) findViewById(R.id.textViewMonth);
        dateFormatMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());

        //tambahan Ieu
        myRef4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showNama(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showNotification((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showNotification1((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Aplikasi Ini Membutuhkan GPS Untuk Bekerja Dengan Baik, Apakah Anda Ingin Menyalakannya ?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Main_Guru.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Main_Guru.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    //tambahan Ieu
    private void showNama(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileGuru uInfo = new ProfileGuru();
            Log.d(TAG, "showNama() returned: " + Lokasi);
            uInfo.setNama(ds.child("USER").child("GURU").child(Lokasi).child(userID).getValue(ProfileGuru.class).getNama());
            uInfo.setSaldo(ds.child("USER").child("GURU").child(Lokasi).child(userID).getValue(ProfileGuru.class).getSaldo());
            NamaGuru.setText(uInfo.getNama());
            SaldoGuru.setText("Saldo Rp." + uInfo.getSaldo());
            Log.d(TAG, "onDataChange() returned: " + uInfo.getNama());
        }
    }

    private void updateLokasi() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    try {
                        //Update to firebase
                        Location location = task.getResult();
                        lat = String.valueOf(location.getLatitude());
                        lng = String.valueOf(location.getLongitude());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    myRef2.child("GURU").child(Lokasi).child(userID)
                            .child("latitude").setValue(lat);
                    myRef2.child("GURU").child(Lokasi).child(userID)
                            .child("longitude").setValue(lng);
                    startLocationService();
                } else {
                    //Toast.makeText(this, "Couldn't get the location",Toast.LENGTH_SHORT).show();
                    Log.d("TEST", "Couldn't load location");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLokasi();
        checkMapServices();
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, LocationUpdate.class);
            serviceIntent.putExtra("Lokasi", Lokasi);
            //this.startService(serviceIntent);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Main_Guru.this.startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("root.example.com.tar_q.services.LocationUpdate".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            Intent intent = new Intent(Main_Guru.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            backToast = Toast.makeText(getBaseContext(), "Tekan Lagi Untuk Keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guru_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    /*public void addCalendarEvent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        long calID = 1;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2018, 11, 10, 16, 35);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2018, 11, 10, 16, 40);
        endMillis = endTime.getTimeInMillis();
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Bangun");
        values.put(CalendarContract.Events.DESCRIPTION, "Kerja");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Indonesia/Jakarta");
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        System.out.println("URI" + uri);
    }*/

    private void showNotification(Map<String, Object> dataSnapshot) {

        final ArrayList<String> Jadwalhari = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map jadwalhari = (Map) entry.getValue();
            Jadwalhari.add((String) jadwalhari.get("jadwalhari"));
        }
        final ArrayList<String> NamaJamaah = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map namajamaah = (Map) entry.getValue();
            NamaJamaah.add((String) namajamaah.get("murid"));
        }
        final ArrayList<String> JmlPertemuan = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map jmlPertemuan = (Map) entry.getValue();
            JmlPertemuan.add((String) jmlPertemuan.get("jmlpertemuan"));
        }
        final ArrayList<String> NoKelas = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map noKelas = (Map) entry.getValue();
            NoKelas.add((String) noKelas.get("nokelas"));
        }

        final ArrayList<String> IdGuru = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map idGuru = (Map) entry.getValue();
            IdGuru.add((String) idGuru.get("idguru"));
        }

        final ArrayList<String> IdMurid = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map idMurid = (Map) entry.getValue();
            IdMurid.add((String) idMurid.get("idmurid"));
        }
        final ArrayList<String> Pelajaran = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map pelajaran = (Map) entry.getValue();
            Pelajaran.add((String) pelajaran.get("pelajaran"));
        }
        final ArrayList<String> listNama = new ArrayList<>();
        final ArrayList<String> listPertemuan = new ArrayList<>();
        final ArrayList<String> listNomorKelas = new ArrayList<>();
        final ArrayList<String> listId = new ArrayList<>();
        if (Jadwalhari != null) {
            int i = 0;
            while (Jadwalhari.size() > i) {
                Log.d(TAG, "Id DB = " + IdGuru);
                Log.d(TAG, "Id Asli = " + userID);
                if (IdGuru.get(i).equals(userID)) {
                    Log.d(TAG, "Jadwal Hari samadengan = " + Jadwalhari.get(i));
                    if (Jadwalhari.get(i).equals("proses")) {
                        listNama.add(NamaJamaah.get(i));
                        listPertemuan.add(JmlPertemuan.get(i));
                        listNomorKelas.add(NoKelas.get(i));
                        listId.add(IdMurid.get(i));
                        NamaMurid = NamaJamaah.get(i);
                        JumlahPertemuan = JmlPertemuan.get(i);
                        NomorKelas = NoKelas.get(i);
                        IdJamaah = IdMurid.get(i);
                        ShowPopupNotifikasiGuruKantor();
                    } else if (Jadwalhari.get(i).equals("request")) {
                    } else if (Jadwalhari.get(i).equals("false")) {
                    } else {
                        int j = 1;
                        String[] a = String.valueOf(Jadwalhari.get(i)).split(",");
                        idAjar.add(IdMurid.get(i));
                        PelajaranH.add(Pelajaran.get(i));
                        Jam.add(Jadwalhari.get(i));
                        Kelas.add(NoKelas.get(i));
                        while (a.length > j) {
                            listNamaEvent = NamaJamaah.get(i);
                            listWaktuEvent = Long.parseLong(a[j]);
                            setEvent(listWaktuEvent, listNamaEvent);
                            j++;
                        }
                    }
                }
                i++;
            }
        }
        mListViewRequestKantor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NamaMurid = listNama.get(position);
                JumlahPertemuan = listPertemuan.get(position);
                NomorKelas = listNomorKelas.get(position);
                IdJamaah = listId.get(position);
                ShowPopupNotifikasiGuruKantor();
            }
        });

        ArrayAdapter namaGuru = new ArrayAdapter(this, R.layout.list_view_style_request, listNama);
        mListViewRequestKantor.setAdapter(namaGuru);
    }

    private void showNotification1(Map<String, Object> dataSnapshot) {

        final ArrayList<String> Jadwalhari = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map jadwalhari = (Map) entry.getValue();
            Jadwalhari.add((String) jadwalhari.get("jadwalhari"));
        }
        final ArrayList<String> NamaJamaah = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map namajamaah = (Map) entry.getValue();
            NamaJamaah.add((String) namajamaah.get("murid"));
        }
        final ArrayList<String> JmlPertemuan = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map jmlPertemuan = (Map) entry.getValue();
            JmlPertemuan.add((String) jmlPertemuan.get("jmlpertemuan"));
        }
        final ArrayList<String> NoKelas = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map noKelas = (Map) entry.getValue();
            NoKelas.add((String) noKelas.get("nokelas"));
        }
        final ArrayList<String> IdGuru = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map idGuru = (Map) entry.getValue();
            IdGuru.add((String) idGuru.get("idguru"));
        }
        final ArrayList<String> IdMurid = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map idMurid = (Map) entry.getValue();
            IdMurid.add((String) idMurid.get("idmurid"));
        }
        final ArrayList<String> Pelajaran = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map pelajaran = (Map) entry.getValue();
            Pelajaran.add((String) pelajaran.get("pelajaran"));
        }
        final ArrayList<String> listNama = new ArrayList<>();
        final ArrayList<String> listPertemuan = new ArrayList<>();
        final ArrayList<String> listNomorKelas = new ArrayList<>();
        final ArrayList<String> listId = new ArrayList<>();
        Log.d(TAG, "KAKAK" + IdGuru);
        if (Jadwalhari != null) {
            int i = 0;
            while (Jadwalhari.size() > i) {
                if (IdGuru.get(i).equals(userID)) {
                    Log.d(TAG, "Jadwal Hari samadengan = " + Jadwalhari.get(i));
                    if (Jadwalhari.get(i).equals("proses")) {
                        listNama.add(NamaJamaah.get(i));
                        listPertemuan.add(JmlPertemuan.get(i));
                        listNomorKelas.add(NoKelas.get(i));
                        listId.add(IdMurid.get(i));
                        NamaMurid = NamaJamaah.get(i);
                        JumlahPertemuan = JmlPertemuan.get(i);
                        NomorKelas = NoKelas.get(i);
                        IdJamaah = IdMurid.get(i);
                        ShowPopupNotifikasiGuruPrivate();
                        Log.d(TAG, NamaMurid);
                        Log.d(TAG, JumlahPertemuan);
                        Log.d(TAG, NomorKelas);
                    } else if (Jadwalhari.get(i).equals("request")) {
                    } else if (Jadwalhari.get(i).equals("false")) {
                    } else {
                        int j = 1;
                        String[] a = String.valueOf(Jadwalhari.get(i)).split(",");
                        idAjar.add(IdMurid.get(i));
                        PelajaranH.add(Pelajaran.get(i));
                        Jam.add(Jadwalhari.get(i));
                        Kelas.add(NoKelas.get(i));
                        while (a.length > j) {
                            listNamaEvent = NamaJamaah.get(i);
                            listWaktuEvent = Long.parseLong(a[j]);
                            setEvent(listWaktuEvent, listNamaEvent);
                            j++;
                        }
                    }
                }
                i++;
            }
        }
        mListViewRequestPrivate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NamaMurid = listNama.get(position);
                JumlahPertemuan = listPertemuan.get(position);
                NomorKelas = listNomorKelas.get(position);
                IdJamaah = listId.get(position);
                ShowPopupNotifikasiGuruPrivate();
            }
        });

        ArrayAdapter namaGuru = new ArrayAdapter(this, R.layout.list_view_style_request, listNama);
        mListViewRequestPrivate.setAdapter(namaGuru);
    }


    public void ShowPopupNotifikasiGuruPrivate() {
        TextView txtclose;
        NotifikasiGuru.setContentView(R.layout.popup_notifikasi_guru);
        etNamaPenerimaPopup = (TextView) NotifikasiGuru.findViewById(R.id.editTextIsiNotifikasiPopup);
        ivFotoMuridPopup = (ImageView) NotifikasiGuru.findViewById(R.id.imageViewFotoMuridPopup);
        btnTerimaPopup = (Button) NotifikasiGuru.findViewById(R.id.btnTerimaPopup);
        btnTolakPopup = (Button) NotifikasiGuru.findViewById(R.id.btnTolakPopup);

        storageRef.child("Jamaah/FotoProfil/" + IdJamaah).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(ivFotoMuridPopup);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        txtclose = (TextView) NotifikasiGuru.findViewById(R.id.txtclose);
        txtclose.setText("X");
        etNamaPenerimaPopup.setText(NamaMurid + " Telah Memilih Anda Untuk Mengajar Untuk " + JumlahPertemuan + " Pertemuan");

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifikasiGuru.dismiss();
            }
        });
        btnTerimaPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef3.child(NomorKelas).child("jadwalhari").setValue("request");
                finish();
                startActivity(getIntent());
                NotifikasiGuru.dismiss();
            }
        });
        btnTolakPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef3.child(NomorKelas).child("jadwalhari").setValue("false");
                NotifikasiGuru.dismiss();
            }
        });
        NotifikasiGuru.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        NotifikasiGuru.show();
    }

    public void ShowPopupNotifikasiGuruKantor() {
        TextView txtclose;
        NotifikasiGuru.setContentView(R.layout.popup_notifikasi_guru);
        etNamaPenerimaPopup = (TextView) NotifikasiGuru.findViewById(R.id.editTextIsiNotifikasiPopup);
        ivFotoMuridPopup = (ImageView) NotifikasiGuru.findViewById(R.id.imageViewFotoMuridPopup);
        btnTerimaPopup = (Button) NotifikasiGuru.findViewById(R.id.btnTerimaPopup);
        btnTolakPopup = (Button) NotifikasiGuru.findViewById(R.id.btnTolakPopup);

        storageRef.child("Jamaah/FotoProfil/" + IdJamaah).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(ivFotoMuridPopup);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        txtclose = (TextView) NotifikasiGuru.findViewById(R.id.txtclose);
        txtclose.setText("X");
        etNamaPenerimaPopup.setText(NamaMurid + " Telah Memilih Anda Untuk Mengajar Untuk " + JumlahPertemuan + " Pertemuan");

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifikasiGuru.dismiss();
            }
        });
        btnTerimaPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef1.child(NomorKelas).child("jadwalhari").setValue("request");
                finish();
                startActivity(getIntent());
                NotifikasiGuru.dismiss();
            }
        });
        btnTolakPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef1.child(NomorKelas).child("jadwalhari").setValue("false");
                NotifikasiGuru.dismiss();
            }
        });
        NotifikasiGuru.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        NotifikasiGuru.show();
    }


    private void setEvent(Long waktu, String murid) {

        Log.d(TAG, "setEvent() returned: ");
        final com.github.sundeepk.compactcalendarview.domain.Event ev1 = new com.github.sundeepk.compactcalendarview.domain.Event(Color.WHITE, waktu, "~" + murid + "~");
        kalenderGuru.addEvent(ev1);
        kalenderGuru.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                ArrayList<String> NamaDiajar = new ArrayList<>();
                try {
                    String[] a = String.valueOf(kalenderGuru.getEvents(dateClicked)).split("~");
                    String[] d = String.valueOf(kalenderGuru.getEvents(dateClicked)).split(",");
                    int i = 1;
                    int j = 14;
                    int k = 27;
                    int l = 1;
                    while (a.length > i) {
                        String b = a[i];
                        String c = d[l];
                        Log.d(TAG, "Tanggal =" + c);
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(String.valueOf(c).substring(j, k)));
                        String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
                        toastMessage("Anda Akan Mengajar " + b + " Pada : " + date);
                        NamaDiajar.add(b);
                        i = i + 2;
                        l = l + 3;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    toastMessage("Anda Tidak Memiliki Jadwal Mengajar");
                    e.printStackTrace();
                }
                ArrayAdapter namaDiajar = new ArrayAdapter(Main_Guru.this, R.layout.list_view_style_request, NamaDiajar);
                mListViewDataAjar.setAdapter(namaDiajar);

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Month.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent mIntent = new Intent(Main_Guru.this, Biodata.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Prosensi) {
            Intent mIntent = new Intent(Main_Guru.this, Presensi.class);
            mIntent.putExtra("Lokasi", Lokasi);
            mIntent.putStringArrayListExtra("List Ajar", idAjar);
            mIntent.putStringArrayListExtra("Pelajaran", PelajaranH);
            mIntent.putStringArrayListExtra("Jam", Jam);
            mIntent.putStringArrayListExtra("Kelas", Kelas);
            Log.d(TAG, "onNavigationItemSelected() returned: |" + Jam+"|"+idAjar);
            startActivity(mIntent);
        } else if (id == R.id.nav_data_jamaah) {
            Intent mIntent = new Intent(Main_Guru.this, Data_Jamaah.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Progres_report) {
            Intent mIntent = new Intent(Main_Guru.this, Progres.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_main_guru) {
            Intent mIntent = new Intent(Main_Guru.this, Main_Guru.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Potensial) {
            Intent mIntent = new Intent(Main_Guru.this, Potensial.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Realcome) {
            Intent mIntent = new Intent(Main_Guru.this, Realcome.class);
            mIntent.putExtra("Lokasi", Lokasi);
            startActivity(mIntent);
        } else if (id == R.id.nav_Tentang) {
            toastMessage("tentang");
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            if (isLocationServiceRunning()) {
                Intent serviceIntent = new Intent(this, LocationUpdate.class);
                //this.startService(serviceIntent);
                stopService(serviceIntent);
            }
            Intent mIntent = new Intent(Main_Guru.this, MainActivity.class);
            startActivity(mIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}