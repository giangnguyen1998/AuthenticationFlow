package edu.nuce.apps.newflowtypes.util

import retrofit2.HttpException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import edu.nuce.apps.newflowtypes.shared.result.Result

object NetworkErrorCodeConverter {

    fun resolveError(e: Exception): Result.Error {
        var error = e

        when(e) {
            is SocketTimeoutException -> {
                error = NetworkErrorException(errorMessage = "Connection error!")
            }
            is ConnectException -> {
                error = NetworkErrorException(errorMessage = "No internet access!")
            }
            is UnknownHostException -> {
                error = NetworkErrorException(errorMessage = "No internet access!")
            }
        }

        if (e is HttpException) {
            when(e.code()) {
                HttpURLConnection.HTTP_BAD_GATEWAY -> {
                    error = NetworkErrorException(e.code(), "Internal error!")
                }
                HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                    error = NetworkErrorException(e.code(), "Internal error!")
                }
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    error = AuthenticationException("authentication error!")
                }
                HttpURLConnection.HTTP_BAD_REQUEST -> {
                    error = NetworkErrorException.parseException(e)
                }
            }
        }

        return Result.Error(error)
    }
}