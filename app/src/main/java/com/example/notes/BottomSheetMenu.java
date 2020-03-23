package com.example.notes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetMenu extends BottomSheetDialogFragment {

    private BottomSheetListener bottomSheetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_bottom_sheet, container, false);

        Button btn_export = v.findViewById(R.id.btn_export);
        Button btn_import = v.findViewById(R.id.btn_import);

        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetListener.onButtonClicked("Export");
                dismiss();
            }
        });

        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetListener.onButtonClicked("Import");
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
        bottomSheetListener = (BottomSheetListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement BottomSheetListener");
        }

    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
}