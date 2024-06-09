package com.example.task_manager_app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CadastrarTarefaActivity extends AppCompatActivity {

    EditText editTextDate;
    EditText editTextDate2;
    Spinner prioritySpinner;
    Spinner statusSpinner;
    SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_tarefa);
        
        editTextDate = findViewById(R.id.editTextDate);
        editTextDate2 = findViewById(R.id.editTextDate2);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        editTextDate.setOnClickListener(v -> showDatePickerDialog(editTextDate));
        editTextDate2.setOnClickListener(v -> showDatePickerDialog(editTextDate2));

        prioritySpinner = findViewById(R.id.spinner);
        statusSpinner = findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.priority_itens,
                android.R.layout.simple_spinner_item
        );
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_itens,
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears.
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        prioritySpinner.setAdapter(priorityAdapter);
        statusSpinner.setAdapter(statusAdapter);
    }

    private void showDatePickerDialog(EditText editTextDate) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar dataSelecionada = Calendar.getInstance();
                dataSelecionada.set(year, month, dayOfMonth);
                editTextDate.setText(dateFormat.format(dataSelecionada.getTime()));
            }
        }, year, month, dayOfMonth);
        datePickerDialog.show();
    }
}