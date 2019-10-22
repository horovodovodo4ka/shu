package pro.horovodovodo4ka.shu.coders

import com.github.fluidsonic.fluid.json.JSONReader
import com.github.fluidsonic.fluid.json.JSONWriter
import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.types.KodablePath

interface Decoder<T> : IKodable<T> {
    override val list: Decoder<List<T>>
    override val dictionary: Decoder<Map<String, T>>
}

interface InnerDecoder<T> : Decoder<T> {
    val jsonPath: KodablePath? get() = null
}

class DefaultDecoder<T>(private val nesting: IKodable<T>, override val jsonPath: KodablePath? = null) :
    InnerDecoder<T> {
    override fun readValue(reader: JSONReader): T = nesting.readValue(reader)
    override fun writeValue(writer: JSONWriter, instance: T) = nesting.writeValue(writer, instance)

    override val list: Decoder<List<T>>
        get() = DefaultDecoder(nesting.list, jsonPath)

    override val dictionary: Decoder<Map<String, T>>
        get() = DefaultDecoder(nesting.dictionary, jsonPath)
}

val <T> IKodable<T>.decoder: Decoder<T> get() = DefaultDecoder(this)
fun <T> Decoder<T>.digInto(path: String) = DefaultDecoder(this, KodablePath(path))