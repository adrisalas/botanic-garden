package com.salastroya.bgserver.infrastructure.poi

import com.salastroya.bgserver.configuration.R2bcConfiguration
import com.salastroya.bgserver.core.poi.model.Poi
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

@DataR2dbcTest
@Import(PoiDataService::class, R2bcConfiguration::class)
class PoiDataServiceTest {

    companion object {
        val NAME = "name"
        val DESCRIPTION = "description"
        val IMAGE = "image"
        val POI = Poi(
            id = null,
            name = NAME,
            description = DESCRIPTION,
            image = IMAGE
        )
    }

    @Autowired
    lateinit var poiDataService: PoiDataService

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        poiDataService.insert(POI)
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        poiDataService.findAll()
            .collect { poiDataService.delete(it.id!!) }
    }

    @Test
    fun findAll(): Unit = runBlocking {
        val list = poiDataService.findAll().toList()

        assertThat(list).hasSize(1)
        assertThat(list[0].id).isNotNull().isNotEqualTo(0)
        assertThat(list[0].name).isEqualTo(NAME)
        assertThat(list[0].description).isEqualTo(DESCRIPTION)
        assertThat(list[0].image).isEqualTo(IMAGE)
    }

    @Test
    fun findById(): Unit = runBlocking {
        val poi = poiDataService.findById(poiDataService.findAll().first().id!!)

        assertThat(poi).isNotNull()
        assertThat(poi?.id).isNotNull().isNotEqualTo(0)
        assertThat(poi?.name).isEqualTo(NAME)
        assertThat(poi?.description).isEqualTo(POI.description)
        assertThat(poi?.image).isEqualTo(POI.image)
    }

    @Test
    fun existsById(): Unit = runBlocking {
        val exists = poiDataService.existsById(poiDataService.findAll().first().id!!)

        assertThat(exists).isTrue()
    }

    @Test
    fun insert(): Unit = runBlocking {
        val newName = "newName"

        poiDataService.insert(POI.copy(name = newName))

        val list = poiDataService.findAll().toList()
        assertThat(list).hasSize(2)
        assertThat(list[0].id).isNotNull().isNotEqualTo(0)
        assertThat(list[1].id).isNotNull().isNotEqualTo(0).isNotEqualTo(list[0].id)
        assertThat(list[1].name).isEqualTo(newName)
    }

    @Test
    fun update(): Unit = runBlocking {
        val newName = "newName"
        val poi = poiDataService.findAll().toList().first()

        val output = poiDataService.update(poi.copy(name = newName))

        assertThat(output.name).isEqualTo(newName)
        val list = poiDataService.findAll().toList()
        assertThat(list[0].id).isNotNull().isNotEqualTo(0)
        assertThat(list[0].name).isEqualTo(newName)
    }

    @Test
    fun delete(): Unit = runBlocking {
        val id = poiDataService.findAll().toList().first().id ?: 0

        poiDataService.delete(id)

        val list = poiDataService.findAll().toList()

        assertThat(list).hasSize(0)
    }
}