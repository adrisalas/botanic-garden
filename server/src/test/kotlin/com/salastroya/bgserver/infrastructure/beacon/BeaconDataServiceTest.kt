package com.salastroya.bgserver.infrastructure.beacon

import com.salastroya.bgserver.configuration.R2bcConfiguration
import com.salastroya.bgserver.core.beacon.model.Beacon
import com.salastroya.bgserver.core.beacon.model.Item
import com.salastroya.bgserver.core.beacon.model.ItemType
import com.salastroya.bgserver.core.beacon.valueobject.BeaconId
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
@Import(BeaconDataService::class, R2bcConfiguration::class)
class BeaconDataServiceTest {

    companion object {
        val BEACON = Beacon(BeaconId("id"), Item(ItemType.PLANT, 1))
    }

    @Autowired
    lateinit var beaconDataService: BeaconDataService

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        beaconDataService.insert(BEACON)
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        beaconDataService.findAll()
            .collect { beaconDataService.delete(it.id) }
    }

    @Test
    fun findAll(): Unit = runBlocking {
        val list = beaconDataService.findAll().toList()

        assertThat(list).hasSize(1)
        assertThat(list[0].id).isEqualTo(BEACON.id)
        assertThat(list[0].item).isEqualTo(BEACON.item)
    }

    @Test
    fun findById(): Unit = runBlocking {
        val beacon = beaconDataService.findById(BEACON.id)

        assertThat(beacon).isNotNull()
        assertThat(beacon?.id).isEqualTo(BEACON.id)
        assertThat(beacon?.item).isEqualTo(BEACON.item)
    }

    @Test
    fun existsById(): Unit = runBlocking {
        val exists = beaconDataService.existsById(BEACON.id)

        assertThat(exists).isTrue()
    }

    @Test
    fun insert(): Unit = runBlocking {
        val newId = "new-beacon"

        beaconDataService.insert(BEACON.copy(beaconId = BeaconId(newId)))

        val list = beaconDataService.findAll().toList()
        assertThat(list).hasSize(2)
        assertThat(list[0].id).isEqualTo(BEACON.id)
        assertThat(list[0].item).isEqualTo(BEACON.item)
        assertThat(list[1].id).isEqualTo(newId)
        assertThat(list[1].item).isEqualTo(BEACON.item)
    }

    @Test
    fun update(): Unit = runBlocking {
        val beacon = beaconDataService.update(BEACON.copy(item = null))

        assertThat(beacon.item).isNull()
        val list = beaconDataService.findAll().toList()
        assertThat(list[0].id).isEqualTo(BEACON.id)
        assertThat(list[0].item).isEqualTo(null)
    }

    @Test
    fun delete(): Unit = runBlocking {
        beaconDataService.delete(BEACON.id)

        val list = beaconDataService.findAll().toList()

        assertThat(list).hasSize(0)
    }
}