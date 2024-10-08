package br.com.mleslie.inmovies.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.mleslie.inmovies.R
import br.com.mleslie.inmovies.data.GenreDB

class GenresAdapter(private val genres: List<GenreDB>) : RecyclerView.Adapter<GenresAdapter.GenreViewHolder>() {

    class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewName: TextView = view.findViewById(R.id.textViewName)

        fun bind(genre: GenreDB) {
            textViewName.text = genre.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_genre, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(genres[position])
    }

    override fun getItemCount() = genres.size
}
