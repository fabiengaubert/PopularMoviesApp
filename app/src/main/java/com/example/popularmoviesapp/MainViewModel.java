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
    private static String lastRequest;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mListMovies.postValue(new ArrayList<Movie>());

        appDatabase=AppDatabase.getInstance(getApplication());
        retrieveMovies(POPULAR_VALUE);
    }

    public LiveData<List<Movie>> getMovies(){
        return mListMovies;
    }

    public void retrieveMovies(String sort) {
        RetrieveMoviesTask retrieveMoviesTask = new RetrieveMoviesTask();
        retrieveMoviesTask.execute(sort);
        lastRequest = sort;
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
                mListMovies.postValue(listMovies);
                //retrieveTrailers(listMovies);
            }
        }
    }

    public void addMovieToFavourites(Movie movie){
        AddMovieToFavouritesTask addMovieToFavouritesTask = new AddMovieToFavouritesTask();
        addMovieToFavouritesTask.execute(movie);
    }

    public class AddMovieToFavouritesTask extends AsyncTask<Movie, Void, Movie>{

        @Override
        protected Movie doInBackground(Movie... movies) {
            appDatabase.movieDao().insertMovie(movies[0]);
            return movies[0];
        }

        @Override
        protected void onPostExecute(Movie movie) {
            for(int i=0; i<mListMovies.getValue().size(); i++){
                if(mListMovies.getValue().get(i).equals(movie)) {
                    mListMovies.getValue().get(i).setIsFavourite(true);
                    mListMovies.postValue(mListMovies.getValue());
                    return;
                }
            }
        }
    }

    public void removeMovieFromFavourites(Movie movie){
        RemoveMovieFromFavouritesTask removeMovieFromFavouritesTask = new RemoveMovieFromFavouritesTask();
        removeMovieFromFavouritesTask.execute(movie);
    }

    public class RemoveMovieFromFavouritesTask extends AsyncTask<Movie, Void, Movie>{

        @Override
        protected Movie doInBackground(Movie... movies) {
            appDatabase.movieDao().deleteMovie(movies[0]);
            return movies[0];
        }

        @Override
        protected void onPostExecute(Movie movie) {
            if(lastRequest.equals(FAVOURITE_VALUE)){
                for(int i=0; i<mListMovies.getValue().size(); i++){
                    if(mListMovies.getValue().get(i).equals(movie)){
                        mListMovies.getValue().remove(i);
                        mListMovies.postValue(mListMovies.getValue());
                        return;
                    }
                }
            }
            else{
                for(int i=0; i<mListMovies.getValue().size(); i++){
                    if(mListMovies.getValue().get(i).equals(movie)) {
                        mListMovies.getValue().get(i).setIsFavourite(false);
                        mListMovies.postValue(mListMovies.getValue());
                        return;
                    }
                }
            }
        }
    }

    public void retrieveTrailers(List<Movie> listMovies){
        RetrieveTrailersTask retrieveTrailersTask = new RetrieveTrailersTask();
        retrieveTrailersTask.execute(new ArrayList<Movie>(listMovies));
    }

    public void retrieveReviews(List<Movie> listMovies){
        RetrieveReviewsTask retrieveReviewsTask = new RetrieveReviewsTask();
        retrieveReviewsTask.execute(new ArrayList<Movie>(listMovies));
    }

    public class RetrieveTrailersTask extends AsyncTask<ArrayList<Movie>, Void, ArrayList<ArrayList<String>>> {

        @Override
        protected ArrayList<ArrayList<String>> doInBackground(ArrayList<Movie>... movieList) {
            ArrayList<ArrayList<String>> listTrailersAllMovies = new ArrayList<>();
            for(Movie movie:movieList[0]){
                listTrailersAllMovies.add(TheMovieDbJsonUtils.getTrailersFromJson(NetworkUtils.getTrailers(movie.getId())));
            }
            return listTrailersAllMovies;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> listTrailersAllMovies) {
            if(listTrailersAllMovies!=null && listTrailersAllMovies.size()!=0) {
                for(int i = 0; i<listTrailersAllMovies.size(); i++) {
                    mListMovies.getValue().get(i).setTrailersPaths(listTrailersAllMovies.get(i));
                }
                mListMovies.postValue(mListMovies.getValue());
            }
        }
    }

    public class RetrieveReviewsTask extends AsyncTask<ArrayList<Movie>, Void, ArrayList<ArrayList<Review>>> {

        @Override
        protected ArrayList<ArrayList<Review>> doInBackground(ArrayList<Movie>... movieList) {
            ArrayList<ArrayList<Review>> listReviewsAllMovies = new ArrayList<>();
            for(Movie movie:movieList[0]){
                listReviewsAllMovies.add(TheMovieDbJsonUtils.getReviewsFromJson(NetworkUtils.getReviews(movie.getId())));
            }
            return listReviewsAllMovies;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<Review>> listReviewsAllMovies) {
            if(listReviewsAllMovies!=null && listReviewsAllMovies.size()!=0) {
                for(int i = 0; i<listReviewsAllMovies.size(); i++) {
                    mListMovies.getValue().get(i).setReviews(listReviewsAllMovies.get(i));
                }
                mListMovies.postValue(mListMovies.getValue());
            }
        }
    }


}
