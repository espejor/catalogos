package com.example.catalogos.api_pictures;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PictureGoogle {
    @SerializedName("items")
    @Expose
    private ArrayList<Item> items;

    public ArrayList<Item> getItems(){
        return items;
    }

    public void setItems(ArrayList<Item> items){
        this.items = items;
    }

    public class Item{

        String link;

        ImageData image;
        public String getLink(){
            return link;
        }

        public void setLink(String link){
            this.link = link;
        }
        public ImageData getImage(){
            return image;
        }

        public void setImage(ImageData image){
            this.image = image;
        }


        public class ImageData {
            String thumbnailLink;

            public String getThumbnailLink(){
                return thumbnailLink;
            }

            public void setThumbnailLink(String thumbnailLink){
                this.thumbnailLink = thumbnailLink;
            }

        }
    }
}
