package com.salastroya.bgserver.infrastructure.plant

import com.salastroya.bgserver.configuration.R2bcConfiguration
import com.salastroya.bgserver.core.plant.model.Plant
import com.salastroya.bgserver.core.plant.model.PlantDetails
import com.salastroya.bgserver.core.plant.model.Season
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
import java.time.Month

@DataR2dbcTest
@Import(PlantDataService::class, R2bcConfiguration::class)
class PlantDataServiceTest {

    companion object {
        val NAME = "name"
        val DESCRIPTION = "description"
        val IMAGE = "image"
        val TYPE = "type"
        val WATER = "Twice a week"
        val DETAILS = PlantDetails(
            season = Season.SUMMER,
            leafType = TYPE,
            water = WATER,
            flowering = Month.JUNE to Month.SEPTEMBER
        )
        val PLANT = Plant(
            id = null,
            commonName = NAME,
            scientificName = NAME,
            description = DESCRIPTION,
            image = IMAGE,
            type = TYPE,
            details = DETAILS
        )
    }

    @Autowired
    lateinit var plantDataService: PlantDataService

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        plantDataService.insert(PLANT)
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        plantDataService.findAll()
            .collect { plantDataService.delete(it.id!!) }
    }

    @Test
    fun findAll(): Unit = runBlocking {
        val list = plantDataService.findAll().toList()

        assertThat(list).hasSize(1)
        assertThat(list[0].id).isNotNull().isNotEqualTo(0)
        assertThat(list[0].commonName).isEqualTo(NAME)
        assertThat(list[0].scientificName).isEqualTo(NAME)
        assertThat(list[0].description).isEqualTo(DESCRIPTION)
        assertThat(list[0].image).isEqualTo(IMAGE)
        assertThat(list[0].type).isEqualTo(TYPE)
        assertThat(list[0].details).isEqualTo(DETAILS)
    }

    @Test
    fun findById(): Unit = runBlocking {
        val plant = plantDataService.findById(plantDataService.findAll().first().id!!)

        assertThat(plant).isNotNull()
        assertThat(plant?.id).isNotNull().isNotEqualTo(0)
        assertThat(plant?.description).isEqualTo(PLANT.description)
        assertThat(plant?.image).isEqualTo(PLANT.image)
    }

    @Test
    fun existsById(): Unit = runBlocking {
        val exists = plantDataService.existsById(plantDataService.findAll().first().id!!)

        assertThat(exists).isTrue()
    }

    @Test
    fun insert(): Unit = runBlocking {
        val newName = "newName"

        plantDataService.insert(PLANT.copy(commonName = newName))

        val list = plantDataService.findAll().toList()
        assertThat(list).hasSize(2)
        assertThat(list[0].id).isNotNull().isNotEqualTo(0)
        assertThat(list[1].id).isNotNull().isNotEqualTo(0).isNotEqualTo(list[0].id)
        assertThat(list[1].commonName).isEqualTo(newName)
    }

    @Test
    fun update(): Unit = runBlocking {
        val newName = "newName"
        val plant = plantDataService.findAll().toList().first()

        val output = plantDataService.update(plant.copy(commonName = newName))

        assertThat(output.commonName).isEqualTo(newName)
        val list = plantDataService.findAll().toList()
        assertThat(list[0].id).isNotNull().isNotEqualTo(0)
        assertThat(list[0].commonName).isEqualTo(newName)
    }

    @Test
    fun delete(): Unit = runBlocking {
        val id = plantDataService.findAll().toList().first().id ?: 0

        plantDataService.delete(id)

        val list = plantDataService.findAll().toList()

        assertThat(list).hasSize(0)
    }
}