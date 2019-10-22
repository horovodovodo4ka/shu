package pro.horovodovodo4ka.shu.resource

import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.coders.Coder
import pro.horovodovodo4ka.shu.coders.Decoder
import pro.horovodovodo4ka.shu.coders.Encoder
import pro.horovodovodo4ka.shu.Headers

abstract class ShuResource<ResourceType : Any> : ShuOperationProvider<ResourceType> {
    final override val origin: ShuRemote
    final override val path: String
    final override var customHeaders: Headers?
    final override val resourceDekoder: () -> Decoder<ResourceType>
    final override val resourceEnkoder: () -> Encoder<ResourceType>

    private constructor(origin: ShuRemote, path: String, customHeaders: Headers?, resourceDekoder: Decoder<ResourceType>?, resourceEnkoder: Encoder<ResourceType>?) {
        this.origin = origin
        this.path = path
        this.customHeaders = customHeaders
        this.resourceDekoder = { resourceDekoder ?: TODO() }
        this.resourceEnkoder = { resourceEnkoder ?: TODO() }
    }

    constructor(origin: ShuRemote, path: String, customHeaders: Headers? = null, resourceCoder: Coder<ResourceType>) : this(
        origin,
        path,
        customHeaders,
        resourceCoder,
        resourceCoder
    )

    constructor(origin: ShuRemote, path: String, customHeaders: Headers? = null, resourceDecoder: Decoder<ResourceType>) : this(
        origin,
        path,
        customHeaders,
        resourceDecoder,
        null
    )

    constructor(origin: ShuRemote, path: String, customHeaders: Headers? = null, resourceEncoder: Encoder<ResourceType>) : this(
        origin,
        path,
        customHeaders,
        null,
        resourceEncoder
    )
}
