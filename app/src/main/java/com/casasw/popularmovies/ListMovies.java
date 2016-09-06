package com.casasw.popularmovies;

import java.util.HashMap;

/**
 * Created by Junior on 01/09/2016.
 */
public class ListMovies {

    HashMap<Integer, Movie> movies;

    public ListMovies(HashMap<Integer, Movie> movies) {
        this.movies = movies;
    }

    public HashMap<Integer, Movie> getMovies() {
        return movies;
    }

    public void setMovies(HashMap<Integer, Movie> movies) {
        this.movies = movies;
    }
}
