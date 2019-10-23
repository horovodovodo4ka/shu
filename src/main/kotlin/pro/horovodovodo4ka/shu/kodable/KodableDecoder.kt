package pro.horovodovodo4ka.shu.kodable

import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.types.KodablePath
import pro.horovodovodo4ka.kodable.core.utils.dekode
import pro.horovodovodo4ka.shu.coders.Decoder
import pro.horovodovodo4ka.shu.resource.ShuOperation

interface InnerDecoder<T> : Decoder<T> {
    val jsonPath: KodablePath? get() = null
    fun digInto(path: String): InnerDecoder<T>
}

fun <T : Any, V : Any> ShuOperation<T, V>.digInto(jsonPath: String): ShuOperation<T, V> {
    val decoder = responseDecoder() as? InnerDecoder ?: return this
    return ShuOperation(origin, method, path, queryParameters, headers, resourceForSend, requestEncoder, { decoder.digInto(jsonPath) })
}

///

class KodableDecoder<T>(private val nesting: IKodable<T>, override val jsonPath: KodablePath? = null) :
    InnerDecoder<T> {

    override fun decode(from: String): T = nesting.dekode(from, jsonPath)

    override val list: Decoder<List<T>>
        get() = KodableDecoder(nesting.list, jsonPath)

    override val dictionary: Decoder<Map<String, T>>
        get() = KodableDecoder(nesting.dictionary, jsonPath)
    override fun digInto(path: String) = KodableDecoder(nesting, KodablePath(path))

}

val <T> IKodable<T>.decoder get() = KodableDecoder(this)
