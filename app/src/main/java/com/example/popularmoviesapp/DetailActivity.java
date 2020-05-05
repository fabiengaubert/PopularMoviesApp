package com.example.popularmoviesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    final private static String SAVED_INSTANCE_CHANGED_STATUS = "mChangeStatus";
    final private static int SIZE_THUMBNAIL = 420;
    private Movie mMovie;
    private boolean mChangeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mChangeStatus = false;

        if(getIntent()!=null) {
            if (getIntent().hasExtra(getString(R.string.movie_object))) {
                mMovie = getIntent().getParcelableExtra(getString(R.string.movie_object));

                if(mMovie.getTitle()!=null){
                    this.setTitle(mMovie.getTitle());
                }

                ImageView posterView = findViewById(R.id.iv_image);

                if(mMovie.getPosterPath().equals(getString(R.string.null_string))){
                    Picasso.get()
                            .load(R.drawable.poster_not_available)
                            .resize(SIZE_THUMBNAIL, 0)
                            .into(posterView);
                }
                else{
                    Picasso.get()
                            .load(NetworkUtils.getPosterUri(mMovie.getPosterPath()))
                            .resize(SIZE_THUMBNAIL, 0)
                            .into(posterView);
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

                final ImageView imageView = findViewById(R.id.ib_star);
                if(mMovie.isFavourite()){
                    imageView.setColorFilter(getColor(R.color.yellow));
                }
                else {
                    imageView.setColorFilter(getColor(R.color.white));
                }

                // add Trailers and Reviews to the LinearLayout programmatically
                //from data received in the intent
                populateTrailers();
                populateReviews();

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView imageView = findViewById(R.id.ib_star);
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
            if (savedInstanceState.containsKey(SAVED_INSTANCE_CHANGED_STATUS)) {
                mChangeStatus = savedInstanceState.getBoolean(SAVED_INSTANCE_CHANGED_STATUS);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(SAVED_INSTANCE_CHANGED_STATUS, mChangeStatus);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.change_favourite_status), mChangeStatus?"true":"false");
        setResult(RESULT_OK, intent);
        super.finish();
    }

    private void populateTrailers() {
        ArrayList<String> mTrailers = mMovie.getTrailersPaths();
        LinearLayout mainLinearLayout = findViewById(R.id.ly_detail);

        addHorizontalLine();

        View trailerLabelView = getLayoutInflater().inflate(R.layout.detail_title_label, mainLinearLayout, false);
        TextView trailerLabelTextView = trailerLabelView.findViewById(R.id.tv_detail_title_label);
        trailerLabelTextView.setText(getString(R.string.trailers_label));
        mainLinearLayout.addView(trailerLabelView);

        if(mTrailers==null||mTrailers.size()==0){
            View trailerItem = getLayoutInflater().inflate(R.layout.trailer_item, mainLinearLayout, false);
            TextView titleTrailerTextView = trailerItem.findViewById(R.id.tv_trailer_title);
            titleTrailerTextView.setText(getString(R.string.no_trailer_available));
            titleTrailerTextView.setVisibility(View.VISIBLE);
            // we re-use a standard trailer item to warn user that there are no trailers
            // so we have to remove the play button
            ImageButton playTrailerButton = trailerItem.findViewById(R.id.ib_play);
            playTrailerButton.setVisibility(View.GONE);
            mainLinearLayout.addView(trailerItem);
            addHorizontalLine();
        }
        else {
            if (mTrailers != null) {
                for (int i=0; i<mTrailers.size(); i++) {
                    View trailerItem = getLayoutInflater().inflate(R.layout.trailer_item, mainLinearLayout, false);
                    TextView titleTrailerTextView = trailerItem.findViewById(R.id.tv_trailer_title);
                    titleTrailerTextView.setText(getString(R.string.trailer_label)+(i+1));
                    titleTrailerTextView.setVisibility(View.VISIBLE);

                    ImageButton playTrailerButton = trailerItem.findViewById(R.id.ib_play);
                    playTrailerButton.setTag(i);
                    mainLinearLayout.addView(trailerItem);
                    addHorizontalLine();
                }
            }
        }
    }

    private void populateReviews() {
        ArrayList<Review> mReviews = mMovie.getReviews();
        LinearLayout mainLinearLayout = findViewById(R.id.ly_detail);

        View reviewLabelView = getLayoutInflater().inflate(R.layout.detail_title_label, mainLinearLayout, false);
        TextView reviewLabelTextView = reviewLabelView.findViewById(R.id.tv_detail_title_label);
        reviewLabelTextView.setText(R.string.reviews_label);
        mainLinearLayout.addView(reviewLabelView);

        if(mReviews==null||mReviews.size()==0){
                View reviewItem = getLayoutInflater().inflate(R.layout.review_item, mainLinearLayout, false);
                TextView titleReviewTextView = reviewItem.findViewById(R.id.tv_review_title);
                titleReviewTextView.setText(R.string.no_review);
                reviewItem.setVisibility(View.VISIBLE);
                mainLinearLayout.addView(reviewItem);
        }
        else {
            if (mReviews != null) {
                for (Review review : mReviews) {
                    View reviewItem = getLayoutInflater().inflate(R.layout.review_item, mainLinearLayout, false);
                    TextView titleReviewTextView = reviewItem.findViewById(R.id.tv_review_title);
                    TextView reviewTextView = reviewItem.findViewById(R.id.tv_review);
                    titleReviewTextView.setText(review.getAuthor());
                    reviewTextView.setText(review.getReview());
                    reviewItem.setVisibility(View.VISIBLE);
                    mainLinearLayout.addView(reviewItem);
                    addHorizontalLine();
                }
            }
        }
    }

    private void addHorizontalLine(){
        LinearLayout mainLinearLayout = findViewById(R.id.ly_detail);
        View horizontalLine = new View(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.horizontal_line_height));
        layoutParams.setMargins(
                getResources().getDimensionPixelSize(R.dimen.horizontal_line_horizontal_padding),
                getResources().getDimensionPixelSize(R.dimen.horizontal_line_vertical_padding),
                getResources().getDimensionPixelSize(R.dimen.horizontal_line_horizontal_padding),
                getResources().getDimensionPixelSize(R.dimen.horizontal_line_vertical_padding));
        horizontalLine.setLayoutParams(layoutParams);
        horizontalLine.setBackgroundColor(getColor(R.color.white));
        mainLinearLayout.addView(horizontalLine);
    }

    public void onClickPlayTrailer(View view){
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                NetworkUtils.getTrailerUri(mMovie.getTrailersPaths().get((int)view.getTag())));
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }
}
