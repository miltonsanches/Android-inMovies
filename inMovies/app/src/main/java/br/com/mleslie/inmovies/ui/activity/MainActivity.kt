package br.com.mleslie.inmovies.ui.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.mleslie.inmovies.ui.adapter.MoviesAdapter
import br.com.mleslie.inmovies.R
import br.com.mleslie.inmovies.data.DatabaseHelper
import br.com.mleslie.inmovies.data.GenreDB
import br.com.mleslie.inmovies.model.GenreResponse
import br.com.mleslie.inmovies.model.Movie
import br.com.mleslie.inmovies.network.RetrofitClient
import br.com.mleslie.inmovies.model.MovieResponse
import br.com.mleslie.inmovies.utils.UiHelper
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    lateinit var dbHelper: DatabaseHelper
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: MoviesAdapter
    private var is_grid: Boolean = true
    private var menu: Menu? = null
    private var show_all: Boolean = true
    private lateinit var progressBar: ProgressBar
    var currentMovies: List<Movie> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        setupSwipeRefreshLayout()
        progressBar = findViewById(R.id.progressBar)

        loadGenres()
        setupToolBar()
        setupRecycler()
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            loadMovies()
        }
    }

    private fun setupRecycler() {
        recyclerView = findViewById(R.id.recyclerView)
        if (is_grid) {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
        } else {
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        adapter = MoviesAdapter(emptyList(), is_grid, dbHelper)
        recyclerView.adapter = adapter
        loadMovies()
    }

    private fun toggleLayout() {
        is_grid = !is_grid
        recyclerView.layoutManager = if (is_grid) {
            menu?.findItem(R.id.action_toggle_layout)?.setIcon(R.drawable.ic_list)
            GridLayoutManager(this, 2)
        } else {
            menu?.findItem(R.id.action_toggle_layout)?.setIcon(R.drawable.ic_grid)
            LinearLayoutManager(this)
        }
        setupRecycler()
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun setupToolBar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.inflateMenu(R.menu.drawer_menu)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_movies -> {
                    show_all = true
                    loadMovies()
                }
                R.id.nav_favorites -> {
                    show_all = false
                    loadMovies()
                }
                R.id.nav_genres -> {
                    val intent = Intent(this, GenresActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_exit -> {
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        this.menu = menu

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)?.apply {
            setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
            setHintTextColor(ContextCompat.getColor(context, R.color.colorGray))
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
        }
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterMovies(it)
                }
                searchView.clearFocus()  // Para fechar o teclado após a busca
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterMovies(it)
                }
                return true
            }
        })

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            filterMovies(it)
        }
        return false // Não lida com o evento para permitir que o teclado seja fechado após a busca
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            filterMovies(it)
        }
        return true
    }

    fun filterMovies(query: String) {
        val filteredMovies = if (show_all) {
            currentMovies.filter { movie ->
                movie.title.contains(query, ignoreCase = true)
            }
        } else {
            currentMovies.filter { movie ->
                dbHelper.isFavorite(movie.id) && movie.title.contains(query, ignoreCase = true)
            }
        }

        if (filteredMovies.isEmpty()) {
            Toast.makeText(this, "No movie found with '$query'.", Toast.LENGTH_LONG).show()
        }

        adapter.updateMovies(filteredMovies)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            R.id.action_toggle_layout -> {
                toggleLayout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadGenres() {
        if (!checkInternetConnection()) {
            showErrorMessage("No Internet Connection")
            return
        }

        RetrofitClient.genreDbService.getGenres().enqueue(object : Callback<GenreResponse> {
            override fun onResponse(call: Call<GenreResponse>, response: Response<GenreResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { genreResponse ->
                        processGenres(genreResponse.genres)
                        Log.d("processAPIs", "Received loadGenres()")
                    }
                } else {
                    showErrorMessage("API Error: ${response.message()}")
                    Log.e("processAPIs", "Error fetching genres: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GenreResponse>, t: Throwable) {
                showErrorMessage("Failed to fetch genres: ${t.message}")
                Log.e("processAPIs", "Failed to fetch genres", t)
            }
        })
    }

    private fun processGenres(genres: List<GenreDB>) {
        Log.d("processAPIs", "process genres: ${genres.size}")
        genres.forEach { genre ->
            dbHelper.insertGenre(genre)
        }
    }

    private fun loadMovies() {
        if (!checkInternetConnection()) {
            showErrorMessage("No Internet Connection")
            swipeRefreshLayout.isRefreshing = false
            return
        }

        swipeRefreshLayout.isRefreshing = false
        UiHelper.showProgressBar(progressBar)
        RetrofitClient.movieDbService.getPopularMovies().enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                UiHelper.hideProgressBar(progressBar)
                if (response.isSuccessful) {
                    response.body()?.let { movieResponse ->
                        currentMovies = movieResponse.results
                        filterMovies("") // Exibe todos os filmes inicialmente
                    }
                } else {
                    showErrorMessage("API Error: ${response.message()}")
                    Log.e("processAPIs", "Error fetching movies: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                UiHelper.hideProgressBar(progressBar)
                showErrorMessage("Error fetching movies: ${t.message}")
                Log.e("processAPIs", "Failed to fetch movies", t)
            }
        })
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

}