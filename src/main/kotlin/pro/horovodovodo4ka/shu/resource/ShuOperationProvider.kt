package pro.horovodovodo4ka.shu.resource

import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.ShuRemote

interface ShuOperationProvider {
    val origin: ShuRemote
    val path: String
    val customHeaders: Headers?
}


