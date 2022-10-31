package com.example.catalogos.dialog_package;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.catalogos.R;
import com.example.catalogos.database.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;


public class newLotDialog extends Dialog {
    public static final int ADD_ITEM_TO_SINGLE_LIST_CODE = 1000;
    private final String fieldToSearch;
    private final String tableToSearch;
    public int keyReturned = 0;
    public String textReturned="";
    Activity activity;
    CursorAdapter cursorAdapter;
    EditText slTextBox;
    DbHelper dbHelper;
    private CharSequence constraint;

    public newLotDialog(@NonNull Context context, CursorAdapter adapter,
                        String tableToSearch, String fieldToSearch, int title, Class<Activity> addEditActivityClass){
        super (context);
        this.activity = (Activity) context;
        this.cursorAdapter = adapter;
        this.fieldToSearch = fieldToSearch;
        this.tableToSearch = tableToSearch;
        dbHelper = new DbHelper (context);
        setContentView (R.layout.search_list_dialog);
        getWindow ().setLayout (800,1200);
        getWindow ().setGravity (TOP|CENTER_HORIZONTAL);
        WindowManager.LayoutParams params = getWindow ().getAttributes ();
        params.y = 50;
        getWindow ().setAttributes (params);
        getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
        show ();

        TextView slTitle = findViewById (R.id.title_search_list);
        slTextBox = findViewById (R.id.search_box);
        ListView listView = findViewById (R.id.search_list_view);
        FloatingActionButton addBtn = findViewById (R.id.addBtn);

        // Prepare your this.cursorAdapter for filtering
        this.cursorAdapter.setFilterQueryProvider(new FilterQueryProvider () {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return  filterList(constraint);
            }
        });

        listView.setTextFilterEnabled(true);

        slTitle.setText (title);
        listView.setAdapter (this.cursorAdapter);
        this.cursorAdapter.getFilter ().filter ("");

        slTextBox.addTextChangedListener (new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
                filterAdapter(s);
            }

            @Override
            public void afterTextChanged(Editable s){

            }
        });


        listView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                updateAnClose(position);
            }
        });

        addBtn.setOnClickListener (new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, addEditActivityClass);

                loadActivity (addEditActivityClass);
            }
        });
    }

    private Cursor filterList(CharSequence constraint){
        this.constraint = constraint;
        return dbHelper.getAllDataFiltered (tableToSearch,fieldToSearch, (String) constraint);
    }

    private void updateAnClose(int position){
        Cursor currentItem = (Cursor) this.cursorAdapter.getItem (position);
        textReturned = currentItem.getString (currentItem.getColumnIndex (fieldToSearch));
        keyReturned = currentItem.getInt (currentItem.getColumnIndex ("_id"));
        dismiss ();
    }

    private void filterAdapter(CharSequence s){
        this.cursorAdapter.getFilter ().filter (s.toString ());
        this.cursorAdapter.notifyDataSetChanged();
    }


    private void loadActivity(Class<Activity> addEditActivityClass){
        Intent intent = new Intent(activity, addEditActivityClass);
        this.activity.startActivityForResult (intent,ADD_ITEM_TO_SINGLE_LIST_CODE);
    }

    @Override
    protected void onStart(){
        super.onStart ();
    }

    public void swapCursor(){
        cursorAdapter.swapCursor (dbHelper.getAllDataFiltered (tableToSearch,fieldToSearch, (String) constraint));
    }
}


