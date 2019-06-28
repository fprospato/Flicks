package com.example.flicks;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;

import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {

    //instances
    Movie movie;
    Config config;

    // the view objects
    ImageView ivPosterImage;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    TextView rbNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // get the objects from the view
        ivPosterImage = (ImageView) findViewById(R.id.ivPosterImage);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        rbNumber = (TextView) findViewById(R.id.rbNumber);

        //get and unwrap the movie from Intent
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        config = (Config) Parcels.unwrap(getIntent().getParcelableExtra("config")); //config for image urls

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
    }


    //poster image
    private void getPosterImage() {

        // build url for poster or backdrop image
        String imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());

        //get placeholder image
        int placeholderId = R.drawable.flicks_movie_placeholder;

        //options for images
        RequestOptions glideOptions = new RequestOptions();
        glideOptions.placeholder(placeholderId) //place holder image
                .error(placeholderId)
                .transforms(new RoundedCorners(10)); //error image

        //load image using Glide
        Glide.with(this)
                .load(imageUrl)
                .apply(glideOptions)
                .into(ivPosterImage);
    }
}
