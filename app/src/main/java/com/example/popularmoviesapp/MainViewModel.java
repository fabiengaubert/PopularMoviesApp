package com.example.popularmoviesapp;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.popularmoviesapp.utilities.NetworkUtils;
import com.example.popularmoviesapp.utilities.TheMovieDbJsonUtils;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String POPULAR_VALUE = "Popular";
    private static final String TOP_VOTE_VALUE = "Top vote";
    private static final String FAVOURITE_VALUE = "Favourite";

    private MutableLiveData<List<Movie>> mListMovies = new MutableLiveData<>();
    private AppDatabase appDatabase;
    private boolean hasConnection;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mListMovies.setValue(new ArrayList<Movie>());

        appDatabase=AppDatabase.getInstance(getApplication());
        retrieveMovies(POPULAR_VALUE);
    }

    public void retrieveMovies(String sort) {
        RetrieveMoviesTask retrieveMoviesTask = new RetrieveMoviesTask();
        retrieveMoviesTask.execute(sort);
    }

    public class RetrieveMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {
            List<Movie> listMovies=null;
            if(strings[0].equals(POPULAR_VALUE)) {
                String result = NetworkUtils.getPopularMovies();
                if(result!=null && result.length()!=0) {
                    listMovies = TheMovieDbJsonUtils.getMoviesFromJson(result);
                    for(Movie movie: listMovies){
                        movie.setIsFavourite(appDatabase.movieDao().getMovie(movie.getId())!=null);
                    }
                }
            }
            else if(strings[0].equals(TOP_VOTE_VALUE)){
                String result = NetworkUtils.getTopVotedMovies();
                if(result!=null && result.length()!=0) {
                    listMovies = TheMovieDbJsonUtils.getMoviesFromJson(result);
                    for(Movie movie: listMovies){
                        movie.setIsFavourite(appDatabase.movieDao().getMovie(movie.getId())!=null);
                    }
                }
            }
            else if(strings[0].equals(FAVOURITE_VALUE)){
                listMovies = new ArrayList<Movie>(appDatabase.movieDao().loadAllMovies());
                for(Movie movie: listMovies){
                    movie.setIsFavourite(true);
                }
            }
            return listMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> listMovies) {
            if(listMovies!=null) {
                mListMovies.setValue(listMovies);
                hasConnection = true;
            }
            else {
                hasConnection = false;
            }
        }
    }

    public LiveData<List<Movie>> getMovies(){
        return mListMovies;
    }

    public void addMovieToFavourites(Movie movie){
        appDatabase.movieDao().insertMovie(movie);

    }

    public void removeMovieFromFavourites(Movie movie){
        appDatabase.movieDao().deleteMovie(movie);
    }

}
