package root.example.com.tar_q.Guru;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import root.example.com.tar_q.Data_Jamaah;
import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;
import root.example.com.tar_q.Tentang;

public class Presensi_Guru extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Add Firebase Function
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef1, myRef2;
    //Storage
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_presensi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(Presensi_Guru.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_guru);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent mIntent = new Intent(Presensi_Guru.this, Biodata_Guru.class);
            startActivity(mIntent);
        }else if (id == R.id.nav_Prosensi) {
            Intent mIntent = new Intent(Presensi_Guru.this, Presensi_Guru.class);
            startActivity(mIntent);
        }else if (id == R.id.nav_data_jamaah) {
            Intent mIntent = new Intent(Presensi_Guru.this, Data_Jamaah.class);
            startActivity(mIntent);
        }else if (id == R.id.nav_jadwal_mengajar) {
            Intent mIntent = new Intent(Presensi_Guru.this, Jadwal_Guru.class);
            startActivity(mIntent);
        }else if (id == R.id.nav_Materi_pengajaran) {
            Intent mIntent = new Intent(Presensi_Guru.this, Guru_Materi.class);
            startActivity(mIntent);
        }else if (id == R.id.nav_Potential_pendapatan) {
            Intent mIntent = new Intent(Presensi_Guru.this, Guru_Potensi.class);
            startActivity(mIntent);
        }else if (id == R.id.nav_Progres_report) {
            Intent mIntent = new Intent(Presensi_Guru.this, Guru_Progres.class);
            startActivity(mIntent);
        }else if (id == R.id.nav_Pendapatan) {
            Intent mIntent = new Intent(Presensi_Guru.this, Tentang.class);
            startActivity(mIntent);
        }else if (id == R.id.nav_Tentang) {
            toastMessage("tentang");
            /*Intent mIntent = new Intent(Data_Jamaah.this, Authors.class);
            startActivity(mIntent);*/
        }else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent mIntent = new Intent(Presensi_Guru.this, MainActivity.class);
            startActivity(mIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}