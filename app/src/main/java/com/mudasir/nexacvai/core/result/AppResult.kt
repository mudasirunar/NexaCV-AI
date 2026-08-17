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
    UNAUTHORIZED,
    QUOTA_EXCEEDED,
    DATABASE,
    UNKNOWN
}

/**
 * Executes [action] if this result is [AppResult.Success].
 */
inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

/**
 * Executes [action] if this result is [AppResult.Error].
 */
inline fun <T> AppResult<T>.onError(action: (Throwable, ErrorType) -> Unit): AppResult<T> {
    if (this is AppResult.Error) action(exception, errorType)
    return this
}

/**
 * Returns the encapsulated data if this instance is [AppResult.Success] or null otherwise.
 */
fun <T> AppResult<T>.getOrNull(): T? = (this as? AppResult.Success)?.data

/**
 * Returns the encapsulated data if this instance is [AppResult.Success] or [default] otherwise.
 */
fun <T> AppResult<T>.getOrDefault(default: T): T = (this as? AppResult.Success)?.data ?: default

/**
 * Transforms the encapsulated data if this instance is [AppResult.Success].
 */
inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> {
    return when (this) {
        is AppResult.Success -> AppResult.Success(transform(data))
        is AppResult.Error -> this
        is AppResult.Loading -> AppResult.Loading
    }
}
