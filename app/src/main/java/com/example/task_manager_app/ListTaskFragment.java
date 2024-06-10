package com.example.task_manager_app;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListTaskFragment extends Fragment {

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_task, container, false);
        mContext = getContext();

        FloatingActionButton button = view.findViewById(R.id.add_new_task);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();

                if (auth.getCurrentUser() != null) {
                    String userId = auth.getCurrentUser().getUid();
                    Map<String, Object> task = new HashMap<>();
                    task.put("name", "Mocked Task");
                    task.put("priority", "medium");
                    task.put("user_id", userId);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("tasks").add(task)
                            .addOnCompleteListener(documentReference -> {
                                Toast.makeText(mContext, "Tarefa criada", Toast.LENGTH_SHORT).show();
                                loadTasks(); // Recarrega as tarefas
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(mContext, "Falha ao criar tarefa", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(mContext, "Nenhum usuário autenticado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadTasks();
        return view;
    }

    private void deleteTask(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection("tasks").document(documentId);

        documentRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(mContext, "Tarefa excluída com sucesso", Toast.LENGTH_SHORT).show();
                    loadTasks();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Falha ao excluir tarefa", Toast.LENGTH_SHORT).show();
                });
    }
    private void loadTasks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tasksRef = db.collection("tasks");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();

        tasksRef.whereEqualTo("user_id", currentUserId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Falha ao buscar tarefas.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            if (getView() == null) {
                return;
            }

            List<String> tasks = new ArrayList<>();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String taskName = document.getString("name");
                if (taskName != null) {
                    tasks.add(taskName);
                }
            }

            ListView taskList = getView().findViewById(R.id.list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.list_item_layout, R.id.text_view_item, tasks) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View listItemView = super.getView(position, convertView, parent);

                    ImageButton buttonMenu = listItemView.findViewById(R.id.button_menu);
                    String currentDocumentId = queryDocumentSnapshots.getDocuments().get(position).getId(); // obtendo o ID da tarefa
                    buttonMenu.setTag(currentDocumentId); // associando o ID da tarefa como uma tag

                    buttonMenu.setOnClickListener(view -> {
                        String taskId = (String) view.getTag(); // recuperando o ID da tarefa do botão clicado
                        PopupMenu popupMenu = new PopupMenu(mContext, buttonMenu);
                        popupMenu.getMenu().add("Editar");
                        popupMenu.getMenu().add("Excluir");
                        String currentTaskName = tasks.get(position);
                        String currentTaskPriority = queryDocumentSnapshots.getDocuments().get(position).getString("priority");

                        popupMenu.setOnMenuItemClickListener(item -> {
                            switch (item.getTitle().toString()) {
                                case "Editar":
                                    EditTaskDialogFragment editTaskDialogFragment = EditTaskDialogFragment.newInstance(taskId, currentTaskName, currentTaskPriority);
                                    editTaskDialogFragment.setTargetFragment(ListTaskFragment.this, 1);
                                    editTaskDialogFragment.show(getActivity().getSupportFragmentManager(), "EditTaskDialogFragment");
                                    return true;
                                case "Excluir":
                                    deleteTask(taskId);
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

            ProgressBar progressBar = getView().findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
        });
    }

}
