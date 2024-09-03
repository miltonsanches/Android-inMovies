package br.com.mleslie.inmovies.ui.activity

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mleslie.inmovies.R
import br.com.mleslie.inmovies.data.DatabaseHelper
import br.com.mleslie.inmovies.ui.adapter.GenresAdapter

class GenresActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GenresAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genres)

        toolbar = findViewById(R.id.toolbarGenres)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Genres"

        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)?.apply {
            setColorFilter(android.graphics.Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        }
        supportActionBar?.setHomeAsUpIndicator(upArrow)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewGenres)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadGenres()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadGenres() {
        val genres = dbHelper.getAllGenres()
        adapter = GenresAdapter(genres)
        recyclerView.adapter = adapter
    }
}
