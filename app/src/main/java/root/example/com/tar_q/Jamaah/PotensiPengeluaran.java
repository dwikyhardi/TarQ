package root.example.com.tar_q.Jamaah;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;
import root.example.com.tar_q.Tentang;

public class PotensiPengeluaran extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jamaah_potensi_pengeluaran);
        Toolbar toolbar = findViewById(R.id.toolbar_jamaah);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(PotensiPengeluaran.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_jamaah);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout){
            mAuth.signOut();
            startActivity(new Intent(PotensiPengeluaran.this,MainActivity.class));
        }else if (id == R.id.nav_Prosensi_jamaah){
            startActivity(new Intent(PotensiPengeluaran.this,PresensiJamaah.class));
        }else if (id == R.id.nav_Jadwal){
            startActivity(new Intent(PotensiPengeluaran.this,JadwalJamaah.class));
        }else if (id == R.id.nav_Potensi_pengeluaran){
            startActivity(new Intent(PotensiPengeluaran.this,PotensiPengeluaran.class));
        }else if (id == R.id.nav_Tentang){
            startActivity(new Intent(PotensiPengeluaran.this,Tentang.class));
        }else if (id == R.id.nav_Account){
            startActivity(new Intent(PotensiPengeluaran.this,Biodata_Jamaah.class));
        }else if (id == R.id.nav_Home){
            startActivity(new Intent(PotensiPengeluaran.this,Main_Jamaah.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private long backPressedTime;
    private Toast backToast;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            Intent intent = new Intent(PotensiPengeluaran.this, Main_Jamaah.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            backToast = Toast.makeText(getBaseContext(), "Tekan Lagi Untuk Keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
