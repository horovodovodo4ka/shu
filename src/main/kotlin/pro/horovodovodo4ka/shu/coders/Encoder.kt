package pro.horovodovodo4ka.shu.coders

interface Encoder<T> {
    val list: Encoder<List<T>>
    val dictionary: Encoder<Map<String, T>>
    fun encode(instance: T): String
}

