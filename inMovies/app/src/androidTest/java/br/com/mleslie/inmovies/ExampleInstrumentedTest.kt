package br.com.mleslie.inmovies

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import android.database.sqlite.SQLiteDatabase
import br.com.mleslie.inmovies.data.DatabaseHelper
import br.com.mleslie.inmovies.data.GenreDB
import android.content.ContentValues
import androidx.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class DatabaseHelperTest {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        dbHelper = DatabaseHelper(context)
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun insertAndReadGenre() {
        // Insert a genre
        val genre = GenreDB(1, "Action")
        val insertResult = dbHelper.insertGenre(genre)
        assertTrue("Check if the insert was successful", insertResult != -1L)

        // Read the genre back
        val cursor = db.query("genres", arrayOf("id", "name"), "id = ?", arrayOf("1"), null, null, null)
        assertTrue("Check if the genre could be read", cursor.moveToFirst())
        assertEquals("Action", cursor.getString(cursor.getColumnIndex("name")))
        cursor.close()
    }

    @Test
    fun updateGenre() {
        // Insert a genre
        val genre = GenreDB(2, "Comedy")
        dbHelper.insertGenre(genre)

        // Update the genre
        val newValues = ContentValues()
        newValues.put("name", "Horror")
        val updateCount = db.update("genres", newValues, "id = ?", arrayOf("2"))
        assertEquals("Check if one row was updated", 1, updateCount)

        // Validate the update
        val cursor = db.query("genres", arrayOf("name"), "id = ?", arrayOf("2"), null, null, null)
        assertTrue("Check if data is present", cursor.moveToFirst())
        assertEquals("Horror", cursor.getString(cursor.getColumnIndex("name")))
        cursor.close()
    }

    @Test
    fun deleteGenre() {
        // Insert a genre
        val genre = GenreDB(3, "Sci-Fi")
        dbHelper.insertGenre(genre)

        // Delete the genre
        val deleteCount = dbHelper.deleteFavorite(3)
        assertEquals("Check if one row was deleted", 1, deleteCount)

        // Validate the deletion
        val cursor = db.query("genres", null, "id = ?", arrayOf("3"), null, null, null)
        assertFalse("Check if the genre was deleted", cursor.moveToFirst())
        cursor.close()
    }

    @Test
    fun queryMultipleGenres() {
        // Insert multiple genres
        dbHelper.insertGenre(GenreDB(4, "Adventure"))
        dbHelper.insertGenre(GenreDB(5, "Thriller"))

        // Query multiple ids
        val ids = listOf(4, 5)
        val genres = dbHelper.getGenreNamesByIds(ids)
        assertEquals("Check if two genres were returned", 2, genres.size)
        assertTrue("Check if Adventure is included", "Adventure" in genres)
        assertTrue("Check if Thriller is included", "Thriller" in genres)
    }

    @Test
    fun countGenres() {
        // Assume database is empty initially, or reset it for testing
        db.delete("genres", null, null)
        dbHelper.insertGenre(GenreDB(6, "Drama"))
        dbHelper.insertGenre(GenreDB(7, "Mystery"))

        // Count the genres
        val count = dbHelper.countGenres()
        assertEquals("Check the number of genres", 2, count)
    }
}