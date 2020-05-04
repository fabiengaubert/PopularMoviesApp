package com.example.popularmoviesapp.utilities;

import android.net.Uri;

import com.example.popularmoviesapp.BuildConfig;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {
    private static final String KEY_PARAM = "api_key";
    //TODO the key needs to be changed here or in added to the gradle.config
    private static final String KEY_VALUE = BuildConfig.TMDB_API_KEY;
    private static final String BASE_URL_MOVIE_DB = "http://api.themoviedb.org/3/movie";
    private static final String BASE_URL_IMAGE = "http://image.tmdb.org/t/p/w185";
    private static final String POPULAR_MOVIES = "/popular";
    private static final String TOP_RATED = "/top_rated";
    private static final String TRAILERS = "/videos";
    private static final String REVIEWS = "/reviews";


    public static String getPopularMovies(){
        return getMovies(POPULAR_MOVIES);
    }

    public static String getTopVotedMovies(){
        return getMovies(TOP_RATED);
    }

    private static String getMovies(String sort){
        URL url = null;
        Uri.Builder builder = Uri.parse(BASE_URL_MOVIE_DB+sort).buildUpon();
        builder.appendQueryParameter(KEY_PARAM, KEY_VALUE);
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getResponseFromHttpUrl(url);
    }

    public static Uri getPosterUri(String posterPath){
        return Uri.parse(BASE_URL_IMAGE+posterPath);
    }

    public static String getTrailers(int movieId){
        URL url = null;
        Uri.Builder builder = Uri.parse(BASE_URL_MOVIE_DB+"/"+movieId+TRAILERS).buildUpon();
        builder.appendQueryParameter(KEY_PARAM, KEY_VALUE);
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getResponseFromHttpUrl(url);
    }

    public static String getReviews(int movieId){
        URL url = null;
        Uri.Builder builder = Uri.parse(BASE_URL_MOVIE_DB+"/"+movieId+REVIEWS).buildUpon();
        builder.appendQueryParameter(KEY_PARAM, KEY_VALUE);
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getResponseFromHttpUrl(url);
    }

    public static String getResponseFromHttpUrl(URL url){
        String result = "";
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput){
                result = scanner.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            httpURLConnection.disconnect();
        }
        return result;
    }
}
