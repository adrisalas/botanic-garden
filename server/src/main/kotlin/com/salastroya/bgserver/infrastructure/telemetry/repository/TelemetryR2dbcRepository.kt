package com.salastroya.bgserver.infrastructure.telemetry.repository

import com.salastroya.bgserver.infrastructure.telemetry.dto.ItemVisitCount
import com.salastroya.bgserver.infrastructure.telemetry.dto.TelemetryDataDto
import com.salastroya.bgserver.infrastructure.telemetry.dto.VisitorsPerDayDto
import com.salastroya.bgserver.infrastructure.telemetry.dto.VisitorsPerHourDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TelemetryR2dbcRepository : CoroutineCrudRepository<TelemetryDataDto, Long> {
    suspend fun insert(telemetryDataDto: TelemetryDataDto): TelemetryDataDto

    @Query(
        """
            SELECT 
                date_trunc('day', date_time) AS day, 
                COUNT(DISTINCT username) AS unique_visitors 
            FROM 
                telemetry_data 
            GROUP BY 
                day 
            ORDER BY 
                day DESC
        """
    )
    fun visitorsPerDay(): Flow<VisitorsPerDayDto>


    @Query(
        """
            SELECT 
                EXTRACT(HOUR FROM date_time) AS hour_of_day,
                COUNT(DISTINCT username) AS unique_visitors
            FROM 
                telemetry_data
            GROUP BY 
                EXTRACT(HOUR FROM date_time)
            ORDER BY 
                hour_of_day;
        """
    )
    fun visitorsPerHour(): Flow<VisitorsPerHourDto>

    @Query(
        """
            SELECT 
                item_id,
                COUNT(*) AS visits
            FROM (
                SELECT 
                    item_id,
                    username,
                    DATE(date_time) AS day
                FROM 
                    telemetry_data
                WHERE 
                    item_type = 'PLANT'
                GROUP BY 
                    item_id,
                    username,
                    day
            ) AS unique_visits
            GROUP BY 
                item_id
            ORDER BY 
                visits DESC
        """
    )
    fun plantsVisitCount(): Flow<ItemVisitCount>

    @Query(
        """
            SELECT 
                item_id,
                COUNT(*) AS visits
            FROM (
                SELECT 
                    item_id,
                    username,
                    DATE(date_time) AS day
                FROM 
                    telemetry_data
                WHERE 
                    item_type = 'POI'
                GROUP BY 
                    item_id,
                    username,
                    day
            ) AS unique_visits
            GROUP BY 
                item_id
            ORDER BY 
                visits DESC
        """
    )
    fun poiVisitCount(): Flow<ItemVisitCount>
}