package com.salastroya.bgserver.core.news

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.news.model.News
import com.salastroya.bgserver.core.news.port.NewsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class NewsUseCasesTest {
    companion object {
        val ID = 1
        val TITLE = "Title"
        val SUBTITLE = "Subtitle"
        val DESCRIPTION = "Subtitle"
        val DATE = LocalDateTime.of(2024, 1, 1, 0, 0)
        val IMAGE = "image"
        val NEWS = News(
            id = ID,
            title = TITLE,
            subtitle = SUBTITLE,
            description = DESCRIPTION,
            date = DATE,
            image = IMAGE
        )
    }

    @MockK
    lateinit var repository: NewsRepository

    @InjectMockKs
    lateinit var useCases: NewsUseCases

    @Test
    fun findAll(): Unit = runBlocking {
        every { repository.findAll() }.returns(flowOf(NEWS))

        val news = useCases.findAll().toList()

        assertThat(news).hasSize(1)
        assertThat(news[0].id).isEqualTo(ID)
        assertThat(news[0].title).isEqualTo(TITLE)
        assertThat(news[0].subtitle).isEqualTo(SUBTITLE)
        assertThat(news[0].description).isEqualTo(DESCRIPTION)
        assertThat(news[0].image).isEqualTo(IMAGE)

    }

    @Test
    fun findById(): Unit = runBlocking {
        coEvery { repository.findById(ID) }.returns(NEWS)

        val news = useCases.findById(ID)

        assertThat(news).isNotNull()
        assertThat(news?.id).isEqualTo(ID)
        assertThat(news?.id).isEqualTo(ID)
        assertThat(news?.title).isEqualTo(TITLE)
        assertThat(news?.subtitle).isEqualTo(SUBTITLE)
        assertThat(news?.description).isEqualTo(DESCRIPTION)
        assertThat(news?.image).isEqualTo(IMAGE)
    }

    @Test
    fun insertWithId(): Unit = runBlocking {
        assertThrows<InvalidUseCaseException> { useCases.insert(NEWS) }
    }

    @Test
    fun insertWithoutId(): Unit = runBlocking {
        coEvery { repository.insert(any()) }.returns(NEWS)

        val news = useCases.insert(NEWS.copy(id = null))

        assertThat(news).isEqualTo(NEWS)
    }

    @Test
    fun update(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(true)
        coEvery { repository.update(any()) }.returns(NEWS)

        val news = useCases.update(NEWS)

        assertThat(news).isEqualTo(NEWS)
    }

    @Test
    fun updateWithoutID(): Unit = runBlocking {
        assertThrows<InvalidUseCaseException> { useCases.update(NEWS.copy(id = null)) }
    }

    @Test
    fun updateNonExisting(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.update(NEWS) }
    }

    @Test
    fun delete(): Unit = runBlocking {
        coEvery { useCases.delete(ID) }.returns(Unit)

        useCases.delete(ID)

        coVerify { useCases.delete(ID) }
    }
}