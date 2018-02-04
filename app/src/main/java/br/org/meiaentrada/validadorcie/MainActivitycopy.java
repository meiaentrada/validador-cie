//package br.org.meiaentrada.validadorcie;
//
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.example.brodda.validadorcie.R;
//
//import br.org.meiaentrada.validadorcie.model.DeviceLocation;
//
//
//public class MainActivitycopy extends AppCompatActivity {
//
//    public static final String TAG = "MainActivity3";
//
//    private LocationManager locationManager;
//    private LocationListener locationListener;
//
//    private TextView txtLatitude;
//    private TextView txtLongitude;
//    private Button btnGetDeviceLocation;
//
//    private DeviceLocation deviceLocation;
//
//    private void initializeComponents() {
//
////        txtLatitude = findViewById(R.id.txtLatitude);
////        txtLongitude = findViewById(R.id.txtLongitude);
////
////        btnGetDeviceLocation = findViewById(R.id.btnGetDeviceLocation);
////
////        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
////        locationListener = new DeviceLocationListener(deviceLocation, getApplicationContext());
////        configureLocationManager();
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.any_layout);
//
//        initializeComponents();
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//
////        switch (requestCode) {
////
////            case 10:
////                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
////                    configureLocationManager();
////                break;
////
////        }
//
//    }
//
//    public void configureLocationManager() {
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////
////            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
////                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////
////                requestPermissions(new String[]{
////                        Manifest.permission.ACCESS_FINE_LOCATION,
////                        Manifest.permission.ACCESS_COARSE_LOCATION,
////                        Manifest.permission.INTERNET
////                }, 10);
////
////                return;
////            }
////
////        } else {
////            configureLocationManager();
////        }
////
////        locationManager.requestLocationUpdates("gps", 5000, 1, locationListener);
//
//    }
//
//    public void getDeviceLocation(View view) {
//
//        txtLatitude.append("" + deviceLocation.getLatitude());
//        txtLongitude.append("" + deviceLocation.getLongitude());
//
//
//    }
//
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
