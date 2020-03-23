package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.notes.Adapters.NotesAdapter;
import com.example.notes.Models.Note;
import com.example.notes.db.NoteDBHelper;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity implements BottomSheetMenu.BottomSheetListener {

    FButton addBtn;
    TextView tv;
    NoteDBHelper noteDBHelper;
    RecyclerView rv;

    NotesAdapter adapter;
    ArrayList<String> allNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);

        noteDBHelper = new NoteDBHelper(getApplicationContext(), null);

        addBtn = findViewById(R.id.addBtn);
//        tv = findViewById(R.id.tv);
        rv = findViewById(R.id.rv);

        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);

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
            rv.smoothScrollToPosition(adapter.getItemCount()-1);
    }

    public void addNoteDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.add_note, viewGroup, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText et = dialogView.findViewById(R.id.et);
        et.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        viewGroup.setBackgroundColor(getResources().getColor(R.color.custom_gray));
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dialogView.getWindowToken(), 0);
            }
        });
        builder.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(dialogView.getWindowToken(), 0);
                        if (et.getText().toString().length()>0)
                        {
                            noteDBHelper.addNote(et.getText().toString());
                            Toast.makeText(getApplicationContext(), "Note Added", Toast.LENGTH_SHORT).show();
                            showNotes();
                        }
                        else {
                            et.setError("Type something");
                        }
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.item_search);
        final MenuItem ie_item = menu.findItem(R.id.item_export);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchView,0);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.onActionViewCollapsed();
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        ie_item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getSupportFragmentManager(), "import_export");

                return false;
            }
        });

        return true;
    }

    private static boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
//        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
//            Log.e(TAG, "Storage not available or read only");
//            return false;
//        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
//        CellStyle cs = wb.createCellStyle();
//        cs.setFillForegroundColor(HSSFColor.LIME.index);
//        cs.setFillPattern(HSSFCellStyle.);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Notes");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Id");
//        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Note");
//        c.setCellStyle(cs);


        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));

        // Create a path where we will place our List of objects on external storage
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                {
                    os.close();
                    wb.close();
                }
            } catch (Exception ex) {
            }
        }
        return success;
    }

    @Override
    public void onButtonClicked(String text) {
        if (text.equals("Export")){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                Date date = new Date();
                try {
//                    createBackup();
                    saveExcelFile(MainActivity.this, "hui");
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
        }
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void createBackup() throws Exception {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet("Sheet No: 1");
        HSSFSheet secondSheet = workbook.createSheet("Sheet No: 2");
        HSSFRow rowA = firstSheet.createRow(0);
        HSSFCell cellA = rowA.createCell(0);
        cellA.setCellValue(new HSSFRichTextString("Sheet One"));
        HSSFRow rowB = secondSheet.createRow(0);
        HSSFCell cellB = rowB.createCell(0);
        cellB.setCellValue(new HSSFRichTextString("Sheet two"));

        FileOutputStream fos = null;

        try {
            File file = new File(Environment.getExternalStorageDirectory().toString(), "note1.xls");
            fos = new FileOutputStream(file);
            workbook.write(fos);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            if(fos!=null)
            {
                try {
                    fos.flush();
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            Toast.makeText(MainActivity.this, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();
        }

    }

}
