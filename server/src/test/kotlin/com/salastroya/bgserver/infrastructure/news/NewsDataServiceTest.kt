package com.salastroya.bgserver.infrastructure.news

import com.salastroya.bgserver.configuration.R2bcConfiguration
import com.salastroya.bgserver.core.news.model.News
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import java.time.LocalDateTime

@DataR2dbcTest
@Import(NewsDataService::class, R2bcConfiguration::class)
class NewsDataServiceTest {

    companion object {
        val TITLE = "Title"
        val SUBTITLE = "Subtitle"
        val DESCRIPTION = "Subtitle"
        val DATE = LocalDateTime.of(2024, 1, 1, 0, 0)
        val IMAGE = "image"
        val NEWS = News(
            id = null,
            title = TITLE,
            subtitle = SUBTITLE,
            description = DESCRIPTION,
            date = DATE,
            image = IMAGE
        )
    }

    @Autowired
    lateinit var newsDataService: NewsDataService

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        newsDataService.insert(NEWS)
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        newsDataService.findAll()
            .collect { newsDataService.delete(it.id!!) }
    }

    @Test
    fun findAll(): Unit = runBlocking {
        val list = newsDataService.findAll().toList()

        assertThat(list).hasSize(1)
        assertThat(list[0].id).isNotNull().isNotEqualTo(0)
        assertThat(list[0].title).isEqualTo(TITLE)
        assertThat(list[0].subtitle).isEqualTo(SUBTITLE)
        assertThat(list[0].description).isEqualTo(DESCRIPTION)
        assertThat(list[0].date).isEqualTo(DATE)
        assertThat(list[0].image).isEqualTo(IMAGE)
    }

    @Test
    fun findById(): Unit = runBlocking {
        val news = newsDataService.findById(newsDataService.findAll().first().id!!)

        assertThat(news).isNotNull()
        assertThat(news?.id).isNotNull().isNotEqualTo(0)
        assertThat(news?.title).isEqualTo(NEWS.title)
        assertThat(news?.subtitle).isEqualTo(NEWS.subtitle)
        assertThat(news?.description).isEqualTo(NEWS.description)
        assertThat(news?.date).isEqualTo(NEWS.date)
        assertThat(news?.image).isEqualTo(NEWS.image)
    }

    @Test
    fun existsById(): Unit = runBlocking {
        val exists = newsDataService.existsById(newsDataService.findAll().first().id!!)

        assertThat(exists).isTrue()
    }

    @Test
    fun insert(): Unit = runBlocking {
        val newTitle = "newTitle"

        newsDataService.insert(NEWS.copy(title = newTitle))

        val list = newsDataService.findAll().toList()
        assertThat(list).hasSize(2)
        assertThat(list[0].id).isNotNull().isNotEqualTo(0)
        assertThat(list[1].id).isNotNull().isNotEqualTo(0).isNotEqualTo(list[0].id)
        assertThat(list[1].title).isEqualTo(newTitle)
    }

    @Test
    fun update(): Unit = runBlocking {
        val newTitle = "newTitle"
        val news = newsDataService.findAll().toList().first()

        val output = newsDataService.update(news.copy(title = newTitle))

        assertThat(output.title).isEqualTo(newTitle)
        val list = newsDataService.findAll().toList()
        assertThat(list[0].id).isNotNull().isNotEqualTo(0)
        assertThat(list[0].title).isEqualTo(newTitle)
    }

    @Test
    fun delete(): Unit = runBlocking {
        val id = newsDataService.findAll().toList().first().id ?: 0

        newsDataService.delete(id)

        val list = newsDataService.findAll().toList()

        assertThat(list).hasSize(0)
    }
}