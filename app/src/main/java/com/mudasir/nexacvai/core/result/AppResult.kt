package com.mudasir.nexacvai.core.result

/**
 * Standardized sealed interface for repository and use case result wrappers.
 */
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Error(val exception: Throwable, val errorType: ErrorType = ErrorType.UNKNOWN) : AppResult<Nothing>
    object Loading : AppResult<Nothing>
}

enum class ErrorType {
    NOT_FOUND,
    VALIDATION,
    NETWORK,
    UNKNOWN
}
