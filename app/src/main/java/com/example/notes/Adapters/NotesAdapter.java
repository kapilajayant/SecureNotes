package com.example.notes.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.MainActivity;
import com.example.notes.R;
import com.example.notes.db.NoteDBHelper;

import java.util.ArrayList;
import java.util.Random;


public class NotesAdapter extends RecyclerView.Adapter <NotesAdapter.NoteViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<String> mList = new ArrayList<>();
    private ArrayList<String> mListTemp;
    int colorId = 0;
//    private int colorPallete[] = {mContext.getResources().getColor(R.color.light_blue),
//            mContext.getResources().getColor(R.color.light_brown),
//            mContext.getResources().getColor(R.color.light_green),
//            mContext.getResources().getColor(R.color.light_purple)
//    };

    public NotesAdapter(Context mContext, ArrayList<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
        // new object reference is required so that both thses lists wont point to the same object in the memory
        // and end up treating as same.
        mListTemp = new ArrayList<>(mList);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
        view = (CardView) view;
        Random random = new Random(4);
        colorId++;
        if (colorId>4){
            colorId = colorId/4;
        }
        switch (colorId) {
            case 1:
                ((CardView) view).setCardBackgroundColor(mContext.getResources().getColor(R.color.light_brown));
                break;
            case 2:
                ((CardView) view).setCardBackgroundColor(mContext.getResources().getColor(R.color.light_green));
                break;
            case 3:
                ((CardView) view).setCardBackgroundColor(mContext.getResources().getColor(R.color.light_blue));
                break;
            case 4:
                ((CardView) view).setCardBackgroundColor(mContext.getResources().getColor(R.color.light_purple));
                break;
        }
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

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<String> filterList = new ArrayList<>();
            if(charSequence == null || charSequence.length()==0)
            {
                filterList.addAll(mListTemp);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(String item : mListTemp)
                {
                    if (item.toLowerCase().contains(filterPattern))
                    {
                        filterList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mList.clear();
            mList.addAll((ArrayList<String>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_note;

        public NoteViewHolder(@NonNull final View itemView) {
            super(itemView);
            tv_note = itemView.findViewById(R.id.tv_note);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "Text Copied!", Toast.LENGTH_SHORT).show();
                    ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", mList.get(getAdapterPosition()));
                    clipboard.setPrimaryClip(clip);
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
//                                    Toast.makeText(mContext, "Pressed Update", Toast.LENGTH_SHORT).show();
                                    updateDialog(view, getAdapterPosition());
                                    break;
                                case R.id.menu2:
//                                    Toast.makeText(mContext, "Pressed Delete", Toast.LENGTH_SHORT).show();
                                    NoteDBHelper noteDBHelper = new NoteDBHelper(mContext, null);
                                    Boolean isDeleted = noteDBHelper.deleteNote(mList.get(getAdapterPosition()));
                                    if(isDeleted){
                                        Toast.makeText(mContext, "Note Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        mList.remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                        notifyDataSetChanged();
                                    }
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
            dialogView.setBackgroundColor(mContext.getResources().getColor(R.color.custom_gray));
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

    private void updateDialog(View view, final int adapterPosition) {
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        Activity activity = (Activity)mContext;
        final View dialogView = LayoutInflater.from(activity).inflate(R.layout.add_note, viewGroup, false);
        TextView title_tv = dialogView.findViewById(R.id.title_tv);
        title_tv.setText("Update Note");
        final EditText et = dialogView.findViewById(R.id.et);
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et,InputMethodManager.SHOW_IMPLICIT);
        et.setText(mList.get(adapterPosition));
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
        builder.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NoteDBHelper noteDBHelper = new NoteDBHelper(mContext,null);
                        ArrayList<String> list = noteDBHelper.updateNote(mList.get(adapterPosition),et.getText().toString());
//                        refresh(list);
//                        mList.clear();
//                        mList = list;
                        mList = new NoteDBHelper(mContext,null).getAllNotes();
                        notifyDataSetChanged();
//                        new NotesAdapter(mContext, list);
//                        mContext.startActivity(new Intent(mContext, MainActivity.class));
//                        ((Activity) mContext).finish();
                        Toast.makeText(mContext, "Note Updated", Toast.LENGTH_SHORT).show();
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

    public void refresh(ArrayList<String> list){
        this.mList.clear();
        this.mList = list;
        notifyDataSetChanged();
    }

}
