/*

Validador CIE - meiaentrada.org.br
Data: 31.12.2017
email: tecnologia@meiaentrada.org.br

Ambiente de compilação recomendado:
Android Studio 3.0.1
Build #AI-171.4443003, built on November 9, 2017
JRE: 1.8.0_152-release-915-b08 x86_64
JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o

*/

package br.org.meiaentrada.validadorcie;

import java.util.Map;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.DatabaseUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.widget.EditText;
import android.net.ConnectivityManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;

import android.content.SharedPreferences;

import java.io.IOException;

import com.example.brodda.validadorcie.R;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.Detector;

import android.graphics.Color;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.Security;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.encoders.Base64;
import org.json.*;

import android.graphics.PorterDuff;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

import java.text.Normalizer;

import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import android.widget.Button;

import com.android.volley.toolbox.JsonObjectRequest;

import android.support.constraint.ConstraintSet;
import android.location.LocationManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.Location;
import android.provider.Settings.Secure;
import android.text.InputType;

import java.util.InputMismatchException;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.android.volley.AuthFailureError;

import android.util.DisplayMetrics;

import java.io.UnsupportedEncodingException;

import br.org.meiaentrada.validadorcie.configuration.GlobalConstants;


public class MainActivity extends AppCompatActivity {

    BarcodeDetector barcodeDetector;
    String evento_cfg;
    SharedPreferences sharedPref;
    DatabaseHandler db = new DatabaseHandler(this);
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private TextView barcodeValue;
    private TextView conectado;
    private TextView evento;
    private ProgressBar fotop;
    private String codigo_cfg;
    private String crl_origem;
    private String chavepublica_origem;
    private ImageView foto;
    private Button prox;
    private Button eventod;
    private Button codigod;
    private Button cpfd;
    private ConstraintLayout layout1;
    public AlertDialog alerta;
    LocationManager locationManager;
    String provider;
    MyLocationListener mylistener;
    Criteria criteria;
    String latitude;
    String longitude;
    String android_id;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.activity_main);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        evento_cfg = sharedPref.getString("evento", "");
        codigo_cfg = sharedPref.getString("codigo", "");
        cameraView = findViewById(R.id.camera);
        barcodeValue = findViewById(R.id.resultado);
        prox = findViewById(R.id.proximo);
        prox.setVisibility(View.GONE);
        eventod = findViewById(R.id.evento_definir);
        codigod = findViewById(R.id.codigo_definir);
        cpfd = findViewById(R.id.cpf_definir);
        foto = findViewById(R.id.foto);
        foto.setVisibility(View.GONE);
        fotop = findViewById(R.id.fotop);
        fotop.setVisibility(View.GONE);
        conectado = findViewById(R.id.conectado);
        evento = findViewById(R.id.evento);
        evento.setText(evento_cfg);
        layout1 = findViewById(R.id.layout1);
        android_id = Secure.ANDROID_ID;


        if (checkAndRequestPermissions()) {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setCostAllowed(false);
            provider = locationManager.getBestProvider(criteria, false);


            int rcl = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (rcl == PackageManager.PERMISSION_GRANTED) {

                Location location = locationManager.getLastKnownLocation(provider);
                mylistener = new MyLocationListener();
                if (location != null) {
                    mylistener.onLocationChanged(location);
                }
                locationManager.requestLocationUpdates(provider, 200, 1, mylistener);

            }

            barcodeDetector = new BarcodeDetector.Builder(this)
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();

            cameraSource = new CameraSource.Builder(this, barcodeDetector)
                    .setRequestedPreviewSize(1600, 1024)
                    .setAutoFocusEnabled(true)
                    .build();

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {


                    try {

                        int rc = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                        if (rc == PackageManager.PERMISSION_GRANTED) {
                            cameraSource.start(cameraView.getHolder());
                        }


                    } catch (IOException ex) {

                        ex.printStackTrace();

                    }

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }


            });

            // detecta o QR code
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() != 0) {
                        barcodeValue.post(new Runnable() {
                            @Override
                            public void run() {


                                String docum = barcodes.valueAt(0).displayValue;

                                cameraSource.stop();
                                cameraView.setVisibility(View.GONE);
                                eventod.setVisibility(View.GONE);
                                codigod.setVisibility(View.GONE);
                                cpfd.setVisibility(View.GONE);
                                evento.setVisibility(View.GONE);

                                ConstraintSet set = new ConstraintSet();
                                set.clone(layout1);
                                set.clear(barcodeValue.getId(), ConstraintSet.TOP);
                                set.clear(barcodeValue.getId(), ConstraintSet.BOTTOM);
                                set.connect(barcodeValue.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 8);
                                set.connect(barcodeValue.getId(), ConstraintSet.BOTTOM, layout1.getId(), ConstraintSet.BOTTOM, 8);
                                set.applyTo(layout1);


                                retornoValidacao emissor = pega_emissor(docum);

                                if (evento_cfg.isEmpty()) {
                                    evento_cfg = "Evento indefinido";
                                }

                                if (emissor.erro) {


                                    barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                    Long tsLong = System.currentTimeMillis() / 1000;
                                    String ts = tsLong.toString();
                                    db.adiciona_captura(docum, emissor.erro, ts, evento_cfg);
                                    barcodeValue.setText(emissor.resultado);
                                    prox.setVisibility(View.VISIBLE);

                                } else {

                                    String emissor_chave = emissor.resultado.concat("_chave");
                                    String emissor_crl = emissor.resultado.concat("_crl");

                                    chavepublica_origem = sharedPref.getString(emissor_chave, "");
                                    crl_origem = sharedPref.getString(emissor_crl, "");

                                    retornoValidacao resultado_valida = valida_certificado(docum, chavepublica_origem, crl_origem);

                                    if (resultado_valida.erro) {

                                        barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                        prox.setVisibility(View.VISIBLE);

                                    } else {

                                        barcodeValue.setTextColor(Color.rgb(0, 255, 0));

                                        if (verifica_sinal_dados()) {

                                            String urlimagem = GlobalConstants.URL_FOTOS + MD5(docum) + "/image.jpg";
                                            downloadImagem(urlimagem);
                                            //dialogo_aviso(MD5(docum));

                                        } else {
                                            prox.setVisibility(View.VISIBLE);
                                        }


                                    }

                                    Long tsLong = System.currentTimeMillis() / 1000;
                                    String ts = tsLong.toString();
                                    db.adiciona_captura(docum, resultado_valida.erro, ts, evento_cfg);
                                    barcodeValue.setText(resultado_valida.resultado);

                                }


                            }


                        });
                    }
                }
            });

        }


    }

    // rotina de gps
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            latitude = Double.toString(location.getLatitude());
            longitude = Double.toString(location.getLongitude());

        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {


        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }


    //checa e solicita permissoes de acesso
    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int intern = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int acl = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int rps = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        int ans = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE);
        int aws = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (intern != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }
        if (rps != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ans != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (aws != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (acl != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return checkAndRequestPermissions();
        }

        return true;


    }

    public String UTF(String str) {

        try {

            return new String(str.getBytes("ISO-8859-1"), "UTF-8");


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        return str;
    }

    private class item_captura {

        private String id;
        private String certificado;
        private String resultado;
        private String horario;
        private String evento;
        private String latitude;
        private String longitude;
        private String idDispositivo;

    }

    private class retornoValidacao {

        private String resultado;
        private boolean erro;

    }


    public boolean isCPF(String CPF) {

        if (CPF.equals("00000000000") || CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return (false);

        char dig10, dig11;
        int sm, i, r, num, peso;


        try {

            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {

                num = (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char) (r + 48);


            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char) (r + 48);


            return ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)));


        } catch (InputMismatchException erro) {
            return (false);
        }
    }


    // remove acentos de uma string
    public static String stripAccents(String input) {
        return input == null ? null :
                Normalizer.normalize(input, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // calcula hash MD5 de uma string
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int arr : array) {
                sb.append(Integer.toHexString((arr & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    // verifica mudancas de estado de conectividade
    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if (verifica_sinal_dados()) {

                pega_chaves_nv();
                conectado.setText("");

            } else {

                String offline = "OFFLINE";
                conectado.setText(offline);

            }

        }


    };


    //verifica se tem sinal de dados
    public boolean verifica_sinal_dados() {


        try {

            int rcl = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);
            if (rcl == PackageManager.PERMISSION_GRANTED) {

                ConnectivityManager conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMgr != null) {
                    return (conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;


    }


    public void dialogo_aviso(String aviso) {


        if (alerta != null) {
            alerta.dismiss();
        }


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final TextView et = new TextView(this);
        et.setText(aviso);
        alertDialogBuilder.setView(et);
        et.setTextColor(Color.BLACK);
        et.setTextSize(18);

        int paddingPixel = 14;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        et.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        alerta = alertDialogBuilder.create();
        alerta.show();

    }


    public void dialogo_cpf(View view) {


        if (alerta != null) {
            alerta.dismiss();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setHint("Informe o CPF do estudante");
        et.getBackground().mutate().setColorFilter(getResources().getColor(R.color.common_google_signin_btn_text_light), PorterDuff.Mode.SRC_ATOP);

        alertDialogBuilder.setView(et);

        int paddingPixel = 20;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        et.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);


        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String retorno = et.getText().toString();
                if (isCPF(retorno)) {
                    valida_cpf(retorno);
                } else {
                    if (retorno.length() > 0) {
                        dialogo_aviso("CPF inválido");
                    }
                }

            }
        });

        alerta = alertDialogBuilder.create();
        alerta.show();


    }


    public void dialogo_evento(View view) {


        if (alerta != null) {
            alerta.dismiss();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText et = new EditText(this);
        et.setHint("Informe o nome do evento");
        et.getBackground().mutate().setColorFilter(getResources().getColor(R.color.common_google_signin_btn_text_light), PorterDuff.Mode.SRC_ATOP);

        alertDialogBuilder.setView(et);

        int paddingPixel = 20;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        et.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);


        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String retorno = et.getText().toString();

                if (!retorno.isEmpty()) {

                    evento.setText(et.getText().toString());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("evento", retorno);
                    editor.apply();
                    evento_cfg = sharedPref.getString("evento", "");

                }

            }
        });

        alerta = alertDialogBuilder.create();
        alerta.show();


    }

    public void dialogo_codigo(View view) {

        if (alerta != null) {
            alerta.dismiss();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setHint("Informe o código de acesso");
        et.getBackground().mutate().setColorFilter(getResources().getColor(R.color.common_google_signin_btn_text_light), PorterDuff.Mode.SRC_ATOP);

        alertDialogBuilder.setView(et);

        int paddingPixel = 20;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        et.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String retorno = et.getText().toString();

                if (!retorno.isEmpty()) {

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("codigo", retorno);
                    editor.apply();

                    codigo_cfg = sharedPref.getString("codigo", "");

                }

            }
        });

        alerta = alertDialogBuilder.create();
        alerta.show();

    }


    // proximo qr_code
    public void proximo_qrcode(View view) {

        barcodeValue.setText("");
        barcodeValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        foto.setVisibility(View.GONE);
        prox.setVisibility(View.GONE);
        cameraView.setVisibility(View.VISIBLE);
        eventod.setVisibility(View.VISIBLE);
        codigod.setVisibility(View.VISIBLE);
        evento.setVisibility(View.VISIBLE);
        cpfd.setVisibility(View.VISIBLE);


        try {

            int rc = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(cameraView.getHolder());
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    // validacao de documento por cpf
    public void valida_cpf(final String cpfe) {


        if (verifica_sinal_dados()) {

            fotop.setVisibility(View.VISIBLE);

            cameraSource.stop();
            cameraView.setVisibility(View.GONE);
            eventod.setVisibility(View.GONE);
            codigod.setVisibility(View.GONE);
            cpfd.setVisibility(View.GONE);
            evento.setVisibility(View.GONE);


            ConstraintSet set = new ConstraintSet();
            set.clone(layout1);
            set.clear(barcodeValue.getId(), ConstraintSet.TOP);
            set.clear(barcodeValue.getId(), ConstraintSet.BOTTOM);
            set.connect(barcodeValue.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 8);
            set.connect(barcodeValue.getId(), ConstraintSet.BOTTOM, layout1.getId(), ConstraintSet.BOTTOM, 8);
            set.applyTo(layout1);


            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.GET, GlobalConstants.URL_CPF,
                    new Response.Listener<String>() {


                        @Override
                        public void onResponse(String response) {

                            fotop.setVisibility(View.GONE);

                            try {

                                JSONObject obj = new JSONObject(response);

                                Boolean retorno = obj.getBoolean("status");

                                if (retorno) {

                                    barcodeValue.setTextColor(Color.rgb(0, 255, 0));
                                    downloadImagem(obj.getString("foto"));
                                    barcodeValue.setText(GlobalConstants.DOC_VALIDO);

                                } else {

                                    barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                    String msgerro = obj.getString("msg");
                                    barcodeValue.setText("\n".concat(UTF(msgerro)).concat("\n"));
                                    prox.setVisibility(View.VISIBLE);

                                }

                            } catch (Exception e) {

                                e.printStackTrace();
                                fotop.setVisibility(View.GONE);
                                barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                barcodeValue.setText("\nErro de conectividade, tente novamente\n");
                                prox.setVisibility(View.VISIBLE);


                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            fotop.setVisibility(View.GONE);
                            barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                            barcodeValue.setText("\nErro de conectividade, tente novamente\n");
                            prox.setVisibility(View.VISIBLE);

                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("cpf", cpfe);
                    params.put("codigoAcesso", codigo_cfg);

                    return params;
                }
            };


            queue.add(postRequest);


        } else {
            dialogo_aviso("Sem conectividade");
        }


    }


    // envia captura  para o servidor
    public void manda_captura() {

        try {

            final item_captura proxi;
            proxi = db.retorna_proximo();
            if (!proxi.id.equals("")) {

                RequestQueue queue = Volley.newRequestQueue(this);
                HashMap<String, Object> params = new HashMap<>();
                params.put("certificado", proxi.certificado);
                params.put("status", proxi.resultado.equals("0"));
                params.put("data", proxi.horario);
                params.put("evento", proxi.evento);
                params.put("latitude", proxi.latitude);
                params.put("longitude", proxi.longitude);
                params.put("idDispositivo", proxi.idDispositivo);
                params.put("codigoAcesso", codigo_cfg);

                JsonObjectRequest postRequest = new JsonObjectRequest(GlobalConstants.URL_CAPTURAS, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    Boolean retorno = response.getBoolean("status");

                                    if (retorno) {

                                        db.deleta_item(proxi.id);
                                        manda_captura();

                                    } else {

                                        String msgerro = response.getString("msg");
                                        dialogo_aviso(msgerro);

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();

                    }
                });

                queue.add(postRequest);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    // busca chaves publicas e CRLs no meiaentrada.org.br
    public void pega_chaves_nv() {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstants.URL_CHAVES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);


                            JSONArray retorno = jsonObject.getJSONArray("retorno");

                            for (int i = 0; i < retorno.length(); i++) {
                                try {

                                    JSONObject oneObject = retorno.getJSONObject(i);
                                    String json_emissor = stripAccents(oneObject.getString("emissor")).replaceAll("\\p{Z}", "").replaceAll("-", "");
                                    String json_chavepublica = oneObject.getString("chavePublica");
                                    String json_crl = oneObject.getString("crl");
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString(json_emissor + "_chave", json_chavepublica);
                                    editor.putString(json_emissor + "_crl", json_crl);
                                    editor.apply();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (db.total_capturas() > 0) {
                                manda_captura();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);


    }


    // busca emissor dentro do certificado
    retornoValidacao pega_emissor(String certDNE) {

        retornoValidacao retornove = new retornoValidacao();
        retornove.resultado = GlobalConstants.ERRO_INVALIDO;
        retornove.erro = true;


        try {

            certDNE = "-----BEGIN ATTRIBUTE CERTIFICATE-----\n" + certDNE + "\n-----END ATTRIBUTE CERTIFICATE-----";
            PEMParser pemattr = new PEMParser(new StringReader(certDNE));
            Object objattr2 = pemattr.readObject();
            pemattr.close();
            X509AttributeCertificateHolder attr2 = (X509AttributeCertificateHolder) objattr2;
            AttributeCertificateHolder h = attr2.getHolder();
            X500Name[] nomex = h.getIssuer();
            String nomefull = nomex[0] + "";
            Integer indice1 = nomefull.indexOf("OU=");
            Integer indice2 = nomefull.indexOf("CN=");
            nomefull = nomefull.substring(indice1 + 3, indice2 - 1);
            nomefull = nomefull.replaceAll("\\s+", "");
            retornove.resultado = nomefull;
            retornove.erro = false;
            return retornove;

        } catch (Exception e) {
            return retornove;
        }
    }


    // executa validacao  do certificado localmente ( chave publica e CRl sao mandatorias, mesmo que a CRL nao tenha certif. revogados )
    retornoValidacao valida_certificado(String certDNE, String chavepublica, String crl) {


        retornoValidacao retornov = new retornoValidacao();
        retornov.resultado = GlobalConstants.ERRO_INVALIDO;
        retornov.erro = true;

        try {
            if (!chavepublica.isEmpty() && !crl.isEmpty()) {
                Security.addProvider(new BouncyCastleProvider());
                X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.decode(chavepublica));
                KeyFactory kf = KeyFactory.getInstance("RSA");
                RSAPublicKey chapub = (RSAPublicKey) kf.generatePublic(keySpecX509);
                certDNE = "-----BEGIN ATTRIBUTE CERTIFICATE-----\n" + certDNE + "\n-----END ATTRIBUTE CERTIFICATE-----";
                PEMParser pemattr = new PEMParser(new StringReader(certDNE));
                Object objattr2 = pemattr.readObject();
                pemattr.close();
                X509AttributeCertificateHolder attr2 = (X509AttributeCertificateHolder) objattr2;
                crl = "-----BEGIN X509 CRL-----\n" + crl + "\n-----END X509 CRL-----";
                PEMParser pemacrl = new PEMParser(new StringReader(crl));
                Object objcrl = pemacrl.readObject();
                pemacrl.close();
                X509CRLHolder jceCRL = (X509CRLHolder) objcrl;
                if (jceCRL.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(chapub))) {
                    if (attr2.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(chapub))) {
                        if (attr2.isValidOn(new Date())) {
                            AttributeCertificateHolder h = attr2.getHolder();
                            X500Name[] nomex = h.getIssuer();
                            String nomefull = nomex[0] + "";
                            Integer indicenome = nomefull.indexOf("CN=");
                            nomefull = nomefull.substring(indicenome + 3, nomefull.length());
                            retornov.resultado = "\n" + nomefull + "\n";
                            retornov.erro = false;
                            Attribute[] attribs = attr2.getAttributes();
                            Attribute a = attribs[0];
                            String apar = a.getAttrValues() + "";
                            retornov.resultado = retornov.resultado.concat("CPF: " + apar.substring(9, 20) + GlobalConstants.DOC_VALIDO);
                            JcaX509CRLConverter converter = new JcaX509CRLConverter();
                            converter.setProvider("BC");
                            X509CRL crlconv = converter.getCRL(jceCRL);
                            X509CRLEntry xentry = crlconv.getRevokedCertificate(attr2.getSerialNumber());
                            if (xentry != null) {
                                retornov.resultado = GlobalConstants.ERRO_REVOGADO;
                                retornov.erro = true;
                            }

                        } else {
                            retornov.resultado = GlobalConstants.ERRO_EXPIRADO;
                            retornov.erro = true;
                        }
                    }
                }
            }
            return retornov;
        } catch (Exception e) {
            e.printStackTrace();
            return retornov;
        }

    }


    // download de foto de estudante usando Picasso
    private void downloadImagem(String urlimagem) {


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widths = displayMetrics.widthPixels;

        fotop.setVisibility(View.VISIBLE);
        Picasso.with(getApplicationContext())
                .load(urlimagem)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .resize(widths, 0)
                .into(foto, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                        foto.setVisibility(View.VISIBLE);
                        fotop.setVisibility(View.GONE);
                        prox.setVisibility(View.VISIBLE);

                        ConstraintSet set = new ConstraintSet();
                        set.clone(layout1);
                        set.clear(barcodeValue.getId(), ConstraintSet.TOP);
                        set.clear(barcodeValue.getId(), ConstraintSet.BOTTOM);
                        set.connect(barcodeValue.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 8);
                        set.applyTo(layout1);

                    }

                    @Override
                    public void onError() {

                        fotop.setVisibility(View.GONE);
                        prox.setVisibility(View.VISIBLE);

                    }
                });


    }


    // cria base de dados local e rotinas de manipulacao
    public class DatabaseHandler extends SQLiteOpenHelper {

        private DatabaseHandler(Context context) {
            super(context, "validadorDNE", null, 1);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {

            String CRIA_TABELA_CAPTURAS = "CREATE TABLE capturas ("
                    + "id INTEGER PRIMARY KEY,"
                    + "certificado TEXT,"
                    + "resultado TEXT,"
                    + "horario TEXT,"
                    + "evento TEXT,"
                    + "latitude TEXT,"
                    + "longitude TEXT,"
                    + "idDispositivo TEXT,"
                    + "CONSTRAINT restricao UNIQUE (certificado, horario));";

            db.execSQL(CRIA_TABELA_CAPTURAS);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS capturas");

            onCreate(db);
        }

        private void adiciona_captura(String certif, boolean resul, String horario, String evento) {

            SQLiteDatabase dbx = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("certificado", MD5(certif));
            values.put("resultado", resul);
            values.put("horario", horario);
            values.put("evento", evento);
            values.put("latitude", latitude);
            values.put("longitude", longitude);
            values.put("idDispositivo", android_id);

            dbx.insert("capturas", null, values);
            dbx.close();

            if (verifica_sinal_dados()) {
                manda_captura();
            }

        }


        private int total_capturas() {
            SQLiteDatabase db = this.getReadableDatabase();
            long cnt = DatabaseUtils.queryNumEntries(db, "capturas");
            Integer cnti = (int) cnt;
            db.close();
            return cnti;
        }


        private void deleta_item(String id) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("capturas", "id = ?", new String[]{id});
            db.close();


        }

        private item_captura retorna_proximo() {

            String selectQuery = "SELECT * FROM capturas LIMIT 1";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            item_captura item = new item_captura();

            item.id = "";


            if (cursor.moveToFirst()) {

                Integer idint = cursor.getInt(0);
                item.id = idint.toString();
                item.certificado = cursor.getString(1);
                item.resultado = cursor.getString(2);
                item.horario = cursor.getString(3);
                item.evento = cursor.getString(4);
                item.latitude = cursor.getString(5);
                item.longitude = cursor.getString(6);
                item.idDispositivo = cursor.getString(7);

            }

            cursor.close();
            db.close();

            return item;
        }


    }


}

