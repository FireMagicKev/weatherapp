package com.knewman.weathertest.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.knewman.weathertest.models.CurrentWeatherResponse
import com.knewman.weathertest.network.CurrentWeatherManager
import com.knewman.weathertest.util.PreferencesManager
import com.knewman.weathertest.util.state.State
import com.knewman.weathertest.ui.search.SearchViewModel
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class SearchViewModelTest : TestCase() {
    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)

    @get:Rule
    val testInstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var currentWeatherManager: CurrentWeatherManager

    @Mock
    private lateinit var preferencesManager: PreferencesManager

    @Mock
    private lateinit var viewModel: SearchViewModel

    private var successfulResponse: CurrentWeatherResponse? = null
    private val json = Json { }

    private val query = "Atlanta,GA,US"
    private val lat = 37.422740
    private val lng = -122.139956

    @Before
    public override fun setUp() {
        super.setUp()
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(currentWeatherManager, preferencesManager)

        successfulResponse = try {
            val stream = javaClass.getResourceAsStream("/current_weather_response_success.json") ?: return
            json.decodeFromStream<CurrentWeatherResponse>(stream)
        } catch (e: Exception) {
            null
        }
    }

    @Test
    fun shouldParseJsonAsResponse() = testScope.runTest {
        val successfulResponse = successfulResponse
        if (successfulResponse == null) {
            fail("successResponse is null")
        }

        assert(successfulResponse?.weather?.isNotEmpty() == true)
    }

    @Test
    fun shouldEmitSuccessState() = testScope.runTest {
        val successfulResponse = successfulResponse
        if (successfulResponse == null) {
            fail("successResponse is null")
            return@runTest
        }

        Mockito.`when`(currentWeatherManager.fetchWeatherByCity(Mockito.anyString()))
            .thenReturn(State.Success(successfulResponse))

        viewModel.fetchWeatherResultsByCityInput()
        advanceUntilIdle()
        assertEquals(viewModel.weatherResults.value, State.Success(successfulResponse))
    }

    @Test
    fun shouldEmitErrorState() = testScope.runTest {
        val exception = Exception()
        Mockito.`when`(currentWeatherManager.fetchWeatherByCity(Mockito.anyString())).thenReturn(State.Error(exception))

        viewModel.fetchWeatherResultsByCityInput()
        advanceUntilIdle()
        assertEquals(viewModel.weatherResults.value, State.Error(exception))
    }

    @After
    public override fun tearDown() {
        super.tearDown()
        Dispatchers.resetMain()
    }
}