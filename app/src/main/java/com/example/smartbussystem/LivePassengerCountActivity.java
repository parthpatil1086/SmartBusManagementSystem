package com.example.smartbussystem;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class LivePassengerCountActivity extends AppCompatActivity {

    private static final String API_URL =
            "https://darkslategray-lion-860323.hostingersite.com/smartBus/get_passenger_count.php";

    // Poll interval in milliseconds
    private static final long REFRESH_INTERVAL_MS = 10000;

    private RecyclerView recyclerView;
    private PassengerCountAdapter adapter;
    private ArrayList<PassengerCountModel> list;
    private FirebaseFirestore db;

    // busNumber -> routeName, loaded once from Firestore and reused on every refresh
    private final HashMap<String, String> routeNameMap = new HashMap<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_passenger_count);

        recyclerView = findViewById(R.id.recyclerViewPassenger);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new PassengerCountAdapter(list);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Load route names once, then start polling the PHP API
        loadRouteNamesThenStartPolling();
    }

    /**
     * Loads busNumber -> routeName mapping from Firestore once.
     * Once that's done, kicks off the repeating HTTP fetch.
     */
    private void loadRouteNamesThenStartPolling() {
        db.collection("routes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    routeNameMap.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String busNumber = document.getString("busNumber");
                        String routeName = document.getString("routeName");

                        if (busNumber != null) {
                            routeNameMap.put(busNumber, routeName);
                        }
                    }

                    startPolling();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            this,
                            "Failed to load routes",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Still start polling even if route names failed to load,
                    // routeName will just show as null/unknown
                    startPolling();
                });
    }

    /**
     * Starts a repeating task that fetches passenger counts every
     * REFRESH_INTERVAL_MS milliseconds.
     */
    private void startPolling() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                fetchPassengerCounts();
                handler.postDelayed(this, REFRESH_INTERVAL_MS);
            }
        };

        handler.post(refreshRunnable);
    }

    /**
     * Calls the PHP API on a background thread, parses the JSON response,
     * then updates the RecyclerView on the main thread.
     */
    private void fetchPassengerCounts() {
        new Thread(() -> {
            try {
                String response = makeHttpGetRequest(API_URL);
                parseAndUpdateUi(response);
            } catch (Exception e) {
                runOnUiThread(() -> {
                    // Only show the error toast on the very first failed load
                    // to avoid spamming toasts every 5 seconds if network drops
                    if (isFirstLoad) {
                        Toast.makeText(
                                this,
                                "Failed to load passenger counts",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        }).start();
    }

    private String makeHttpGetRequest(String urlString) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();

            InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            StringBuilder result = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            if (responseCode < 200 || responseCode >= 300) {
                throw new Exception("HTTP error code: " + responseCode);
            }

            return result.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Parses the JSON response from the PHP API and updates the RecyclerView.
     * Expected format:
     * {
     *   "status": "success",
     *   "total_buses": 2,
     *   "data": [
     *     { "bus_id": "BUS001", "count": 12 },
     *     { "bus_id": "BUS002", "count": 5 }
     *   ]
     * }
     */
    private void parseAndUpdateUi(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);

        String status = jsonObject.optString("status", "");
        if (!"success".equals(status)) {
            throw new Exception("API returned non-success status");
        }

        JSONArray dataArray = jsonObject.getJSONArray("data");

        ArrayList<PassengerCountModel> newList = new ArrayList<>();

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject busObject = dataArray.getJSONObject(i);

            String busId = busObject.getString("bus_id");
            int count = busObject.getInt("count");

            String routeName = routeNameMap.get(busId);

            newList.add(new PassengerCountModel(busId, routeName, count));
        }

        runOnUiThread(() -> {
            list.clear();
            list.addAll(newList);
            adapter.notifyDataSetChanged();
            isFirstLoad = false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop polling when the activity is destroyed to avoid leaks/crashes
        if (refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }
}
