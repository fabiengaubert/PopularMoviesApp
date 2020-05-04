package com.example.popularmoviesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmoviesapp.utilities.NetworkUtils;
import com.example.popularmoviesapp.utilities.TheMovieDbJsonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    final private static int SIZE_THUMBNAIL = 420;
    private boolean hasConnection;
    private AppDatabase mDb;
    private Movie mMovie;
    private boolean mChangeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mChangeStatus = false;

        mDb = AppDatabase.getInstance(getApplicationContext());

        if(getIntent()!=null) {
            if (getIntent().hasExtra(getString(R.string.movie_object))) {
                mMovie = getIntent().getParcelableExtra(getString(R.string.movie_object));

                this.setTitle(mMovie.getTitle());

                ImageView posterView = findViewById(R.id.iv_image);

                if(mMovie.getPosterPath().equals(getString(R.string.null_string))){
                    Picasso.get()
                            .load(R.drawable.poster_not_available)
                            .resize(SIZE_THUMBNAIL, 0)
                            .into(posterView);
                }
                else{
                    Picasso.get().load(NetworkUtils.getPosterUri(mMovie.getPosterPath())).resize(SIZE_THUMBNAIL, 0).into(posterView);
                }

                TextView dateTextView = findViewById(R.id.tv_date);
                dateTextView.setText(mMovie.getDate());

                TextView voteTextView = findViewById(R.id.tv_vote);
                voteTextView.setText(mMovie.getVoteAverage() +"/10");

                TextView descriptionTextView = findViewById(R.id.tv_description);
                if(mMovie.getOverview().length()==0) {
                    descriptionTextView.setText(R.string.no_overview);
                }
                else{
                    descriptionTextView.setText(mMovie.getOverview());
                }
                populateTrailers(mMovie.getId());
                populateReviews(mMovie.getId());

                final ImageView imageView = findViewById(R.id.ib_star);
                if(mMovie.isFavourite()){
                    imageView.setColorFilter(getColor(R.color.yellow));
                }
                else {
                    imageView.setColorFilter(getColor(R.color.white));
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView imageView = findViewById(R.id.ib_star);
                        // we check if the movie exists already in the DB
                        //
                        //TODO we should create a fragment instead, to share the viewmodel between the two screens
                        //at least create new thread
                        //Detail Activity should not access the database directly
                        if(mMovie.isFavourite()){
                            mChangeStatus = !mChangeStatus;
                            mMovie.setIsFavourite(false);
                            imageView.setColorFilter(getColor(R.color.white));
                        }
                        else {
                            mChangeStatus = !mChangeStatus;
                            mMovie.setIsFavourite(true);
                            imageView.setColorFilter(getColor(R.color.yellow));
                        }
                    }
                });
            }
        }
        if(savedInstanceState!=null) {
            if (savedInstanceState.containsKey("mChangeStatus")) {
                mChangeStatus = savedInstanceState.getBoolean("mChangeStatus");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("mChangeStatus", mChangeStatus);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("change_favourite_status", mChangeStatus?"true":"false");
        setResult(RESULT_OK, intent);
        super.finish();
    }

    public void populateTrailers(int movieId){
        PopulateTrailersTask populateTrailersTask = new PopulateTrailersTask();
        populateTrailersTask.execute(movieId);
    }

    public void populateReviews(int movieId){
        PopulateReviewsTask populateReviewsTask = new PopulateReviewsTask();
        populateReviewsTask.execute(movieId);
    }

    public class PopulateTrailersTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... ints) {
            return NetworkUtils.getTrailers(ints[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if(s!=null && s.length()!=0) {
                ArrayList<String> listTrailers = TheMovieDbJsonUtils.getTrailersFromJson(s);
                //TODO update UI
                //posterAdapter.setMoviesData(listMovies);
                hasConnection = true;
            }
            else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                hasConnection = false;
            }
        }
    }

    public class PopulateReviewsTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... ints) {
            return NetworkUtils.getReviews(ints[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if(s!=null && s.length()!=0) {
                ArrayList<Review> listReviews = TheMovieDbJsonUtils.getReviewsFromJson(s);
                //TODO update UI
                //posterAdapter.setMoviesData(listMovies);
                hasConnection = true;
            }
            else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                hasConnection = false;
            }
        }
    }

}
