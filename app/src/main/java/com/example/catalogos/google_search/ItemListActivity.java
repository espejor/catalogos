package com.example.catalogos.google_search;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catalogos.BuildConfig;
import com.example.catalogos.R;
import com.example.catalogos.api_pictures.PictureGoogle;
import com.example.catalogos.services.GoogleAPIService;
import com.example.catalogos.services.ImageSaver;
import com.example.catalogos.xyz.App;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    private static final int NUM_OF_PAGES = 1;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static String TEXT_TO_SEARCH = "text_to_search";
    public String textToSearch;
    ArrayList<PictureGoogle.Item> pictureList = new ArrayList<> ();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);
        toolbar.setTitle (getTitle ());

        textToSearch = getIntent().getStringExtra(TEXT_TO_SEARCH);

//        FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab);
//        fab.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View view){
//                Snackbar.make (view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction ("Action", null).show ();
//            }
//        });

        if (findViewById (R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById (R.id.item_list_);
        assert recyclerView != null;
        setupRecyclerView ((RecyclerView) recyclerView,new ArrayList<> ());

        String uri = "https://customsearch.googleapis.com/customsearch/v1/";
        String googleKey = BuildConfig.googleKey;
        String cx = "5e6330eb047b792d2";
        String imgType = "photo";
        String searchType = "image";
        String fileType = "jpg png jpeg bmp";
        String imgSize = "MEDIUM";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(uri)
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder ().serializeNulls().create()
                ))
                .build();
        GoogleAPIService googleAPIService = retrofit.create(GoogleAPIService.class);

        // get All results <= 100 items
        for (int start=0;start<NUM_OF_PAGES;start++){
            Call<PictureGoogle> call = googleAPIService
                    .getPictures (textToSearch, googleKey, cx,
//                        imgType,
                            searchType,
                            fileType,
                            imgSize,
                            start);
            call.enqueue (new Callback<PictureGoogle> () {
                @Override
                public void onResponse(Call<PictureGoogle> call, Response<PictureGoogle> response){
                    if (response.isSuccessful ()) {
                        assert response.body () != null;
                        ArrayList<PictureGoogle.Item> list = response.body ().getItems ();
                        pictureList.addAll (filterImageLinks (list));
                        View recyclerView = findViewById (R.id.item_list_);
                        assert recyclerView != null;
                        setupRecyclerView ((RecyclerView) recyclerView, pictureList);
                    } else {
                        Log.d ("Error", "");
                        return;
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t){
                    Log.d ("Error", t.toString ());
                }
            });
        }
    }

    private ArrayList<PictureGoogle.Item> filterImageLinks(ArrayList<PictureGoogle.Item> pictureList){
        Set<String> values = new HashSet<> (Arrays.asList("jpg","png","bmp","jpeg"));
        ArrayList<PictureGoogle.Item> newList = new ArrayList<> ();
        for (int i=0;i< pictureList.size ();i++){
            PictureGoogle.Item item = pictureList.get (i);
            String ext = FilenameUtils.getExtension (item.getLink ());
            if ( values.contains (ext)){
                newList.add (item);
            }
        }
        return newList;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, ArrayList<PictureGoogle.Item> pictureList){
        recyclerView.setAdapter (new SimpleItemRecyclerViewAdapter (this, pictureList, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<PictureGoogle.Item> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener () {

            @Override
            public void onClick(View view){

                int index = (int) view.getTag();
                PictureGoogle.Item item = mValues.get(index);
                Intent i = new Intent ();
                i.putExtra ("fileName",mParentActivity.textToSearch);
                i.putExtra ("link",item.getLink ());

                mParentActivity.setResult (RESULT_OK,i);
                mParentActivity.finish ();

            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List items,
                                      boolean twoPane){
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from (parent.getContext ())
                    .inflate (R.layout.item_list_content, parent, false);
//            this.viewHolder = new ViewHolder (view);
            return new ViewHolder (view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position){

//            holder.mImageView.setImageURI (Uri.parse (mValues.get(position).getLink ()));
            new SetThumbnailFromWeb (holder).execute (position);

//            setImageURI (Uri.parse (mValues.get(position).getLink ()));

            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        private class SetThumbnailFromWeb extends AsyncTask<Integer, Integer, Bitmap>{
            ImageSaver imageSaver = new ImageSaver (App.getContext());

            ViewHolder viewHolder;
            public SetThumbnailFromWeb(ViewHolder holder){
                viewHolder = holder;
            }

            @Override
            protected Bitmap doInBackground(Integer... position){
                return  imageSaver.makeBitmap (mValues.get(position[0].intValue ()).getImage ().getThumbnailLink ());
            }

            @Override
            protected void onPostExecute(Bitmap bitmap){
                super.onPostExecute (bitmap);
                this.viewHolder.getmImageView ().setImageBitmap (bitmap);
            }
        }


        @Override
        public int getItemCount(){
            return mValues.size ();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView mImageView;

            ViewHolder(View view){
                super (view);

                mImageView = view.findViewById (R.id.imageInList);
            }
            public ImageView getmImageView(){
                return mImageView;
            }

        }
    }
}