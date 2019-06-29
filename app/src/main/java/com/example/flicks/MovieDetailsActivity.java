package com.example.flicks;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieDetailsActivity extends AppCompatActivity {

    //constants
    public final static String API_BASE_URL = "https://api.themoviedb.org/3"; //base url for api
    public final static String API_KEY_PARAM = "api_key"; //parameter name for API key
    public final static String TAG = "MovieDetailsActivity"; //tag for logging calls

    //instances
    AsyncHttpClient client;
    Movie movie;
    Config config;
    ArrayList<Movie> movies;

    // the view objects
    ImageView ivPosterImage;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    TextView rbNumber;
    RecyclerView rvMovies; //where we will display the movies
    MovieAdapter adapter; //adapter wired to recycler view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //initialize client
        client = new AsyncHttpClient();

        //initialize movies list
        movies = new ArrayList<>();

        // get the objects from the view
        ivPosterImage = (ImageView) findViewById(R.id.ivPosterImage);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        rbNumber = (TextView) findViewById(R.id.rbNumber);

        //get and unwrap the movie from Intent
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        config = (Config) Parcels.unwrap(getIntent().getParcelableExtra("config")); //config for image urls

        //resolve the recycler view and connect a layout manager
        adapter = new MovieAdapter(movies);
        adapter.setConfig(config);
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);

        //determine orientation
        boolean isPortrait = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (isPortrait) {
            rvMovies.setLayoutManager(new LinearLayoutManager(this));
        } else {
            rvMovies.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        }
        rvMovies.setAdapter(adapter);

        //log what we're showing
        Log.i("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        //set title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        //set rating number
        rbNumber.setText(voteAverage + "/5");

        //get the poster
        getPosterImage();

        //get movie recommendations
        getRecommendations();
    }


    //poster image
    private void getPosterImage() {

        // build url for poster or backdrop image
        String imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        String backdropImageUrl = config.getImageUrl(config.getPosterSize(), movie.getBackdropPath());

        //get placeholder image
        int placeholderId = R.drawable.flicks_movie_placeholder;
        int backdropPlaceholderId = R.drawable.flicks_backdrop_placeholder;

        //options for portrait images
        RequestOptions glideOptions = new RequestOptions();
        glideOptions.placeholder(placeholderId) //place holder image
                .error(placeholderId)
                .transforms(new RoundedCorners(10)); //error image

        //load portrait image using Glide
        Glide.with(this)
                .load(imageUrl)
                .apply(glideOptions)
                .into(ivPosterImage);
    }

    private void getRecommendations() {
        //create the URL
        String url = API_BASE_URL + "/movie/" + movie.getId() +"/recommendations";

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

                        //notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() - 1);
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
