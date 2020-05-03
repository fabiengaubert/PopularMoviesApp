package com.example.popularmoviesapp;

public class Review {
    private String mAuthor;
    private String mContent;

    public Review(){

    }
    public Review(String author, String content){
        mAuthor = author;
        mContent = content;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }
}
