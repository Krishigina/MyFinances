package com.myfinances.ui.screens.articles

import com.myfinances.ui.components.ListItemModel

/**
 * Определяет все возможные состояния UI для экрана "Статьи".
 */
sealed interface ArticlesUiState {
    /**
     * Состояние загрузки данных с сервера.
     */
    data object Loading : ArticlesUiState

    /**
     * Успешное состояние, когда список категорий загружен.
     * @param query Текущий поисковый запрос, введенный пользователем.
     * @param categoryItems Отфильтрованный список статей (категорий) для отображения.
     */
    data class Success(
        val query: String,
        val categoryItems: List<ListItemModel>
    ) : ArticlesUiState

    /**
     * Состояние ошибки при загрузке.
     * @param message Сообщение об ошибке.
     */
    data class Error(val message: String) : ArticlesUiState

    /**
     * Состояние отсутствия интернет-соединения.
     */
    data object NoInternet : ArticlesUiState
}