package com.devst.ciraquizz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.devst.ciraquizz.model.Residuo;
import com.devst.ciraquizz.util.CatalogoResiduos;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

// Pantalla 2 - Escanear QR: lee el código del contenedor y muestra el residuo
public class EscanearQrActivity extends AppCompatActivity {

    private View hojaResultado;
    private TextView txtEmoji, txtNombreResiduo, txtContenedor, txtConsejo, txtInstruccion;

    // Abre la cámara de la librería zxing y recibe el texto del QR
    private final ActivityResultLauncher<ScanOptions> lanzadorEscaner =
            registerForActivityResult(new ScanContract(), resultado -> {
                if (resultado.getContents() == null) {
                    txtInstruccion.setText("Escaneo cancelado. Toca para intentar de nuevo");
                } else {
                    mostrarResiduo(resultado.getContents());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear_qr);

        hojaResultado    = findViewById(R.id.hojaResultado);
        txtEmoji         = findViewById(R.id.txtEmoji);
        txtNombreResiduo = findViewById(R.id.txtNombreResiduo);
        txtContenedor    = findViewById(R.id.txtContenedor);
        txtConsejo       = findViewById(R.id.txtConsejo);
        txtInstruccion   = findViewById(R.id.txtInstruccion);
        Button btnEscanearOtro = findViewById(R.id.btnEscanearOtro);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        findViewById(R.id.zonaEscaneo).setOnClickListener(v -> abrirEscaner());
        btnEscanearOtro.setOnClickListener(v -> abrirEscaner());

        // Apenas se abre la pantalla, se abre la cámara
        abrirEscaner();
    }

    private void abrirEscaner() {
        ScanOptions opciones = new ScanOptions();
        opciones.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        opciones.setPrompt("Enfoca el código QR del contenedor");
        opciones.setBeepEnabled(true);
        opciones.setOrientationLocked(false);
        lanzadorEscaner.launch(opciones);
    }

    private void mostrarResiduo(String codigo) {
        Residuo residuo = CatalogoResiduos.buscar(codigo);
        hojaResultado.setVisibility(View.VISIBLE);

        if (residuo == null) {
            // Validación: el QR no pertenece a CIRA
            txtEmoji.setText("❓");
            txtNombreResiduo.setText("Código no reconocido");
            txtContenedor.setText("");
            txtConsejo.setText("Este QR no pertenece a un contenedor CIRA.");
            return;
        }

        txtEmoji.setText(residuo.getEmoji());
        txtNombreResiduo.setText(residuo.getNombre());
        txtContenedor.setText("Contenedor: " + CatalogoResiduos.nombreContenedor(residuo.getCategoria()));
        txtConsejo.setText(residuo.getConsejo());
    }
}