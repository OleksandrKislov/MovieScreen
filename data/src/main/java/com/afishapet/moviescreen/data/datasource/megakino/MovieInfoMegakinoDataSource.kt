package com.afishapet.moviescreen.data.datasource.megakino

import com.afishapet.moviescreen.data.datasource.MovieInfoRemoteDataSource
import com.afishapet.moviescreen.domain.models.MovieDaySchedule
import com.afishapet.moviescreen.domain.models.MovieInfo
import com.afishapet.moviescreen.domain.models.Session
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieInfoMegakinoDataSource @Inject constructor() : MovieInfoRemoteDataSource {

    private data class MegakinoMovieSessionInfo(
        val eventId: String,
        val userAgent: String,
    )

    private var movieSessionsInfo: List<MegakinoMovieSessionInfo> = emptyList()

    override suspend fun getMovieInfoInCinema(
        movieId: String,
        cinemaId: String
    ): MovieInfo {

        val movieDoc = Jsoup
            .connect("${MegakinoConfig.BASE_URL}/cinema/film/$movieId")
            .userAgent(MegakinoConfig.USER_AGENT)
            .get()

        val description = movieDoc.select(".event-details > ul > li").eachText()

        val eachDayEvents = movieDoc.select(".event-afisha-row")
            .find {
                it.select(".eventsList .button-common").attr("href").contains(cinemaId)
            }
            ?.select(".eventsList")

        val schedule = eachDayEvents?.map { dayEventsRow ->
            MovieDaySchedule(
                date = dayEventsRow.select("div").text()
                    .trim()
                    .run {
                        val dayOfWeek = substringAfterLast(" ")
                        replaceAfterLast(" ", "| $dayOfWeek")
                    },
                sessions = dayEventsRow.select(" > li")
                    .apply { removeFirstOrNull() }
                    .map {
                        Session(
                            time = it.select(".time-timetable").text(),
                            price = it.select(".price-timetable").text(),
                            id = it.select(".button-common").attr("href")
                                .substringAfter("[['eventId', ")
                                .substringBefore("]]")
                        )
                    }
            )
        } ?: emptyList()

        movieSessionsInfo = eachDayEvents?.map { dayEventsRow ->
            dayEventsRow.select(" > li")
                .map {
                    MegakinoMovieSessionInfo(
                        eventId = it.select(".button-common").attr("href")
                            .substringAfter("[['eventId', ")
                            .substringBefore("]]"),
                        userAgent = it.select(".button-common").attr("href")
                            .substringAfter("show('")
                            .substringBefore("'")
                    )
                }
        }?.flatten() ?: emptyList()

        return MovieInfo(
            movieId = movieId,
            description = description,
            schedule = schedule
        )
    }

    override suspend fun getMovieBookingUrl(
        cinemaId: String,
        movieId: String,
        eventId: String
    ): String {

        if (movieSessionsInfo.isEmpty() || movieSessionsInfo.none { it.eventId == eventId }) {
            getMovieInfoInCinema(movieId, cinemaId)
        }

        val movieSessionInfo = movieSessionsInfo.find { it.eventId == eventId }
            ?: throw NoSuchElementException("No such movie session.")

        return MegakinoConfig.BOOKING_BASE_URL
            .replace("[userAgent]", movieSessionInfo.userAgent)
            .replace("[siteId]", cinemaId)
            .replace("[showId]", movieId)
            .replace("[eventId]", eventId)
    }
}