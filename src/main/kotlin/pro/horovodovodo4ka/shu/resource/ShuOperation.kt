package pro.horovodovodo4ka.shu.resource

import com.github.kittinunf.fuel.core.Method
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.QueryParameters
import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.coders.Decoder
import pro.horovodovodo4ka.shu.coders.Encoder

class ShuRawResponse(val httpStatusCode: Int, val headers: Headers, body: () -> ByteArray) {
    val body by lazy { body() }
}

class ShuResponse<ResponseType : Any>(val value: ResponseType, val rawResponse: ShuRawResponse) {
    operator fun component1() = value
    operator fun component2() = rawResponse.httpStatusCode
    operator fun component3() = rawResponse.headers
    operator fun component4() = rawResponse.body
}

class ShuOperationException(override val cause: Throwable, val rawResponse: ShuRawResponse) : Throwable(cause) {
    operator fun component1() = cause
    operator fun component2() = rawResponse.httpStatusCode
    operator fun component3() = rawResponse.headers
    operator fun component4() = rawResponse.body
}

class ShuOperation<RequestType : Any, ResponseType : Any>(
    val origin: ShuRemote,
    val method: Method,
    val path: String,
    val queryParameters: QueryParameters? = null,
    val headers: Headers? = null,
    val resourceForSend: RequestType? = null,
    val requestEncoder: () -> Encoder<RequestType> = { TODO() },
    val responseDecoder: () -> Decoder<ResponseType> = { TODO() }
) {
    /**
     * [context] is used for tracking call point. Should not be overridden directly/
     */
    suspend fun run(context: StackTraceElement? = Throwable().stackTrace.getOrNull(1)) = withContext(Dispatchers.IO) { origin.request(this@ShuOperation, context) }
}
