package com.example.suas.weather.network


sealed class NetworkResult

data class NetworkError(val error: String): NetworkResult()

sealed class Success<out E>(val data: E): NetworkResult()
data class SuggestionResult(val result: List<NetworkModels.AutocompleteItem>): Success<List<NetworkModels.AutocompleteItem>>(result)
data class ConditionResult(val result: NetworkModels.Observation): Success<NetworkModels.Observation>(result)

typealias Callback = (result: NetworkResult) -> Unit