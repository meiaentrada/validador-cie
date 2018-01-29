package br.org.meiaentrada.validadorcie;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.HashMap;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.DatabaseUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.net.ConnectivityManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;

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


import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.openssl.PEMParser;

import org.json.*;

import android.graphics.PorterDuff;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

import java.text.Normalizer;

import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.JsonObjectRequest;

import android.support.constraint.ConstraintSet;
import android.location.LocationManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.Location;
import android.provider.Settings.Secure;
import android.text.InputType;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.android.volley.AuthFailureError;

import android.util.DisplayMetrics;

import br.org.meiaentrada.validadorcie.components.DateInputMask;
import br.org.meiaentrada.validadorcie.components.DatePickerFragment;
import br.org.meiaentrada.validadorcie.configuration.GlobalConstants;
import br.org.meiaentrada.validadorcie.model.ItemCaptura;
import br.org.meiaentrada.validadorcie.model.RetornoValidacao;
import br.org.meiaentrada.validadorcie.model.ValidacaoDTO;
import br.org.meiaentrada.validadorcie.enumeration.BarcodeType;
import br.org.meiaentrada.validadorcie.service.BarcodeService;
import br.org.meiaentrada.validadorcie.service.CertificadoService;
import br.org.meiaentrada.validadorcie.service.HttpService;
import br.org.meiaentrada.validadorcie.service.ToastService;
import br.org.meiaentrada.validadorcie.util.CpfUtil;
import br.org.meiaentrada.validadorcie.util.HashUtil;
import br.org.meiaentrada.validadorcie.util.StringContentEncoder;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    BarcodeDetector barcodeDetector;
    String eventoCfg;
    SharedPreferences sharedPref;
    DatabaseHandler db = new DatabaseHandler(this);

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private TextView barcodeValue;
    private TextView conectado;
    private TextView evento;
    private ProgressBar fotop;
    private String codigoCfg;
    private String crlOrigem;
    private String chavepublicaOrigem;
    private ImageView foto;

    private FloatingActionButton prox;
    private FloatingActionButton fabEvento;
    private FloatingActionButton fabCodigoAcesso;
    private FloatingActionButton fabCpf;
    private FloatingActionButton fabCodigoDataNascimento;

    private ConstraintLayout layout1;
    public AlertDialog alerta;

    LocationManager locationManager;
    String provider;
    MyLocationListener mylistener;
    Criteria criteria;
    String latitude;
    String longitude;
    String androidId;

    FloatingActionButton fabMenu;
    Animation animFabOpen, animFabClose, animFabRotateClock, animFabRotateAntiClock;

    boolean isMenuOpen = false;

    private Gson jsonParser = new Gson();

    private CertificadoService certificadoService = new CertificadoService();


    ConstraintLayout contCpf, contEvento, contChave, contCodData;

    View layoutDialogValidarCodigoUsoDataNascimento;

    CodigoUsoDataNascimentoActivity codigoUsoDataNascimentoActivity = new CodigoUsoDataNascimentoActivity();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.activity_main);

//        layoutDialogValidarCodigoUsoDataNascimento =
//                View.inflate(this, R.layout.dialog_validar_codigo_uso_data_nascimento, null);
//        EditText editDataNascimento = layoutDialogValidarCodigoUsoDataNascimento.findViewById(R.id.dataNacimento);
//        editDataNascimento.setOnFocusChangeListener(new ShowDatePickerListener());

//        editDataNascimento.addTextChangedListener(new DateInputMask(editDataNascimento));

