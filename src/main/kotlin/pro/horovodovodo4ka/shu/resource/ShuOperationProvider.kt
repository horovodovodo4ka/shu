package pro.horovodovodo4ka.shu.resource

import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.coders.Decoder
import pro.horovodovodo4ka.shu.coders.Encoder
import pro.horovodovodo4ka.shu.Headers

interface ShuOperationProvider<ResourceType : Any> {
    val origin: ShuRemote
    val path: String
    var customHeaders: Headers?
    val resourceDekoder: () -> Decoder<ResourceType>
    val resourceEnkoder: () -> Encoder<ResourceType>
}

