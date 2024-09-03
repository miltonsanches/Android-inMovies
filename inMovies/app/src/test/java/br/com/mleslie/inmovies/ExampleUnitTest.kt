package br.com.mleslie.inmovies

import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.mleslie.inmovies.ui.activity.MainActivity
import br.com.mleslie.inmovies.data.DatabaseHelper
import br.com.mleslie.inmovies.model.Movie
import br.com.mleslie.inmovies.ui.adapter.MoviesAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import androidx.test.core.app.ActivityScenario

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var dbHelperMock: DatabaseHelper
    private lateinit var adapterMock: MoviesAdapter

    @Before
    fun setup() {
        // Prepare the mocks
        dbHelperMock = Mockito.mock(DatabaseHelper::class.java)
        adapterMock = Mockito.mock(MoviesAdapter::class.java)

        // Use ActivityScenario to launch the activity
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            activity.dbHelper = dbHelperMock
            activity.adapter = adapterMock
            activity.currentMovies = listOf(
                Movie(
                    id = 1,
                    title = "Action Movie",
                    overview = "An exciting action-packed film",
                    poster_path = "/path/to/action.jpg",
                    adult = false,
                    backdrop_path = "/path/to/backdrop.jpg",
                    genre_ids = listOf(1),
                    original_language = "en",
                    original_title = "Action Movie",
                    popularity = 7.5,
                    release_date = "2021-07-16",
                    video = false,
                    vote_average = 8.2,
                    vote_count = 150
                ),
                Movie(
                    id = 2,
                    title = "Comedy Movie",
                    overview = "A hilarious journey of laughter",
                    poster_path = "/path/to/comedy.jpg",
                    adult = false,
                    backdrop_path = "/path/to/backdrop_comedy.jpg",
                    genre_ids = listOf(2),
                    original_language = "en",
                    original_title = "Comedy Movie",
                    popularity = 6.0,
                    release_date = "2021-08-01",
                    video = false,
                    vote_average = 8.3,
                    vote_count = 180
                )
            )
        }
    }

    @Test
    fun testFilterMovies() {
        // Example test logic here
    }
}
