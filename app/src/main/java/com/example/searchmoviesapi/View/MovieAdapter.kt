package com.example.searchmoviesapi.View

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Model.Movie
import com.example.searchmoviesapi.R
import com.squareup.picasso.Picasso

// Adapter class for the RecyclerView to display a list of movies
class MovieAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    // ViewHolder class to hold and manage the views for each item in the RecyclerView
    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextView to display the movie title
        private val titleTextView: TextView = itemView.findViewById(R.id.movieTitle)
        // TextView to display the IMDb ID
        private val imdbTextView: TextView = itemView.findViewById(R.id.movieImdbId)
        // ImageView to display the movie poster
        private val posterImageView: ImageView = itemView.findViewById(R.id.moviePoster)

        // Method to bind the movie data to the views
        fun bind(movie: Movie) {
            // Set the movie title in the titleTextView
            titleTextView.text = movie.title
            // Set the IMDb ID in the imdbTextView
            imdbTextView.text = movie.imdbId
            // Use Picasso library to load the movie poster image from the URL and display it in the posterImageView
            Picasso.get()
                .load(movie.imgPoster) // Load the image from the provided URL
                .resize(200, 300) // Resize the image to 200x300 pixels
                .centerCrop() // Crop the image to fill the ImageView
                .into(posterImageView) // Display the image in the posterImageView

            // Set a click listener on the item view to open the MovieDetailActivity
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, MovieDetailActivity::class.java).apply {
                    // Pass the poster URL and IMDb ID to the MovieDetailActivity
                    putExtra("POSTER_URL", movie.imgPoster)
                    putExtra("IMDB_ID", movie.imdbId)
                }
                // Start the MovieDetailActivity
                context.startActivity(intent)
            }
        }
    }

    // Method to create a new ViewHolder when there are no existing ViewHolders available for reuse
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // Inflate the item_movie layout to create a new item view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        // Return a new instance of MovieViewHolder with the inflated view
        return MovieViewHolder(view)
    }

    // Method to bind data to the ViewHolder
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        // Bind the movie data to the ViewHolder at the given position
        holder.bind(movies[position])
    }

    // Method to get the total number of items in the RecyclerView
    override fun getItemCount(): Int = movies.size // Return the size of the movie list
}
