package br.org.meiaentrada.validadorcie;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.brodda.validadorcie.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.org.meiaentrada.validadorcie.configuration.GlobalConstants;
import br.org.meiaentrada.validadorcie.enumeration.BarcodeType;
import br.org.meiaentrada.validadorcie.model.SQLiteCapturaDao;
import br.org.meiaentrada.validadorcie.model.DatabaseManager;
import br.org.meiaentrada.validadorcie.model.DeviceLocation;
import br.org.meiaentrada.validadorcie.model.Captura;
import br.org.meiaentrada.validadorcie.model.CapturaDao;
import br.org.meiaentrada.validadorcie.model.RetornoValidacao;
import br.org.meiaentrada.validadorcie.model.ValidacaoDTO;
import br.org.meiaentrada.validadorcie.service.BarcodeService;
import br.org.meiaentrada.validadorcie.service.CertificadoService;
import br.org.meiaentrada.validadorcie.service.SharedPreferencesService;
import br.org.meiaentrada.validadorcie.service.ToastService;
import br.org.meiaentrada.validadorcie.util.AnimationUtil;
import br.org.meiaentrada.validadorcie.util.CpfUtil;
import br.org.meiaentrada.validadorcie.util.HashUtil;
import br.org.meiaentrada.validadorcie.util.StringContentEncoder;
import br.org.meiaentrada.validadorcie.view.ValidationView;


