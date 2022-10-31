package com.example.catalogos.dialog_package;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.catalogos.R;
import com.example.catalogos.database.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class SearchingMultiDialog extends Dialog {
    public static final int ADD_ITEM_TO_MULTI_LIST_CODE = 200;

    private String fieldToSearch;
    public ArrayList<String> textsReturned = new ArrayList<> ();
    public ArrayList<Integer> keysReturned = new ArrayList<> ();

    Activity activity;
    CursorAdapter cursorAdapter;
    EditText slTextBox;
    Cursor cursor;
    String tableToSearch = "";
    ListView listView;
    ArrayList<String> itemsSelected;
    private DbHelper dbHelper;
    ArrayList<String> savedItemsSelected = new ArrayList ();;

    public SearchingMultiDialog(@NonNull Context context, CursorAdapter adapter,
                                String tableToSearch, String fieldToSearch, int title, Class<Activity> addEditActivityClass,
                                ArrayList<String> itemsSelected){
        super (context);
        this.activity = (Activity) context;
        this.itemsSelected = itemsSelected;
        this.fieldToSearch = fieldToSearch;
        cursorAdapter = adapter;
        this.tableToSearch = tableToSearch;
        dbHelper = new DbHelper (context);
        setContentView (R.layout.search_mult_list_dialog);

        //---------- Prepare dialog window
        getWindow ().setLayout (MATCH_PARENT,1400);
        getWindow ().setGravity (TOP|CENTER_HORIZONTAL);
        WindowManager.LayoutParams params = getWindow ().getAttributes ();
        params.y = 50;
        getWindow ().setAttributes (params);
        getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
        show ();

        // Variable UI
        TextView slTitle = findViewById (R.id.title_search_list);
        slTextBox = findViewById (R.id.search_box2);
        listView = findViewById (R.id.search_multi_list_view);
        Button addBtn = findViewById (R.id.addBtn2);
        Button clearBtn = findViewById (R.id.clear_btn2);
        FloatingActionButton okBtn = findViewById (R.id.okBtn);

        // Cursores y adaptadores
        cursor = dbHelper.getAllData (tableToSearch,fieldToSearch);
        cursorAdapter.swapCursor (cursor);
        listView.setAdapter (cursorAdapter);
        cursorAdapter.getFilter ().filter ("");

        slTitle.setText (title);

        // Prepare your this.cursorAdapter for filtering
        this.cursorAdapter.setFilterQueryProvider(new FilterQueryProvider () {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return  filterList(constraint);
            }
        });

        listView.setTextFilterEnabled(true);

        listView.setAdapter (this.cursorAdapter);
        this.cursorAdapter.getFilter ().filter ("");

        slTextBox.addTextChangedListener (new TextWatcher () {


            CursorAdapter adapter;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                for (int i = 0; i < listView.getCount (); i++) {
                    if (listView.isItemChecked (i)){
                        String item = ((TextView)(listView.getChildAt (i))).getText ().toString ();
                        if (!savedItemsSelected.contains (item))
                            savedItemsSelected.add (item);
                    }
                }
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
                adapter = filterAdapter(s);
            }

            @Override
            public void afterTextChanged(Editable s){
//                updateListView (adapter);
                adapter.getFilter().filter(s, new Filter.FilterListener() {
                    public void onFilterComplete(int count){
                        adapter.notifyDataSetChanged ();
                        Cursor cursor = adapter.getCursor ();

                        for (int i = 0; i < cursor.getCount (); i++) {
                            // if the current (filtered)
                            // listview you are viewing has the name included in the list,
                            // check the box
                            cursor.moveToPosition (i);
                            @SuppressLint("Range") String item = cursor.getString (cursor.getColumnIndex (SearchingMultiDialog.this.fieldToSearch));

                            if (savedItemsSelected.contains (item)) {
                                listView.setItemChecked (i, true);
                            } else {
                                listView.setItemChecked (i, false);
                            }
                        }
                    }
                });
            }

        });


        clearBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view){
                slTextBox.setText ("");
            }
        });

        listView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                update(position);
            }
        });

        addBtn.setOnClickListener (new View.OnClickListener(){

            @Override
            public void onClick(View v){
                loadActivity(addEditActivityClass);
            }
        });

        okBtn.setOnClickListener (new View.OnClickListener(){

            @Override
            public void onClick(View v){
                dismiss ();
            }
        });

        if(itemsSelected.size () > 0)
            initializeListView(adapter);
    }

    private void update(int position){
        Cursor currentItem = (Cursor) cursorAdapter.getItem (position);
        String textSelected = currentItem.getString (currentItem.getColumnIndex (fieldToSearch));
        int keySelected = currentItem.getInt (currentItem.getColumnIndex ("_id"));
        if(listView.isItemChecked (position)) {
            if (! textsReturned.contains (textSelected)) {
                textsReturned.add (textSelected);
                keysReturned.add (keySelected);
            }
        }else {
            textsReturned.remove (textSelected);
            keysReturned.remove (Integer.valueOf(keySelected));
        }
    }

    private CursorAdapter filterAdapter(CharSequence s){
        this.cursorAdapter.getFilter ().filter (s.toString ());
        this.cursorAdapter.notifyDataSetChanged();
        return cursorAdapter;
    }


    private Cursor filterList(CharSequence constraint){
        return dbHelper.getAllDataFiltered (tableToSearch,fieldToSearch, (String) constraint);
    }

    private void loadActivity(Class<Activity> addEditActivityClass){
        Intent intent = new Intent(activity, addEditActivityClass);
        this.activity.startActivityForResult (intent, ADD_ITEM_TO_MULTI_LIST_CODE);
    }

    private void initializeListView(CursorAdapter adapter){
//        cursor.moveToFirst ();
        for (int i = 0; i < adapter.getCount (); i++) {
            Cursor currentItem = (Cursor) adapter.getItem (i);
            String text = currentItem.getString (currentItem.getColumnIndex (fieldToSearch));
            int id = currentItem.getInt (currentItem.getColumnIndex ("_id"));
            if(itemsSelected.contains (text)) {
                listView.setItemChecked (i, true);
                textsReturned.add (text);
                keysReturned.add (id);
            }
        }
    }

    private void updateListView(CursorAdapter adapter){
//        cursor.moveToFirst ();
        for (int i = 0; i < adapter.getCount (); i++) {
            Cursor currentItem = (Cursor) adapter.getItem (i);
            String text = currentItem.getString (currentItem.getColumnIndex (fieldToSearch));
            int id = currentItem.getInt (currentItem.getColumnIndex ("_id"));
            if(savedItemsSelected.contains (text)) {
                listView.setItemChecked (i, true);
                textsReturned.add (text);
                keysReturned.add (id);
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart ();
    }

    public void swapCursor(){
        cursor = dbHelper.getAllData (tableToSearch,fieldToSearch);
        cursorAdapter.swapCursor (cursor);
    }
}


