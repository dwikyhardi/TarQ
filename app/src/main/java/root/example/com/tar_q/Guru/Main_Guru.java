package root.example.com.tar_q.Guru;

import android.Manifest;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.everything.providers.android.calendar.CalendarProvider;
import me.everything.providers.android.calendar.Event;
import root.example.com.tar_q.Jamaah.Find_Guru;
import root.example.com.tar_q.Jamaah.Main_Jamaah;
import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;
import root.example.com.tar_q.services.LocationUpdate;

public class Main_Guru extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Main_Guru";
    //Add Firebase Function
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef1, myRef2;
    //Storage
    private FirebaseStorage storage;
    private StorageReference storageRef;


    //Lokasi Update
    private FusedLocationProviderClient mLastLocation;

    private long backPressedTime;
    private Toast backToast;
    private String userID, IdJamaah;
    private String lat, lng;
    public  String publicNamaGuru;
    private String NamaMurid, NomorKelas, JumlahPertemuan;


    private CompactCalendarView kalenderGuru;
    private SimpleDateFormat dateFormatMonth;

    //resource Layout
    private ImageView imageProfileGuru, ivFotoMuridPopup;
    private TextView NamaGuru, EmailGuru, Month;
    private Button test, btnTerimaPopup, btnTolakPopup;
    private Dialog NotifikasiGuru;
    private TextView etNamaPenerimaPopup;


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

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        myRef1 = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("PRIVATE");
        myRef2 = mFirebaseDatabase.getReference().child("TARQ").child("USER");
        mLastLocation = LocationServices.getFusedLocationProviderClient(this);
        userID = user.getUid();




        addCalendarEvent();
        test = (Button) findViewById(R.id.testcal);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastMessage("Nothing to Do Here!");
                ShowPopupNotifikasiGuru();
            }
        });


        //Resource Layout
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
        kalenderGuru = (CompactCalendarView) findViewById(R.id.calenderGuru);

        Month = (TextView) findViewById(R.id.textViewMonth);
        dateFormatMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());


        final com.github.sundeepk.compactcalendarview.domain.Event ev1 = new com.github.sundeepk.compactcalendarview.domain.Event(Color.WHITE, 1544605200000L, "~Coba~");
        kalenderGuru.addEvent(ev1);
        kalenderGuru.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                try{
                    String[] a = String.valueOf(kalenderGuru.getEvents(dateClicked)).split("~");
                    String b = a[1];
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(String.valueOf(kalenderGuru.getEvents(dateClicked)).substring(30, 43)));
                    String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
                    toastMessage("Anda Akan Mengajar " + b + " Pada : " + date);
                    Log.d(TAG, "onDayClick() returnee: " + String.valueOf(kalenderGuru.getEvents(dateClicked)).substring(30, 42));
                } catch (ArrayIndexOutOfBoundsException e){
                    toastMessage("Anda Tidak Memiliki Jadwal Mengajar");
                    e.printStackTrace();
                }

            }



            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Month.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                    myRef2.child("GURU").child(userID)
                            .child("latitude").setValue(lat);
                    myRef2.child("GURU").child(userID)
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
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, LocationUpdate.class);
//        this.startService(serviceIntent);

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

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileGuru uInfo = new ProfileGuru();
            uInfo.setNama(ds.child("USER").child("GURU").child(userID).getValue(ProfileGuru.class).getNama());
            publicNamaGuru = uInfo.getNama();
            NamaGuru.setText(uInfo.getNama());
        }
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

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent mIntent = new Intent(Main_Guru.this, Biodata_Guru.class);
            startActivity(mIntent);
        } else if (id == R.id.nav_Tentang) {
            toastMessage("tentang");
            /*Intent mIntent = new Intent(Main_Guru.this, Authors.class);
            startActivity(mIntent);*/
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent mIntent = new Intent(Main_Guru.this, MainActivity.class);
            startActivity(mIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    public void addCalendarEvent() {
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
    }

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
        System.out.println("Bangsadh" + IdGuru);
        if(Jadwalhari != null){
            int i = 0;
            while(Jadwalhari.size() > i){
                if(IdGuru.get(i).equals(userID)){
                    if(Jadwalhari.get(i).equals("proses")){
                        NamaMurid = NamaJamaah.get(i);
                        JumlahPertemuan = JmlPertemuan.get(i);
                        NomorKelas = NoKelas.get(i);
                        ShowPopupNotifikasiGuru();
                        Log.d(TAG,NamaMurid);
                        Log.d(TAG,JumlahPertemuan);
                        Log.d(TAG,NomorKelas);
                    }
                }
                i++;
            }
        }
    }
    public void ShowPopupNotifikasiGuru() {
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
        txtclose =(TextView) NotifikasiGuru.findViewById(R.id.txtclose);
        txtclose.setText("X");
        etNamaPenerimaPopup.setText(NamaMurid + " Telah Memilih Anda Untuk Mengajar Untuk" + JumlahPertemuan + " Pertemuan");

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
}