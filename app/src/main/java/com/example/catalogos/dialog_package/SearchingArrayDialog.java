package com.example.catalogos.dialog_package;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.catalogos.R;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.hallmarks_package.hallmarks_data.HallmarkType;
import com.google.android.material.textfield.TextInputEditText;

public class SearchingArrayDialog extends Dialog {
   public static final int ADD_ITEM_TO_ARRAY_LIST_CODE = 5000;
   public String textReturned="";
   Activity activity;
   private Context context;
   ArrayAdapter<String> arrayAdapter;
   EditText slTextBox;
   DbHelper dbHelper;
   private CharSequence constraint;
   ListView listView;
   Dialog editDataDialog;

   public SearchingArrayDialog(@NonNull Context context, ArrayAdapter<String> adapter, int title){
      super (context);
      this.activity = (Activity) context;
      this.context = context;
      this.arrayAdapter = adapter;
      dbHelper = new DbHelper (context);
      setContentView (R.layout.search_list_dialog);
      getWindow ().setLayout (MATCH_PARENT,1200);
      getWindow ().setGravity (TOP|CENTER_HORIZONTAL);
      WindowManager.LayoutParams params = getWindow ().getAttributes ();
      params.y = 50;
      getWindow ().setAttributes (params);
      getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
      show ();

      TextView slTitle = findViewById (R.id.title_search_list);
      slTextBox = findViewById (R.id.search_box);
      listView = findViewById (R.id.search_list_view);
      Button addBtn = findViewById (R.id.addBtn);
      Button clearBtn = findViewById (R.id.clear_btn);

      // Prepare your this.cursorAdapter for filtering

      listView.setTextFilterEnabled(true);

      slTitle.setText (title);
      listView.setAdapter (this.arrayAdapter);
      this.arrayAdapter.getFilter ().filter ("");

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
      listView.setOnLongClickListener (new View.OnLongClickListener () {
         @Override
         public boolean onLongClick(View view){
            return false;
         }
      });

      clearBtn.setOnClickListener (new View.OnClickListener () {
         @Override
         public void onClick(View view){
            slTextBox.setText ("");
         }
      });

      addBtn.setVisibility (View.VISIBLE);
      addBtn.setOnClickListener (new View.OnClickListener () {

         @Override
         public void onClick(View v){
//               Intent intent = new Intent (context, addEditActivityClass);

            loadActivity ();
         }
      });
   }
//
//   private Cursor filterList(CharSequence constraint){
//      this.constraint = constraint;
//      return dbHelper.getAllDataFiltered (tableToSearch,fieldToSearch, (String) constraint);
//   }

   private void updateAnClose(int position){
//      Cursor currentItem = (Cursor) this.arrayAdapter.getItem (position);
      textReturned = "";
//      keyReturned = currentItem.getInt (currentItem.getColumnIndex ("_id"));
      dismiss ();
   }

   private void filterAdapter(CharSequence s){
      this.arrayAdapter.getFilter ().filter (s.toString ());
      this.arrayAdapter.notifyDataSetChanged();
   }


   private void loadActivity(){
//      Intent intent = new Intent(activity, addEditActivityClass);
//      this.activity.startActivityForResult (intent, ADD_ITEM_TO_ARRAY_LIST_CODE);

      editDataDialog = new Dialog (context);
      editDataDialog.setContentView (R.layout.edit_item_list_dialog);

//      TextInputLayout til = editDataDialog.findViewById (R.id.til_main_field);
      TextInputEditText tiet;
      tiet = editDataDialog.findViewById (R.id.tiet_main_field);
      tiet.setHint (R.string.hallmark_type);
      tiet.setText ("");
      Button okBtn = editDataDialog.findViewById (R.id.btn_ok);
      Button cancelBtn = editDataDialog.findViewById (R.id.btn_cancel);
      cancelBtn.setOnClickListener (new View.OnClickListener () {
         @Override
         public void onClick(View view){
            editDataDialog.dismiss ();
         }
      });

      okBtn.setOnClickListener (new View.OnClickListener () {
         @Override
         public void onClick(View view){
            updateList(tiet);
            editDataDialog.dismiss ();
         }
      });


      editDataDialog.show ();

   }

   private void updateList(TextInputEditText tiet){
      String text = tiet.getText ().toString ();
      insertElement(text);
      listView.setAdapter (this.arrayAdapter);
   }

   private void insertElement(String text){
      new HallmarkType (context).add (text);
   }

   @Override
   protected void onStart(){
      super.onStart ();
   }

}
