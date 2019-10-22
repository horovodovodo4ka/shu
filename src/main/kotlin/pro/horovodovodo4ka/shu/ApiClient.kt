package pro.horovodovodo4ka.shu

import com.github.fluidsonic.fluid.json.JSONReader
import com.github.fluidsonic.fluid.json.JSONWriter
import com.github.kittinunf.fuel.core.Response
import pro.horovodovodo4ka.astaroth.LogType
import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.types.KodablePath

typealias Headers = Map<String, String>
typealias QueryParameters = Map<String, Any?>
typealias ResourceTypeWithHeaders<ResultType> = Pair<ResultType, Headers>
typealias ResourceAnyResponse = ResourceTypeWithHeaders<Any?>
typealias ResourceResponseWithHeaders<ResultType> = Result<ResourceTypeWithHeaders<ResultType>>

object Network : LogType

///

interface Decoder<T> : IKodable<T> {
    override val list: Decoder<List<T>>
    override val dictionary: Decoder<Map<String, T>>
}

interface InnerDecoder<T> : Decoder<T> {
    val jsonPath: KodablePath? get() = null
}

class DefaultDecoder<T>(private val nesting: IKodable<T>, override val jsonPath: KodablePath? = null) : InnerDecoder<T> {
    override fun readValue(reader: JSONReader): T = nesting.readValue(reader)
    override fun writeValue(writer: JSONWriter, instance: T) = nesting.writeValue(writer, instance)

    override val list: Decoder<List<T>>
        get() = DefaultDecoder(nesting.list, jsonPath)

    override val dictionary: Decoder<Map<String, T>>
        get() = DefaultDecoder(nesting.dictionary, jsonPath)
}

val <T> IKodable<T>.decoder: Decoder<T> get() = DefaultDecoder(this)
fun <T> Decoder<T>.digInto(path: String) = DefaultDecoder(this, KodablePath(path))

////
interface Encoder<T> : IKodable<T> {
    override val list: Encoder<List<T>>
    override val dictionary: Encoder<Map<String, T>>
}

class DefaultEncoder<T>(private val nesting: IKodable<T>) : Encoder<T> {

    override fun readValue(reader: JSONReader): T = nesting.readValue(reader)
    override fun writeValue(writer: JSONWriter, instance: T) = nesting.writeValue(writer, instance)

    override val list: Encoder<List<T>>
        get() = DefaultEncoder((this as Encoder<T>).list)

    override val dictionary: Encoder<Map<String, T>>
        get() = DefaultEncoder((this as Encoder<T>).dictionary)
}

val <T> IKodable<T>.encoder: Encoder<T> get() = DefaultEncoder(this)

////
interface Coder<T> : Decoder<T>, Encoder<T> {
    override val list: Coder<List<T>>
    override val dictionary: Coder<Map<String, T>>
}

interface InnerCoder<T> : Coder<T>, InnerDecoder<T>

class DefaultCoder<T>(private val nesting: IKodable<T>, override val jsonPath: KodablePath? = null) : InnerCoder<T> {

    override fun readValue(reader: JSONReader): T = nesting.readValue(reader)
    override fun writeValue(writer: JSONWriter, instance: T) = nesting.writeValue(writer, instance)

    override val list: Coder<List<T>>
        get() = DefaultCoder(nesting.list, jsonPath)

    override val dictionary: Coder<Map<String, T>>
        get() = DefaultCoder(nesting.dictionary, jsonPath)
}

val <T> IKodable<T>.coder: Coder<T> get() = DefaultCoder(this)
fun <T> Coder<T>.digInto(path: String) = DefaultCoder(this, KodablePath(path))
///

interface ApiClient {

    fun addMiddleware(block: Middleware.() -> Unit)

    suspend fun <RequestType : Any, ResponseType : Any> request(operation: Operation<RequestType, ResponseType>): ResourceResponseWithHeaders<ResponseType>
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
    fun success(block: (ResourceAnyResponse) -> Unit)
}
