package root.example.com.tar_q.Guru;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;

public class Main_Guru extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef1, myRef2;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    StorageReference storageReference;
    private long backPressedTime;
    private Toast backToast;
    private String userID;
    private TextView cari, kiriman;
    private GoogleMap mMap;
    private static int REQUEST_CODE = 0;
    public static final int PICK_UP = 1;
    private ListView listViewBelumTerkirim;

    private static final String TAG = "AddToDatabase";

    TextView namaDonatur;
    TextView emailDonatur;
    TextView etBarang;
    TextView etKuantitas;
    private ImageView fotoHistory;
    private TextView etNamaPenerima, etNamaPengirim, etAlamatPenerima, etNamaDonatur, etPesan;
    private Dialog more, myNotif;
    private ImageView imageProfile;
    private Button btnCari;
    public LatLng indonesia;
    private String NamaPenerima, NamaPengirim, IdPenerima, NamaDonaturPopup, Status;
    private static final int LOCATION_REQUEST = 500;
    private String NamaDonatur, LatDonatur, LngDonatur, Ngaran, Waktos;
    private NavigationView nav_view;
    ArrayList<LatLng> lispoints;
    public LatLng alamatLatLng = null;
    public Double alamatLatitude, alamatLongitude;

    //Gambar
    private Button BtnFotoBarang;
    private ImageView ImgViewBarang;
    private Uri filePath1;
    public static final int REQUEST_CODE_CAMERA_BARANG = 0022;
    private String[] items = {"Camera"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__guru);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        more = new Dialog(this);
        myNotif = new Dialog(this);



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(Main_Guru.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = user.getUid();




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                }
        };

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
            toastMessage("AKUN");
            /*Intent mIntent = new Intent(Main_Guru.this, Donatur_Account.class);
            startActivity(mIntent);*/
        } else if (id == R.id.nav_kirim_barang) {
            toastMessage("Kirim");
            /*Intent mIntent = new Intent(Main_Guru.this, Donatur_Main.class);
            startActivity(mIntent);*/
        } else if (id == R.id.nav_history) {
            toastMessage("history");
            /*Intent mIntent = new Intent(Main_Guru.this, Donatur_History.class);
            startActivity(mIntent);*/
        }else if (id == R.id.nav_Tentang) {
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
    public void toastMessage(String message){
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show();
    }
}

