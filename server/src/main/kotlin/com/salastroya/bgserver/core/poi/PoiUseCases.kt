package com.salastroya.bgserver.core.poi

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.poi.model.Poi
import com.salastroya.bgserver.core.poi.port.PoiRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PoiUseCases(
    private val repository: PoiRepository,
) {

    fun findAll(): Flow<Poi> {
        return repository.findAll()
    }

    suspend fun findById(id: Int): Poi? {
        return repository.findById(id)
    }

    suspend fun existsById(id: Int): Boolean {
        return repository.existsById(id)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun insert(poi: Poi): Poi {
        if (poi.id != null) {
            throw InvalidUseCaseException("Point of interest id cannot be provided for insert")
        }

        return repository.insert(poi)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun update(poi: Poi): Poi {
        if (poi.id == null) {
            throw InvalidUseCaseException("Point of interest id cannot be null for update")
        }
        if (!repository.existsById(poi.id)) {
            throw InvalidUseCaseException("Point of interest with id ${poi.id} does not exist")
        }

        return repository.update(poi)
    }

    @Transactional
    suspend fun delete(id: Int) {
        repository.delete(id)
    }
}