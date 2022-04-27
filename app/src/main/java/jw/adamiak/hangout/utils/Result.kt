package jw.adamiak.hangout.utils

sealed class Result {
	data class Success(val successMessage: String): Result()
	object Loading: Result()
	data class Error(val error: String): Result()
}

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
	class Loading<T>(data: T? = null): Resource<T>(data)
	class Success<T>(data: T?): Resource<T>(data)
	class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
}