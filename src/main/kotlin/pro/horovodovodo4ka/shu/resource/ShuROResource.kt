package pro.horovodovodo4ka.shu.resource

import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.coders.Decoder

interface ShuROResource<ResourceType : Any> : ShuOperationProvider {
    val resourceDecoder: () -> Decoder<ResourceType>
}

fun <ResourceType : Any> ro(origin: ShuRemote, path: String, customHeaders: Headers? = null, decoder: Decoder<ResourceType>): ShuROResource<ResourceType> =
    object : ShuROResource<ResourceType> {
        override val origin: ShuRemote = origin
        override val path: String = path
        override var customHeaders: Headers? = customHeaders
        override val resourceDecoder: () -> Decoder<ResourceType> = { decoder }
    }