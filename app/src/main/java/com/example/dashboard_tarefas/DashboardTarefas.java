package com.example.dashboard_tarefas;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.task_manager_app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardTarefas extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_tarefas);
        List<String> tarefas = new ArrayList<>(Arrays.asList("Tarefa 1", "Tarefa 2", "Tarefa 3", "Tarefa 4", "Tarefa 5"));
        ListView listaDeTarefas = findViewById(R.id.dashboard_tarefas_list_view);
        listaDeTarefas.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tarefas));
    }
}
