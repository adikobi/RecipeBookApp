package com.adi.recipebook.note;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.adi.recipebook.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {
    FirebaseFirestore fstore;
    EditText noteTitle, noteContent;
    ProgressBar progressBarSave;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fstore = FirebaseFirestore.getInstance();
        noteContent = findViewById(R.id.addNoteContent);
        noteTitle = findViewById(R.id.addNoteTitle);
        progressBarSave = findViewById(R.id.progressBar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nTitle = noteTitle.getText().toString();
                String nContent = noteContent.getText().toString();

                if (nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(AddNote.this, "can not save with empty field", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBarSave.setVisibility(View.VISIBLE);

                // save note:

                // note >> note note note
                DocumentReference docref = fstore.collection("notes").document(user.getUid()).collection("myNotes").document();
                Map<String, Object> note = new HashMap<>();
                note.put("title",nTitle);
                note.put("content",nContent);

                docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddNote.this, "recipe added", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNote.this, e.toString(), Toast.LENGTH_SHORT).show();
                        progressBarSave.setVisibility(View.VISIBLE);

                    }
                });
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.close){
            onBackPressed();
            Toast.makeText(this, "not saved", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
