package com.example.task_manager_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_manager_app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeProfileActivity extends AppCompatActivity {

    private TextInputEditText editTextName;
    private TextInputEditText editTextEmail;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        Button btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        editTextName.setText(currentUser.getDisplayName());
        editTextEmail.setText(currentUser.getEmail());

        btnUpdateProfile.setOnClickListener(view -> {
            String newName = editTextName.getText().toString();
            String newEmail = editTextEmail.getText().toString();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangeProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            // Update email if changed
                            if (!newEmail.equals(currentUser.getEmail())) {
                                currentUser.updateEmail(newEmail)
                                        .addOnCompleteListener(emailTask -> {
                                            if (emailTask.isSuccessful()) {
                                                Toast.makeText(ChangeProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ChangeProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(ChangeProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });

            Intent intent = new Intent(ChangeProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
