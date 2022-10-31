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
import java.util.Arrays;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class SearchingMultiDialogMultiColumn extends Dialog {
    public static final int ADD_ITEM_TO_MULTI_LIST_MULTI_COLUMN_CODE = 300;

    private final String[] keysToSearch;
    private String[] fieldsToSearch;
    public ArrayList<String[]> textsReturned = new ArrayList<> ();
    public ArrayList<int[]> keysReturned = new ArrayList<> ();
    Activity activity;

//    Cursor cursor;
//    String tableToSearch = "";
    CursorAdapter cursorAdapter;
    EditText slTextBox;
    Cursor cursor;
    String tableToSearch = "";
    ListView listView;
    ArrayList<String[]> itemsSelected;
    private DbHelper dbHelper;
    ArrayList<String> savedItemsSelected = new ArrayList ();;
    int columns;

    public SearchingMultiDialogMultiColumn(@NonNull Context context, CursorAdapter adapter,
                                           String tableToSearch, String[] fieldsToSearch, String[] keysToSearch, int title, Class<Activity> addEditActivityClass,
                                           ArrayList<String[]> itemsSelected,int columns){
        super (context);
        this.activity = (Activity) context;
        this.tableToSearch = tableToSearch;
        this.itemsSelected = itemsSelected;
        this.fieldsToSearch = fieldsToSearch;
        cursorAdapter = adapter;
        this.keysToSearch = keysToSearch;
        this.columns = columns;

        dbHelper = new DbHelper (context);
        setContentView (R.layout.search_mult_list_dialog);
        getWindow ().setLayout (MATCH_PARENT,1400);
        getWindow ().setGravity (TOP|CENTER_HORIZONTAL);
        WindowManager.LayoutParams params = getWindow ().getAttributes ();
        params.y = 50;
        getWindow ().setAttributes (params);
        getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
        show ();

        TextView slTitle = findViewById (R.id.title_search_list);
        listView = findViewById (R.id.search_multi_list_view);
        slTextBox = findViewById (R.id.search_box2);
        Button addBtn = findViewById (R.id.addBtn2);
        Button clearBtn = findViewById (R.id.clear_btn2);
        FloatingActionButton okBtn = findViewById (R.id.okBtn);

        // Cursores y adaptadores
//        cursor = dbHelper.getAllData (tableToSearch,fieldsToSearch[0]);
//        cursorAdapter.swapCursor (cursor);
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
                        Cursor c = (Cursor) listView.getItemAtPosition (i);
                        String item = c.getString (c.getColumnIndex (fieldsToSearch[0])) +
                                ((c.getString (c.getColumnIndex (fieldsToSearch[1])) != null)?
                                (" corte " + c.getString (c.getColumnIndex (fieldsToSearch[1]))) : "");
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
                        Cursor c = adapter.getCursor ();

                        for (int i = 0; i < c.getCount (); i++) {
                            // if the current (filtered)
                            // listview you are viewing has the name included in the list,
                            // check the box
                            c.moveToPosition (i);

                            String item = c.getString (c.getColumnIndex (fieldsToSearch[0])) +
                                    ((c.getString (c.getColumnIndex (fieldsToSearch[1])) != null)?
                                            (" talla " + c.getString (c.getColumnIndex (fieldsToSearch[1]))) : "");

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
                Cursor currentItem = (Cursor) cursorAdapter.getItem (position);
                String[] textSelected = new String[columns];
                int[] keySelected = new int[columns];
                for (int i = 0; i < columns; i++) {
                    textSelected[i] = currentItem.getString (currentItem.getColumnIndex (fieldsToSearch[i]));
                    keySelected[i] = currentItem.getInt (currentItem.getColumnIndex (keysToSearch[i]));
                }

                if(listView.isItemChecked (position)) {
                    if (! textsReturned.contains (textSelected)) {
                        textsReturned.add (textSelected);
                        keysReturned.add (keySelected);
                    }
                }else {
                    removeArray (textsReturned,textSelected);
                    removeArray(keysReturned,keySelected);
                }
            }
        });

        addBtn.setOnClickListener (new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(activity, addEditActivityClass);
                activity.startActivityForResult (intent, ADD_ITEM_TO_MULTI_LIST_MULTI_COLUMN_CODE);
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

    private void removeArray(ArrayList<int[]> textsReturned, int[] textSelected){
        for (int index = 0; index < textsReturned.size(); index++)
            if (Arrays.equals (textSelected,textsReturned.get(index))) {
                textsReturned.remove(index);
                return;
            }
    }



    private void removeArray(ArrayList<String[]> textsReturned, String[] textSelected){
        for (int index = 0; index < textsReturned.size(); index++)
            if (Arrays.equals (textSelected,textsReturned.get(index))) {
                textsReturned.remove(index);
                return;
            }
    }

    private void initializeListView(CursorAdapter adapter){
//        cursor.moveToFirst ();
        int count = 0;
        for (int i = 0; i < adapter.getCount (); i++) {
            Cursor currentItem = (Cursor) adapter.getItem (i);

            String[] texts = new String[columns];
            int[] keys = new int[columns];
            for (int j = 0; j < columns; j++) {
                texts[j] = currentItem.getString (currentItem.getColumnIndex (fieldsToSearch[j]));
                keys[j] = currentItem.getInt (currentItem.getColumnIndex (keysToSearch[j]));
            }

            if(count < itemsSelected.size () && isSelected (texts)) {
                count++;
                listView.setItemChecked (i, true);
                textsReturned.add (texts);
                keysReturned.add (keys);
            }
        }
    }

    private boolean isSelected(String[] texts){
        boolean selected = false;
        for (int i = 0; i < itemsSelected.size (); i++) {
            for (int j = 0; j < columns; j++) {
                if (itemsSelected.get (i)[j] == null) {
                    if (texts[j] == null) {
                        selected = true;
                    } else {
                        selected = false;
                        break;
                    }
                } else {
                    if (itemsSelected.get (i)[j].equals (texts[j])) {
                        selected = true;
                    } else {
                        selected = false;
                        break;
                    }
                }
            }
            if (selected)
                return true;
        }
        return false;
    }

    private CursorAdapter filterAdapter(CharSequence s){
        this.cursorAdapter.getFilter ().filter (s.toString ());
        this.cursorAdapter.notifyDataSetChanged();
        return cursorAdapter;
    }



    private Cursor filterList(CharSequence constraint){
        return dbHelper.getAllDataFiltered (tableToSearch,fieldsToSearch[0], (String) constraint);
    }

    @Override
    protected void onStart(){
        super.onStart ();
    }

    public void swapCursor(){
        cursorAdapter.getCursor ().requery ();
//        Cursor cursor = dbHelper.getAllJewelsGemstonesCuts ();
//        cursorAdapter.swapCursor (cursor);
    }
}


