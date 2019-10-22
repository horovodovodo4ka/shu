package pro.horovodovodo4ka.shu

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method.DELETE
import com.github.kittinunf.fuel.core.Method.GET
import com.github.kittinunf.fuel.core.Method.POST
import com.github.kittinunf.fuel.core.Method.PUT
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.interceptors.redirectResponseInterceptor
import com.github.kittinunf.fuel.core.requests.tryCancel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import pro.horovodovodo4ka.astaroth.Log
import pro.horovodovodo4ka.astaroth.d
import pro.horovodovodo4ka.astaroth.e
import pro.horovodovodo4ka.astaroth.i
import pro.horovodovodo4ka.kodable.core.utils.dekode
import pro.horovodovodo4ka.kodable.core.utils.enkode
import pro.horovodovodo4ka.shu.extension.headersMap
import pro.horovodovodo4ka.shu.extension.uriQueryString
import java.net.URI
import java.util.*

class ApiClientImpl(private val apiUrl: String) : ApiClient {

    private var manager = FuelManager()

    init {
        manager.basePath = apiUrl

        manager.removeAllResponseInterceptors()
        manager.addResponseInterceptor(redirectResponseInterceptor(manager))
        addValidator(::validateWithMiddlewares)
    }

    private class MiddlewareImpl : Middleware {

        var headersImpl: ((Decoder<*>) -> Headers?)? = null
        var requestBarrierImpl: (suspend (Decoder<*>) -> Unit)? = null

        var validateResponseImpl: ((Response) -> Unit)? = null

        var recoverImpl: (suspend (Decoder<*>, Throwable) -> Unit)? = null
        var successImpl: ((ResourceAnyResponse) -> Unit)? = null

        override fun headers(block: (Decoder<*>) -> Headers?) {
            headersImpl = block
        }

        override fun requestBarrier(block: suspend (Decoder<*>) -> Unit) {
            requestBarrierImpl = block
        }

        override fun validateResponse(block: (Response) -> Unit) {
            validateResponseImpl = block
        }

        override fun recover(block: suspend (Decoder<*>, Throwable) -> Unit) {
            recoverImpl = block
        }

        override fun success(block: (ResourceAnyResponse) -> Unit) {
            successImpl = block
        }
    }

    private val middlewares = mutableListOf<MiddlewareImpl>()

    override fun addMiddleware(block: Middleware.() -> Unit) {
        middlewares.add(MiddlewareImpl().also(block))
    }

    private fun validateWithMiddlewares(response: Response) {
        middlewares.mapNotNull { it.validateResponseImpl }.forEach { it.invoke(response) }
    }

    private fun fullUrl(path: String, query: QueryParameters? = null): String {
        val url = URI("$apiUrl/$path")
        val urlPath = url.path?.replace(Regex("/+"), "/") ?: ""
        val newUrl = with(url) {
            val newQuery = listOfNotNull(this.query, query?.uriQueryString()).takeIf { it.isNotEmpty() }?.joinToString("&")
            URI(scheme, userInfo, host, port, urlPath, newQuery, fragment)
        }
        return newUrl.toString()
    }

    private suspend fun <T : Any> runRequest(request: Request, decoder: Decoder<T>): Pair<T, Headers> = coroutineScope {
        val requestJob = async {
            val (result, response) = request.response(decoder)
            result to response.headersMap
        }

        requestJob.invokeOnCompletion {
            if (it !is CancellationException) return@invokeOnCompletion
            request.tryCancel()
        }

        requestJob.await()
    }

    override suspend fun <RequestType : Any, ResponseType : Any> request(operation: Operation<RequestType, ResponseType>): ResourceResponseWithHeaders<ResponseType> = Result.of {
        with(operation) {
            val body = when (method) {
                GET, DELETE -> null
                POST, PUT -> resourceForSend?.let { requestEncoder().enkode(it) }
                else -> throw Exception("Unsupported HTTP method $method")
            }

            val decoder = responseDecoder()

            val fullUrl = fullUrl(path)
            val queryList = queryParameters?.toList()

            makeJob(decoder) { state ->

                val headersList = (middlewares.mapNotNull { it.headersImpl?.invoke(decoder) } + headers).filterNotNull()

                val headers = headersList.reduce { acc, map -> acc + map }

                val request = manager.request(method, fullUrl, queryList).header(headers)

                body?.also { request.body(it) }
                state.request = request

                runRequest(request, decoder)
            }
        }
    }

    private class RequestHolder<T : ResourceAnyResponse>(
        var request: Request? = null,
        var task: (RequestHolder<T>) -> Deferred<T>
    )

    private suspend fun <T : ResourceTypeWithHeaders<M>, M : Any> makeJob(mapper: Decoder<M>, block: suspend (RequestHolder<T>) -> T): T = coroutineScope {
        val state = RequestHolder<T> {
            async {
                try {
                    checkForError(mapper) { block(it) }
                } catch (e: Exception) {
                    it.request?.tryCancel()
                    it.task(it).cancel()
                    throw e
                }
            }
        }

        state.task(state).await()
    }

    private suspend fun <T : ResourceTypeWithHeaders<M>, M : Any> checkForError(mapper: Decoder<M>, request: suspend () -> T): T {
        return try {
            middlewares.mapNotNull { it.requestBarrierImpl }.map { it(mapper) }
            request().also { result -> middlewares.mapNotNull { it.successImpl }.forEach { it(result) } }
        } catch (e: CancellationException) {
            throw e
        } catch (exception: Exception) {
            val error = when (exception) {
                is FuelError -> exception.exception
                else -> exception
            }
            val results = middlewares
                .mapNotNull { it.recoverImpl }
                .map { Result.of { it(mapper, error) } }

            results
                .firstOrNull { it.isSuccess }
                ?.let {
                    checkForError(mapper, request)
                }
                ?: throw results.lastOrNull { it.isFailure }?.exceptionOrNull() ?: error
        }
    }

    private suspend fun <T : Any> Request.response(decoder: Decoder<T>): Pair<T, Response> {
        Log.d(Network, "\n$this")

        val start = Date()

        val (request, response, result) = request.timeout(15_000).awaitStringResponseResult()

        val delta = Date().time - start.time

        result.fold({
            Log.i(Network, lazyMessage = { "\n$request\n---<time: ${delta}ms>---\n\n$response" })
        }, {
            Log.e(Network, lazyMessage = { "\n$request\n---<time: ${delta}ms>---\n\n$response\n\nerror: $it" })
        })

        val value = decoder.dekode(result.get(), (decoder as? InnerDecoder)?.jsonPath)
        return value to response
    }

//region=========VALIDATORS==========================

    private fun addValidator(validator: (Response) -> Unit) {
        manager.addResponseInterceptor { next: (Request, Response) -> Response ->
            { request: Request, response: Response ->
                validator(response)
                next(request, response)
            }
        }
    }

//endregion================================
}
