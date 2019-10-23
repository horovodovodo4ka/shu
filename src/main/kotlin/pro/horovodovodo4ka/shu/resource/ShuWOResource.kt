package pro.horovodovodo4ka.shu.resource

import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.coders.Encoder

interface ShuWOResource<ResourceType : Any> : ShuOperationProvider {
    val resourceEncoder: () -> Encoder<ResourceType>
}

fun <ResourceType : Any> wo(origin: ShuRemote, path: String, customHeaders: Headers? = null, encoder: Encoder<ResourceType>): ShuWOResource<ResourceType> =
    object : ShuWOResource<ResourceType> {
        override val origin: ShuRemote = origin
        override val path: String = path
        override var customHeaders: Headers? = customHeaders
        override val resourceEncoder: () -> Encoder<ResourceType> = { encoder }
    }