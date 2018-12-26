package root.example.com.tar_q.Jamaah;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import butterknife.OnItemSelected;
import root.example.com.tar_q.Guru.Main_Guru;
import root.example.com.tar_q.MainActivity;
import root.example.com.tar_q.R;
import root.example.com.tar_q.Tentang;

public class Main_Jamaah extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DialogInterface.OnDismissListener {
    private final String TAG = "Main_Jamaah";

    //Add Firebase Function
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, myRef1;
    //Storage
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Dialog NotifikasiMurid;

    private CompactCalendarView kalenderJamaah;
    private SimpleDateFormat dateFormatMonth;

    private TextView etIsiPopupCancel;
    private ImageView ivFotoGuruPopupCancel;
    private Button btnCLosePopupCancel;



    private long backPressedTime;
    private Toast backToast;
    private String userID, namaGuru, idGuru;

    private TextView Month;

    //calendar
    private MaterialCalendarView materialCalendarView;

    //resource Layout
    private ImageView imageProfileJamaah;
    private TextView NamaJamaah, EmailJamaah, SaldoJamaah;
    private Button btnBelajar;
    public String publicNamaJamaah, NomorKelas;

    private String listNamaEvent;
    private Long listWaktuEvent;


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
        myRef1 = mFirebaseDatabase.getReference().child("TARQ").child("KELAS").child("PRIVATE");
        userID = user.getUid();
        btnBelajar = (Button) findViewById(R.id.btnBelajar);
        NotifikasiMurid = new Dialog(this);

        //Add Resource
        //Resource Layout
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://lkptarq93.appspot.com");
        imageProfileJamaah = (ImageView) header.findViewById(R.id.imageProfileJamaah);
        NamaJamaah = (TextView) header.findViewById(R.id.textViewNamaJamaah);
        EmailJamaah = (TextView) header.findViewById(R.id.textViewEmailJamaah);
        SaldoJamaah = (TextView) header.findViewById(R.id.textViewSaldoJamaah);
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

        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showNotification((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ProfileJamaah uInfo = new ProfileJamaah();
            uInfo.setNama(ds.child("USER").child("JAMAAH").child(userID).getValue(ProfileJamaah.class).getNama());
            uInfo.setSaldo(ds.child("USER").child("JAMAAH").child(userID).getValue(ProfileJamaah.class).getSaldo());
            SaldoJamaah.setText("Saldo Rp." + uInfo.getSaldo());
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

    private void showNotification(Map<String, Object> dataSnapshot) {

        final ArrayList<String> Jadwalhari = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map jadwalhari = (Map) entry.getValue();
            Jadwalhari.add((String) jadwalhari.get("jadwalhari"));
        }
        final ArrayList<String> NamaGuru = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map namaGuru= (Map) entry.getValue();
            NamaGuru.add((String) namaGuru.get("guru"));
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
        final ArrayList<String> IdMurid= new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map idMurid = (Map) entry.getValue();
            IdMurid.add((String) idMurid.get("idmurid"));
        }
        final ArrayList<String> listNama = new ArrayList<>();
        final ArrayList<String> listPertemuan= new ArrayList<>();
        final ArrayList<String> listNomorKelas = new ArrayList<>();
        final ArrayList<String> listId = new ArrayList<>();
        if(Jadwalhari != null){
            int i = 0;
            while(Jadwalhari.size() > i){
                if(IdMurid.get(i).equals(userID)){
                    if(Jadwalhari.get(i).equals("proses")){
                        listNama.add(NamaGuru.get(i));
                        listPertemuan.add(JmlPertemuan.get(i));
                        listNomorKelas.add(NoKelas.get(i));
                        listId.add(IdGuru.get(i));
                    }
                    if(Jadwalhari.get(i).equals("request")){
                    }
                    if(Jadwalhari.get(i).equals("false")){
                        namaGuru = NamaGuru.get(i);
                        idGuru = IdGuru.get(i);
                        NomorKelas = NoKelas.get(i);
                        ShowPopupNotifikasiMurid(namaGuru,idGuru);
                    }
                    else{
                        int j = 1;
                        String[] a = String.valueOf(Jadwalhari.get(i)).split(",");
                        while(a.length > j){
                            int sab = j * 35000;
                            listNamaEvent = NamaGuru.get(i);
                            listWaktuEvent = Long.parseLong(a[j]);
                            setEvent(listWaktuEvent, listNamaEvent);
                            j++;
                        }
                    }
                }
                i++;
            }
        }
    }
    
    private void ShowPopupNotifikasiMurid(String Guru, String IdGuru){
        TextView txtclose;
        NotifikasiMurid.setContentView(R.layout.popup_notifikasi_murid);
        etIsiPopupCancel = (TextView) NotifikasiMurid.findViewById(R.id.editTextIsiNotifikasiPopupCancel);
        ivFotoGuruPopupCancel = (ImageView) NotifikasiMurid.findViewById(R.id.imageViewFotoGuruPopupCancel);
        btnCLosePopupCancel = (Button) NotifikasiMurid.findViewById(R.id.btnClosePopupCancel);

        storageRef.child("Guru/IdentitasGuru/" + IdGuru).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Glide.with(getApplicationContext()).load(uri).into(ivFotoGuruPopupCancel);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        txtclose =(TextView) NotifikasiMurid.findViewById(R.id.txtclose);
        txtclose.setText("X");
        etIsiPopupCancel.setText("Permintaan Anda Tidak Diterima Oleh " + Guru);

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifikasiMurid.dismiss();
            }
        });
        btnCLosePopupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifikasiMurid.dismiss();
            }
        });
        NotifikasiMurid.setOnDismissListener(this);
        NotifikasiMurid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        NotifikasiMurid.show();
    }


    private void setEvent(Long waktu, String guru){

        final com.github.sundeepk.compactcalendarview.domain.Event ev1 = new com.github.sundeepk.compactcalendarview.domain.Event(Color.WHITE, waktu, "~" + guru + "~");
        kalenderJamaah.addEvent(ev1);
        kalenderJamaah.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                ArrayList<String> NamaDiajar = new ArrayList<>();
                try{
                    String[] a = String.valueOf(kalenderJamaah.getEvents(dateClicked)).split("~");
                    String[] d = String.valueOf(kalenderJamaah.getEvents(dateClicked)).split(",");
                    int i = 1;
                    int j = 14;
                    int k = 27;
                    int l = 1;
                    while(a.length > i){
                        String b = a[i];
                        String c = d[l];
                        Log.d(TAG, "Tanggal =" + c);
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(String.valueOf(c).substring(j, k)));
                        String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
                        toastMessage("Anda Akan Belajar Dengan " + b + " Pada : " + date);
                        NamaDiajar.add(b);
                        i = i + 2;
                        l = l + 3;
                    }
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        myRef1.child(NomorKelas).setValue(null);
    }
}

