package com.example.popularmoviesapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {

    private int mImageWidth;
    private ArrayList<Movie> mMovies;
    final private ItemClickListener mOnItemClickListener;

    public interface ItemClickListener{
        void onItemClick(Movie clickedMovie);
    }

    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        public PosterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rv_posters_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnItemClickListener.onItemClick(mMovies.get(clickedPosition));
        }
    }

    PosterAdapter(ItemClickListener itemClickListener, int imageWidth) {
        mMovies = new ArrayList<Movie>();
        mOnItemClickListener = itemClickListener;
        mImageWidth = imageWidth;
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.poster_list_item, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterAdapter.PosterViewHolder holder, int position) {
        // if there is no image we still need to set the size of the ImageView
        if(mMovies.get(position).getPosterPath().equals("null")){
            Picasso.get()
                    .load(R.drawable.poster_not_available)
                    .resize(mImageWidth, 0)
                    .into(holder.imageView);
        }
        else {
            Picasso.get()
                    .load(NetworkUtils.getPosterUri(mMovies.get(position).getPosterPath()))
                    .resize(mImageWidth, 0)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mMovies==null){
            return 0;
        }
        return mMovies.size();
    }

    public void setMoviesData(ArrayList<Movie> newMoviesList){
        mMovies = newMoviesList;
        notifyDataSetChanged();
    }

    public Movie getMovie(int position){
        return mMovies.get(position);
    }

    public ArrayList<Movie> getMovieList(){
        return mMovies;
    }

    public void setImageWidth(int imageWidth) {
        mImageWidth = imageWidth;
    }

}
