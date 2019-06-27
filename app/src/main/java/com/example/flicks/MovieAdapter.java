package com.example.flicks;

import android.content.Context;
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

        // build url for poster image
        String imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());

        //options for images
        RequestOptions glideOptions = new RequestOptions();
        glideOptions.placeholder(R.drawable.flicks_movie_placeholder) //place holder image
                .error(R.drawable.flicks_movie_placeholder)
                .transforms(new RoundedCorners(10)); //error image


        //load image using Glide
        Glide.with(context)
                .load(imageUrl)
                .apply(glideOptions)
                .into(holder.ivPosterImage);
    }


    //returns the total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }


    //create the viewholder as a static inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        //track view objects
        ImageView ivPosterImage;
        TextView tvTitle;
        TextView tvOverview;


        //consturcutor
        public ViewHolder(View itemView) {
            super(itemView);

            //lookup view objects by id
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
        }
    }
}
