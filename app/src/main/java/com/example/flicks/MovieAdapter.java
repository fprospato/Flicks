package com.example.flicks;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    //instances
    ArrayList<Movie> movies; //list of movies
    Config config; //congif for image urls
    Context context;


    // initialize with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }


    //getter for config
    public Config getConfig() {
        return config;
    }


    //setter for config
    public void setConfig(Config config) {
        this.config = config;
    }


    //creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //get the context and create the inflator
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //create the view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false); //(item row layout, parent, attaching the root?)

        //return a new viewHolder
        return new ViewHolder(movieView);
    }


    //binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get movie data at the specific position
        Movie movie = movies.get(position);

        //populate view with movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        //determine orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;


        // build url for poster or backdrop image
        String imageUrl = (isPortrait) ? config.getImageUrl(config.getPosterSize(), movie.getPosterPath()) : config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());

        //get correct placeholder and imageView for current orientation
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? holder.ivPosterImage : holder.ivBackdropImage;

        //options for images
        RequestOptions glideOptions = new RequestOptions();
        glideOptions.placeholder(placeholderId) //place holder image
                .error(placeholderId)
                .transforms(new RoundedCorners(10)); //error image


        //load image using Glide
        Glide.with(context)
                .load(imageUrl)
                .apply(glideOptions)
                .into(imageView);
    }


    //returns the total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }


    //create the ViewHolder as a inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //track view objects
        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;


        //consturcutor
        public ViewHolder(View itemView) {
            super(itemView);

            //lookup view objects by id
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);

            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            // get item position
            int position = getAdapterPosition();

            // check for valid position (actually is in the view)
            if (position != RecyclerView.NO_POSITION) {

                // get movie from the position (won't work if the class is static)
                Movie movie = movies.get(position);

                // create intent for the new activity
                Intent intent = new Intent(context, MovieDetailsActivity.class);

                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));

                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
