package root.example.com.tar_q.Guru;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import root.example.com.tar_q.R;

public class Form_Tahsin extends AppCompatActivity {

    private EditText editTextMateriTahsin, editTextPencapaianTahsin, editTextKendalaTahsin,
                     editTextSolusiTahsin, editTextKeteranganTahsin;
    private Button KirimFormtahsin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form__tahsin);

        editTextMateriTahsin = (EditText) findViewById(R.id.editTextMateriTahsin);
        editTextPencapaianTahsin = (EditText) findViewById(R.id.editTextPencapaianTahsin);
        editTextKendalaTahsin = (EditText) findViewById(R.id.editTextKendalaTahsin);
        editTextSolusiTahsin = (EditText) findViewById(R.id.editTextSolusiTahsin);
        editTextKeteranganTahsin = (EditText) findViewById(R.id.editTextKeteranganTahsin);

    }
}
