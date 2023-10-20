package org.berendeev.weather.data

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.berendeev.weather.models.Coordinates
import org.berendeev.weather.network.ForecastDatasource
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ForecastRepositoryImplTest {
    private val datasource = mockk<ForecastDatasource>()
    private var repo: ForecastRepositoryImpl? = null
    private var resultStateFlow: StateFlow<ForecastRepository.Result?>? = null

    @Before
    fun before() {
    }

    @After
    fun cleanInstances() {
        repo = null
        resultStateFlow = null
    }

    @Test
    fun beforeBeingInitializedStateIsNull() = runTest {
        givenNewRepository()
        whenRepoIsObserved()
        thenInitialStateIsNull()
    }

    @Test
    fun successfulInitialization() = runTest {
        givenNewRepository()
        whenDataSourceSucceed()
        whenRepoIsObserved()
        thenFinalResultIsSuccess()
    }


    private fun TestScope.givenNewRepository() {
        repo = ForecastRepositoryImpl(datasource, StandardTestDispatcher(this.testScheduler))
    }

    private suspend fun TestScope.whenRepoIsObserved() {
        val request = ForecastRepository.Request(Coordinates(0.0, 0.0))
        resultStateFlow = requireRepo().observe(request).stateIn(this.backgroundScope)
    }

    private fun whenDataSourceSucceed() {
        coEvery {
            datasource.fetchForecast(any())
        } coAnswers {
            delay(1.seconds)
            ForecastDatasource.ApiModel(
                current_weather = ForecastDatasource.ApiModel.CurrentWeather("time", 11f, 0, 0f, 0)
            )
        }
    }

    private fun thenInitialStateIsNull() {
        val result = requireResultStateFlow().value
        assertNull(result)
    }

    private fun TestScope.thenFinalResultIsSuccess() {
        advanceTimeBy(1.minutes)
        val result = requireResultStateFlow().value
        assertNotNull(result)
    }


    private fun requireResultStateFlow(): StateFlow<ForecastRepository.Result?> {
        return resultStateFlow ?: fail("ForecastRepository is not observed")
    }

    private fun requireRepo(): ForecastRepository {
        return repo ?: fail("ForecastRepository is not initialized")
    }
}