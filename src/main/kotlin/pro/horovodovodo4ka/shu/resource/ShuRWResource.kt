package pro.horovodovodo4ka.shu.resource

import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.coders.Coder
import pro.horovodovodo4ka.shu.coders.Decoder
import pro.horovodovodo4ka.shu.coders.Encoder

interface ShuRWResource<ResourceType : Any> : ShuROResource<ResourceType>, ShuWOResource<ResourceType>

fun <ResourceType : Any> rw(origin: ShuRemote, path: String, customHeaders: Headers? = null, coder: Coder<ResourceType>): ShuRWResource<ResourceType> =
    object : ShuRWResource<ResourceType> {
        override val origin: ShuRemote = origin
        override val path: String = path
        override var customHeaders: Headers? = customHeaders
        override val resourceDecoder: () -> Decoder<ResourceType> = { coder }
        override val resourceEncoder: () -> Encoder<ResourceType> = { coder }
    }