package edu.nuce.apps.newflowtypes.util

import edu.nuce.apps.newflowtypes.data.IgnoreAuth
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import timber.log.Timber

class DefaultAuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val method = request.tag(Invocation::class.java)?.method()

        if (method != null && method.isAnnotationPresent(IgnoreAuth::class.java)) {
            Timber.e("has annotation")
        }

        return chain.proceed(request)
    }
}