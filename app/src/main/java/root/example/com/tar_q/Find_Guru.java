package root.example.com.tar_q;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Find_Guru extends AppCompatActivity {
    private final String TAG = "Find_guru";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    private Spinner PilihKelas;
    private String Kelas_Atas = "";


    private ListView mListViewGuru;
    private TextView TV_ATAS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__guru);

        mListViewGuru = (ListView) findViewById(R.id.listViewGuru);
        PilihKelas = (Spinner) findViewById(R.id.PilihKelas);
        TV_ATAS = (TextView) findViewById(R.id.TV_ATAS);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("TARQ").child("USER").child("GURU");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        PilihKelas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected() called with: adapterView = [" + adapterView + "], view = [" + view + "], i = [" + i + "], l = [" + l + "]");
                Kelas_Atas = PilihKelas.getSelectedItem().toString().trim();
                Log.d(TAG, "onItemSelected() Awal returned: " + Kelas_Atas);

                switch (Kelas_Atas) {
                    case "Pra Tahsin 3": {
                        Kelas_Atas = "pratahsin3";
                        break;
                    }
                    case "Pra Tahsin 2": {
                        Kelas_Atas = "pratahsin2";
                        break;
                    }
                    case "Pra Tahsin 1": {
                        Kelas_Atas = "pratahsin1";
                        break;
                    }
                    case "Tahsin 1": {
                        Kelas_Atas = "tahsin1";
                        break;
                    }
                    case "Tahsin 2": {
                        Kelas_Atas = "tahsin2";
                        break;
                    }
                    case "Tahsin 3": {
                        Kelas_Atas = "tahsin3";
                        break;
                    }
                    case "Tahsin 4": {
                        Kelas_Atas = "tahsin4";
                        break;
                    }
                    case "Tahfizh": {
                        Kelas_Atas = "tahfizh";
                        break;
                    }
                    case "Bahasa Arab": {
                        Kelas_Atas = "bahasaarab";
                        break;
                    }
                }
                Log.d(TAG, "onItemSelected() Akhir returned: " + Kelas_Atas);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        showData((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showData(Map<String, Object> dataSnapshot) {

        final ArrayList<String> Nama = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map nama = (Map) entry.getValue();
            Nama.add((String) nama.get("nama"));
        }

        final ArrayList<String> Id_Guru = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_penerima = (Map) entry.getValue();
            Id_Guru.add((String) id_penerima.get("id_user"));
        }

        final ArrayList<String> Alamat = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_penerima = (Map) entry.getValue();
            Alamat.add((String) id_penerima.get("alamat"));
        }

        final ArrayList<String> Latitude = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_penerima = (Map) entry.getValue();
            Latitude.add((String) id_penerima.get("latitude"));
        }
        final ArrayList<String> Longitude = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_penerima = (Map) entry.getValue();
            Longitude.add((String) id_penerima.get("longitude"));
        }
        final ArrayList<String> NoHp = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_penerima = (Map) entry.getValue();
            NoHp.add((String) id_penerima.get("nohp"));
        }
        final ArrayList<String> Verifikasi = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_penerima = (Map) entry.getValue();
            Verifikasi.add((String) id_penerima.get("verifikasi"));
        }
        final ArrayList<String> Kelas = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            Map id_penerima = (Map) entry.getValue();
            Kelas.add((String) id_penerima.get(Kelas_Atas));
        }
        Log.d(TAG, "showData() returned: " + Kelas_Atas);
        final ArrayList<String> listNama = new ArrayList<>();
        final ArrayList<String> listId = new ArrayList<>();
        try {
            int i = 0;
            while (Nama.size() > i) {
                if (Verifikasi.get(i).equals("true")) {
                    if (Kelas.get(i).equals("true")) {
                        listId.add(Id_Guru.get(i));
                        listNama.add(Nama.get(i));
                    }
                }
                i++;
            }
        } catch (NullPointerException e) {
        }

        mListViewGuru.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toastMessage(Nama.get(position));
            }
        });
        ArrayAdapter namaGuru = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listNama);
        mListViewGuru.setAdapter(namaGuru);
    }

    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
