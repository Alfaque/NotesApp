package com.alfaque.notesapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alfaque.notesapp.databinding.ActivityAddNoteBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.alfaque.notesapp.DataBase.NotesDataBase;
import com.alfaque.notesapp.Helper_Classes.FileUtils;
import com.alfaque.notesapp.Model.Note;
import com.alfaque.notesapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.alfaque.notesapp.Activity.HomeActivity.REQ_IMAGE;
import static com.alfaque.notesapp.Activity.HomeActivity.REQ_STORAGE_PERMISSION;

public class AddNoteActivity extends AppCompatActivity {
    ActivityAddNoteBinding activityAddNoteBinding;
    String title, subtitle, notedesc, dateTime;
    String selectedColor;
    String selectedImagePath;
    Uri selectedImageUri;
    public final String TAG = "AddNoteActivity_TAG";
    Note alreadyAvailableNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddNoteBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_note);

        selectedColor = "#97000000";//default color for note
        selectedImagePath = "";
        getDateTime();


        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
                alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
                setViewOrUpdateNote();
            }
        }



        activityAddNoteBinding.addnoteBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityAddNoteBinding.addnoteDoneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValues()) {
                    saveNote();
                }
            }
        });

        initBottomSheet();
        setSubTitleIndicatorColor();

        activityAddNoteBinding.delImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activityAddNoteBinding.addnoteImageview.setImageResource(0);
                activityAddNoteBinding.delImageImageView.setVisibility(View.GONE);
                activityAddNoteBinding.addnoteImageview.setVisibility(View.GONE);
                selectedImagePath = "";
            }
        });
        activityAddNoteBinding.delWeburlImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityAddNoteBinding.delWeburlImageView.setVisibility(View.GONE);
                activityAddNoteBinding.addnoteUrlTextview.setText("");
                activityAddNoteBinding.addnoteUrlTextview.setVisibility(View.GONE);

            }
        });


        if (getIntent() != null) {
            if (getIntent().getBooleanExtra(HomeActivity.isFromShortCutMenu, false)) {

                if (getIntent().getStringExtra(HomeActivity.shortCutMenuType).equals("image")) {


                    selectedImagePath = getIntent().getStringExtra("path");
                    activityAddNoteBinding.addnoteImageview.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                    activityAddNoteBinding.addnoteImageview.setVisibility(View.VISIBLE);
                    activityAddNoteBinding.delImageImageView.setVisibility(View.VISIBLE);
                }
                if (getIntent().getStringExtra(HomeActivity.shortCutMenuType).equals("url")) {


                    String webUrl = getIntent().getStringExtra("url");
                    activityAddNoteBinding.addnoteUrlTextview.setText(webUrl);
                    activityAddNoteBinding.addnoteUrlTextview.setVisibility(View.VISIBLE);
                    activityAddNoteBinding.delWeburlImageView.setVisibility(View.VISIBLE);
                }

            }
        }

    }

    private void setViewOrUpdateNote() {
        if (alreadyAvailableNote != null) {
            activityAddNoteBinding.addnoteDatetimeTextView.setText(alreadyAvailableNote.getDateTime());
            activityAddNoteBinding.addnoteTitleEdittext.setText(alreadyAvailableNote.getTitle());
            activityAddNoteBinding.addnoteNoteEdittext.setText(alreadyAvailableNote.getNoteText());
            if (alreadyAvailableNote.getSubtitle() != null && !alreadyAvailableNote.getSubtitle().equals("")) {
                activityAddNoteBinding.addnoteSubtitleEdittext.setText(alreadyAvailableNote.getSubtitle());
            }
            if (alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().equals("")) {
                activityAddNoteBinding.addnoteImageview.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
                activityAddNoteBinding.addnoteImageview.setVisibility(View.VISIBLE);
                selectedImagePath = alreadyAvailableNote.getImagePath();

                activityAddNoteBinding.delImageImageView.setVisibility(View.VISIBLE);

            }
            if (alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().equals("")) {
                activityAddNoteBinding.addnoteUrlTextview.setText(alreadyAvailableNote.getWebLink());
                activityAddNoteBinding.addnoteUrlTextview.setVisibility(View.VISIBLE);
                activityAddNoteBinding.delWeburlImageView.setVisibility(View.VISIBLE);
            }


        }

    }


    private void initBottomSheet() {
        View view = findViewById(R.id.layout_miscellanous);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });


        //setup listener for colors

        final ImageView imageView1 = findViewById(R.id.img_1);
        final ImageView imageView2 = findViewById(R.id.img_2);
        final ImageView imageView3 = findViewById(R.id.img_3);
        final ImageView imageView4 = findViewById(R.id.img_4);

        view.findViewById(R.id.color_indicator_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedColor = "#97000000";//default color

                imageView1.setImageResource(R.drawable.ic_done);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                setSubTitleIndicatorColor();
            }
        });
        view.findViewById(R.id.color_indicator_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedColor = "#2196F3";//blue
                imageView1.setImageResource(0);
                imageView2.setImageResource(R.drawable.ic_done);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                setSubTitleIndicatorColor();
            }
        });
        view.findViewById(R.id.color_indicator_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedColor = "#FFEB3B";//yellow
                imageView1.setImageResource(0);
                imageView2.setImageResource(0);
                imageView3.setImageResource(R.drawable.ic_done);
                imageView4.setImageResource(0);
                setSubTitleIndicatorColor();
            }
        });
        view.findViewById(R.id.color_indicator_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedColor = "#F44336";//red
                imageView1.setImageResource(0);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(R.drawable.ic_done);
                setSubTitleIndicatorColor();
            }
        });


        if (alreadyAvailableNote != null) {
            if (alreadyAvailableNote.getColor() != null && !TextUtils.isEmpty(alreadyAvailableNote.getColor())) {

                switch (alreadyAvailableNote.getColor()) {
                    case "#97000000":
                        view.findViewById(R.id.color_indicator_1).performClick();
                        break;
                    case "#2196F3":
                        view.findViewById(R.id.color_indicator_2).performClick();
                        break;
                    case "#FFEB3B":
                        view.findViewById(R.id.color_indicator_3).performClick();
                        break;
                    case "#F44336":
                        view.findViewById(R.id.color_indicator_4).performClick();
                        break;
                }

            }

            view.findViewById(R.id.delete_notelayout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.delete_notelayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                }
            });


        }


        //add image listener
        view.findViewById(R.id.add_iamgelayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                //checkPernission
                if (ContextCompat.checkSelfPermission(AddNoteActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //requestPermission
                    ActivityCompat.requestPermissions(AddNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_STORAGE_PERMISSION);
                } else {
                    selectImage();
                }

            }
        });


        view.findViewById(R.id.add_urllayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showDialog();
            }
        });


    }

    private void showDeleteNoteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this note.");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                class DeleteTask extends AsyncTask<Void, Void, Void> {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        NotesDataBase.getInstance(AddNoteActivity.this).notesDao().deleteNote(alreadyAvailableNote);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        Intent intent = new Intent();
                        intent.putExtra("isNoteDeleted", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }
                new DeleteTask().execute();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();

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
                    Toast.makeText(AddNoteActivity.this, "Enter Link", Toast.LENGTH_SHORT).show();
                } else {
                    if (!Patterns.WEB_URL.matcher(editText.getText().toString()).matches()) {
                        Toast.makeText(AddNoteActivity.this, "Enter Valid  Web Url", Toast.LENGTH_SHORT).show();
                    } else {
                        activityAddNoteBinding.addnoteUrlTextview.setText(editText.getText().toString());
                        activityAddNoteBinding.addnoteUrlTextview.setVisibility(View.VISIBLE);
                        activityAddNoteBinding.delWeburlImageView.setVisibility(View.VISIBLE);
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

    private void setSubTitleIndicatorColor() {

        GradientDrawable drawable = (GradientDrawable) activityAddNoteBinding.subtitleColorIndicator.getBackground();
        drawable.setColor(Color.parseColor(selectedColor));
    }

    private void saveNote() {

        final Note note = new Note();
        note.setNoteText(notedesc);
        note.setTitle(title);
        note.setDateTime(dateTime);
        note.setColor(selectedColor);
        subtitle = activityAddNoteBinding.addnoteSubtitleEdittext.getText().toString();
        if (!TextUtils.isEmpty(subtitle) && subtitle != null) {
            note.setSubtitle(subtitle);
        }
        if (!TextUtils.isEmpty(selectedImagePath) && selectedImagePath != null) {
            note.setImagePath(selectedImagePath);
        }

        if (activityAddNoteBinding.addnoteUrlTextview.getVisibility() == View.VISIBLE) {
            note.setWebLink(activityAddNoteBinding.addnoteUrlTextview.getText().toString());
        }

        if (alreadyAvailableNote != null)//this is for only if you want to update the note.
        {
            note.setId(alreadyAvailableNote.getId());
        }
        Log.i(TAG, "saveNote: ");

        class SaveNoteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDataBase.getInstance(getApplicationContext()).notesDao().insert(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();


    }

    private void getDateTime() {

        dateTime = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date());
        activityAddNoteBinding.addnoteDatetimeTextView.setText(dateTime);

    }

    private boolean checkValues() {

        title = activityAddNoteBinding.addnoteTitleEdittext.getText().toString();
        notedesc = activityAddNoteBinding.addnoteNoteEdittext.getText().toString();
        if (TextUtils.isEmpty(title)) {
            activityAddNoteBinding.addnoteTitleEdittext.setError("enter title.");
            return false;
        }
        if (TextUtils.isEmpty(notedesc)) {
            activityAddNoteBinding.addnoteNoteEdittext.setError("enter note description.");
            return false;
        }

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {

                    selectedImagePath = getPathFormUri(selectedImageUri);
                    Glide.with(AddNoteActivity.this).load(selectedImageUri).into(activityAddNoteBinding.addnoteImageview);
                    activityAddNoteBinding.addnoteImageview.setVisibility(View.VISIBLE);
                    activityAddNoteBinding.delImageImageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private String getPathFormUri(Uri selectedImgUri) {

        String path;
//        Cursor cursor = getContentResolver().query(selectedImgUri, null, null, null, null);
//        if (cursor == null) {
//            path = selectedImgUri.getPath();
//            Log.i(TAG, "getPathFormUri: path:" + path);
//        }//
//        else {
//            cursor.moveToFirst();
//            path = cursor.getString(cursor.getColumnIndex("DATA"));
//            Log.i(TAG, "getPathFormUri: path:" + path);
//            cursor.close();
//        }
        path = FileUtils.getPath(this, selectedImgUri);
        return path;
    }
}