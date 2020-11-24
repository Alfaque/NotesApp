package com.alfaque.notesapp.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alfaque.notesapp.Adapter.NotesAdapter;
import com.alfaque.notesapp.DataBase.NotesDataBase;
import com.alfaque.notesapp.Helper_Classes.FileUtils;
import com.alfaque.notesapp.Model.Note;
import com.alfaque.notesapp.R;
import com.alfaque.notesapp.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NotesAdapter.OnNoteItemClicked {

    public static final int REQ_STORAGE_PERMISSION = 111;
    public static final int REQ_IMAGE = 112;
    public static final String isFromShortCutMenu = "isFromShortCutMenu";
    public static final String shortCutMenuType = "shortCutMenuType";
    ActivityHomeBinding activityHomeBinding;
    public static final int ADD_NOTE_CONST = 101;
    public static final int VIEW_OR_UPDATE_NOTE_CONST = 102;
    public static final int SHOW_NOTE_CONST = 103;
    public final String TAG = "HomeActivity_TAG";
    NotesAdapter notesAdapter;
    List<Note> list = new ArrayList<>();
    int selectedNotePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        notesAdapter = new NotesAdapter(this, list);
        activityHomeBinding.homrRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        activityHomeBinding.homrRecyclerView.setAdapter(notesAdapter);
        activityHomeBinding.addNoteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(HomeActivity.this, AddNoteActivity.class), ADD_NOTE_CONST);
            }
        });
        getNotes(SHOW_NOTE_CONST, false);


        activityHomeBinding.homeSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterResult(editable.toString());
            }
        });


        activityHomeBinding.addSimplenoteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(HomeActivity.this, AddNoteActivity.class), ADD_NOTE_CONST);


            }
        });

        activityHomeBinding.addImagenoteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //checkPernission
                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //requestPermission
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_STORAGE_PERMISSION);
                } else {
                    selectImage();
                }


            }
        });


        activityHomeBinding.addLinknoteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });


    }

    private void showDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View view = findViewById(R.layout.dialog_layout)  this didnt work.


        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null, false);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();


        final EditText editText = view.findViewById(R.id.item_dialog_edittext);
        editText.requestFocus();

        view.findViewById(R.id.item_dialog_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(editText.getText().toString())) {
                    Toast.makeText(HomeActivity.this, "Enter Link", Toast.LENGTH_SHORT).show();
                } else {
                    if (!Patterns.WEB_URL.matcher(editText.getText().toString()).matches()) {
                        Toast.makeText(HomeActivity.this, "Enter Valid  Web Url", Toast.LENGTH_SHORT).show();
                    } else {


                        Intent intent = new Intent(HomeActivity.this, AddNoteActivity.class);
                        intent.putExtra(isFromShortCutMenu, true);
                        intent.putExtra(shortCutMenuType, "url");
                        intent.putExtra("url", editText.getText().toString().trim());
                        startActivityForResult(intent, ADD_NOTE_CONST);
                        alertDialog.dismiss();
                    }
                }
            }
        });
        view.findViewById(R.id.item_dialog_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    private void selectImage() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose..."), REQ_IMAGE);

    }

    private void filterResult(String text) {
        List<Note> filteredList = new ArrayList<>();
        for (Note note : list) {

            if (note.getTitle().toLowerCase().contains(text.toLowerCase()) || note.getNoteText().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(note);

            }
        }
        notesAdapter.setFilteredList(filteredList);

    }

    private void getNotes(final int requestcode, final boolean isNoteDeleted) {

        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDataBase.getInstance(getApplicationContext()).notesDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                Log.i(TAG, "onPostExecute: MY_NOTES" + notes.toString());
//                notesAdapter.setData(notes);


                if (requestcode == SHOW_NOTE_CONST) {

                    list.addAll(notes);
                    notesAdapter.notifyDataSetChanged();

                } else if (requestcode == ADD_NOTE_CONST) {

                    list.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    activityHomeBinding.homrRecyclerView.smoothScrollToPosition(0);


                } else if (requestcode == VIEW_OR_UPDATE_NOTE_CONST) {

                    list.remove(selectedNotePosition);


                    if (isNoteDeleted) {
                        notesAdapter.notifyItemRemoved(selectedNotePosition);
                    } else {
                        list.add(selectedNotePosition, notes.get(selectedNotePosition));
                        notesAdapter.notifyItemChanged(selectedNotePosition);
                    }
                }


            }
        }


        new GetNotesTask().execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ADD_NOTE_CONST) {
            getNotes(ADD_NOTE_CONST, false);
        } else if (resultCode == RESULT_OK && requestCode == VIEW_OR_UPDATE_NOTE_CONST && data != null) {
            getNotes(VIEW_OR_UPDATE_NOTE_CONST, data.getBooleanExtra("isNoteDeleted", false));
        } else if (resultCode == RESULT_OK && requestCode == REQ_IMAGE && data != null) {

            Uri uri = data.getData();
            String imagePath = FileUtils.getPath(HomeActivity.this, uri);

            Intent intent = new Intent(HomeActivity.this, AddNoteActivity.class);
            intent.putExtra(isFromShortCutMenu, true);
            intent.putExtra(shortCutMenuType, "image");
            intent.putExtra("path", imagePath);
            startActivityForResult(intent, ADD_NOTE_CONST);

        }

    }

    @Override
    public void onNoteItemClickedListener(int posotion, Note note) {
        selectedNotePosition = posotion;
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, VIEW_OR_UPDATE_NOTE_CONST);

    }
}