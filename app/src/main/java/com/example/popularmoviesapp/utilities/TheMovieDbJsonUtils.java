package com.example.popularmoviesapp.utilities;

import com.example.popularmoviesapp.Movie;
import com.example.popularmoviesapp.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class TheMovieDbJsonUtils {
    private static final String TMDB_RESULTS = "results";
    private static final String TMDB_ID = "id";
    private static final String TMDB_TITLE = "title";
    private static final String TMDB_DATE = "release_date";
    private static final String TMDB_OVERVIEW = "overview";
    private static final String TMDB_POSTER_PATH = "poster_path";
    private static final String TMDB_VOTE_AVERAGE = "vote_average";
    private static final String TMDB_TRAILER_KEY = "key";
    private static final String TMDB_REVIEW_AUTHOR = "author";
    private static final String TMDB_REVIEW_CONTENT = "content";

    public static ArrayList<Movie> getMoviesFromJson(String moviesJsonString){
        ArrayList<Movie> listMovies = new ArrayList<Movie>();
        try {
            JSONObject mainJsonObject = new JSONObject(moviesJsonString);
            JSONArray movies = mainJsonObject.getJSONArray(TMDB_RESULTS);
            for(int i= 0; i<movies.length(); i++){
                int id = movies.getJSONObject(i).getInt(TMDB_ID);
                String title = movies.getJSONObject(i).getString(TMDB_TITLE);
                String date = movies.getJSONObject(i).getString(TMDB_DATE);
                String overview = movies.getJSONObject(i).getString(TMDB_OVERVIEW);
                String posterPath = movies.getJSONObject(i).getString(TMDB_POSTER_PATH);
                double voteAverage = movies.getJSONObject(i).getDouble(TMDB_VOTE_AVERAGE);
                listMovies.add(new Movie(id, title, date, overview, posterPath, voteAverage));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listMovies;
    }

    public static ArrayList<String> getTrailersFromJson(String trailersJsonString){
        ArrayList<String> listTrailers = new ArrayList<String>();
        try {
            JSONObject mainJsonObject = new JSONObject(trailersJsonString);
            JSONArray trailers = mainJsonObject.getJSONArray(TMDB_RESULTS);
            for(int i= 0; i<trailers.length(); i++){
                listTrailers.add(trailers.getJSONObject(i).getString(TMDB_TRAILER_KEY));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listTrailers;
    }

    public static ArrayList<Review> getReviewsFromJson(String reviewsJsonString){
        ArrayList<Review> listReviews = new ArrayList<Review>();
        try {
            JSONObject mainJsonObject = new JSONObject(reviewsJsonString);
            JSONArray reviews = mainJsonObject.getJSONArray(TMDB_RESULTS);
            for(int i= 0; i<reviews.length(); i++){
                Review review = new Review();
                review.setAuthor(reviews.getJSONObject(i).getString(TMDB_REVIEW_AUTHOR));
                review.setReview(reviews.getJSONObject(i).getString(TMDB_REVIEW_CONTENT));
                listReviews.add(review);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listReviews;
    }
}
