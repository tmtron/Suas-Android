package com.example.suas.weather.network

// sealed class: only the classes defined in this file can extend NetworkResult
// but in other files we can extend from the child-classes (e.g. SuggestionResult)
// see https://kotlinlang.org/docs/reference/sealed-classes.html
sealed class NetworkResult

// NetworkError extends NetworkResult
data class NetworkError(val error: String): NetworkResult()

// NetworkSuccess extends NetworkResult
sealed class NetworkSuccess<out E>(val data: E): NetworkResult()
data class SuggestionResult(val result: List<NetworkModels.AutocompleteItem>): NetworkSuccess<List<NetworkModels.AutocompleteItem>>(result)
data class ConditionResult(val result: NetworkModels.Observation): NetworkSuccess<NetworkModels.Observation>(result)

typealias NetworkCallback = (result: NetworkResult) -> Unit