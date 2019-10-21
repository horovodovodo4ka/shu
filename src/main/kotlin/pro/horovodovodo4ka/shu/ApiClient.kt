package pro.horovodovodo4ka.shu

import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Method.GET
import com.github.kittinunf.fuel.core.Response
import pro.horovodovodo4ka.astaroth.LogType
import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.types.KodablePath

// Simple wrapper around kotlin.Result due restrictions of using it
class Result<T> {
    private val result: kotlin.Result<T>

    constructor(value: T) {
        this.result = kotlin.Result.success(value)
    }

    constructor(exception: Throwable) {
        this.result = kotlin.Result.failure(exception)
    }

    private constructor(result: kotlin.Result<T>, dummy: Unit = Unit) {
        this.result = result
    }

    fun getOrNull(): T? = result.getOrNull()
    fun exceptionOrNull(): Throwable? = result.exceptionOrNull()
    override fun toString() = result.toString()

    val isSuccess: Boolean get() = result.isSuccess
    val isFailure: Boolean get() = result.isFailure

    fun getOrThrow(): T = result.getOrThrow()
    fun getOrDefault(defaultValue: T): T = result.getOrDefault(defaultValue)

    fun getOrElse(onFailure: (exception: Throwable) -> T): T = result.getOrElse(onFailure)
    fun fold(onSuccess: (value: T) -> T, onFailure: (exception: Throwable) -> T): T = result.fold(onSuccess, onFailure)

    fun map(transform: (value: T) -> T): Result<T> = Result(result.map(transform))
    fun recover(transform: (exception: Throwable) -> T): Result<T> = Result(result.recover(transform))

    fun <R> mapCatching(transform: (value: T) -> R): Result<R> = Result(result.mapCatching(transform))
    fun recoverCatching(transform: (exception: Throwable) -> T): Result<T> = Result(result.recoverCatching(transform))

    fun onSuccess(action: (value: T) -> Unit): Result<T> = Result(result.onSuccess(action))
    fun onFailure(action: (exception: Throwable) -> Unit): Result<T> = Result(result.onFailure(action))
}

fun <T> kotlin.Result<T>.asApiResult(): Result<T> = fold({ Result(it) }, { Result(it) })

typealias Headers = Map<String, String>
typealias QueryParameters = Map<String, Any?>
typealias AnyResponse = Pair<Any?, Headers>
typealias ResourceResponseWithHeaders<ResultType> = Result<Pair<ResultType, Headers>>

object Network : LogType

interface Decoder<T : Any> {
    val decodable: IKodable<T>
    val jsonPath: KodablePath? get() = null
}

interface Encoder<T : Any> {
    val encodable: IKodable<T>
}

interface Coder<T : Any> : Decoder<T>, Encoder<T>

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
