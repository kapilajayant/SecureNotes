package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.Adapters.NotesAdapter;
import com.example.notes.Models.Note;
import com.example.notes.db.NoteDBHelper;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;
import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchConfirmedListener;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainActivity extends AppCompatActivity {

    FButton addBtn;
    TextView tv;
    NoteDBHelper noteDBHelper;
    RecyclerView rv;
    PersistentSearchView persistentSearchView;
    NotesAdapter adapter;
    ArrayList<String> allNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission(Manifest.permission.CAMERA,100);

        noteDBHelper = new NoteDBHelper(getApplicationContext(), null);

        addBtn = findViewById(R.id.addBtn);
//        tv = findViewById(R.id.tv);
        rv = findViewById(R.id.rv);
        rv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        persistentSearchView = findViewById(R.id.persistentSearchView);
//        persistentSearchView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        persistentSearchView.setOnSearchConfirmedListener(new OnSearchConfirmedListener() {
            @Override
            public void onSearchConfirmed(PersistentSearchView searchView, String query) {
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                allNotes = new ArrayList<>(noteDBHelper.getSearchData(query));
                adapter = new NotesAdapter(getApplicationContext(), allNotes);
                rv.setAdapter(adapter);
                rv.scrollToPosition(adapter.getItemCount()-1);
                persistentSearchView.collapse(true);
            }
        });

        persistentSearchView.setOnClearInputBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                persistentSearchView.collapse(true);
                showNotes();
            }
        });

        //called upon onCreate() method
        //---------------------------------------------
        showNotes();
        //---------------------------------------------

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNoteDialog();
            }
        });

    }

    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this,permission)== PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[] { permission },requestCode);
        }
        else {
            Toast
                    .makeText(MainActivity.this,
                            "Permission already granted",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void showNotes() {
            allNotes = new ArrayList<>(noteDBHelper.getAllNotes());
            adapter = new NotesAdapter(this, allNotes);
            rv.setAdapter(adapter);
            rv.scrollToPosition(adapter.getItemCount()-1);
    }

    public void addNoteDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.add_note, viewGroup, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                });
            }
        });
        builder.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText et = dialogView.findViewById(R.id.et);
                        noteDBHelper.addNote(et.getText().toString());
                        Toast.makeText(getApplicationContext(), "Note Added", Toast.LENGTH_SHORT).show();
                        showNotes();
                    }
                }
        );

        builder.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
