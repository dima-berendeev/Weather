package org.berendeev.weather.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.berendeev.weather.data.model.LocationMode
import org.berendeev.weather.models.Coordinates
import javax.inject.Inject

interface LocationModeRepository {
    val state: Flow<LocationMode>

    fun setMode(mode: LocationMode)
}

class FakeLocationModeRepository @Inject constructor() : LocationModeRepository {
    override val state: Flow<LocationMode> = flowOf(LocationMode.Fixed("Amsterdam", Coordinates(1.0, 1.0)))

    override fun setMode(mode: LocationMode) {
        TODO("Not yet implemented")
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FakeLocationModeRepositoryModule {
    @Binds
    abstract fun binds(impl: FakeLocationModeRepository): LocationModeRepository
}