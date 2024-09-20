package com.example.searchmoviesapi.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Model.Movie
import com.example.Model.MovieDetailsResponse
import com.example.Model.RetrofitInstance
import kotlinx.coroutines.launch

// ViewModel class responsible for managing and providing movie data to the UI
class MovieViewModel : ViewModel() {

    // Backing property for the mutable list of movies.
    // This MutableLiveData holds the current list of movies and can be updated within the ViewModel.
    private val _movies = MutableLiveData<List<Movie>>()

    // Exposed immutable LiveData to observe changes to the movie list.
    // This LiveData is safe to expose to the UI layer as it does not allow modifications.
    val movies: LiveData<List<Movie>> = _movies

    // Backing property for mutable movie details.
    // This MutableLiveData holds details of a specific movie and can be updated within the ViewModel.
    private val _movieDetails = MutableLiveData<MovieDetailsResponse>()

    // Exposed immutable LiveData to observe changes to movie details.
    // This LiveData provides the movie details to the UI layer without allowing modifications.
    val movieDetails: LiveData<MovieDetailsResponse> get() = _movieDetails

    // Function to fetch a list of movies from the API based on the search query.
    fun fetchMovies(query: String) {
        // Launch a coroutine in the ViewModel's coroutine scope.
        // Using viewModelScope ensures that the coroutine is automatically canceled when the ViewModel is destroyed.
        viewModelScope.launch {
            try {
                // Make a network call to get the movies matching the query.
                // RetrofitInstance.api.getMovies(query) is a placeholder for the actual API call.
                val response = RetrofitInstance.api.getMovies(query)
                // Update the LiveData with the list of movies retrieved from the API response.
                _movies.value = response.movies
            } catch (e: Exception) {
                // Print the stack trace to log any errors that occur during the network call.
                // This helps in debugging issues with network requests.
                e.printStackTrace()
                Log.d("MovieViewModel", "Error fetching movies: ${e.message}")
            }
        }
    }

    // Function to fetch detailed information for a specific movie based on its IMDb ID.
    fun fetchMovieDetails(imdbId: String) {
        // Launch a coroutine in the ViewModel's coroutine scope.
        viewModelScope.launch {
            try {
                // Make a network call to get detailed information for the movie with the given IMDb ID.
                // RetrofitInstance.api.getMovieDetails(imdbId) is a placeholder for the actual API call.
                val response = RetrofitInstance.api.getMovieDetails(imdbId)
                // Update the LiveData with the detailed movie information retrieved from the API response.
                _movieDetails.value = response
            } catch (e: Exception) {
                // Handle any errors that occur during the network call by printing the stack trace.
                // This helps in identifying and fixing issues related to API requests.
                e.printStackTrace()
                Log.d("MovieViewModel", "Error fetching movie details: ${e.message}")
            }
        }
    }
}
