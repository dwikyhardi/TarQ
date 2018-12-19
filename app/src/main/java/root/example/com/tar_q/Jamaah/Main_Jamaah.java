package root.example.com.tar_q.Jamaah;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import butterknife.OnItemSelected;
import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;
import root.example.com.tar_q.Tentang;

public class Main_Jamaah extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "Main_Jamaah";

    //Add Firebase Function
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    //Storage
    private FirebaseStorage storage;
    private StorageReference storageRef;


    private CompactCalendarView kalenderJamaah;
    private SimpleDateFormat dateFormatMonth;


    private long backPressedTime;
    private Toast backToast;
    private String userID;

    private TextView Month;

    //calendar
    private MaterialCalendarView materialCalendarView;

    //resource Layout
    private ImageView imageProfileJamaah;
    private TextView NamaJamaah, EmailJamaah, TV;
    private Button btnBelajar;
    public String publicNamaJamaah;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__jamaah);
        Toolbar toolbar = findViewById(R.id.toolbar_jamaah);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(root.example.com.tar_q.Jamaah.Main_Jamaah.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_jamaah);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = user.getUid();
        btnBelajar = (Button) findViewById(R.id.btnBelajar);


        //Add Resource
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


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnBelajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Main_Jamaah.this,Find_Guru.class);
                mIntent.putExtra("NamaJamaah", publicNamaJamaah);
                startActivity(mIntent);
            }
        });


        //Kalender
        kalenderJamaah = (CompactCalendarView) findViewById(R.id.calendarJamaah);
        Month = (TextView) findViewById(R.id.textViewMonth);
        dateFormatMonth = new SimpleDateFormat("MMMM - yyyy",Locale.getDefault());
        final com.github.sundeepk.compactcalendarview.domain.Event ev1 = new com.github.sundeepk.compactcalendarview.domain.Event(Color.WHITE, 1544605200000L, "~Coba~");
        kalenderJamaah.addEvent(ev1);
        kalenderJamaah.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                try{
                    String[] a = String.valueOf(kalenderJamaah.getEvents(dateClicked)).split("~");
                    String b = a[1];
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(String.valueOf(kalenderJamaah.getEvents(dateClicked)).substring(30, 43)));
                    String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
                    toastMessage("Anda Akan Mengajar " + b + " Pada : " + date);
                    Log.d(TAG, "onDayClick() returnee: " + String.valueOf(kalenderJamaah.getEvents(dateClicked)).substring(30, 42));
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


    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileJamaah uInfo = new ProfileJamaah();
            uInfo.setNama(ds.child("USER").child("JAMAAH").child(userID).getValue(ProfileJamaah.class).getNama());
            publicNamaJamaah = uInfo.getNama();
            NamaJamaah.setText(uInfo.getNama());
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
            Intent intent = new Intent(root.example.com.tar_q.Jamaah.Main_Jamaah.this, MainActivity.class);
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
        getMenuInflater().inflate(R.menu.jamaah_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_jamaah) {
            return true; }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (id == R.id.nav_logout){
            mAuth.signOut();
            startActivity(new Intent(Main_Jamaah.this,MainActivity.class));
        }else if (id == R.id.nav_Prosensi_jamaah){
            startActivity(new Intent(Main_Jamaah.this,PresensiJamaah.class));
        }else if (id == R.id.nav_Jadwal){
            startActivity(new Intent(Main_Jamaah.this,JadwalJamaah.class));
        }else if (id == R.id.nav_Potensi_pengeluaran){
            startActivity(new Intent(Main_Jamaah.this,PotensiPengeluaran.class));
        }else if (id == R.id.nav_Tentang){
            startActivity(new Intent(Main_Jamaah.this,Tentang.class));
        }else if (id == R.id.nav_Account){
            startActivity(new Intent(Main_Jamaah.this,Biodata_Jamaah.class));
        }else if (id == R.id.nav_Home){
            startActivity(new Intent(Main_Jamaah.this,Main_Jamaah.class));
        }
        return true;
    }

    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

