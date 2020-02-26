package com.example.notes.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.R;

import java.util.ArrayList;


public class NotesAdapter extends RecyclerView.Adapter <NotesAdapter.NoteViewHolder>{

    private Context mContext;
    private ArrayList<String> mList = new ArrayList<>();

    public NotesAdapter(Context mContext, ArrayList<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.tv_note.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_note;

        public NoteViewHolder(@NonNull final View itemView) {
            super(itemView);
            tv_note = itemView.findViewById(R.id.tv_note);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
//                    Toast.makeText(mContext, String.valueOf(getAdapterPosition())+" Long Pressed", Toast.LENGTH_SHORT).show();
                    PopupMenu popup = new PopupMenu(mContext, view);
                    popup.inflate(R.menu.options_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId())
                            {
                                case R.id.menu1:
                                    Toast.makeText(mContext, "Pressed Update", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.menu2:
                                    Toast.makeText(mContext, "Pressed Delete", Toast.LENGTH_SHORT).show();
                                    mList.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                    return false;
                }
            });
        }
        public void addNoteDialog() {
            ViewGroup viewGroup = itemView.findViewById(android.R.id.content);
            final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.add_note, viewGroup, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(dialogView);
            builder.setPositiveButton("Add",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText et = dialogView.findViewById(R.id.et);
                            Toast.makeText(mContext, "Note Added", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            builder.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            Toast.makeText(mContext, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

}
