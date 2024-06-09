package com.example.task_manager_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditTaskDialogFragment extends DialogFragment {

    private EditText editTextTaskName;
    private Spinner spinnerTaskPriority;
    private Button buttonSaveTask;

    private String documentId;

    public static EditTaskDialogFragment newInstance(String documentId, String taskName, String taskPriority) {
        EditTaskDialogFragment fragment = new EditTaskDialogFragment();
        Bundle args = new Bundle();
        args.putString("documentId", documentId);
        args.putString("taskName", taskName);
        args.putString("taskPriority", taskPriority);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_task_dialog, container, false);

        editTextTaskName = view.findViewById(R.id.edit_text_task_name);
        spinnerTaskPriority = view.findViewById(R.id.spinner_task_priority);
        buttonSaveTask = view.findViewById(R.id.button_save_task);

        // Configura o adaptador do Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.task_priority_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskPriority.setAdapter(adapter);

        if (getArguments() != null) {
            documentId = getArguments().getString("documentId");
            editTextTaskName.setText(getArguments().getString("taskName"));
            String priority = getArguments().getString("taskPriority");
            int spinnerPosition = getPriorityPosition(priority);
            spinnerTaskPriority.setSelection(spinnerPosition);
        }

        buttonSaveTask.setOnClickListener(v -> saveTask());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Define o tamanho do diálogo
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        params.width = (int) (metrics.widthPixels * 0.9); // 90% da largura da tela
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private int getPriorityPosition(String priority) {
        switch (priority) {
            case "Low":
                return 0;
            case "Medium":
                return 1;
            case "High":
                return 2;
            default:
                return 1;
        }
    }

    private String getPriorityValue(int position) {
        switch (position) {
            case 0:
                return "Low";
            case 1:
                return "Medium";
            case 2:
                return "High";
            default:
                return "Medium";
        }
    }

    private void saveTask() {
        String taskName = editTextTaskName.getText().toString().trim();
        String taskPriority = getPriorityValue(spinnerTaskPriority.getSelectedItemPosition());

        if (TextUtils.isEmpty(taskName)) {
            Toast.makeText(getContext(), "Task name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference taskRef = db.collection("tasks").document(documentId);

        taskRef.update("name", taskName, "priority", taskPriority)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update task", Toast.LENGTH_SHORT).show());
    }
}
