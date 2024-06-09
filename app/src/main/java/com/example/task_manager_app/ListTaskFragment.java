package com.example.task_manager_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.HashMap;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ListTaskFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_task, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tasksRef = db.collection("tasks");

        tasksRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Falha ao buscar tarefas.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> tasks = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String taskName = document.getString("name");
                if (taskName != null) {
                    tasks.add(taskName);
                }
            }

            ListView taskList = view.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item_layout, R.id.text_view_item, tasks) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View listItemView = super.getView(position, convertView, parent);

                    ImageButton buttonMenu = listItemView.findViewById(R.id.button_menu);
                    buttonMenu.setOnClickListener(view -> {
                        PopupMenu popupMenu = new PopupMenu(getContext(), buttonMenu);
                        popupMenu.getMenu().add("Editar");
                        popupMenu.getMenu().add("Excluir");
                        String currentTaskName = tasks.get(position);
                        String currentDocumentId = queryDocumentSnapshots.getDocuments().get(position).getId();

                        popupMenu.setOnMenuItemClickListener(item -> {
                            switch (item.getTitle().toString()) {
                                case "Editar":
                                    Toast.makeText(getContext(), "Editar " + currentDocumentId, Toast.LENGTH_SHORT).show();
                                    return true;
                                case "Excluir":
                                    // Exclui o documento correspondente do Firestore
                                    deleteTask(currentDocumentId);
                                    return true;
                                default:
                                    return false;
                            }
                        });

                        popupMenu.show();
                    });

                    return listItemView;
                }
            };
            taskList.setAdapter(adapter);
        });

        FloatingActionButton button = view.findViewById(R.id.add_new_task);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> task = new HashMap<>();
                task.put("name", "Mocked Task");
                task.put("priority", "medium");

                db.collection("tasks").add(task).addOnCompleteListener(documentReference -> {
                            Toast.makeText(getContext(), "Tarefa criada", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Falha ao criar tarefa", Toast.LENGTH_SHORT).show();
                        });
            }
        });
        return view;
    }

    private void deleteTask(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection("tasks").document(documentId);

        documentRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Tarefa excluída com sucesso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Falha ao excluir tarefa", Toast.LENGTH_SHORT).show();
                });
    }
}

