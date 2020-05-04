package com.example.popularmoviesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PosterAdapter.ItemClickListener, AdapterView.OnItemSelectedListener {
    private static final int RECOMMENDED_IMAGE_SIZE = 500;
    private static final String POPULAR_VALUE = "Popular";
    private static final String TOP_VOTE_VALUE = "Top vote";
    private static final String FAVOURITE_VALUE = "Favourite";
    private static final int POPULAR_POSITION = 0;
    private static final int TOP_VOTE_POSITION = 1;
    private static final int FAVOURITE_POSITION = 2;
    private static final int DETAIL_ACTIVITY_CODE = 1;

    private static String currentSortState = POPULAR_VALUE;
    private PosterAdapter posterAdapter;
    private RecyclerView recyclerView;
    private boolean hasConnection;
    private MainViewModel mainViewModel;
    private Movie lastVisitedMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);


        recyclerView = findViewById(R.id.rv_posters);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getGridLayoutColumnCount());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        posterAdapter = new PosterAdapter(this, getPosterWidth());
        recyclerView.setAdapter(posterAdapter);

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(getString(R.string.current_sort_state))){
                currentSortState = savedInstanceState.getString(getString(R.string.current_sort_state));
            }
            if(savedInstanceState.containsKey("lastVisitedMovie")){
                lastVisitedMovie = savedInstanceState.getParcelable("lastVisitedMovie");
            }
            /**
            if(savedInstanceState.containsKey(getString(R.string.movies_key))){
                posterAdapter.setMoviesData(savedInstanceState.<Movie>getParcelableArrayList(getString(R.string.movies_key)));
            }

            // if there was no internet, we try again
            if(!hasConnection){
                mainViewModel.retrieveMovies(currentSortState);
            }**/
        }
        else{
            //mainViewModel.retrieveMovies(currentSortState);
        }
        mainViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                posterAdapter.setMoviesData(new ArrayList<Movie>(movies));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if(!posterAdapter.getMovieList().isEmpty()){
            outState.putParcelableArrayList(getString(R.string.movies_key), posterAdapter.getMovieList());
        }
        outState.putString(getString(R.string.current_sort_state), currentSortState);
        outState.putParcelable("lastVisitedMovie", lastVisitedMovie);
        super.onSaveInstanceState(outState);
    }



    // FOR GRIDLAYOUT ORGANISATION
    public int getGridLayoutColumnCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels;
        return Math.round(screenWidth / RECOMMENDED_IMAGE_SIZE);
    }

    public int getPosterWidth(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels;
        return Math.round(screenWidth / getGridLayoutColumnCount());
    }

    // OPTION MENU WITH SPINNER
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.spinner);

        Spinner spinner = (Spinner) menuItem.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, R.layout.spinner_custom_item);
        adapter.setDropDownViewResource(R.layout.spinner_custom_item);

        spinner.setAdapter(adapter);
        if(currentSortState.equals(POPULAR_VALUE)){
            spinner.setSelection(POPULAR_POSITION, false);
        }
        if(currentSortState.equals(TOP_VOTE_VALUE)){
            spinner.setSelection(TOP_VOTE_POSITION, false);
        }
        if(currentSortState.equals(FAVOURITE_VALUE)){
            spinner.setSelection(FAVOURITE_POSITION, false);
        }

        spinner.setOnItemSelectedListener(this);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(!parent.getItemAtPosition(position).toString().equals(currentSortState)){
            mainViewModel.retrieveMovies(parent.getItemAtPosition(position).toString());
            currentSortState = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // GO TO DETAIL ACTIVITY
    @Override
    public void onItemClick(Movie clickedMovie) {
        lastVisitedMovie = clickedMovie;
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(getString(R.string.movie_object), clickedMovie);
        startActivityForResult(intent, DETAIL_ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==DETAIL_ACTIVITY_CODE){
                if(data!=null){
                    if(data.getStringExtra("change_favourite_status").equals("true")){
                        if(lastVisitedMovie.isFavourite()){
                            mainViewModel.removeMovieFromFavourites(lastVisitedMovie);
                        }
                        else{
                            mainViewModel.addMovieToFavourites(lastVisitedMovie);
                        }
                        mainViewModel.retrieveMovies(currentSortState);
                    }
                }
            }
        }
    }
}
