package pro.horovodovodo4ka.shu.resource

import com.github.kittinunf.fuel.core.Method
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.coders.Decoder
import pro.horovodovodo4ka.shu.coders.Encoder
import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.QueryParameters
import pro.horovodovodo4ka.shu.coders.digInto

class ShuResponse<ResponseType: Any>(val data: ResponseType, val headers: Headers) {
    fun component1() = data
    fun component2() = headers
}

open class ShuOperation<RequestType : Any, ResponseType : Any>(
    private val origin: ShuRemote,
    val method: Method,
    val path: String,
    val queryParameters: QueryParameters? = null,
    val headers: Headers? = null,
    val resourceForSend: RequestType? = null,
    val requestEncoder: () -> Encoder<RequestType> = { TODO() },
    val responseDecoder: () -> Decoder<ResponseType> = { TODO() }
) {

    fun digInto(jsonPath: String) =
        ShuOperation(origin, method, path, queryParameters, headers, resourceForSend, requestEncoder, { responseDecoder().digInto(jsonPath) })

    suspend fun run() = withContext(Dispatchers.IO) { origin.request(this@ShuOperation) }
}