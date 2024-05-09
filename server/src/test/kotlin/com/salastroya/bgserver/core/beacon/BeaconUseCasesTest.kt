package com.salastroya.bgserver.core.beacon

import com.salastroya.bgserver.core.beacon.model.Beacon
import com.salastroya.bgserver.core.beacon.model.Item
import com.salastroya.bgserver.core.beacon.model.ItemType
import com.salastroya.bgserver.core.beacon.repository.BeaconRepository
import com.salastroya.bgserver.core.beacon.valueobject.BeaconId
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.plant.PlantUseCases
import com.salastroya.bgserver.core.plant.event.PlantDeletedEvent
import com.salastroya.bgserver.core.poi.PoiUseCases
import com.salastroya.bgserver.core.poi.event.PoiDeletedEvent
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

@ExtendWith(MockKExtension::class)
class BeaconUseCasesTest {

    companion object {
        val ID = "b0d8d05e-7ec4-4a7a-9f29-9b2c14d63d38"
        val BEACON_ID = BeaconId(ID)
        val ITEM = Item(ItemType.POI, 1)

        val BEACON = Beacon(
            beaconId = BEACON_ID,
            item = ITEM
        )
    }

    @MockK
    lateinit var repository: BeaconRepository

    @MockK
    lateinit var plantUseCases: PlantUseCases

    @MockK
    lateinit var poiUseCases: PoiUseCases

    @InjectMockKs
    lateinit var useCases: BeaconUseCases

    @Test
    fun findAll(): Unit = runBlocking {
        every { repository.findAll() }.returns(flowOf(BEACON))

        val beacons = useCases.findAll().toList()

        assertThat(beacons).hasSize(1)
        assertThat(beacons[0].id).isEqualTo(ID)
        assertThat(beacons[0].item).isEqualTo(ITEM)
    }

    @Test
    fun findById(): Unit = runBlocking {
        coEvery { repository.findById(ID) }.returns(BEACON)

        val beacon = useCases.findById(ID)

        assertThat(beacon).isEqualTo(BEACON)
    }

    @Test
    fun insert(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(false)
        coEvery { repository.insert(BEACON) }.returns(BEACON)
        coEvery { poiUseCases.existsById(ITEM.id) }.returns(true)

        val beacon = useCases.insert(BEACON)

        assertThat(beacon).isEqualTo(BEACON)
    }

    @Test
    fun insertExisting(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(true)

        assertThrows<InvalidUseCaseException> { useCases.insert(BEACON) }
    }

    @Test
    fun insertItemPoiNotExisting(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(false)
        coEvery { poiUseCases.existsById(ITEM.id) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.insert(BEACON) }
    }

    @Test
    fun insertItemPlantNotExisting(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(false)
        coEvery { plantUseCases.existsById(ITEM.id) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.insert(BEACON.copy(item = ITEM.copy(ItemType.PLANT))) }
    }

    @Test
    fun update(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(true)
        coEvery { repository.update(BEACON) }.returns(BEACON)
        coEvery { poiUseCases.existsById(ITEM.id) }.returns(true)

        val beacon = useCases.update(BEACON)

        assertThat(beacon).isEqualTo(BEACON)
    }

    @Test
    fun updateNotExisting(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.update(BEACON) }
    }

    @Test
    fun updateItemNotExisting(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(true)
        coEvery { poiUseCases.existsById(ITEM.id) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.update(BEACON) }
    }

    @Test
    fun delete(): Unit = runBlocking {
        coEvery { repository.delete(ID) }.returns(Unit)

        useCases.delete(ID)

        coVerify { repository.delete(ID) }
    }

    @Test
    fun handleEventPlantDeleted(): Unit = runBlocking {
        every { repository.findAll() }.returns(flowOf(BEACON.copy(item = ITEM.copy(ItemType.PLANT))))
        coEvery { repository.update(BEACON.copy(item = null)) }.returns(BEACON.copy(item = null))

        useCases.handleEvent(PlantDeletedEvent(this, 1))

        coVerify { repository.update(BEACON.copy(item = null)) }
    }

    @Test
    fun handleEventPoiDeleted(): Unit = runBlocking {
        every { repository.findAll() }.returns(flowOf(BEACON))
        coEvery { repository.update(BEACON.copy(item = null)) }.returns(BEACON.copy(item = null))

        useCases.handleEvent(PoiDeletedEvent(this, 1))

        coVerify { repository.update(BEACON.copy(item = null)) }
    }
}