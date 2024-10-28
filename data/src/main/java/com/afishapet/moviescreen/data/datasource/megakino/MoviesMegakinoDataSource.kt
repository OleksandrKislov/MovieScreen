package com.afishapet.moviescreen.data.datasource.megakino

import com.afishapet.moviescreen.data.datasource.MoviesRemoteDataSource
import com.afishapet.moviescreen.domain.models.Movie
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesMegakinoDataSource @Inject constructor() : MoviesRemoteDataSource {

    private var movies: List<Movie> = emptyList()

    override suspend fun getAllMovies(): List<Movie> {

        val cinemasDoc = Jsoup
            .connect("${MegakinoConfig.BASE_URL}/cinema/list")
            .userAgent(MegakinoConfig.USER_AGENT)
            .get()

        val cinemaIds = cinemasDoc.select(".cinema-list-item").map {
            it.select("a[href]").attr("href").substringAfter("cinemaId=")
        }

        val result = coroutineScope {
            cinemaIds.map { cinemaId ->
                async {
                    val moviesDoc = Jsoup
                        .connect("${MegakinoConfig.BASE_URL}/cinema/filteredFilms?cinemaId=$cinemaId")
                        .userAgent(MegakinoConfig.USER_AGENT)
                        .get()

                    moviesDoc.select(".category-img").map {
                        Movie(
                            posterImageUrl = it.select("img[src]").attr("src"),
                            name = it.select(".info-visible > a").text(),
                            genre = it.select(".info-visible > small > p").text(),
                            cost = it.select(".small-img-price").text(),
                            id = it.select(".event-info").attr("data-link").substringAfter("/cinema/film/"),
                            cinemaIds = listOf(cinemaId)
                        )
                    }
                }
            }.awaitAll().flatten().groupBy { it.id }.values.map {
                it.reduce { acc, data ->
                    acc.copy(cinemaIds = acc.cinemaIds + data.cinemaIds)
                }
            }
        }

        movies = result

        return result
    }

    override suspend fun getMoviesInCinemaOnDate(
        date: String,
        cinemaId: String
    ): List<Movie> {
        val moviesDoc = Jsoup
            .connect("${MegakinoConfig.BASE_URL}/cinema/filteredFilms?cinemaId=$cinemaId&period=$date")
            .userAgent(MegakinoConfig.USER_AGENT)
            .get()

        val result = moviesDoc.select(".category-img").map {
            Movie(
                posterImageUrl = it.select("img[src]").attr("src"),
                name = it.select(".info-visible > a").text(),
                genre = it.select(".info-visible > small > p").text(),
                cost = it.select(".small-img-price").text(),
                id = it.select(".event-info").attr("data-link").substringAfter("/cinema/film/"),
                cinemaIds = listOf(cinemaId)
            )
        }

        movies = result

        return result
    }

    override suspend fun getMovieById(movieId: String): Movie {

        if (movies.isEmpty() || movies.none { it.id == movieId }) {
            getAllMovies()
        }

        val movie = movies.find { it.id == movieId }
            ?: throw NoSuchElementException("Movie with such id doesn't exist.")

         return movie
    }
}