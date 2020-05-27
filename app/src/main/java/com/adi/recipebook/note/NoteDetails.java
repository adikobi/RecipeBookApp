package com.adi.recipebook.note;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.adi.recipebook.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteDetails extends AppCompatActivity {

    Intent data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data =getIntent();

        TextView content = findViewById(R.id.noteDetailsContent);
        TextView title = findViewById(R.id.noteDetailsTitle);

        content.setMovementMethod(new ScrollingMovementMethod());
        content.setText(data.getStringExtra("content"));
        title.setText(data.getStringExtra("title"));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EditNote.class);
                i.putExtra("title", data.getStringExtra("title"));
                i.putExtra("content", data.getStringExtra("content"));
                i.putExtra("noteId", data.getStringExtra("noteId"));
                startActivity(i);
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
