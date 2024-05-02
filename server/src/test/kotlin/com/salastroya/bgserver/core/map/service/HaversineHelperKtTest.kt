package com.salastroya.bgserver.core.map.service

import com.salastroya.bgserver.core.map.model.Point
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class HaversineHelperKtTest {
    @Test
    fun distanceBetweenNashvilleAndLosAngeles() {
        val nashville = Point(null, 36.12, -86.67, emptyList())
        val losAngeles = Point(null, 33.94, -118.40, emptyList())

        val output = distanceBetweenTwoGeoPointsInMeters(nashville, losAngeles)

        assertThat(output).isEqualTo(2_887_259.95)
    }

    @Test
    fun distanceBetweenRondaAndMalagaUniversity() {
        val ronda = Point(null, 36.740655, -5.165849, emptyList())
        val uma = Point(null, 36.715185, -4.478087, emptyList())

        val output = distanceBetweenTwoGeoPointsInMeters(ronda, uma)

        assertThat(output).isEqualTo(61_376.63)
    }

    @Test
    fun orderDoesNotMatter() {
        val ronda = Point(null, 36.740655, -5.165849, emptyList())
        val uma = Point(null, 36.715185, -4.478087, emptyList())

        val rondaToUma = distanceBetweenTwoGeoPointsInMeters(ronda, uma)
        val umaToRonda = distanceBetweenTwoGeoPointsInMeters(uma, ronda)

        assertThat(rondaToUma).isEqualTo(umaToRonda)
    }
}