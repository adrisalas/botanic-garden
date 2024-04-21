package com.salastroya.bgserver.infrastructure.poi

import com.salastroya.bgserver.core.poi.model.Poi
import com.salastroya.bgserver.core.poi.port.PoiRepository
import com.salastroya.bgserver.infrastructure.news.repository.PoiR2bcRepository
import com.salastroya.bgserver.infrastructure.poi.mapper.toDto
import com.salastroya.bgserver.infrastructure.poi.mapper.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class PoiDataService(
    private val repository: PoiR2bcRepository
) : PoiRepository {

    override fun findAll(): Flow<Poi> {
        return repository.findAll()
            .map { it.toModel() }
    }


    override suspend fun findById(id: Int): Poi? {
        return repository.findById(id)?.toModel()
    }

    override suspend fun existsById(id: Int): Boolean {
        return repository.existsById(id)
    }

    override suspend fun insert(poi: Poi): Poi {
        return repository.insert(poi.toDto()).toModel()
    }

    override suspend fun update(poi: Poi): Poi {
        return repository.update(poi.toDto()).toModel()
    }

    override suspend fun delete(id: Int) {
        repository.deleteById(id)
    }
}