package pro.horovodovodo4ka.shu.coders

import com.github.fluidsonic.fluid.json.JSONReader
import com.github.fluidsonic.fluid.json.JSONWriter
import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.types.KodablePath

////
interface Coder<T> : Decoder<T>, Encoder<T> {
    override val list: Coder<List<T>>
    override val dictionary: Coder<Map<String, T>>
}

interface InnerCoder<T> : Coder<T>, InnerDecoder<T>
class DefaultCoder<T>(private val nesting: IKodable<T>, override val jsonPath: KodablePath? = null) :
    InnerCoder<T> {

    override fun readValue(reader: JSONReader): T = nesting.readValue(reader)
    override fun writeValue(writer: JSONWriter, instance: T) = nesting.writeValue(writer, instance)

    override val list: Coder<List<T>>
        get() = DefaultCoder(nesting.list, jsonPath)

    override val dictionary: Coder<Map<String, T>>
        get() = DefaultCoder(nesting.dictionary, jsonPath)
}

val <T> IKodable<T>.coder: Coder<T> get() = DefaultCoder(this)
fun <T> Coder<T>.digInto(path: String) = DefaultCoder(this, KodablePath(path))