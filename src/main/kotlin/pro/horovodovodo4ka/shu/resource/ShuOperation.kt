package pro.horovodovodo4ka.shu.resource

import com.github.kittinunf.fuel.core.Method
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.QueryParameters
import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.coders.Decoder
import pro.horovodovodo4ka.shu.coders.Encoder

class ShuResponse<ResponseType : Any>(val data: ResponseType, val headers: Headers) {
    fun component1() = data
    fun component2() = headers
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
    suspend fun run() = withContext(Dispatchers.IO) { origin.request(this@ShuOperation) }
}