package com.example.flicks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flicks.models.Movie;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    //list of movies
    ArrayList<Movie> movies;

    // initialize with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    //creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //get the context and create the inflator
        Context context = parent.getContext();
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

        // TODO - set image using Glide
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
