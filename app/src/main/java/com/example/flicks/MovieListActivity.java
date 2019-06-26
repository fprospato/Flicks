package com.example.flicks;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    //constants
    public final static String API_BASE_URL = "https://api.themoviedb.org/3"; //base url for api
    public final static String API_KEY_PARAM = "api_key"; //parameter name for API key
    public final static String API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed"; //API key -- TODO move to secure location
    public final static String TAG = "MovieListActivity"; //tag for logging calls


    //instance fields
    AsyncHttpClient client;
    String imageBaseURL; //base url for loading images
    String posterSize; //poster size to user when fetching images


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        //initialize client
        client = new AsyncHttpClient();

        //get congfiguration method
        getConfiguration();
    }


    //get the configuration from the API
    private void getConfiguration() {
        //create the URL
        String url = API_BASE_URL + "/configuration";

        //set request parameters (they're appended to the url)
        RequestParams params  = new RequestParams();
        params.put(API_KEY_PARAM, API_KEY); //API key is always required

        //execute GET request (expecting JSON reponse)
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                //try to get the poster
                try {
                    //get images data
                    JSONObject images = response.getJSONObject("images");

                    //get the image base url
                    imageBaseURL = images.getString("secure_base_url"); //get string based on the identifier

                    //get the poster size
                    JSONArray posterSizeOptions = images.getJSONArray("poster_sizes"); //parse the value as an array

                    //get the item at index 3 or use w342 as fallback
                    posterSize = posterSizeOptions.optString(3, "w342"); //will try to get 3 item in index first, if does not work use w342


                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                //show error to user
                logError("Failed getting configuration", throwable, true);
            }
        });
    }


    //handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        //always log the error
        Log.e(TAG, message, error);

        //alert the user to avoid silent errors
        if (alertUser) {
            //show a toast to alert the user
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
