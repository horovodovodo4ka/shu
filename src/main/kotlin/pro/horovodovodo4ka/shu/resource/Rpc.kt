package pro.horovodovodo4ka.shu.resource

import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.coders.Decoder
import pro.horovodovodo4ka.shu.coders.Encoder

interface Rpc<RequestType : Any, ResponseType : Any> : ShuOperationProvider {
    val requestDecoder: () -> Decoder<ResponseType>
    val requestEncoder: () -> Encoder<RequestType>
}

fun <RequestType : Any, ResponseType : Any> rpc(origin: ShuRemote, path: String, customHeaders: Headers? = null, encoder: Encoder<RequestType>, decoder: Decoder<ResponseType>) =
    object : Rpc<RequestType, ResponseType> {
        override val origin: ShuRemote = origin
        override val path: String = path
        override val customHeaders: Headers? = customHeaders
        override val requestDecoder: () -> Decoder<ResponseType> = { decoder }
        override val requestEncoder: () -> Encoder<RequestType> = { encoder }
    }