public class MainActivity
        extends AppCompatActivity
        implements ValidationView, DatePickerDialog.OnDateSetListener {

    public static final String TAG = "MainActivity";
    private final Gson JSON_PARSER = new Gson();

    private String mDeviceId;
    private String mEvento;
    private String mCodigoAcesso;
    private String crlOrigem;
    private String chavePublicaOrigem;
    boolean isMenuOpen = false;

    DatabaseManager databaseHandler;
    private CapturaDao capturaDao;
    private DeviceLocation deviceLocation;
    DeviceThings deviceThings;
    private SharedPreferencesService sharedPreferencesService;

    private SharedPreferences sharedPref;
    BroadcastReceiver networkStateReceiver;
    BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private TextView txtBarcodeValue, txtInternetStatus, txtEvento;
    private ProgressBar progressBar;
    private ImageView imgPhoto;

    private FloatingActionButton prox;
    private FloatingActionButton fabEvento;
    private FloatingActionButton fabCodigoAcesso;
    private FloatingActionButton fabCpf;
    private FloatingActionButton fabCodigoDataNascimento;
    private FloatingActionButton fabMenu;
    private Animation animFabOpen, animFabClose, animFabRotateClock, animFabRotateAntiClock;

    private ConstraintLayout layout1;
    public AlertDialog alerta;

    LocationManager locationManager;
    String provider;
    LocationListener mLocationListener;
    Criteria criteria;

    private ConstraintLayout contCpf, contEvento, contChave, contCodData;
    private List<View> menuContainers;

    private CertificadoService certificadoService = new CertificadoService();

    public void initializeUIComponents() {

        cameraView = findViewById(R.id.camera);
        txtBarcodeValue = findViewById(R.id.status);
        prox = findViewById(R.id.proximo);
        prox.setVisibility(View.GONE);
        imgPhoto = findViewById(R.id.foto);
        imgPhoto.setVisibility(View.GONE);
        progressBar = findViewById(R.id.fotop);
        progressBar.setVisibility(View.GONE);
        txtInternetStatus = findViewById(R.id.conectado);
        txtEvento = findViewById(R.id.evento);
        txtEvento.setText(mEvento);
        layout1 = findViewById(R.id.layout1);

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

        menuContainers = new ArrayList<>(Arrays.asList(contChave, contEvento, contCpf, contCodData));

        fabMenu.setOnClickListener(view -> {

            if (isMenuOpen)
                closeMenu();
            else
                openMenu();

        });

    }

    public void initializeDeviceResources() {

        deviceLocation = new DeviceLocation();
        deviceThings = new DeviceThings(this);
        sharedPreferencesService = new SharedPreferencesService(this);

        sharedPref = deviceThings.getSharedPref();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true)
                .build();

        // LOCATION MANAGER
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);
        provider = locationManager.getBestProvider(criteria, false);

        int rcl = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (rcl == PackageManager.PERMISSION_GRANTED) {

            Location location = locationManager.getLastKnownLocation(provider);
            mLocationListener = new DeviceLocationListener(deviceLocation, this);
            if (location != null)
                mLocationListener.onLocationChanged(location);
            locationManager.requestLocationUpdates(provider, 200, 1, mLocationListener);

        }

        // BROADCAST RECEIVER
        networkStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if (verificarSinalDados()) {

                    getCrlsAndChavesPublicas();
                    txtInternetStatus.setText(getString(R.string.online));

                } else
                    txtInternetStatus.setText(getString(R.string.offline));

            }

        };

        // CAMERA VIEW
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {

                    int rc = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                    if (rc == PackageManager.PERMISSION_GRANTED)
                        cameraSource.start(cameraView.getHolder());

                } catch (IOException ex) {

                    Log.e("CAMERA_SOURCE", ex.getMessage());

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

        // BARCODE DETECTOR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(() -> {

                        String document = barcodes.valueAt(0).displayValue;

                        cameraSource.stop();
                        cameraView.setVisibility(View.GONE);

                        contEvento.setVisibility(View.GONE);
                        contChave.setVisibility(View.GONE);
                        contCpf.setVisibility(View.GONE);
                        contCodData.setVisibility(View.GONE);

                        txtEvento.setVisibility(View.GONE);

                        ConstraintSet set = new ConstraintSet();
                        set.clone(layout1);
                        set.clear(txtBarcodeValue.getId(), ConstraintSet.TOP);
                        set.clear(txtBarcodeValue.getId(), ConstraintSet.BOTTOM);
                        set.connect(txtBarcodeValue.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 8);
                        set.connect(txtBarcodeValue.getId(), ConstraintSet.BOTTOM, layout1.getId(), ConstraintSet.BOTTOM, 8);
                        set.applyTo(layout1);

                        mEvento = sharedPref.getString(GlobalConstants.SHARED_PREF_EVENTO, GlobalConstants.SHARED_PREF_DEFAULT);
                        if (mEvento.isEmpty()) {
                            mEvento = getString(R.string.evento_indefinido);
                        }

                        BarcodeType barcodeType = BarcodeService.getBarcodeType(document);
                        if (barcodeType == BarcodeType.CDNE_URL) {

                            String[] fields =
                                    BarcodeService.getCodigoAndDataNascimento(document);

                            String codigoUso = fields[0];
                            String dataNascimento = fields[1];
                            String codigoAcesso = sharedPref.getString(GlobalConstants.SHARED_PREF_CODIGO_ACESSO, GlobalConstants.SHARED_PREF_DEFAULT);
                            String evento = sharedPref.getString(GlobalConstants.SHARED_PREF_EVENTO, GlobalConstants.SHARED_PREF_DEFAULT);

                            try {

                                evento = URLEncoder.encode(evento, "UTF-8");

                            } catch (UnsupportedEncodingException e) {

                                Log.e(TAG, e.getMessage());
                            }

                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                    String.format(
                                            GlobalConstants.URL_VALIDATE_CODIGO_USO_AND_DT_NASCIMENTO,
                                            codigoAcesso, dataNascimento, codigoUso, evento), response -> {

                                Long tsLong = System.currentTimeMillis() / 1000;
                                String ts = tsLong.toString();

                                ValidacaoDTO validacaoDTO = JSON_PARSER.fromJson(response, ValidacaoDTO.class);
                                if (!validacaoDTO.getStatus()) {

                                    txtBarcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                    txtBarcodeValue.setText(R.string.documento_invalido);
                                    prox.setVisibility(View.VISIBLE);

                                } else {

                                    txtBarcodeValue.setTextColor(Color.rgb(0, 255, 0));
                                    txtBarcodeValue.setText(R.string.documento_invalido);

                                    if (verificarSinalDados())
                                        downloadImagem(validacaoDTO.getFoto());
                                    else
                                        prox.setVisibility(View.VISIBLE);

                                }
                                closeMenuWithoutAnimation();

                                Captura captura = new Captura();
                                captura.setStatus(String.valueOf(validacaoDTO.getStatus()));
                                captura.setEvento(mEvento);
                                captura.setLatitude(String.valueOf(deviceLocation.getLatitude()));
                                captura.setLongitude(String.valueOf(deviceLocation.getLongitude()));
                                captura.setIdDispositivo(mDeviceId);
                                captura.setHorario(ts);
                                captura.setCodigoAcesso(mCodigoAcesso);

                                capturaDao.save(captura);

                            }, error -> Log.e(TAG, error.getMessage()));

                            queue.add(stringRequest);

                        } else {

                            RetornoValidacao emissor = certificadoService.pegaEmissor(document);

                            if (emissor.getErro()) {

                                txtBarcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                Long tsLong = System.currentTimeMillis() / 1000;
                                String ts = tsLong.toString();

                                Captura captura = new Captura();
                                captura.setCertificado(document);
                                captura.setStatus(String.valueOf(emissor.getErro()));
                                captura.setEvento(mEvento);
                                captura.setLatitude(String.valueOf(deviceLocation.getLatitude()));
                                captura.setLongitude(String.valueOf(deviceLocation.getLongitude()));
                                captura.setIdDispositivo(mDeviceId);
                                captura.setHorario(ts);
                                captura.setCodigoAcesso(mCodigoAcesso);

                                capturaDao.save(captura);

                                txtBarcodeValue.setText(emissor.getResultado());
                                prox.setVisibility(View.VISIBLE);

                            } else {

                                String emissor_chave = emissor.getResultado().concat("_chave");
                                String emissor_crl = emissor.getResultado().concat("_crl");

                                chavePublicaOrigem = sharedPref.getString(emissor_chave, GlobalConstants.SHARED_PREF_DEFAULT);
                                crlOrigem = sharedPref.getString(emissor_crl, GlobalConstants.SHARED_PREF_DEFAULT);

                                RetornoValidacao resultado_valida = certificadoService.validaCertificado(document, chavePublicaOrigem, crlOrigem);

                                if (resultado_valida.getErro()) {

                                    txtBarcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                    prox.setVisibility(View.VISIBLE);

                                } else {

                                    txtBarcodeValue.setTextColor(Color.rgb(0, 255, 0));

                                    if (verificarSinalDados()) {

                                        String urlimagem = GlobalConstants.URL_FOTOS + HashUtil.getMD5(document) + "/image.jpg";
                                        downloadImagem(urlimagem);

                                    } else
                                        prox.setVisibility(View.VISIBLE);

                                }

                                Long tsLong = System.currentTimeMillis() / 1000;
                                String ts = tsLong.toString();

                                Captura captura = new Captura(document,
                                        String.valueOf(resultado_valida.getErro()),
                                        ts,
                                        mEvento,
                                        String.valueOf(deviceLocation.getLatitude()),
                                        String.valueOf(deviceLocation.getLongitude()),
                                        mDeviceId
                                );
                                captura.setCodigoAcesso(mCodigoAcesso);
                                capturaDao.save(captura);
                                txtBarcodeValue.setText(resultado_valida.getResultado());

                            }

                        }
                    });
                }
            }

        });

    }

    @Override
    public void onCreate(Bundle state) {

        super.onCreate(state);
        setContentView(R.layout.activity_main);

        databaseHandler = new DatabaseManager(this, null);
        capturaDao = new SQLiteCapturaDao(databaseHandler);

        initializeUIComponents();
        initializeDeviceResources();

        mCodigoAcesso = sharedPreferencesService.getCodigoAcesso();
        mEvento = sharedPreferencesService.getEvento();
        mDeviceId = HashUtil.getMD5(mCodigoAcesso);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);

    }

    @Override
    public void onStart() {

        super.onStart();

    }

    @Override
    public void onResume() {

        super.onResume();

        if (capturaDao.count() > 0)
            pushCapturaToServer();

    }

    @Override
    public void onPause() {

        super.onPause();
        unregisterReceiver(networkStateReceiver);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    public boolean verificarSinalDados() {

        try {

            int rcl = ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_NETWORK_STATE);

            if (rcl == PackageManager.PERMISSION_GRANTED) {

                ConnectivityManager connectivityManager =
                        (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null)
                    return (connectivityManager.getActiveNetworkInfo().isAvailable() &&
                            connectivityManager.getActiveNetworkInfo().isConnected());

            }

        } catch (Exception e) {

            Log.e(TAG, e.getMessage());

        }
        return false;

    }

    public void openDialogAviso(String message) {

        if (alerta != null)
            alerta.dismiss();

        final TextView et = new TextView(this);
        et.setText(message);
        et.setTextColor(Color.BLACK);
        et.setTextSize(18);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(et);

        int paddingPixel = 14;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        et.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                });

        alerta = alertDialogBuilder.create();
        alerta.show();

    }

    public void openDialogEvento(View view) {

        if (alerta != null)
            alerta.dismiss();

        final EditText edtDialogEvento = new EditText(this);
        edtDialogEvento.setHint(R.string.informe_codigo_evento);
        edtDialogEvento.getBackground()
                .mutate()
                .setColorFilter(getResources().getColor(
                        R.color.common_google_signin_btn_text_light),
                        PorterDuff.Mode.SRC_ATOP);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.evento);
        alertDialogBuilder.setView(edtDialogEvento);

        int paddingPixel = 20;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        edtDialogEvento.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

        alertDialogBuilder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
        });

        alertDialogBuilder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
            String evento = edtDialogEvento.getText().toString();
            if (!evento.isEmpty()) {

                txtEvento.setText(evento);
                sharedPreferencesService.setEvento(evento);
                mEvento = sharedPreferencesService.getEvento();

            }
        });
        alerta = alertDialogBuilder.create();
        alerta.show();

    }

    public void openDialogCpf(View view) {

        if (alerta != null)
            alerta.dismiss();

        EditText edtCpf = new EditText(this);
        edtCpf.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtCpf.setHint(R.string.informe_cpf_estudante);
        edtCpf.getBackground().mutate()
                .setColorFilter(getResources().getColor(
                        R.color.common_google_signin_btn_text_light), PorterDuff.Mode.SRC_ATOP);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.validar_cpf_estudante);
        alertDialogBuilder.setView(edtCpf);

        int paddingPixel = 20;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        edtCpf.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

        alertDialogBuilder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
        });

        alertDialogBuilder.setPositiveButton(R.string.dialog_ok, (dialog, id) -> {
            String cpf = edtCpf.getText().toString();
            if (CpfUtil.isValid(cpf))
                validarCpf(cpf);
            else
                openDialogAviso(getString(R.string.documento_invalido));
        });

        alerta = alertDialogBuilder.create();
        alerta.show();

    }

    public void openDialogCodigo(View view) {

        if (alerta != null)
            alerta.dismiss();

        final View layout = View.inflate(this, R.layout.dialog_codigo_acesso, null);

        String codigo = sharedPreferencesService.getCodigoAcesso();

        TextView textViewEmail = layout.findViewById(R.id.textEmail);
        if (!codigo.isEmpty())
            textViewEmail.setText(String.format("%s\n%s",
                    getString(R.string.usuario_atual), sharedPreferencesService.getEmail()));
        else
            textViewEmail.setText(GlobalConstants.SHARED_PREF_DEFAULT);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(layout)
                .setTitle(R.string.codigo_acesso)
                .setPositiveButton(R.string.dialog_ok, (d, id) -> {

                    EditText editChaveAcesso = layout.findViewById(R.id.editChaveAcesso);
                    String codigoAcesso = editChaveAcesso.getText().toString().trim();

                    if (!codigoAcesso.isEmpty()) {

                        sharedPreferencesService.setCodigoAcesso(codigoAcesso);
                        mCodigoAcesso = sharedPreferencesService.getCodigoAcesso();

                        String endpoint = GlobalConstants.URL_VALIDATE_OPERADOR + codigoAcesso;
                        RequestQueue queue = Volley.newRequestQueue(this);

                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.GET, endpoint, null, response -> {

                                    try {

                                        String email = response.getString("email");
                                        sharedPreferencesService.setEmail(email);

                                    } catch (JSONException e) {

                                        Log.e(TAG, e.getMessage());

                                    }

                                }, error -> Log.e(TAG, error.getMessage()));

                        queue.add(jsObjRequest);
                        ToastService.showToast(getString(R.string.codigo_acesso_salvo), getApplicationContext());

                    } else
                        ToastService.showToast(getString(R.string.codigo_acesso_em_branco), getApplicationContext());

                })
                .setNegativeButton(R.string.dialog_cancel, (d, id) -> {
                });

        alerta = builder.create();
        alerta.show();

    }

    public void dialogoValidacaoCasoUsoDataNascimento(View view) {

        if (alerta != null)
            alerta.dismiss();

        final View layout = View.inflate(this, R.layout.dialog_validar_codigo_uso_data_nascimento, null);
        final EditText edtCodigoUso = layout.findViewById(R.id.codigoUso);
        final EditText editDataNascimento = layout.findViewById(R.id.dataNacimento);

        editDataNascimento.setOnClickListener((v) -> showDatePicker(editDataNascimento));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(layout)
                .setTitle(R.string.validar_por_codigo_uso_e_data_nascimento)
                .setPositiveButton(R.string.ok, (d, id) -> {

                    String codigoUso = edtCodigoUso.getText().toString();
                    String dataNascimento = editDataNascimento.getText().toString();

                    String[] dateArray = dataNascimento.split("/");
                    dataNascimento = String.format("%s-%s-%s", dateArray[2], dateArray[1], dateArray[0]);

                    validarCodigoUsoDataNascimento(codigoUso, dataNascimento);

                }).setNegativeButton(R.string.dialog_cancel, (d, id) -> {
                }
        );

        alerta = builder.create();
        alerta.show();

    }

    // proximo qr_code
    public void proximoQrcode(View view) {

        txtBarcodeValue.setText("");
        txtBarcodeValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        imgPhoto.setVisibility(View.GONE);
        prox.setVisibility(View.GONE);
        cameraView.setVisibility(View.VISIBLE);
        contEvento.setVisibility(View.VISIBLE);
        contChave.setVisibility(View.VISIBLE);
        txtEvento.setVisibility(View.VISIBLE);
        contCpf.setVisibility(View.VISIBLE);

        try {

            int rc = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(cameraView.getHolder());
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void validarCpf(final String cpf) {

        if (verificarSinalDados()) {

            progressBar.setVisibility(View.VISIBLE);
            cameraSource.stop();
            cameraView.setVisibility(View.GONE);
            txtEvento.setVisibility(View.GONE);

            closeMenuWithoutAnimation();

            ConstraintSet set = new ConstraintSet();
            set.clone(layout1);
            set.clear(txtBarcodeValue.getId(), ConstraintSet.TOP);
            set.clear(txtBarcodeValue.getId(), ConstraintSet.BOTTOM);
            set.connect(txtBarcodeValue.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 8);
            set.connect(txtBarcodeValue.getId(), ConstraintSet.BOTTOM, layout1.getId(), ConstraintSet.BOTTOM, 8);
            set.applyTo(layout1);

            String endpoint = String.format(GlobalConstants.URL_CPF,
                    mCodigoAcesso,
                    cpf,
                    mDeviceId,
                    deviceLocation.getLatitude(),
                    deviceLocation.getLongitude()
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.GET, endpoint,
                    response -> {

                        progressBar.setVisibility(View.GONE);

                        try {

                            JSONObject obj = new JSONObject(response);

                            Boolean status = obj.getBoolean("status");
                            if (status) {

                                downloadImagem(obj.getString("foto"));
                                txtBarcodeValue.setTextColor(Color.rgb(0, 255, 0));
                                txtBarcodeValue.setText(GlobalConstants.DOC_VALIDO);

                            } else {

                                String errorMessage = obj.getString("msg");
                                txtBarcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                txtBarcodeValue.setText(String.format(
                                        "\n%s\n", StringContentEncoder.makeUtf8(errorMessage)));
                                prox.setVisibility(View.VISIBLE);

                            }


                        } catch (Exception e) {

                            Log.e(TAG, e.getMessage());

                            progressBar.setVisibility(View.GONE);
                            txtBarcodeValue.setTextColor(Color.rgb(255, 0, 0));
                            txtBarcodeValue.setText("\n" + R.string.erro_conectividade + "\n");
                            prox.setVisibility(View.VISIBLE);

                        }

                    },
                    error -> {

                        Log.e(TAG, error.getMessage());

                        progressBar.setVisibility(View.GONE);
                        txtBarcodeValue.setTextColor(Color.rgb(255, 0, 0));
                        txtBarcodeValue.setText(
                                "\n" + R.string.erro_conectividade + "\n");
                        prox.setVisibility(View.VISIBLE);

                    }

            ) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    return new HashMap<>();

                }

            };
            queue.add(postRequest);

        } else
            openDialogAviso(getString(R.string.sem_conectividade));

    }

    public void validarCodigoUsoDataNascimento(String codigoUso, String dataNascimento) {

        if (verificarSinalDados()) {

            progressBar.setVisibility(View.VISIBLE);

            cameraSource.stop();
            cameraView.setVisibility(View.GONE);
            txtEvento.setVisibility(View.GONE);

            closeMenuWithoutAnimation();

            ConstraintSet set = new ConstraintSet();
            set.clone(layout1);
            set.clear(txtBarcodeValue.getId(), ConstraintSet.TOP);
            set.clear(txtBarcodeValue.getId(), ConstraintSet.BOTTOM);
            set.connect(txtBarcodeValue.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 8);
            set.connect(txtBarcodeValue.getId(), ConstraintSet.BOTTOM, layout1.getId(), ConstraintSet.BOTTOM, 8);
            set.applyTo(layout1);

            String codigoAcesso = sharedPreferencesService.getCodigoAcesso();
            String evento =
                    StringContentEncoder.makeUtf8QueryString(sharedPreferencesService.getEvento());

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest postRequest = new StringRequest(Request.Method.GET,
                    String.format(GlobalConstants.URL_VALIDATE_CODIGO_USO_AND_DT_NASCIMENTO,
                            codigoAcesso, dataNascimento, codigoUso, evento),

                    response -> {

                        progressBar.setVisibility(View.GONE);

                        try {

                            JSONObject obj = new JSONObject(response);

                            Boolean status = obj.getBoolean("status");
                            if (status) {

                                txtBarcodeValue.setTextColor(Color.rgb(0, 255, 0));
                                downloadImagem(obj.getString("foto"));
                                txtBarcodeValue.setText(GlobalConstants.DOC_VALIDO);

                            } else {

                                txtBarcodeValue.setTextColor(Color.rgb(255, 0, 0));
                                String msgerro = obj.getString("msg");
                                txtBarcodeValue.setText("\n".concat(StringContentEncoder.makeUtf8(msgerro)).concat("\n"));
                                prox.setVisibility(View.VISIBLE);

                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            txtBarcodeValue.setTextColor(Color.rgb(255, 0, 0));
                            txtBarcodeValue.setText("\n" + R.string.erro_conectividade + "\n");
                            prox.setVisibility(View.VISIBLE);

                        }

                    },
                    error -> {

                        progressBar.setVisibility(View.GONE);
                        txtBarcodeValue.setTextColor(Color.rgb(255, 0, 0));
                        txtBarcodeValue.setText("\n" + R.string.erro_conectividade + "\n");
                        prox.setVisibility(View.VISIBLE);

                    }
            ) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    return new HashMap<>();

                }

            };

            queue.add(postRequest);

        } else
            openDialogAviso(getString(R.string.sem_conectividade));

    }

    public void pushCapturaToServer() {

        try {

            if (capturaDao.next() != null) {

                Captura captura = capturaDao.next();
                if (captura.getCodigoAcesso() == null || captura.getCodigoAcesso().isEmpty())
                    captura.setCodigoAcesso(mCodigoAcesso);

                String jsonCaptura = JSON_PARSER.toJson(captura);

                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest postRequest = new JsonObjectRequest(
                        GlobalConstants.URL_CAPTURAS,
                        new JSONObject(jsonCaptura),
                        response -> {
                            try {

                                Boolean status = response.getBoolean("status");
                                if (status) {
                                    capturaDao.delete(captura.getId());
                                    pushCapturaToServer();
                                } else
                                    openDialogAviso(response.getString("msg"));

                            } catch (Exception e) {

                                Log.e(MainActivity.class.getName(), e.getMessage());

                            }
                        },
                        error -> Log.e(TAG, error.getMessage()));
                queue.add(postRequest);

            }

        } catch (Exception e) {

            Log.e(TAG, e.getMessage());

        }

    }

    // busca chaves publicas e CRLs no meiaentrada.org.br
    public void getCrlsAndChavesPublicas() {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                GlobalConstants.URL_CHAVES,
                response -> {

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray retorno = jsonObject.getJSONArray("retorno");

                        for (int i = 0; i < retorno.length(); i++) {

                            try {

                                JSONObject oneObject = retorno.getJSONObject(i);

                                String jsonEmissor = oneObject.getString("emissor");
                                jsonEmissor = StringContentEncoder.stripAccents(jsonEmissor);
                                jsonEmissor = jsonEmissor
                                        .replaceAll("\\p{Z}", "")
                                        .replaceAll("-", "");

                                String json_chavepublica = oneObject.getString("chavePublica");
                                String json_crl = oneObject.getString("crl");
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(jsonEmissor + "_chave", json_chavepublica);
                                editor.putString(jsonEmissor + "_crl", json_crl);
                                editor.apply();

                            } catch (JSONException e) {

                                Log.e(TAG, e.getMessage());

                            }

                        }

                        if (capturaDao.count() > 0)
                            pushCapturaToServer();

                    } catch (JSONException e) {

                        Log.e(TAG, e.getMessage());
                    }

                },
                error -> Log.e(TAG, error.getMessage()));

        queue.add(stringRequest);

    }

    // download de imgPhoto de estudante usando Picasso
    private void downloadImagem(String url) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widths = displayMetrics.widthPixels;

        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(getApplicationContext())
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .resize(widths, 0)
                .into(imgPhoto, new Callback() {
                    @Override
                    public void onSuccess() {

                        imgPhoto.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        prox.setVisibility(View.VISIBLE);

                        ConstraintSet set = new ConstraintSet();
                        set.clone(layout1);
                        set.clear(txtBarcodeValue.getId(), ConstraintSet.TOP);
                        set.clear(txtBarcodeValue.getId(), ConstraintSet.BOTTOM);
                        set.connect(txtBarcodeValue.getId(), ConstraintSet.TOP, layout1.getId(), ConstraintSet.TOP, 8);
                        set.applyTo(layout1);

                    }

                    @Override
                    public void onError() {

                        progressBar.setVisibility(View.GONE);
                        prox.setVisibility(View.VISIBLE);

                    }
                });

    }

    private void setDate(final Calendar calendar) {

        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

        final View layout = View.inflate(this, R.layout.dialog_validar_codigo_uso_data_nascimento, null);
        EditText editDataNascimento = layout.findViewById(R.id.dataNacimento);
        editDataNascimento.setText(dateFormat.format(calendar.getTime()));

    }

    public void showDatePicker(EditText editDataNascimento) {

        Calendar calendario = Calendar.getInstance();
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH);
        int ano = calendario.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, day) -> {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            Date date = calendar.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            editDataNascimento.setText(simpleDateFormat.format(date));

        }, ano, mes, dia);
        datePickerDialog.show();

    }

    private void closeMenu() {

        AnimationUtil.addAnimation(menuContainers, animFabClose);

        fabMenu.startAnimation(animFabRotateAntiClock);

        fabCodigoAcesso.setClickable(false);
        fabEvento.setClickable(false);
        fabCpf.setClickable(false);
        fabCodigoDataNascimento.setClickable(false);

        isMenuOpen = false;

    }

    private void closeMenuWithoutAnimation() {

        AnimationUtil.addAnimation(menuContainers, animFabClose);

        fabMenu.startAnimation(animFabRotateAntiClock);

        fabCodigoAcesso.setClickable(false);
        fabEvento.setClickable(false);
        fabCpf.setClickable(false);
        fabCodigoDataNascimento.setClickable(false);

        isMenuOpen = false;

    }

    private void openMenu() {

        AnimationUtil.addAnimation(menuContainers, animFabOpen);

        fabMenu.startAnimation(animFabRotateClock);

        fabCodigoAcesso.setClickable(true);
        fabEvento.setClickable(true);
        fabCpf.setClickable(true);
        fabCodigoDataNascimento.setClickable(true);

        isMenuOpen = true;

    }

}
