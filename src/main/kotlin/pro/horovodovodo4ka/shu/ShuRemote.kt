package pro.horovodovodo4ka.shu

import com.github.kittinunf.fuel.core.Response
import pro.horovodovodo4ka.astaroth.LogType
import pro.horovodovodo4ka.shu.coders.Decoder
import pro.horovodovodo4ka.shu.resource.ShuOperation
import pro.horovodovodo4ka.shu.resource.ShuResponse

typealias Headers = Map<String, String>
typealias QueryParameters = Map<String, Any?>

object Network : LogType

interface ShuRemote {
    fun addMiddleware(block: Middleware.() -> Unit)
    suspend fun <RequestType : Any, ResponseType : Any> request(operation: ShuOperation<RequestType, ResponseType>): Result<ShuResponse<ResponseType>>
}

@DslMarker
annotation class MiddlewareDsl

interface Middleware {
    @MiddlewareDsl
    fun headers(block: (Decoder<*>) -> Headers?)

    @MiddlewareDsl
    fun requestBarrier(block: suspend (Decoder<*>) -> Unit)

    @MiddlewareDsl
    fun validateResponse(block: (Response) -> Unit)

    @MiddlewareDsl
    fun recover(block: suspend (Decoder<*>, Throwable) -> Unit)

    @MiddlewareDsl
    fun success(block: (ShuResponse<*>) -> Unit)
}
