package br.org.meiaentrada.validadorcie;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.example.brodda.validadorcie.R;


public class CodigoUsoDataNascimentoActivity extends Activity {

    EditText editDataNascimento = findViewById(R.id.dataNacimento);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_validar_codigo_uso_data_nascimento);

        editDataNascimento = findViewById(R.id.dataNacimento);

    }

    public void setDataNascimento(String dataNascimento) {

        if (editDataNascimento == null)
            editDataNascimento = findViewById(R.id.dataNacimento);

        editDataNascimento.setText(dataNascimento);

    }


}
