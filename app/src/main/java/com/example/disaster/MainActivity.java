package com.example.disaster;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    EditText empname, empID;
    Button submitbutton, addFieldsButton;
    LinearLayout fieldsLinearLayout;
    DatabaseReference databaseFields;

    ArrayList<Integer> editFieldsId;
    ArrayList<Integer> textViewId;

    String databaseKey = "";

    HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseFields = FirebaseDatabase.getInstance().getReference("Disaster Management Form");
        editFieldsId = new ArrayList<>();
        textViewId = new ArrayList<>();

        fieldsLinearLayout = findViewById(R.id.fields_linear_layout);

        empname = findViewById(R.id.employee_name_input);
        empID = findViewById(R.id.RegisterID);

        editFieldsId.add(R.id.employee_name_input);
        editFieldsId.add(R.id.RegisterID);

        textViewId.add(R.id.employee_name_tv);
        textViewId.add(R.id.register_id_tv);

        submitbutton = findViewById(R.id.btn_submit);
        addFieldsButton = findViewById(R.id.add_fields);

        addFieldsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateFieldDialog();
            }
        });

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = empname.getText().toString();
                String id = empID.getText().toString();
                if (name.isEmpty()) {
                    empname.setError("Enter name");
                    empname.requestFocus();
                    return;
                }
                if (id.isEmpty()) {
                    empID.setError("Enter Id");
                    empID.requestFocus();
                    return;
                }
                databaseKey = name + "_" + id;
                addFieldToDatabase();
            }
        });
    }

    private void showCreateFieldDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_new_field);

        final EditText fieldName = dialog.findViewById(R.id.dialog_field_name);

        Button Cancel = dialog.findViewById(R.id.dialog_cancel_btn);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button Add = dialog.findViewById(R.id.dialog_add_button);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fieldName.getText().toString();
                if (name.isEmpty()) {
                    fieldName.setError("Enter field name");
                    fieldName.requestFocus();
                    return;
                }
                createNewTextView(name);
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
    }

    // add textview
    private void createNewTextView(String text) {

        int id = generatePin();

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setId(id);
        tv.setTextColor(Color.parseColor("#000000"));
        tv.setTextSize(30);
        textViewId.add(id);
        fieldsLinearLayout.addView(tv);
        createNewEditText();
    }

    // add edittext
    private void createNewEditText() {

        int id = generatePin();

        EditText et = new EditText(this);

        et.setId(id);
        editFieldsId.add(id);

        et.setBackgroundResource(R.color.editTextBackground);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setTextSize(30);
        et.setPadding(4, 4, 4, 4);
        fieldsLinearLayout.addView(et);
    }

    public int generatePin() {
        Random generator = new Random();
        return 100000 + generator.nextInt(900000);
    }

    private void addFieldToDatabase() {

        hashMap = new HashMap<>();

        for (int i = 0; i < editFieldsId.size(); i++) {

            EditText ed = findViewById(editFieldsId.get(i));

            TextView tv = findViewById(textViewId.get(i));

            hashMap.put(tv.getText().toString(), ed.getText().toString());

            ed.setText("");
        }

        databaseKey = databaseKey + "_" + System.currentTimeMillis();

        databaseFields.child(databaseKey).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showCompleteDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "unable to store to database. please try again!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showCompleteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Data Uploaded");
        builder.setPositiveButton("Show Form", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MainActivity.this, ShowSubmittedForm.class);
                i.putExtra("hash", hashMap);
                i.putExtra("key", databaseKey);
                startActivity(i);
                finish();
                dialog.dismiss();
            }
        });

        builder.show();
    }
}
