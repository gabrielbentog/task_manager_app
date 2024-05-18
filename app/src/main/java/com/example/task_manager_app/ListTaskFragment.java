package com.example.task_manager_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListTaskFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_task, container, false);


        // Inflate the layout for this fragment
        List<String> tasks = new ArrayList<>(Arrays.asList("Tarefa 1", "Tarefa 2", "Tarefa 3", "Tarefa 4", "Tarefa 5"));
        ListView taskList = view.findViewById(R.id.list_view);
        taskList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, tasks));

        return view;
    }

}