package com.shikidroid.paginator

/**
 * Реализация интерфейса [Paginator]
 */
class PaginatorImpl<Key, T>(
    private val initialKey: Key,
    inline val onLoadUpdated: (Boolean) -> Unit,
    inline val onRequest: (nextKey: Key) -> Result<List<T>>,
    inline val getNextKey: (List<T>) -> Key,
    inline val onError: (Throwable?) -> Unit,
    inline val onSuccess: (items: List<T>, newKey: Key) -> Unit
): Paginator<Key, T> {

    private var currentKey: Key = initialKey
    private var isMakingRequest = false

    override fun loadNextItems() {
        if (isMakingRequest) {
            return
        } else {
            isMakingRequest = true
            onLoadUpdated(true)
            val result = onRequest(currentKey)
            isMakingRequest = false
            val items = result.getOrElse {
                onError(it)
                onLoadUpdated(false)
                return
            }
            currentKey = getNextKey(items)
            onSuccess(items, currentKey)
            onLoadUpdated(false)
        }

    }

    override fun reset() {
        currentKey = initialKey
    }
}