package com.example.smartbussystem;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class BusMapActivity extends AppCompatActivity {

    private MapView mapView;

    private Button btnBus1;
    private Button btnBus2;
    private Button btnBus3;

    private FirebaseFirestore db;

    private Marker busMarker;

    private String selectedBus = "BUS001";

    private static final int LOCATION_REQUEST_CODE = 100;

    private FusedLocationProviderClient fusedLocationClient;

    private Marker passengerMarker;

    private double userLatitude = 0;

    private double userLongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationClient =
                LocationServices
                        .getFusedLocationProviderClient(this);

        getCurrentLocation();

        Configuration.getInstance()
                .setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_bus_map);

        mapView =
                findViewById(R.id.mapView);

        btnBus1 =
                findViewById(R.id.btnBus1);

        btnBus2 =
                findViewById(R.id.btnBus2);

        btnBus3 =
                findViewById(R.id.btnBus3);

        mapView.setMultiTouchControls(true);

        mapView.getController()
                .setZoom(15.0);

        db =
                FirebaseFirestore.getInstance();

        btnBus1.setOnClickListener(v -> {

            selectedBus = "BUS001";

            loadSelectedBus();

        });

        btnBus2.setOnClickListener(v -> {

            selectedBus = "BUS002";

            loadSelectedBus();

        });

        btnBus3.setOnClickListener(v -> {

            selectedBus = "BUS003";

            loadSelectedBus();

        });

        loadSelectedBus();
    }

    private void getCurrentLocation()
    {
        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_REQUEST_CODE
            );

            return;
        }

        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(location -> {

                    if(location == null)
                    {
                        return;
                    }

                    userLatitude =
                            location.getLatitude();

                    userLongitude =
                            location.getLongitude();

                    GeoPoint userPoint =
                            new GeoPoint(
                                    userLatitude,
                                    userLongitude
                            );

                    if(passengerMarker == null)
                    {
                        passengerMarker =
                                new Marker(mapView);

                        passengerMarker.setTitle(
                                "📍 You"
                        );

                        mapView.getOverlays()
                                .add(passengerMarker);
                    }

                    passengerMarker.setPosition(
                            userPoint
                    );

                    mapView.invalidate();
                });
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults)
    {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if(requestCode
                == LOCATION_REQUEST_CODE)
        {
            if(grantResults.length > 0
                    &&
                    grantResults[0]
                            == PackageManager.PERMISSION_GRANTED)
            {
                getCurrentLocation();
            }
        }
    }
    private void loadSelectedBus() {

        db.collection("bus_locations")
                .document(selectedBus)
                .addSnapshotListener(
                        (document, error) -> {

                            if (error != null) {
                                return;
                            }

                            if (document == null) {
                                return;
                            }

                            if (!document.exists()) {
                                return;
                            }

                            updateBusLocation(document);

                        });
    }

    private void updateBusLocation(
            DocumentSnapshot document
    ) {

        Double latitude =
                document.getDouble(
                        "latitude"
                );

        Double longitude =
                document.getDouble(
                        "longitude"
                );

        Long timestamp =
                document.getLong(
                        "timestamp"
                );

        if (latitude == null
                || longitude == null) {
            return;
        }

        GeoPoint point =
                new GeoPoint(
                        latitude,
                        longitude
                );
        float[] results =
                new float[1];

        Location.distanceBetween(
                userLatitude,
                userLongitude,
                latitude,
                longitude,
                results
        );

        float distanceMeters =
                results[0];


        if (busMarker == null) {

            busMarker =
                    new Marker(
                            mapView
                    );

            busMarker.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
            );

            mapView.getOverlays()
                    .add(busMarker);
        }

        long diff =
                System.currentTimeMillis()
                        - (timestamp == null ? 0 : timestamp);

        String status;

        if (diff < 60000) {
            status = "🟢 Online";
        } else {
            status = "🔴 Offline";
        }

        busMarker.setPosition(point);

        busMarker.setTitle(selectedBus);

        busMarker.setSubDescription(
                "📏 Distance : "
                        + ((int)distanceMeters)
                        + " meters"

                        + "\n\nLatitude : "
                        + latitude

                        + "\nLongitude : "
                        + longitude

                        + "\n"
                        + status
        );

        mapView.getController()
                .setCenter(point);

        mapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mapView != null) {
            mapView.onPause();
        }
    }
}