@file:OptIn(ExperimentalCoroutinesApi::class)

package org.berendeev.weather.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.berendeev.weather.common.ApplicationCoroutineScope
import org.berendeev.weather.data.model.ForecastData
import org.berendeev.weather.models.Coordinates
import org.berendeev.weather.network.ForecastDatasource
import javax.inject.Inject
import javax.inject.Singleton

interface ForecastRepository {
    fun observe(request: Request): Flow<State?>

    class Request(val coordinates: Coordinates) {
        internal val channel = Channel<CompletableDeferred<Unit>>()
        suspend fun update() {
            val deferred = CompletableDeferred<Unit>()
            channel.send(deferred)
            deferred.await()
        }
    }

    sealed interface State {
        data class Error(val message: String?) : State
        data class Success(val forecast: ForecastData, val lastUpdateFailed: Boolean) : State
    }
}

@Singleton
class ForecastRepositoryImpl @Inject constructor(
    private val forecastDatasource: ForecastDatasource,
    @ApplicationCoroutineScope private val coroutineScope: CoroutineScope,
) : ForecastRepository {

    override fun observe(request: ForecastRepository.Request) = flow<ForecastRepository.State?> {
        emit(null)
        var apiModel: ForecastDatasource.ApiModel? = null
        var deferred: CompletableDeferred<Unit>? = null
        while (true) {
            val state = try {
                val newApiModel = withContext(Dispatchers.IO) {
                    forecastDatasource.fetchForecast(request.coordinates)
                }
                apiModel = newApiModel
                ForecastRepository.State.Success(ForecastData(apiModel.current_weather.temperature), false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                if (apiModel == null) {
                    ForecastRepository.State.Error(e.message)
                } else {
                    ForecastRepository.State.Success(ForecastData(apiModel.current_weather.temperature), true)
                }
            }
            deferred?.complete(Unit)
            emit(state)
            deferred = request.channel.receive()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ForecastRepositoryRepoModule {
    @Binds
    @Singleton
    abstract fun binds(impl: ForecastRepositoryImpl): ForecastRepository
}
