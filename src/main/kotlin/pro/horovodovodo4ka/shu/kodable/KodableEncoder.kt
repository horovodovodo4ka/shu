package pro.horovodovodo4ka.shu.kodable

import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.utils.enkode
import pro.horovodovodo4ka.shu.coders.Encoder

class KodableEncoder<T: Any>(private val nesting: IKodable<T>) : Encoder<T> {
    override fun encode(instance: T): String = nesting.enkode(instance)

    override val list: Encoder<List<T>>
        get() = KodableEncoder(nesting.list)

    override val dictionary: Encoder<Map<String, T>>
        get() = KodableEncoder(nesting.dictionary)
}

val <T: Any> IKodable<T>.encoder get() = KodableEncoder(this)