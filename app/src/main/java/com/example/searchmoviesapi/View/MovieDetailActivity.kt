package com.example.searchmoviesapi.View

import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.searchmoviesapi.ViewModel.MovieViewModel
import com.example.searchmoviesapi.R
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.ui.PlayerView
import com.squareup.picasso.Picasso

class MovieDetailActivity : AppCompatActivity() {

    // Declare the MovieViewModel to observe movie data and ExoPlayer for video playback
    private lateinit var movieViewModel: MovieViewModel
    private var isExpanded = false // To track the state of the description (expanded or collapsed)
    private lateinit var exoPlayer: ExoPlayer // Declare the ExoPlayer instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail) // Set the content view to the specified layout

        // Retrieve intent extras for poster URL and IMDb ID
        val posterUrl = intent.getStringExtra("POSTER_URL") ?: ""
        val imdbId = intent.getStringExtra("IMDB_ID") ?: ""

        // Initialize views by finding them by their ID
        val playview: ImageView = findViewById(R.id.playerview)
        val posterImageView: ImageView = findViewById(R.id.movieDetailPoster)
        val playerView: PlayerView = findViewById(R.id.player_view)
        val actorsTextView: TextView = findViewById(R.id.actorsTextView)
        val directorTextView: TextView = findViewById(R.id.directorTextView)
        val descriptionTextView: TextView = findViewById(R.id.descriptionTextView)
        val toggleButton: Button = findViewById(R.id.toggleButton)
        val pipButton: ImageView = findViewById(R.id.pipButton)
        val rotateButton: ImageView = findViewById(R.id.rotate)

        // Load the poster image into the ImageView using Picasso
        Picasso.get().load(posterUrl).into(posterImageView)

        // Initialize the MovieViewModel to observe movie details
        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        // Observe changes to the movie details from the ViewModel
        movieViewModel.movieDetails.observe(this) { details ->
            // Extract actors, director, and description from movie details
            val actors = details.short.actor.joinToString(", ") { it.name }
            val director = details.short.director.joinToString(", ") { it.name }
            val description = details.short.description

            // Set text for actors, director, and description TextViews
            actorsTextView.text = "Actors: $actors"
            directorTextView.text = "Director: $director"
            descriptionTextView.text = "Description: $description"

            // Set up the toggle button to expand/collapse the description
            toggleButton.setOnClickListener {
                isExpanded = !isExpanded
                if (isExpanded) {
                    descriptionTextView.maxLines = Int.MAX_VALUE // Show full text when expanded
                    toggleButton.text = "Less" // Update button text to "Less"
                } else {
                    descriptionTextView.maxLines = 1 // Collapse to one line when not expanded
                    toggleButton.text = "More" // Update button text to "More"
                }
            }
        }

        // Fetch movie details using the IMDb ID if it is not empty
        if (imdbId.isNotEmpty()) {
            movieViewModel.fetchMovieDetails(imdbId)
        }

        // Set up play view click listener to initialize and show the player view
        playview.setOnClickListener {
            posterImageView.visibility = View.GONE // Hide poster image when playing
            playview.visibility = View.GONE // Hide play view button
            playerView.visibility = View.VISIBLE // Show ExoPlayer view
            pipButton.visibility = View.VISIBLE // Show PiP button
            initializePlayer(playerView) // Initialize ExoPlayer
        }

        // Set up Picture-in-Picture button click listener
        pipButton.setOnClickListener {
            enterPictureInPictureMode() // Enter PiP mode
        }

        // Set up rotate button click listener to switch screen orientation
        rotateButton.setOnClickListener {
            val newOrientation = if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // Switch to portrait if currently landscape
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE // Switch to landscape if currently portrait
            }
            requestedOrientation = newOrientation // Apply the new orientation
        }
    }

    private fun initializePlayer(playerView: PlayerView) {

        exoPlayer = ExoPlayer.Builder(this).build()

        Log.d("DRM_Test", "ExoPlayer initialized")

        playerView.player = exoPlayer
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val drmConfiguration = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
            .setLicenseUri("https://proxy.uat.widevine.com/proxy?provider=widevine_test" )
            .build()

        Log.d("DRM_Test", "DRM configuration set with Widevine")

        val mediaItem = MediaItem.Builder()
            .setUri("https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd")
            .setDrmConfiguration(drmConfiguration)
            .build()

        Log.d("DRM_Test", "MediaItem created with URI: https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd")

        val dashMediaSource = DashMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)
        exoPlayer.setMediaSource(dashMediaSource)

        Log.d("DRM_Test", "DashMediaSource set for ExoPlayer")

        exoPlayer.prepare()

        Log.d("DRM_Test", "ExoPlayer prepared")

        exoPlayer.playWhenReady = true

    }



    // Override method to enter Picture-in-Picture mode
    override fun enterPictureInPictureMode() {
        val aspectRatio = Rational(16, 9)  // Specify the aspect ratio for PiP
        val pipParams = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
            .build()
        enterPictureInPictureMode(pipParams) // Enter PiP mode with specified parameters
    }

    // Release ExoPlayer resources when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release() // Release ExoPlayer to free up resources
    }
}