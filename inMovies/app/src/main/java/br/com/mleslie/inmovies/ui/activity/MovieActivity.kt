package br.com.mleslie.inmovies.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.mleslie.inmovies.R
import br.com.mleslie.inmovies.data.DatabaseHelper
import br.com.mleslie.inmovies.data.MovieDB
import br.com.mleslie.inmovies.model.Movie
import com.squareup.picasso.Picasso

class MovieActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var isFavorite = false
    private var menu: Menu? = null
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        dbHelper = DatabaseHelper(this)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val movie = intent.getSerializableExtra("movie") as? Movie
        movie?.let {
            initializeMovieData(it)
        } ?: finish()

    }

    private fun initializeMovieData(movie: Movie) {
        supportActionBar?.title = movie.original_title

        val textViewCategory: TextView = findViewById(R.id.textViewCategory)
        val genreNames = dbHelper.getGenreNamesByIds(movie.genre_ids)
        textViewCategory.text = "Genre: " + genreNames.joinToString(", ")

        findViewById<ImageView>(R.id.imageViewMovie).also {
            Picasso.get().load("https://image.tmdb.org/t/p/w500${movie.backdrop_path}").into(it)
        }

        findViewById<TextView>(R.id.textViewOriginalTitle).text = movie.original_title
        findViewById<TextView>(R.id.textViewReleaseDate).text = "Release date: ${movie.release_date}"
        findViewById<TextView>(R.id.textViewOverview).text = "Overview: ${movie.overview}"

        isFavorite = dbHelper.isFavorite(movie.id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_movie, menu)
        this.menu = menu
        updateFavoriteIcon()
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        updateFavoriteIcon()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_favorite) {
            toggleFavoriteStatus()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleFavoriteStatus() {
        isFavorite = !isFavorite
        updateFavoriteIcon()
        (intent.getSerializableExtra("movie") as? Movie)?.let { movie ->
            if (isFavorite) dbHelper.insertMovie(MovieDB(movie.id, true))
            else dbHelper.deleteFavorite(movie.id)
        }
    }

    private fun updateFavoriteIcon() {
        menu?.findItem(R.id.action_favorite)?.icon = if (isFavorite)
            ContextCompat.getDrawable(this, R.drawable.ic_star_on)
        else
            ContextCompat.getDrawable(this, R.drawable.ic_star_off)
        // Log.d("processAPIs", "Favorite icon updated to ${if (isFavorite) "ON" else "OFF"}")
    }
}