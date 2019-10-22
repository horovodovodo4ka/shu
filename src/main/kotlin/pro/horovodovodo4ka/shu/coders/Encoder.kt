package pro.horovodovodo4ka.shu.coders

import com.github.fluidsonic.fluid.json.JSONReader
import com.github.fluidsonic.fluid.json.JSONWriter
import pro.horovodovodo4ka.kodable.core.IKodable

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