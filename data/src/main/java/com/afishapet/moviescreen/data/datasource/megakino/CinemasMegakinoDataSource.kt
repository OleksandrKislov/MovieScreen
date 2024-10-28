package com.afishapet.moviescreen.data.datasource.megakino

import com.afishapet.moviescreen.data.datasource.CinemasRemoteDataSource
import com.afishapet.moviescreen.domain.models.Cinema
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CinemasMegakinoDataSource @Inject constructor() : CinemasRemoteDataSource {

    private var cinemas: List<Cinema> = emptyList()

    override suspend fun getCinemas(): List<Cinema> {
        val cinemasDoc = Jsoup
            .connect("${MegakinoConfig.BASE_URL}/cinema/list")
            .userAgent(MegakinoConfig.USER_AGENT)
            .get()

        val result = cinemasDoc.select(".cinema-list-item").map {
            Cinema(
                name = it.select(".title").text(),
                address = it.select(".address").text(),
                logoImageUrl = it.select("img[src]").attr("src"),
                id = it.select("a[href]").attr("href").substringAfter("cinemaId=")
            )
        }

        cinemas = result

        return result
    }

    override suspend fun getCinemaById(id: String): Cinema {
        if (cinemas.isEmpty() || cinemas.none { it.id == id }) {
            getCinemas()
        }

        val cinema = cinemas.find { it.id == id }
            ?: throw NoSuchElementException("Cinema with such id doesn't exist.")

        return cinema
    }
}