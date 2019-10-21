package pro.horovodovodo4ka.shu.extension

fun Any.uriQuery(): Map<String, String> {
    return uriQuery(null).toMap()
}

fun Any.uriQueryString() : String = uriQuery().toList().joinToString("&") { "${it.first}=${it.second}" }

private fun Any.uriQuery(prefix: String?): List<Pair<String, String>> {
    return when (this) {
        is Map<*, *> -> this.uriQuery(prefix)
        is Array<*> -> this.asIterable().uriQuery(prefix)
        is Iterable<*> -> this.uriQuery(prefix)

        else -> listOf((prefix ?: toString()) to toString())
    }
}

private fun Map<*, *>.uriQuery(prefix: String?): List<Pair<String, String>> {
    return map { entry ->
        var key = entry.key.toString()
        prefix?.also { key = "$prefix[$key]" }
        entry.value?.uriQuery(key)?.toList() ?: emptyList()
    }
        .flatten()
}

private fun Iterable<*>.uriQuery(prefix: String?): List<Pair<String, String>> {
    return mapIndexed { index, value ->
        var key = index.toString()
        prefix?.also { key = "$prefix[$key]" }
        value?.uriQuery(key)?.toList() ?: emptyList()
    }
        .flatten()
}