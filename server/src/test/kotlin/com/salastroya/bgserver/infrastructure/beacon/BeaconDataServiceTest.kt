package com.salastroya.bgserver.infrastructure.beacon

import com.salastroya.bgserver.configuration.R2bcConfiguration
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import


@DataR2dbcTest
@Import(BeaconDataService::class, R2bcConfiguration::class)
class BeaconDataServiceTest {

    @Autowired
    lateinit var beaconDataService: BeaconDataService

    @Test
    fun findAll() {
        runBlocking {
            val list = beaconDataService.findAll().toList()

            assertThat(list).hasSize(0)
        }
    }
}