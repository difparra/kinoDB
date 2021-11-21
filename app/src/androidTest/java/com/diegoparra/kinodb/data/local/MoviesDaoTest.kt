package com.diegoparra.kinodb.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diegoparra.kinodb.data.local.entities.GenreEntity
import com.diegoparra.kinodb.data.local.entities.GenreMovieCrossRef
import com.diegoparra.kinodb.data.local.entities.MovieEntity
import com.diegoparra.kinodb.data.local.entities.MovieWithGenresDb
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class MoviesDaoTest {

    private val testDispatcher = TestCoroutineScope()
    private lateinit var database: KinoDatabase
    private lateinit var dao: MoviesDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val genre1 = GenreEntity("1", "Action")
    private val genre2 = GenreEntity("2", "Comedy")

    private val movie1 = MovieEntity(
        movieId = "451048",
        posterUrl = "https://image.tmdb.org/t/p/original/esgrxpxrQufea2A3iHJUquG4mdz.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/original/7WJjFviFBffEJvkAms4uWwbcVUk.jpg",
        title = "Jungle Cruise",
        overview = "Principios del siglo XX. Frank (Dwayne Johnson) es el carismático capitán de una peculiar embarcación que recorre la selva amazónica. Allí, a pesar de los peligros que el río Amazonas les tiene preparados, Frank llevará en su barco a la científica Lily Houghton (Emily Blunt) y a su hermano McGregor Houghton (Jack Whitehall). Su misión será encontrar un árbol místico que podría tener poderes curativos. Claro que su objetivo no será fácil, y en su aventura se encontrarán con toda clase de dificultades, además de una expedición alemana que busca también este árbol con propiedades curativas. Esta comedia de acción y aventuras está basada en la atracción Jungle Cruise de los parques de ocio de Disney.",
        language = Locale.forLanguageTag("en"),
        releaseDate = LocalDate.of(2021, 7, 28),
        voteAverage = 79,
        runtimeMinutes = null,
        homepageUrl = null,
        popularity = 0.0
    )
    private val movie2 = MovieEntity(
        movieId = "436969",
        posterUrl = "https://image.tmdb.org/t/p/original/fPJWlhXA2VXf4MlQ3JenVsz1iba.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/original/jlGmlFOcfo8n5tURmhC7YVd4Iyy.jpg",
        title = "The Suicide Squad",
        overview = "Un grupo de super villanos se encuentran encerrados en Belle Reve, una prisión de alta seguridad con la tasa de mortalidad más alta de Estados Unidos. Para salir de allí harán cualquier cosa, incluso unirse al grupo Task Force X, dedicado a llevar a cabo misiones suicidas bajo las órdenes de Amanda Waller. Fuertemente armados son enviados a la isla Corto Maltese, una jungla repleta de enemigos.",
        language = Locale.forLanguageTag("en"),
        releaseDate = LocalDate.of(2021, 7, 28),
        voteAverage = 80,
        runtimeMinutes = 132,
        homepageUrl = null,
        popularity = 0.0
    )


    @Before
    fun initDb() {
        database = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                KinoDatabase::class.java
            ).allowMainThreadQueries()
            .build()
        dao = database.moviesDao()
    }

    @After
    fun closeDb() = database.close()

    @After
    fun cleanTestDispatcher() = testDispatcher.cleanupTestCoroutines()


    @Test
    fun insertOrUpdateAllGenres_emptyDatabase_itemsWereInserted() = testDispatcher.runBlockingTest {
        assertThat(dao.getGenres()).isEmpty()
        dao.insertOrUpdateAllGenres(listOf(genre1, genre2))
        assertThat(dao.getGenres()).isEqualTo(listOf(genre1, genre2))
    }

    @Test
    fun insertOrUpdateAllMovies_emptyDatabase_itemsWereInserted() = testDispatcher.runBlockingTest {
        assertThat(dao.getAllMovies()).isEmpty()
        dao.insertOrUpdateAllMovies(listOf(movie1, movie2))
        assertThat(dao.getAllMovies()).isNotEmpty()
        assertThat(dao.getAllMovies()).isEqualTo(listOf(movie1, movie2))
    }


    @Test
    fun insertGenre_insertsButDoNotReplace() = testDispatcher.runBlockingTest {
        dao.insertOrUpdateAllGenres(listOf(genre1, genre2))
        assertThat(dao.getGenres()).isEqualTo(listOf(genre1, genre2))

        dao.insertGenre(genre2.copy(name = ""))

        assertThat(dao.getGenres()).isEqualTo(listOf(genre1, genre2))
    }

    @Test
    fun insertOrUpdateGenre_insertIfNewOrUpdateIfExistent() = testDispatcher.runBlockingTest {
        dao.insertOrUpdateAllGenres(listOf(genre1, genre2))
        assertThat(dao.getGenres()).isEqualTo(listOf(genre1, genre2))

        val updatedGenre2 = genre2.copy(name = "Ciencia Ficción")
        dao.insertOrUpdateGenre(updatedGenre2)

        assertThat(dao.getGenres()).isEqualTo(listOf(genre1, updatedGenre2))
    }

    @Test
    fun getMoviesByGenre_moviesNotFound_returnEmptyList() = testDispatcher.runBlockingTest {
        dao.insertOrUpdateAllGenres(listOf(genre1, genre2))
        dao.insertOrUpdateAllMovies(listOf(movie1, movie2))
        dao.insertGenreMovieCrossRef(GenreMovieCrossRef("10", "20"))

        assertThat(dao.getMoviesByGenre(genre1.genreId)).isEmpty()
    }

    @Test
    fun getMoviesByGenre_moviesFound_returnMoviesList() = testDispatcher.runBlockingTest {
        dao.insertOrUpdateAllGenres(listOf(genre1, genre2))
        dao.insertOrUpdateAllMovies(listOf(movie1, movie2))
        dao.insertGenreMovieCrossRef(
            GenreMovieCrossRef(genreId = genre1.genreId, movieId = movie2.movieId)
        )
        dao.insertGenreMovieCrossRef(
            GenreMovieCrossRef(genreId = genre2.genreId, movieId = movie1.movieId)
        )
        dao.insertGenreMovieCrossRef(
            GenreMovieCrossRef(genreId = genre2.genreId, movieId = movie2.movieId)
        )

        assertThat(dao.getMoviesByGenre(genre1.genreId)).isEqualTo(listOf(movie2))
        assertThat(dao.getMoviesByGenre(genre2.genreId)).containsExactly(movie1, movie2)
    }

    @Test
    fun getMovieById_movieExist_returnMovie() = testDispatcher.runBlockingTest {
        dao.insertOrUpdateMovie(movie1)
        dao.insertOrUpdateGenre(genre1)
        dao.insertGenreMovieCrossRef(GenreMovieCrossRef(genre1.genreId, movie1.movieId))

        assertThat(dao.getMovieById(movie1.movieId))
            .isEqualTo(MovieWithGenresDb(movie1, listOf(genre1)))
        assertThat(dao.getMovieById(movie2.movieId)).isNull()
    }

    @Test
    fun searchMovieByName_noResults_returnEmptyList() = testDispatcher.runBlockingTest {
        dao.insertOrUpdateAllMovies(listOf(movie1, movie2))

        val result = dao.searchMovieByName("zzzzzzzzzz")
        assertThat(result).isNotNull()
        assertThat(result).isEmpty()
    }

    @Test
    fun searchMovieByName_resultsFound_returnMovies() = testDispatcher.runBlockingTest {
        val mMovie1 = movie1.copy(title = "Jungle Cruise", normalisedTitle = "jungle cruise")
        val mMovie2 = movie2.copy(title = "The Suicide Squad", normalisedTitle = "the suicide squad")
        dao.insertOrUpdateAllMovies(listOf(mMovie1, mMovie2))

        val result = dao.searchMovieByName("uis")
        assertThat(result).isEqualTo(listOf(mMovie1))
    }

    @Test
    fun searchMovieByName_ignoreCase_returnResult() = testDispatcher.runBlockingTest {
        val mMovie1 = movie1.copy(title = "Jungle Cruise", normalisedTitle = "jungle cruise")
        val mMovie2 = movie2.copy(title = "The Suicide Squad", normalisedTitle = "the suicide squad")
        dao.insertOrUpdateAllMovies(listOf(mMovie1, mMovie2))

        assertThat(dao.searchMovieByName("Suicid")).isEqualTo(listOf(mMovie2))
        assertThat(dao.searchMovieByName("suicid")).isEqualTo(listOf(mMovie2))
    }

    @Test
    fun searchMovieByName_ignoreAccents_returnResult() = testDispatcher.runBlockingTest {
        val mMovie1 = movie1.copy(title = "Jungle Cruise", normalisedTitle = "jungle cruise")
        val mMovie2 = movie2.copy(title = "El escuadrón suicida", normalisedTitle = "el escuadron suicida")
        dao.insertOrUpdateAllMovies(listOf(mMovie1, mMovie2))

        assertThat(dao.searchMovieByName("adrón")).isEqualTo(listOf(mMovie2))
        assertThat(dao.searchMovieByName("adron")).isEqualTo(listOf(mMovie2))
    }

    @Test
    fun insertMovieWithGenres_insertGenreMovieAndCrossRefTables() = testDispatcher.runBlockingTest {
        dao.insertMovieWithGenres(MovieWithGenresDb(movie1, listOf(genre1, genre2)))

        assertThat(dao.getGenres()).isEqualTo(listOf(genre1, genre2))
        assertThat(dao.getAllMovies()).isEqualTo(listOf(movie1))
        assertThat(dao.getMovieById(movie1.movieId))
            .isEqualTo(MovieWithGenresDb(movie1, listOf(genre1, genre2)))
    }

    @Test
    fun insertMovieWithGenres_inputBlankGenreName_doNotUpdate() = testDispatcher.runBlockingTest {
        dao.insertOrUpdateAllGenres(listOf(genre2))
        dao.insertMovieWithGenres(MovieWithGenresDb(movie1, listOf(genre1, genre2.copy(name = ""))))

        assertThat(dao.getGenres()).isEqualTo(listOf(genre1, genre2))
    }


}