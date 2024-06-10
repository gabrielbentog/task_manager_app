package com.example.task_manager_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        prioritySpinner.setAdapter(priorityAdapter);
        statusSpinner.setAdapter(statusAdapter);

        Button btnCancel = findViewById(R.id.btnCancelar);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastrarTarefaActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button btnCriar = findViewById(R.id.btnCriar);
        btnCriar.setOnClickListener(v -> criarTarefa());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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

    private void criarTarefa() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            Map<String, Object> task = new HashMap<>();
            task.put("name", "Nova Tarefa");
            task.put("priority", prioritySpinner.getSelectedItem().toString());
            task.put("status", statusSpinner.getSelectedItem().toString());
            task.put("user_id", userId);
            task.put("start_date", editTextDate.getText().toString());
            task.put("final_date", editTextDate2.getText().toString());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("tasks").add(task)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Tarefa criada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CadastrarTarefaActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Falha ao criar tarefa", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Nenhum usuário autenticado", Toast.LENGTH_SHORT).show();
        }
    }
}
