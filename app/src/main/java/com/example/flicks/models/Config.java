package com.example.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Config {

    //instance fields
    String imageBaseURL; //base url for loading images
    String posterSize; //poster size to user when fetching images
    String backdropSize; //back drop size

    // no-arg, empty constructor required for Parceler
    public Config() {}

    public Config(JSONObject object) throws JSONException {
        //get images data
        JSONObject images = object.getJSONObject("images");

        //get the image base url
        imageBaseURL = images.getString("secure_base_url"); //get string based on the identifier

        //get the poster size
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes"); //parse the value as an array

        //get the item at index 3 or use w342 as fallback
        posterSize = posterSizeOptions.optString(3, "w342"); //will try to get 3 item in index first, if does not work use w342

        //parse the backdrop sizes and use the optiona t index 1 or w780 as a fallback
        JSONArray backdropSizesOptions= images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizesOptions.optString(1, "w780");
    }


    //helper method for creating urls
    public String getImageUrl(String size, String path) {
        return String.format("%s%s%s", imageBaseURL, size, path); //concatenate all three
    }

    public String getImageBaseURL() {
        return imageBaseURL;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
