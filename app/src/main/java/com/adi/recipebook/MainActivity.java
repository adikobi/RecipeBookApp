package com.adi.recipebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.adi.recipebook.auth.Login;
import com.adi.recipebook.auth.Registor;
import com.adi.recipebook.note.AddNote;
import com.adi.recipebook.note.EditNote;
import com.adi.recipebook.note.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import model.Adapter;
import model.Note;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView noteList;
    Adapter adapter;
    FirebaseFirestore fstore;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        Query query = fstore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Note> allNotes = new  FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();


        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {
                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("noteId", docId);
                        v.getContext().startActivity(i);
                    }
                });
                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            menu.setGravity(Gravity.END);
                        }
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                Intent i = new Intent(v.getContext(), EditNote.class);
                                i.putExtra("title", note.getTitle());
                                i.putExtra("content", note.getContent());
                                i.putExtra("noteId", docId);
                                startActivity(i);                                return false;
                            }
                        });


                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference docRef = fstore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // note deleted
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error in deleting Note", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        menu.show();
                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };
        noteList = findViewById(R.id.notelist);
        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);

        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteList.setAdapter(noteAdapter);
        View headerView = nav_view.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.userDisplayName);
        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

        if (user.isAnonymous()){
            userEmail.setVisibility(View.GONE);
            userName.setText("temporary user");
        }else {
            userEmail.setVisibility(View.VISIBLE);
            userEmail.setText(user.getEmail());
            userName.setText(user.getDisplayName());
        }


        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddNote.class));

            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()){

            case R.id.addNote:
                startActivity(new Intent(this, AddNote.class));
                break;


            case R.id.sync:
                if (user.isAnonymous()){
                    startActivity(new Intent(this, Login.class));
                }else{
                    Toast.makeText(this, "you are connected", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.logout:
                checkUser();
                break;
            default:
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        if (user.isAnonymous()){
            displayAlert();
        }
        else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Splash.class));
            finish();
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("you are loge in with temporary account, logging out will Delete all recipes")
                .setPositiveButton("Sync recipes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Registor.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // todo delete all notes;

                        // todo delete the annonymus user

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), Splash.class));
                                finish();
                            }
                        });
                    }
                });
    warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settinsgs){
            Toast.makeText(this, "setting was clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView noteTitle, noteContent;
        View view;

        public NoteViewHolder(@NonNull View itemView){
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            view = itemView;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }
}