//        codigoUsoDataNascimentoActivity.editDataNascimento.;

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        eventoCfg = sharedPref.getString("evento", "");
        codigoCfg = sharedPref.getString("codigo", "");
        cameraView = findViewById(R.id.camera);
        barcodeValue = findViewById(R.id.resultado);
        prox = findViewById(R.id.proximo);
        prox.setVisibility(View.GONE);
        foto = findViewById(R.id.foto);
        foto.setVisibility(View.GONE);
        fotop = findViewById(R.id.fotop);
        fotop.setVisibility(View.GONE);
        conectado = findViewById(R.id.conectado);
        evento = findViewById(R.id.evento);
        evento.setText(eventoCfg);
        layout1 = findViewById(R.id.layout1);
        androidId = Secure.ANDROID_ID;

        contCpf = findViewById(R.id.cont_cpf);
        contEvento = findViewById(R.id.cont_evento);
        contChave = findViewById(R.id.cont_chave_acesso);
        contCodData = findViewById(R.id.cont_codigo_data_nasc);

        fabMenu = findViewById(R.id.menu);
        fabCodigoAcesso = findViewById(R.id.codigo_definir);
        fabCpf = findViewById(R.id.cpf_definir);
        fabEvento = findViewById(R.id.evento_definir);
        fabCodigoDataNascimento = findViewById(R.id.codigo_uso_dt_nascimento);

        animFabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        animFabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        animFabRotateClock = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
        animFabRotateAntiClock = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise);

        fabMenu.setOnClickListener(view -> {

            if (isMenuOpen) {
                contChave.startAnimation(animFabClose);
                contCpf.startAnimation(animFabClose);
                contEvento.startAnimation(animFabClose);
                contCodData.startAnimation(animFabClose);

                fabMenu.startAnimation(animFabRotateAntiClock);

                fabCodigoAcesso.setClickable(false);
                fabCpf.setClickable(false);
                fabCodigoDataNascimento.setClickable(false);
                fabEvento.setClickable(false);

                isMenuOpen = false;
            } else {
                contChave.startAnimation(animFabOpen);
                contCpf.startAnimation(animFabOpen);
                contEvento.startAnimation(animFabOpen);
                contCodData.startAnimation(animFabOpen);

                fabMenu.startAnimation(animFabRotateClock);

                fabCodigoAcesso.setClickable(true);
                fabCpf.setClickable(true);
                fabEvento.setClickable(true);
                fabCodigoDataNascimento.setClickable(true);
                isMenuOpen = true;
            }

        });


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
                if (location != null)
                    mylistener.onLocationChanged(location);
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
                        barcodeValue.post(() -> {

                            String document = barcodes.valueAt(0).displayValue;

                            cameraSource.stop();
                            cameraView.setVisibility(View.GONE);
                            fabEvento.setVisibility(View.GONE);
                            fabCodigoAcesso.setVisibility(View.GONE);
                            fabCpf.setVisibility(View.GONE);
                            evento.setVisibility(View.GONE);
                            fabCodigoDataNascimento.setVisibility(View.GONE);

                            ConstraintSet set = new ConstraintSet();
                            set.clone(layout1);
                            set.clear(barcodeValue.getId(), ConstraintSet.TOP);
                            set.clear(barcodeValue.getId(), ConstraintSet.BOTTOM);
                            set.connect(barcodeValue.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 8);
                            set.connect(barcodeValue.getId(), ConstraintSet.BOTTOM, layout1.getId(), ConstraintSet.BOTTOM, 8);
                            set.applyTo(layout1);

                            eventoCfg = sharedPref.getString("evento", "");
                            if (eventoCfg.isEmpty())
                                eventoCfg = "Evento indefinido";

                            BarcodeType barcodeType = BarcodeService.getBarcodeType(document);

                            if (barcodeType == BarcodeType.CDNE_URL) {

                                String[] fields =
                                        BarcodeService.getCodigoAndDataNascimento(document);

                                String codigoUso = fields[0];
                                String dataNascimento = fields[1];
                                String codigoAcesso = sharedPref.getString("codigo", "");
                                String evento = sharedPref.getString("evento", "");

                                try {
                                    evento = URLEncoder.encode(evento, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                        GlobalConstants.URL_VALIDATE_CODIGO_USO_AND_DT_NASCIMENTO + String.format(
                                                "?codigoAcesso=%s&dataNascimento=%s&codigoUso=%s&evento=%s", codigoAcesso, dataNascimento, codigoUso, evento), response -> {

                                    Long tsLong = System.currentTimeMillis() / 1000;
                                    String ts = tsLong.toString();

                                    ValidacaoDTO validacaoDTO = jsonParser.fromJson(response, ValidacaoDTO.class);
                                    if (!validacaoDTO.getStatus()) {

                                        barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                        barcodeValue.setText("Documento Invalido!");
                                        prox.setVisibility(View.VISIBLE);

                                    } else {

                                        barcodeValue.setTextColor(Color.rgb(0, 255, 0));
                                        barcodeValue.setText("Documento Valido!");

                                        if (verificaSinalDados())
                                            downloadImagem(validacaoDTO.getFoto());
                                        else
                                            prox.setVisibility(View.VISIBLE);

                                    }

                                    db.adicionaCaptura("", validacaoDTO.getStatus(), ts, eventoCfg);

                                }, error -> Log.e(MainActivity.class.getName(), error.getMessage()));

                                queue.add(stringRequest);

                            } else {

                                RetornoValidacao emissor = pegaEmissor(document);

                                if (emissor.getErro()) {

                                    barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                    Long tsLong = System.currentTimeMillis() / 1000;
                                    String ts = tsLong.toString();
                                    db.adicionaCaptura(document, emissor.getErro(), ts, eventoCfg);
                                    barcodeValue.setText(emissor.getResultado());
                                    prox.setVisibility(View.VISIBLE);

                                } else {

                                    String emissor_chave = emissor.getResultado().concat("_chave");
                                    String emissor_crl = emissor.getResultado().concat("_crl");

                                    chavepublicaOrigem = sharedPref.getString(emissor_chave, "");
                                    crlOrigem = sharedPref.getString(emissor_crl, "");

                                    RetornoValidacao resultado_valida = certificadoService.validaCertificado(document, chavepublicaOrigem, crlOrigem);

                                    if (resultado_valida.getErro()) {

                                        barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                        prox.setVisibility(View.VISIBLE);

                                    } else {

                                        barcodeValue.setTextColor(Color.rgb(0, 255, 0));

                                        if (verificaSinalDados()) {

                                            String urlimagem = GlobalConstants.URL_FOTOS + HashUtil.getMD5(document) + "/image.jpg";
                                            downloadImagem(urlimagem);

                                        } else {
                                            prox.setVisibility(View.VISIBLE);
                                        }

                                    }

                                    Long tsLong = System.currentTimeMillis() / 1000;
                                    String ts = tsLong.toString();
                                    db.adicionaCaptura(document, resultado_valida.getErro(), ts, eventoCfg);
                                    barcodeValue.setText(resultado_valida.getResultado());

                                }

                            }
                        });
                    }
                }
            });

        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);

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
        registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    //Checa e solicita permissoes de acesso
    private boolean checkAndRequestPermissions() {

        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int intern = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int acl = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int rps = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int ans = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int aws = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        if (intern != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        if (rps != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        if (ans != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        if (aws != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE);
        if (acl != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return checkAndRequestPermissions();
        }
        return true;

    }

    // remove acentos de uma string
    public static String stripAccents(String input) {
        return input == null ? null :
                Normalizer.normalize(input, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // verifica mudancas de estado de conectividade
    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (verificaSinalDados()) {

                pegaChavesNv();
                conectado.setText("");

            } else {

                String offline = "OFFLINE";
                conectado.setText(offline);

            }

        }

    };

    //verifica se tem sinal de dados
    public boolean verificaSinalDados() {

        try {

            int rcl = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);
            if (rcl == PackageManager.PERMISSION_GRANTED) {

                ConnectivityManager conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMgr != null)
                    return (conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public void dialogoAviso(String aviso) {

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

    public void dialogoCpf(View view) {

        if (alerta != null)
            alerta.dismiss();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Validar CPF do Estudante");

        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Informe o CPF do Estudante");
        editText.getBackground().mutate()
                .setColorFilter(getResources().getColor(
                        R.color.common_google_signin_btn_text_light), PorterDuff.Mode.SRC_ATOP);

        alertDialogBuilder.setView(editText);

        int paddingPixel = 20;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        editText.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

        alertDialogBuilder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
        });

        alertDialogBuilder.setPositiveButton(R.string.dialog_ok, (dialog, id) -> {

            String retorno = editText.getText().toString();
            if (CpfUtil.isValid(retorno))
                validaCpf(retorno);
            else {
                if (retorno.length() > 0)
                    dialogoAviso("CPF inválido");

            }

        });

        alerta = alertDialogBuilder.create();
        alerta.show();

    }

    public void dialogoValidacaoCasoUsoDataNascimento(View view) {

        if (alerta != null)
            alerta.dismiss();

        final View layout = View.inflate(this, R.layout.dialog_validar_codigo_uso_data_nascimento, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(layout)
                .setTitle("Validar por Código de Uso e Data de Nascimento")
                .setPositiveButton(R.string.dialog_ok, (d, id) -> {

                    EditText edtCodigoUso = layout.findViewById(R.id.codigoUso);
                    EditText editDataNascimento = layout.findViewById(R.id.dataNacimento);

                    String codigoUso = edtCodigoUso.getText().toString();
                    String dataNascimento = editDataNascimento.getText().toString();

                    validaCodigoUsoDataNascimento(codigoUso, dataNascimento);


                }).setNegativeButton(R.string.dialog_cancel, (d, id) -> {
                }
        );

        alerta = builder.create();
        alerta.show();

    }

    public void dialogoEvento(View view) {

        if (alerta != null) {
            alerta.dismiss();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Evento");
        final EditText et = new EditText(this);
        et.setHint("Informe o nome do evento");
        et.getBackground().mutate().setColorFilter(getResources().getColor(R.color.common_google_signin_btn_text_light), PorterDuff.Mode.SRC_ATOP);

        alertDialogBuilder.setView(et);

        int paddingPixel = 20;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        et.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

        alertDialogBuilder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
        });

        alertDialogBuilder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
            String retorno = et.getText().toString();

            if (!retorno.isEmpty()) {

                evento.setText(et.getText().toString());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("evento", retorno);
                editor.apply();
                eventoCfg = sharedPref.getString("evento", "");

            }
        });

        alerta = alertDialogBuilder.create();
        alerta.show();

    }

    public void dialogoCodigo(View view) {

        if (alerta != null)
            alerta.dismiss();

        final View layout = View.inflate(this, R.layout.dialog_codigo_acesso, null);

        String codigo = sharedPref.getString("codigo", "");
        TextView textViewEmail = layout.findViewById(R.id.textEmail);
        textViewEmail.setText("");

        if (!codigo.isEmpty()) {
            String email = "Usuário Atual: \n" + sharedPref.getString("email", "");
            textViewEmail.setText(email);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(layout)
                .setTitle("Código de Acesso")
                .setPositiveButton(R.string.dialog_ok, (d, id) -> {

                    EditText editChaveAcesso = layout.findViewById(R.id.editChaveAcesso);
                    String codigoAcesso = editChaveAcesso.getText().toString().trim();

                    if (!codigoAcesso.isEmpty()) {

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("codigo", codigoAcesso);

                        codigoCfg = sharedPref.getString("codigo", "");

                        String endpoint = GlobalConstants.URL_VALIDATE_OPERADOR + "/" + codigoAcesso;
                        RequestQueue queue = Volley.newRequestQueue(this);

                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.GET, endpoint, null, response -> {

                                    try {

                                        String email = response.getString("email");
                                        editor.putString("email", email);
                                        editor.apply();

                                    } catch (JSONException e) {
                                        Log.e("", e.getMessage());
                                    }

                                }, error -> {

                                    // TODO Auto-generated method stub

                                });

                        queue.add(jsObjRequest);
                        ToastService.showToast("Código de acesso salvo.", getApplicationContext());

                    } else {

                        ToastService.showToast("Código de acesso em branco.", getApplicationContext());

                    }


                }).setNegativeButton(R.string.dialog_cancel, (d, id) -> {
                }
        );

        alerta = builder.create();
        alerta.show();

    }


    // proximo qr_code
    public void proximoQrcode(View view) {

        barcodeValue.setText("");
        barcodeValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        foto.setVisibility(View.GONE);
        prox.setVisibility(View.GONE);
        cameraView.setVisibility(View.VISIBLE);
        fabEvento.setVisibility(View.VISIBLE);
        fabCodigoAcesso.setVisibility(View.VISIBLE);
        evento.setVisibility(View.VISIBLE);
        fabCpf.setVisibility(View.VISIBLE);

        try {

            int rc = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(cameraView.getHolder());
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void validaCpf(final String cpfe) {

        if (verificaSinalDados()) {

            fotop.setVisibility(View.VISIBLE);

            cameraSource.stop();
            cameraView.setVisibility(View.GONE);
            fabEvento.setVisibility(View.GONE);
            fabCodigoAcesso.setVisibility(View.GONE);
            fabCpf.setVisibility(View.GONE);
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
                    response -> {

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
                                barcodeValue.setText("\n".concat(StringContentEncoder.makeUtf8(msgerro)).concat("\n"));
                                prox.setVisibility(View.VISIBLE);

                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                            fotop.setVisibility(View.GONE);
                            barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                            barcodeValue.setText("\nErro de conectividade, tente novamente\n");
                            prox.setVisibility(View.VISIBLE);

                        }
                    },
                    error -> {

                        fotop.setVisibility(View.GONE);
                        barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                        barcodeValue.setText("\nErro de conectividade, tente novamente\n");
                        prox.setVisibility(View.VISIBLE);

                    }
            ) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("cpf", cpfe);
                    params.put("codigoAcesso", codigoCfg);

                    return params;
                }

            };

            queue.add(postRequest);

        } else {
            dialogoAviso("Sem conectividade");
        }

    }

    public void validaCodigoUsoDataNascimento(String codigoUso, String dataNascimento) {

        if (verificaSinalDados()) {

            fotop.setVisibility(View.VISIBLE);

            cameraSource.stop();
            cameraView.setVisibility(View.GONE);
            fabEvento.setVisibility(View.GONE);
            fabCodigoAcesso.setVisibility(View.GONE);
            fabCpf.setVisibility(View.GONE);
            evento.setVisibility(View.GONE);

            ConstraintSet set = new ConstraintSet();
            set.clone(layout1);
            set.clear(barcodeValue.getId(), ConstraintSet.TOP);
            set.clear(barcodeValue.getId(), ConstraintSet.BOTTOM);
            set.connect(barcodeValue.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 8);
            set.connect(barcodeValue.getId(), ConstraintSet.BOTTOM, layout1.getId(), ConstraintSet.BOTTOM, 8);
            set.applyTo(layout1);

            String codigoAcesso = sharedPref.getString("codigo", "");
            String evento = sharedPref.getString("evento", "");

            try {
                evento = URLEncoder.encode(evento, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest postRequest = new StringRequest(Request.Method.GET,
                    GlobalConstants.URL_VALIDATE_CODIGO_USO_AND_DT_NASCIMENTO + String.format(
                            "?codigoAcesso=%s&dataNascimento=%s&codigoUso=%s&evento=%s", codigoAcesso, dataNascimento, codigoUso, evento), response -> {

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
                        barcodeValue.setText("\n".concat(StringContentEncoder.makeUtf8(msgerro)).concat("\n"));
                        prox.setVisibility(View.VISIBLE);

                    }

                } catch (Exception e) {

                    e.printStackTrace();
                    fotop.setVisibility(View.GONE);
                    barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                    barcodeValue.setText("\nErro de conectividade, tente novamente\n");
                    prox.setVisibility(View.VISIBLE);

                }
            }, error -> {

                fotop.setVisibility(View.GONE);
                barcodeValue.setTextColor(Color.rgb(255, 0, 0));
                barcodeValue.setText("\nErro de conectividade, tente novamente\n");
                prox.setVisibility(View.VISIBLE);

            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }

            };

            queue.add(postRequest);

        } else {
            dialogoAviso("Sem conectividade");
        }

    }

    // envia captura  para o servidor
    public void mandaCaptura() {

        try {

            final ItemCaptura proxi = db.retornaProximo();
            if (!proxi.getId().equals("")) {

                RequestQueue queue = Volley.newRequestQueue(this);
                HashMap<String, Object> params = new HashMap<>();
                params.put("certificado", proxi.getCertificado());
                params.put("status", proxi.getResultado().equals("0"));
                params.put("data", proxi.getHorario());
                params.put("evento", proxi.getEvento());
                params.put("latitude", proxi.getLatitude());
                params.put("longitude", proxi.getLongitude());
                params.put("idDispositivo", proxi.getIdDispositivo());
                params.put("codigoAcesso", codigoCfg);

                JsonObjectRequest postRequest = new JsonObjectRequest(GlobalConstants.URL_CAPTURAS, new JSONObject(params),
                        response -> {
                            try {

                                Boolean retorno = response.getBoolean("status");
                                if (retorno) {

                                    db.deletaItem(proxi.getId());
                                    mandaCaptura();

                                } else {

                                    String msgerro = response.getString("msg");
                                    dialogoAviso(msgerro);

                                }

                            } catch (Exception e) {
                                Log.e(MainActivity.class.getName(), e.getMessage());
                            }
                        },
                        error -> Log.e(MainActivity.class.getName(), error.getMessage()));

                queue.add(postRequest);

            }

        } catch (Exception e) {
            Log.e(MainActivity.class.getName(), e.getMessage());
        }

    }

    // busca chaves publicas e CRLs no meiaentrada.org.br
    public void pegaChavesNv() {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstants.URL_CHAVES,
                response -> {

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

                        if (db.totalCapturas() > 0) {
                            mandaCaptura();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> Log.e(MainActivity.class.getName(), error.getMessage()));

        queue.add(stringRequest);

    }

    // busca emissor dentro do certificado
    RetornoValidacao pegaEmissor(String certDNE) {

        RetornoValidacao retornove = new RetornoValidacao();
        retornove.setResultado(GlobalConstants.ERRO_INVALIDO);
        retornove.setErro(true);

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
            retornove.setResultado(nomefull);
            retornove.setErro(false);
            return retornove;

        } catch (Exception e) {
            return retornove;
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
                .into(foto, new Callback() {
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

        private void adicionaCaptura(String certif, boolean resul, String horario, String evento) {

            SQLiteDatabase dbx = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("certificado", certif != null ? HashUtil.getMD5(certif) : null);
            values.put("resultado", resul);
            values.put("horario", horario);
            values.put("evento", evento);
            values.put("latitude", latitude);
            values.put("longitude", longitude);
            values.put("idDispositivo", androidId);

            dbx.insert("capturas", null, values);
            dbx.close();

            if (verificaSinalDados()) {
                mandaCaptura();
            }

        }

        private int totalCapturas() {

            SQLiteDatabase db = this.getReadableDatabase();
            long cnt = DatabaseUtils.queryNumEntries(db, "capturas");
            Integer cnti = (int) cnt;
            db.close();
            return cnti;

        }

        private void deletaItem(String id) {

            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("capturas", "id = ?", new String[]{id});
            db.close();

        }

        private ItemCaptura retornaProximo() {

            String selectQuery = "SELECT * FROM capturas LIMIT 1";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            ItemCaptura item = new ItemCaptura();

            item.setId("");

            if (cursor.moveToFirst()) {

                Integer idint = cursor.getInt(0);
                item.setId(idint.toString());
                item.setCertificado(cursor.getString(1));
                item.setResultado(cursor.getString(2));
                item.setHorario(cursor.getString(3));
                item.setEvento(cursor.getString(4));
                item.setLatitude(cursor.getString(5));
                item.setLongitude(cursor.getString(6));
                item.setIdDispositivo(cursor.getString(7));

            }

            cursor.close();
            db.close();

            return item;
        }

    }

    private void setDate(final Calendar calendar) {

        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

        final View layout = View.inflate(this, R.layout.dialog_validar_codigo_uso_data_nascimento, null);
        EditText editDataNascimento = layout.findViewById(R.id.dataNacimento);
        editDataNascimento.setText(dateFormat.format(calendar.getTime()));

    }

    public void showDatePickerDialog(View view) {

//        DatePickerFragment fragment = new DatePickerFragment();
//        fragment.show(this.getSupportFragmentManager(), "");

    }

    public void showDatePicker() {

        Calendar calendario = Calendar.getInstance();
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH);
        int ano = calendario.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, day) -> {

            final View layout = View.inflate(this, R.layout.dialog_validar_codigo_uso_data_nascimento, null);
            EditText editDataNascimento = layout.findViewById(R.id.dataNacimento);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            Date date = calendar.getTime();
            editDataNascimento.setText(date.toString());

        }, ano, mes, dia);
        datePickerDialog.show();

    }

    private class ShowDatePickerListener
            implements View.OnFocusChangeListener, View.OnClickListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus)
                showDatePicker();

        }

        @Override
        public void onClick(View v) {
            showDatePicker();
        }

    }

}
