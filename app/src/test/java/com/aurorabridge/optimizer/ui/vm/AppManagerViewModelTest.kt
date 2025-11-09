package com.aurorabridge.optimizer.ui.vm

import com.aurorabridge.optimizer.repository.AppRepository
import com.aurorabridge.optimizer.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class AppManagerViewModelTest {

    // Используем Mockito для создания "фальшивых" версий зависимостей
    @Mock
    private lateinit var appRepository: AppRepository

    private lateinit var viewModel: AppManagerViewModel

    // Диспетчер для тестирования корутин
    private val testDispatcher = StandardTestDispatcher()

    // Этот код выполняется перед каждым тестом
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher) // Заменяем главный диспетчер на тестовый
        viewModel = AppManagerViewModel(appRepository)
    }

    // Этот код выполняется после каждого теста
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Возвращаем обратно основной диспетчер
    }

    @Test
    fun `test initial state loads user apps`() = runTest {
        // Подготовка: Создаем тестовый список приложений
        val userApps = listOf(AppItem("com.example.app1", "App 1", true, false))
        `when`(appRepository.getApps(false)).thenReturn(userApps)

        // Действие: не требуется, viewModel инициализируется в setUp

        // Проверка: Убеждаемся, что LiveData содержит правильные данные
        advanceUntilIdle() // Прокручиваем корутины до завершения
        assertEquals(userApps, viewModel.apps.value)
    }

    @Test
    fun `test filter changes to system apps`() = runTest {
        // Подготовка
        val systemApps = listOf(AppItem("com.android.system", "System App", false, true))
        `when`(appRepository.getApps(true)).thenReturn(systemApps)

        // Действие: Вызываем функцию для смены фильтра
        viewModel.onFilterChanged(AppManagerViewModel.AppFilter.SYSTEM)

        // Проверка
        advanceUntilIdle()
        verify(appRepository).getApps(true) // Проверяем, что был вызван метод с правильным параметром
        assertEquals(systemApps, viewModel.apps.value)
    }
}
