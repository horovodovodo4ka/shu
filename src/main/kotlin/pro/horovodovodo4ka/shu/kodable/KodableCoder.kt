package pro.horovodovodo4ka.shu.kodable

import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.types.KodablePath
import pro.horovodovodo4ka.kodable.core.utils.dekode
import pro.horovodovodo4ka.kodable.core.utils.enkode
import pro.horovodovodo4ka.shu.coders.Coder

interface InnerCoder<T> : Coder<T>, InnerDecoder<T>
///

class KodableCoder<T>(private val nesting: IKodable<T>, override val jsonPath: KodablePath? = null) :
    InnerCoder<T> {

    override fun decode(from: String): T = nesting.dekode(from, jsonPath)

    override fun encode(instance: T): String = nesting.enkode(instance)

    override val list: Coder<List<T>>
        get() = KodableCoder(nesting.list, jsonPath)

    override val dictionary: Coder<Map<String, T>>
        get() = KodableCoder(nesting.dictionary, jsonPath)

    override fun digInto(path: String) = KodableCoder(nesting, KodablePath(path))
}

val <T> IKodable<T>.coder get() = KodableCoder(this)