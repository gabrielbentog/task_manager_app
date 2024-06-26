package com.example.task_manager_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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
                Intent intent = new Intent(mContext, CreateTaskActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    public void onResume() {
        super.onResume();
        loadTasks();
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
        ProgressBar progressBar = getView().findViewById(R.id.progress_bar);
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
                    String currentDocumentId = queryDocumentSnapshots.getDocuments().get(position).getId();
                    buttonMenu.setTag(currentDocumentId);
                    ImageView imageViewPriority = listItemView.findViewById(R.id.image_view_priority);
                    String currentPriority = queryDocumentSnapshots.getDocuments().get(position).getString("priority");
                    if (currentPriority != null) {
                        switch (currentPriority.toLowerCase()) {
                            case "alta":
                                imageViewPriority.setImageResource(R.drawable.high_priority);
                                break;
                            case "média":
                                imageViewPriority.setImageResource(R.drawable.medium_priority);
                                break;
                            case "baixa":
                                imageViewPriority.setImageResource(R.drawable.low_priority);
                                break;
                            default:
                                imageViewPriority.setVisibility(View.GONE);
                                break;
                        }
                    } else {
                        imageViewPriority.setVisibility(View.GONE);
                    }
                    String currentStatus = queryDocumentSnapshots.getDocuments().get(position).getString("status");
                    TextView textViewStatus = listItemView.findViewById(R.id.text_view_status);
                    if (currentStatus != null) {
                        textViewStatus.setText(currentStatus);
                        switch (currentStatus.toLowerCase()) {
                            case "a fazer":
                                textViewStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.status_background_grey));
                                break;
                            case "em progresso":
                                textViewStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.status_background_blue));
                                break;
                            case "concluída":
                                textViewStatus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.status_background_green));
                                break;
                            default:
                                textViewStatus.setVisibility(View.GONE);
                                break;
                        }
                    } else {
                        textViewStatus.setVisibility(View.GONE);
                    }

                    buttonMenu.setOnClickListener(view -> {
                        String taskId = (String) view.getTag();
                        PopupMenu popupMenu = new PopupMenu(mContext, buttonMenu);
                        popupMenu.getMenu().add("Editar");
                        popupMenu.getMenu().add("Excluir");
                        String currentTaskName = tasks.get(position);
                        String currentTaskPriority = queryDocumentSnapshots.getDocuments().get(position).getString("priority");
                        String currentTaskStatus = queryDocumentSnapshots.getDocuments().get(position).getString("status");

                        popupMenu.setOnMenuItemClickListener(item -> {
                            switch (item.getTitle().toString()) {
                                case "Editar":
                                    EditTaskDialogFragment editTaskDialogFragment = EditTaskDialogFragment.newInstance(taskId, currentTaskName, currentTaskPriority, currentTaskStatus);
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
                    notifyDataSetChanged();
                    return listItemView;
                }
            };
            taskList.setAdapter(adapter);

            progressBar.setVisibility(View.GONE);
        });
    }

}
