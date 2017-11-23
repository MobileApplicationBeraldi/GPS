package macc.example.com.postiongps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.location.FusedLocationProviderClient;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private FusedLocationProviderClient flpc=null;
    private LocationRequest mLocationRequest;
    private TextView tv=null;
    private LocationCallback mLocationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Long time = location.getTime();
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String msg="Time: "+time+"\nLat "+lat+"\nLon"+lon;
                    tv.setText(msg);
                }
            };
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }
    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this,"connected...",Toast.LENGTH_LONG).show();
        if (
                //ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
            return;
        }
        getLocation();
       }

       private void getLocation(){
           if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                   ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) Toast.makeText(this,"now I can call",Toast.LENGTH_LONG).show();
        flpc = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); // every 10 s
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        flpc.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());

        Toast.makeText(this,"placing a call...",Toast.LENGTH_LONG).show();

        Task task = flpc.getLastLocation();

        task.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                  mLocation = (Location) task.getResult();
                  double latitude = mLocation.getLatitude();
                  double longitude = mLocation.getLongitude();

                  tv.setText("Current position is\nLat: "+latitude+"\nLon: "+longitude);
            }
        });
       }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode==0) {
                if (grantResults.length ==1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        //&& grantResults[1] == PackageManager.PERMISSION_GRANTED
                        ) {
                    getLocation();

                } else {
                    Toast.makeText(this,"Permission denied..",Toast.LENGTH_LONG).show();}
            }
        }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
