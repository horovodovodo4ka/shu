package pro.horovodovodo4ka.shu.coders

////
interface Coder<T> : Decoder<T>, Encoder<T> {
    override val list: Coder<List<T>>
    override val dictionary: Coder<Map<String, T>>
}
