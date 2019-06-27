package com.example.flicks;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    //constants
    public final static String API_BASE_URL = "https://api.themoviedb.org/3"; //base url for api
    public final static String API_KEY_PARAM = "api_key"; //parameter name for API key
    public final static String TAG = "MovieListActivity"; //tag for logging calls


    //instance fields
    AsyncHttpClient client;
    String imageBaseURL; //base url for loading images
    String posterSize; //poster size to user when fetching images
    ArrayList<Movie> movies; //list of currently playing movies


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        //initialize client
        client = new AsyncHttpClient();

        //initialize movies list
        movies = new ArrayList<>();

        //get congfiguration method
        getConfiguration();
    }


    //get list of current movies
    private void getNowPlaying() {
        //create the URL
        String url = API_BASE_URL + "/movie/now_playing";

        //set request parameters (they're appended to the url)
        RequestParams params  = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); //API key is always required

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                //load results into movies
                try {
                    //get the results array
                    JSONArray results = response.getJSONArray("results");

                    //add all the movies to the array
                    for (int i = 0; i < results.length(); i++) {
                        //get movie and append to array
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                    }

                    //log that we added a movie
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));

                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //show error
                logError("Failed to get data from now playing endpoint", throwable, true);
            }
        });

    }


    //get the configuration from the API
    private void getConfiguration() {
        //create the URL
        String url = API_BASE_URL + "/configuration";

        //set request parameters (they're appended to the url)
        RequestParams params  = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); //API key is always required

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

                    //log that it worked
                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s", imageBaseURL, posterSize));

                    //get the now playing movies
                    getNowPlaying();
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
