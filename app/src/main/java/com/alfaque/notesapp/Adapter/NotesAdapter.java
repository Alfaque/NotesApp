package com.alfaque.notesapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alfaque.notesapp.Model.Note;
import com.alfaque.notesapp.databinding.ItemNoteBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyNotesHolder> {
    Context context;
    List<Note> list;
    OnNoteItemClicked onNoteItemClicked;

    public NotesAdapter(Context context, List<Note> list) {
        this.context = context;
        this.list = list;
        this.onNoteItemClicked = (OnNoteItemClicked) context;
    }

    @NonNull

    @Override
    public MyNotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding itemNoteBinding = ItemNoteBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyNotesHolder(itemNoteBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNotesHolder holder, final int position) {
        final Note note = list.get(position);
        holder.itemNoteBinding.itemNoteDatetimeTextView.setText(note.getDateTime());
        holder.itemNoteBinding.itemNoteTitleTextView.setText(note.getTitle());
        if (!TextUtils.isEmpty(note.getSubtitle()) && note.getSubtitle() != null) {
            holder.itemNoteBinding.itemNoteSubtitleTextView.setText(note.getSubtitle());
        } else {
            holder.itemNoteBinding.itemNoteSubtitleTextView.setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(note.getImagePath()) && note.getImagePath() != null) {

            Bitmap bitmap = BitmapFactory.decodeFile(note.getImagePath());
            holder.itemNoteBinding.itemNoteImageview.setImageBitmap(bitmap);
            holder.itemNoteBinding.itemNoteImageview.setVisibility(View.VISIBLE);

        } else {
            holder.itemNoteBinding.itemNoteImageview.setVisibility(View.GONE);

        }

        GradientDrawable drawable = (GradientDrawable) holder.itemNoteBinding.getRoot().getBackground();
        if (note.getColor() != null && !TextUtils.isEmpty(note.getColor())) {
            drawable.setColor(Color.parseColor(note.getColor()));
        } else {
            drawable.setColor(Color.parseColor("#97000000"));
        }

        holder.itemNoteBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNoteItemClicked.onNoteItemClickedListener(position, note);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(List<Note> filteredList) {

        this.list = filteredList;
        notifyDataSetChanged();

    }


    public class MyNotesHolder extends RecyclerView.ViewHolder {
        ItemNoteBinding itemNoteBinding;

        public MyNotesHolder(@NonNull ItemNoteBinding itemNoteBinding) {
            super(itemNoteBinding.getRoot());
            this.itemNoteBinding = itemNoteBinding;
        }
    }

    public interface OnNoteItemClicked {
        public void onNoteItemClickedListener(int posotion, Note note);
    }
}
