package com.example.searchmoviesapi.View
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.searchmoviesapi.ViewModel.MovieViewModel
import com.example.searchmoviesapi.R

// MainActivity serves as the entry point for the app where users can search for movies
class MainActivity : AppCompatActivity() {

    // Declares a variable for MovieViewModel, which will be used to interact with the data layer
    private lateinit var viewModel: MovieViewModel

    // Declares a variable for the RecyclerView Adapter that will handle the display of movie items
    private lateinit var adapter: MovieAdapter

    // onCreate method is called when the activity is first created or recreated after a configuration change (e.g., screen rotation)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view to use the activity_main layout file
        setContentView(R.layout.activity_main)

        // Find the RecyclerView from the layout using its ID and store a reference to it
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        // Set the layout manager for the RecyclerView to LinearLayoutManager, which arranges items in a vertical list
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Find the EditText where the user will input their search query (movie title)
        val searchEditText: EditText = findViewById(R.id.searchEditText)
        // Find the ImageView that acts as the search button (a clickable image)
        val searchButton: ImageView = findViewById(R.id.searchButton)

        // Initialize the ViewModel using ViewModelProvider to get an instance of MovieViewModel
        // ViewModel will survive configuration changes (e.g., screen rotation) and retain data
        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        // Observe changes in the LiveData object 'movies' from MovieViewModel
        // This ensures that whenever 'movies' LiveData is updated, the observer updates the UI (RecyclerView) accordingly
        viewModel.movies.observe(this, Observer { movies ->
            // Initialize a new instance of MovieAdapter, passing in the list of movies fetched from the ViewModel
            adapter = MovieAdapter(movies)
            // Set the RecyclerView adapter to the newly created adapter, which will handle displaying the movie list
            recyclerView.adapter = adapter
        })

        // Set an OnClickListener for the search button so that when the user clicks it, the app performs a search
        searchButton.setOnClickListener {
            // Extract the user's search query from the EditText as a string
            val query = searchEditText.text.toString()

            // Check if the query is not empty to prevent blank searches
            if (query.isNotEmpty()) {
                // Use the ViewModel to trigger a movie search based on the user's query
                // The ViewModel will handle the API request and update the LiveData with the results
                viewModel.fetchMovies(query)
            } else {
                // Optionally, show a message to the user to input a search query (e.g., using a Toast)
                // Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
