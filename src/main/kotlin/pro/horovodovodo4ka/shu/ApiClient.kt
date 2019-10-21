package pro.horovodovodo4ka.shu

import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Method.GET
import com.github.kittinunf.fuel.core.Response
import pro.horovodovodo4ka.astaroth.LogType
import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.types.KodablePath

typealias Headers = Map<String, String>
typealias QueryParameters = Map<String, Any?>
typealias AnyResponse = Pair<Any?, Headers>
typealias ResourceResponseWithHeaders<ResultType> = Result<Pair<ResultType, Headers>, Exception>

object Network : LogType

interface Decoder<T : Any> {
    val decodable: IKodable<T>
    val jsonPath: KodablePath? get() = null
}

interface Encoder<T : Any> {
    val encodable: IKodable<T>
}

interface Coder<T : Any> : Decoder<T>, Encoder<T>


fun <T: Any> kotlin.Result<T>.asApiResult(): Result<T, Throwable> = fold({ Result.success(it) }, { Result.error(it) })

///

interface ApiClient {

    fun addMiddleware(block: Middleware.() -> Unit)

    suspend fun <ResultType : Any> requestCollection(
        path: String,
        query: QueryParameters? = null,
        customHeader: Headers? = null,
        responseDecoder: Decoder<ResultType>
    ): ResourceResponseWithHeaders<List<ResultType>>

    suspend fun <ResultType : Any, RequestType : Any> requestResource(
        path: String,
        resource: RequestType? = null,
        query: QueryParameters? = null,
        method: Method = GET,
        customHeader: Headers? = null,
        requestEncoder: Encoder<RequestType>,
        responseDecoder: Decoder<ResultType>
    ): ResourceResponseWithHeaders<ResultType?>
}

interface Middleware {
    fun headers(block: (Decoder<*>) -> Headers?)
    fun requestBarrier(block: suspend (Decoder<*>) -> Unit)
    fun validateResponse(block: (Response) -> Unit)
    fun recover(block: suspend (Decoder<*>, Throwable) -> Unit)
    fun success(block: (AnyResponse) -> Unit)
}

val moreDataPath = KodablePath(".data")
val moreDataItemsPath = KodablePath(".data.items")
val moreErrorPath = KodablePath(".errors")
