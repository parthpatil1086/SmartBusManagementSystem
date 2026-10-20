package com.example.smartbussystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import androidx.activity.result.ActivityResultLauncher;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;

public class ValidateTicketActivity
        extends AppCompatActivity {

    Button btnScan;
    Button btnLogout;

    TextView tvResult;

    FirebaseFirestore db;
    // GPS Tracking
    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    // ---- Backend PHP endpoint that returns the ESP32's current LAN IP ----
    // Expected response: {"status":"success","bus_id":"BUS001","ip":"192.168.1.50"}
    private static final String GET_ESP_IP_API =
            "https://darkslategray-lion-860323.hostingersite.com/smartBus/get_esp_ip.php";

    // ---- In-memory cache of the ESP32's IP, keyed by bus_id ----
    // Cleared on app restart. Only re-fetched from PHP when a call
    // using the cached IP fails.
    private static String cachedEspIp = null;
    private static String cachedEspBusId = null;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        db =
                FirebaseFirestore
                        .getInstance();

        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                getSharedPreferences(
                        "SmartBus",
                        MODE_PRIVATE
                );

        String role =
                prefs.getString(
                        "role",
                        ""
                );

        if (!role.equals("validator")) {
            Intent intent =
                    new Intent(
                            ValidateTicketActivity.this,
                            LoginActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();

            return;
        }

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(
                R.layout.activity_validate_ticket
        );

        btnScan =
                findViewById(
                        R.id.btnScan
                );

        btnLogout =
                findViewById(
                        R.id.btnLogout
                );

        tvResult =
                findViewById(
                        R.id.tvResult
                );

        db =
                FirebaseFirestore
                        .getInstance();

        // Initialize GPS tracking
        fusedLocationClient =
                LocationServices
                        .getFusedLocationProviderClient(this);

        checkLocationPermission();

// Start continuous location updates
        startLocationUpdates();

        btnScan.setOnClickListener(v -> {

            ScanOptions options =
                    new ScanOptions();

            options.setPrompt(
                    "Scan SmartBus Ticket"
            );

            options.setBeepEnabled(
                    true
            );

            options.setOrientationLocked(
                    true
            );

            barcodeLauncher.launch(
                    options
            );

        });

        btnLogout.setOnClickListener(v -> {
            prefs.edit()
                    .remove("role")
                    .apply();

            Intent intent =
                    new Intent(
                            ValidateTicketActivity.this,
                            LoginActivity.class
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            if (fusedLocationClient != null
                    &&
                    locationCallback != null) {
                fusedLocationClient
                        .removeLocationUpdates(
                                locationCallback
                        );
            }

            finish();

        });

    }

    ActivityResultLauncher<ScanOptions>
            barcodeLauncher =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {

                        if (result.getContents()
                                != null) {

                            validateTicket(
                                    result.getContents()
                            );

                        } else {

                            tvResult.setText(
                                    "QR Scan Cancelled"
                            );

                        }

                    });

    private void validateTicket(String qrData) {

        try {

            Uri uri = Uri.parse(qrData);

            String ticketId =
                    uri.getQueryParameter("id");

            if (ticketId == null) {

                tvResult.setText(
                        "❌ Invalid QR Code"
                );

                return;
            }

            DocumentReference ref =
                    db.collection("tickets")
                            .document(ticketId);

            ref.get()
                    .addOnSuccessListener(doc -> {

                        if (!doc.exists()) {

                            tvResult.setText(
                                    "❌ TICKET NOT FOUND"
                            );

                            return;
                        }

                        String passenger =
                                doc.getString(
                                        "passengerName"
                                );

                        String route =
                                doc.getString(
                                        "route"
                                );

                        String from =
                                doc.getString(
                                        "from"
                                );

                        String to =
                                doc.getString(
                                        "to"
                                );

                        String date =
                                doc.getString(
                                        "date"
                                );

                        String time =
                                doc.getString(
                                        "time"
                                );

                        String busNumber =
                                doc.getString(
                                        "busNumber"
                                );

                        Long passengerCount =
                                doc.getLong(
                                        "passengers"
                                );

                        String status =
                                doc.getString(
                                        "status"
                                );

                        String today =
                                new SimpleDateFormat(
                                        "yyyy-MM-dd",
                                        Locale.getDefault()
                                ).format(
                                        new Date()
                                );

                        if (!today.equals(date)) {

                            tvResult.setText(

                                    "⚠ TICKET EXPIRED\n\n"

                                            + "Passenger : "
                                            + passenger

                                            + "\nPassengers : "
                                            + passengerCount

                                            + "\nBus Number : "
                                            + busNumber

                                            + "\nRoute : "
                                            + route

                                            + "\nFrom : "
                                            + from

                                            + "\nTo : "
                                            + to

                                            + "\nDate : "
                                            + date

                                            + "\nTime : "
                                            + time
                            );

                            return;
                        }

                        // Already Used
                        if (status != null
                                && status.equalsIgnoreCase("used")) {

                            tvResult.setText(

                                    "❌ TICKET ALREADY USED\n\n"

                                            + "Passenger : "
                                            + passenger

                                            + "\nPassengers : "
                                            + passengerCount

                                            + "\nBus Number : "
                                            + busNumber

                                            + "\nRoute : "
                                            + route

                                            + "\nFrom : "
                                            + from

                                            + "\nTo : "
                                            + to

                                            + "\nDate : "
                                            + date

                                            + "\nTime : "
                                            + time

                                            + "\nStatus : USED"
                            );

                            return;
                        }

                        // Mark ticket as used
                        ref.update(
                                "status",
                                "used"
                        );

                        // Trigger the bus's ESP32 exit door for this many passengers
                        if (passengerCount != null
                                && busNumber != null) {

                            openBusDoorForPassengers(
                                    busNumber,
                                    passengerCount.intValue()
                            );
                        }

                        tvResult.setText(

                                "✅ VALID TICKET\n\n"

                                        + "Passenger : "
                                        + passenger

                                        + "\nPassengers : "
                                        + passengerCount

                                        + "\nBus Number : "
                                        + busNumber

                                        + "\nRoute : "
                                        + route

                                        + "\nFrom : "
                                        + from

                                        + "\nTo : "
                                        + to

                                        + "\nDate : "
                                        + date

                                        + "\nTime : "
                                        + time

                                        + "\n\nStatus : VERIFIED"
                                        + "\n🚪 Opening exit door..."

                        );

                    })
                    .addOnFailureListener(e -> {

                        tvResult.setText(
                                "❌ Error : "
                                        + e.getMessage()
                        );

                    });

        } catch (Exception e) {

            tvResult.setText(
                    "❌ INVALID QR FORMAT"
            );
        }
    }

    // ─────────────────────────────────────────
    // Uses a cached ESP32 IP whenever possible to avoid the extra
    // PHP lookup on every scan:
    //
    //   1) If we have a cached IP for this busId -> try it directly.
    //      - success -> done.
    //      - fails   -> clear cache, fall through to step 2.
    //   2) No cached IP (first call, or previous one failed) ->
    //      fetch IP from PHP, call ESP32, cache IP on success.
    //
    // Only called on a VALID (non-expired, non-used) ticket.
    // Runs entirely on a background thread; all UI updates go
    // through runOnUiThread().
    // ─────────────────────────────────────────
    private void openBusDoorForPassengers(String busId, int count) {

        new Thread(() -> {

            try {

                boolean hasCachedIp =
                        cachedEspIp != null
                                && busId.equals(cachedEspBusId);

                if (hasCachedIp) {

                    try {

                        callOpenDoor(cachedEspIp, count);
                        return; // success using cached IP

                    } catch (Exception cachedFailEx) {

                        android.util.Log.w(
                                "SmartBus",
                                "Cached ESP32 IP failed ("
                                        + cachedEspIp
                                        + "), re-fetching IP: "
                                        + cachedFailEx.getMessage()
                        );

                        // Drop the stale cache and fall through
                        // to a fresh lookup below.
                        cachedEspIp = null;
                        cachedEspBusId = null;
                    }
                }

                // ---- No usable cached IP: fetch fresh from PHP ----
                String freshIp = fetchEspIpFromServer(busId);

                callOpenDoor(freshIp, count);

                // Cache only after a successful door call
                cachedEspIp = freshIp;
                cachedEspBusId = busId;

            } catch (Exception e) {

                android.util.Log.e(
                        "SmartBus",
                        "openBusDoorForPassengers failed: "
                                + e.getMessage()
                );

                showDoorResult(
                        false,
                        "Door request failed: " + e.getMessage()
                );
            }

        }).start();
    }

    // ─────────────────────────────────────────
    // Looks up the ESP32's current LAN IP for a bus_id via PHP.
    // Throws if the lookup fails or returns no IP.
    // ─────────────────────────────────────────
    private String fetchEspIpFromServer(String busId) throws Exception {

        String lookupUrl =
                GET_ESP_IP_API
                        + "?bus_id=" + Uri.encode(busId);

        String ipResponse = httpGet(lookupUrl);

        JSONObject ipJson = new JSONObject(ipResponse);

        String status =
                ipJson.optString("status", "");

        if (!"success".equalsIgnoreCase(status)) {
            throw new Exception("IP lookup failed for " + busId);
        }

        String espIp = ipJson.optString("ip", "");

        if (espIp.isEmpty()) {
            throw new Exception("No IP found for bus " + busId);
        }

        return espIp;
    }

    // ─────────────────────────────────────────
    // Calls /openDoor?count=N directly on the ESP32 at the given IP.
    // Throws on network failure or a non-"success" ESP32 response,
    // so the caller can decide whether to retry with a fresh IP.
    // On success, reports the result to the UI.
    // ─────────────────────────────────────────
    private void callOpenDoor(String espIp, int count) throws Exception {

        String openDoorUrl =
                "http://" + espIp
                        + "/openDoor?count=" + count;

        String doorResponse = httpGet(openDoorUrl);

        android.util.Log.d(
                "SmartBus",
                "ESP32 /openDoor response: " + doorResponse
        );

        JSONObject doorJson = new JSONObject(doorResponse);

        String doorStatus =
                doorJson.optString("status", "");

        if (!"success".equalsIgnoreCase(doorStatus)) {

            String msg =
                    doorJson.optString(
                            "message",
                            "Unknown error"
                    );

            throw new Exception("ESP32 error: " + msg);
        }

        int remaining =
                doorJson.optInt("remaining", count);

        showDoorResult(
                true,
                "Door opened for " + count
                        + " passenger(s). Remaining: "
                        + remaining
        );
    }

    // ─────────────────────────────────────────
    // Appends a door status line to whatever is currently shown,
    // without wiping out the ticket details already displayed.
    // ─────────────────────────────────────────
    private void showDoorResult(boolean success, String message) {

        runOnUiThread(() -> {

            String prefix = success ? "🚪✅ " : "🚪⚠ ";

            tvResult.append("\n" + prefix + message);

        });
    }

    // ─────────────────────────────────────────
    // Simple blocking GET helper used by background threads.
    // Throws on any network/HTTP error; caller is expected to
    // catch and handle.
    // ─────────────────────────────────────────
    private String httpGet(String urlString) throws Exception {

        URL url = new URL(urlString);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode =
                connection.getResponseCode();

        InputStream stream =
                (responseCode == 200)
                        ? connection.getInputStream()
                        : connection.getErrorStream();

        StringBuilder result = new StringBuilder();

        if (stream != null) {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(stream)
                    );

            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
            stream.close();
        }

        connection.disconnect();

        if (responseCode != 200) {
            throw new Exception(
                    "HTTP " + responseCode + ": " + result
            );
        }

        return result.toString();

    }

    // Determine which bus belongs to logged-in validator
    private String getAssignedBus() {
        SharedPreferences prefs =
                getSharedPreferences(
                        "SmartBus",
                        MODE_PRIVATE
                );

        String username =
                prefs.getString(
                        "username",
                        ""
                );

        if (username.equalsIgnoreCase("validator1")) {
            return "BUS001";
        } else if (username.equalsIgnoreCase("validator2")) {
            return "BUS002";
        } else if (username.equalsIgnoreCase("validator3")) {
            return "BUS003";
        }

        return "";
    }

    // Start GPS tracking every 5 seconds
    private void startLocationUpdates() {
        LocationRequest request =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        5000
                )
                        .setMinUpdateIntervalMillis(
                                3000
                        )
                        .build();

        locationCallback =
                new LocationCallback() {
                    @Override
                    public void onLocationResult(
                            LocationResult result
                    ) {
                        super.onLocationResult(result);

                        if (result == null) {
                            return;
                        }

                        Location location =
                                result.getLastLocation();

                        if (location == null) {
                            return;
                        }

                        updateBusLocation(
                                location.getLatitude(),
                                location.getLongitude()
                        );
                    }
                };

        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient
                    .requestLocationUpdates(
                            request,
                            locationCallback,
                            getMainLooper()
                    );
        }
    }

    // Upload validator GPS to Firestore
    private void updateBusLocation(
            double latitude,
            double longitude
    ) {
        String busId =
                getAssignedBus();

        if (busId.isEmpty()) {
            return;
        }

        java.util.HashMap<String, Object> map =
                new java.util.HashMap<>();

        map.put(
                "latitude",
                latitude
        );

        map.put(
                "longitude",
                longitude
        );

        map.put(
                "timestamp",
                System.currentTimeMillis()
        );

        db.collection("bus_locations")
                .document(busId)
                .set(map)
                .addOnSuccessListener(
                        unused ->
                                android.util.Log.d(
                                        "GPS",
                                        "Updated " + busId
                                )
                )
                .addOnFailureListener(
                        e ->
                                android.util.Log.e(
                                        "GPS",
                                        e.getMessage()
                                )
                );
    }
    private void checkLocationPermission()
    {
        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
                ==
                PackageManager.PERMISSION_GRANTED)
        {
            startLocationUpdates();
        }
        else
        {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    )
    {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if(requestCode
                ==
                LOCATION_PERMISSION_REQUEST)
        {
            if(grantResults.length > 0
                    &&
                    grantResults[0]
                            ==
                            PackageManager.PERMISSION_GRANTED)
            {
                startLocationUpdates();

                tvResult.append(
                        "\n📍 GPS Permission Granted"
                );
            }
            else
            {
                tvResult.append(
                        "\n❌ GPS Permission Denied"
                );
            }
        }
    }
}
