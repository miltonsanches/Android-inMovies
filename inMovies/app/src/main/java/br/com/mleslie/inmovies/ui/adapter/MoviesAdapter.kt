package br.com.mleslie.inmovies.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.mleslie.inmovies.R
import br.com.mleslie.inmovies.data.DatabaseHelper
import com.squareup.picasso.Picasso
import br.com.mleslie.inmovies.model.Movie
import br.com.mleslie.inmovies.ui.activity.MovieActivity
import br.com.mleslie.inmovies.utils.Constants

class MoviesAdapter(
    var movies: List<Movie>,
    private val isGrid: Boolean,
    private val dbHelper: DatabaseHelper
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutId = if (isGrid) R.layout.item_movie_grid else R.layout.item_movie
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MovieViewHolder(view, isGrid, dbHelper)
    }

    class MovieViewHolder(
        private val view: View,
        private val isGrid: Boolean,
        private val dbHelper: DatabaseHelper
    ) : RecyclerView.ViewHolder(view) {
        val imageViewPoster: ImageView = view.findViewById(R.id.imageViewPoster)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textViewReleaseDate: TextView? = if (!isGrid) view.findViewById(R.id.textViewReleaseDate) else null
        val textViewOverview: TextView? = if (!isGrid) view.findViewById(R.id.textViewOverview) else null
        val imageViewFavorite: ImageView? = if (isGrid) view.findViewById(R.id.imageViewFavorite) else null

        fun bind(movie: Movie) {
            textViewTitle.text = movie.title
            Picasso.get().load(Constants.BASE_URL_IMAGE + movie.poster_path).into(imageViewPoster)

            if (!isGrid) {
                textViewReleaseDate?.text = movie.release_date
                textViewOverview?.text = movie.overview
            } else {
                val isFavorite = dbHelper.isFavorite(movie.id)
                imageViewFavorite?.setImageDrawable(
                    if (isFavorite)
                        ContextCompat.getDrawable(view.context, R.drawable.ic_star_on)
                    else
                        ContextCompat.getDrawable(view.context, R.drawable.ic_star_off)
                )
            }

            view.setOnClickListener {
                val context = view.context
                val intent = Intent(context, MovieActivity::class.java).apply {
                    putExtra("movie", movie)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = movies.size
            override fun getNewListSize(): Int = newMovies.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return movies[oldItemPosition].id == newMovies[newItemPosition].id
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return movies[oldItemPosition] == newMovies[newItemPosition]
            }
        })
        this.movies = newMovies
        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }
}
