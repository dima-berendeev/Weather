package org.berendeev.weather.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.berendeev.weather.data.model.LocationMode
import javax.inject.Inject
import javax.inject.Singleton

interface LocationModeRepository {
    val state: StateFlow<LocationMode?>

    fun setMode(mode: LocationMode)
}

class FakeLocationModeRepository @Inject constructor() : LocationModeRepository {
    override val state: MutableStateFlow<LocationMode?> = MutableStateFlow(null)

    override fun setMode(mode: LocationMode) {
        state.value = mode
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FakeLocationModeRepositoryModule {
    @Binds
    @Singleton
    abstract fun binds(impl: FakeLocationModeRepository): LocationModeRepository
}