package com.example.task_manager_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingFragment extends Fragment {
    private ImageView userImage;
    private TextView userName;
    private Button btnChangeProfile;
    private FirebaseAuth auth;
    private FirebaseUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        FirebaseAuth auth;
        FirebaseUser user;
        Button button = view.findViewById(R.id.btn_logout);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userImage = view.findViewById(R.id.user_image);
        userName = view.findViewById(R.id.user_name);
        btnChangeProfile = view.findViewById(R.id.btn_change_profile);

        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                userName.setText(user.getDisplayName());
            } else {
                userName.setText(user.getEmail());
            }
        } else {
            userName.setText("Usuário não autenticado");
        }

        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangeProfileActivity.class);
                startActivity(intent);

                getActivity().finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);

                getActivity().finish();
            }
        });

        return view;
    }
}