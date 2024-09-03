package br.com.mleslie.inmovies.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "inMovies.db"
        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_GENRES = """
            CREATE TABLE genres (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL
            )
        """

        private const val SQL_CREATE_MOVIES = """
            CREATE TABLE movies (
                id INTEGER PRIMARY KEY,
                favorite INTEGER NOT NULL
            )
        """

        private const val SQL_DELETE_GENRES = "DROP TABLE IF EXISTS genres"
        private const val SQL_DELETE_MOVIES = "DROP TABLE IF EXISTS movies"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_GENRES)
        db.execSQL(SQL_CREATE_MOVIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_GENRES)
        db.execSQL(SQL_DELETE_MOVIES)
        onCreate(db)
    }

    fun insertGenre(genre: GenreDB): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id", genre.id)
            put("name", genre.name)
        }
        return db.insert("genres", null, values)
    }

    fun insertMovie(movie: MovieDB): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id", movie.id)
            put("favorite", if (movie.favorite) 1 else 0)
        }
        return db.insert("movies", null, values)
    }

    fun getAllGenres(): List<GenreDB> {
        val genres = mutableListOf<GenreDB>()
        val db = this.readableDatabase
        val cursor = db.query("genres", arrayOf("id", "name"), null, null, null, null, null)
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                val name = getString(getColumnIndexOrThrow("name"))
                genres.add(GenreDB(id, name))
            }
        }
        cursor.close()
        return genres
    }

    fun countGenres(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(id) FROM genres", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun getGenreNamesByIds(ids: List<Int>): List<String> {
        val db = this.readableDatabase
        val names = mutableListOf<String>()
        val cursor = db.query(
            "genres",
            arrayOf("name"),  // Only fetch the name column
            "id IN (${ids.joinToString()})",  // SQL for matching multiple IDs
            null,
            null,
            null,
            null
        )
        with(cursor) {
            while (moveToNext()) {
                names.add(getString(getColumnIndexOrThrow("name")))
            }
        }
        cursor.close()
        return names
    }

    fun isFavorite(movieId: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            "movies",
            arrayOf("id"),
            "id = ?",
            arrayOf(movieId.toString()),
            null,
            null,
            null
        )
        val isFavorite = cursor.count > 0
        cursor.close()
        return isFavorite
    }

    fun deleteFavorite(movieId: Int): Int {
        val db = this.writableDatabase
        return db.delete("movies", "id = ?", arrayOf(movieId.toString()))
    }
}