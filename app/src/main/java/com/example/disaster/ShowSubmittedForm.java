package com.example.disaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ShowSubmittedForm extends AppCompatActivity {

    HashMap<String, String> hashMap;
    LinearLayout linearLayout;

    String databaseKey = "";
    DatabaseReference databaseFields;


    ArrayList<Integer> editFieldsId;
    ArrayList<Integer> textViewId;

    Button edit, close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_submitted_form);

        Intent i = getIntent();
        if (i != null) {
            hashMap = (HashMap<String, String>) i.getSerializableExtra("hash");
            databaseKey = i.getStringExtra("key");
        }

        databaseFields = FirebaseDatabase.getInstance().getReference("Disaster Management Form");


        linearLayout = findViewById(R.id.result_linear_layout);
        editFieldsId = new ArrayList<>();
        textViewId = new ArrayList<>();

        edit = findViewById(R.id.edit_fields_btn);
        close = findViewById(R.id.btn_close);

        for (String key : hashMap.keySet()) {
            createNewTextView(key);
            createNewEditText(hashMap.get(key));
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditForm();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void EditForm() {
        HashMap<String, String> map = new HashMap<>();

        for (int i = 0; i < editFieldsId.size(); i++) {

            EditText ed = findViewById(editFieldsId.get(i));

            TextView tv = findViewById(textViewId.get(i));

            map.put(tv.getText().toString(), ed.getText().toString());

            ed.setText("");
        }

        databaseFields.child(databaseKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showCompleteDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowSubmittedForm.this, "unable to store to database. please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // add textview
    private void createNewTextView(String text) {

        int id = generatePin();

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setId(id);
        textViewId.add(id);
        linearLayout.addView(tv);
    }

    // add edittext
    private void createNewEditText(String value) {

        int id = generatePin();

        EditText et = new EditText(this);

        et.setId(id);
        editFieldsId.add(id);

        et.setBackgroundResource(R.color.editTextBackground);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setText(value);
        et.setPadding(4, 4, 4, 4);
        linearLayout.addView(et);
    }

    public int generatePin() {
        Random generator = new Random();
        return 100000 + generator.nextInt(900000);
    }

    private void showCompleteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowSubmittedForm.this);
        builder.setCancelable(false);
        builder.setTitle("Data Uploaded");
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });

        builder.show();
    }

}
