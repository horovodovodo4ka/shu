package pro.horovodovodo4ka.shu.coders

interface Decoder<T> {
    val list: Decoder<List<T>>
    val dictionary: Decoder<Map<String, T>>

    fun decode(from: String): T
}

