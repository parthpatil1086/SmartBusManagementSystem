package com.example.smartbussystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView txtName, txtEmail;

    LinearLayout editProfileSection, aboutSection;

    EditText editName, editPhone, editEmail;
    Button btnSaveProfile, btnLogout;

    FirebaseAuth auth;
    FirebaseFirestore db;

    DocumentReference userRef;
    ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // INIT VIEWS
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);

        editProfileSection = findViewById(R.id.editProfileSection);
        aboutSection = findViewById(R.id.aboutSection);

        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // LOGIN CHECK
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        userRef = db.collection("passengers").document(uid);

        // ==============================
        // REAL TIME FIRESTORE LISTENER
        // ==============================
        listener = userRef.addSnapshotListener((snapshot, error) -> {

            if (snapshot != null && snapshot.exists()) {

                String name = snapshot.getString("name");
                String email = snapshot.getString("email");
                String phone = snapshot.getString("phone");

                txtName.setText(name != null ? name : "Passenger");
                txtEmail.setText(email != null ? email : "No Email");

                // Auto fill edit fields
                editName.setText(name);
                editPhone.setText(phone);
                editEmail.setText(email);
            }
        });

        // ==============================
        // TOGGLE EDIT PROFILE
        // ==============================
        findViewById(R.id.cardEditProfile).setOnClickListener(v -> {

            if (editProfileSection.getVisibility() == View.GONE) {
                editProfileSection.setVisibility(View.VISIBLE);
                aboutSection.setVisibility(View.GONE);
            } else {
                editProfileSection.setVisibility(View.GONE);
            }
        });

        // ==============================
        // TOGGLE ABOUT
        // ==============================
        findViewById(R.id.cardAbout).setOnClickListener(v -> {

            if (aboutSection.getVisibility() == View.GONE) {
                aboutSection.setVisibility(View.VISIBLE);
                editProfileSection.setVisibility(View.GONE);
            } else {
                aboutSection.setVisibility(View.GONE);
            }
        });

        // ==============================
        // SAVE PROFILE (REAL TIME UPDATE)
        // ==============================
        btnSaveProfile.setOnClickListener(v -> {

            String name = editName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            HashMap<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("phone", phone);
            map.put("email", email);

            userRef.update(map)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                    );
        });

        // ==============================
        // LOGOUT
        // ==============================
        btnLogout.setOnClickListener(v -> {

            auth.signOut();

            getSharedPreferences("SmartBus", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}