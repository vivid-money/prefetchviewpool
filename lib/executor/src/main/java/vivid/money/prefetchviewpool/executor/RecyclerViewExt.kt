package vivid.money.prefetchviewpool.executor

import androidx.recyclerview.widget.RecyclerView
import vivid.money.prefetchviewpool.core.PrefetchViewPool
import vivid.money.prefetchviewpool.core.ViewHolderProducer
import vivid.money.prefetchviewpool.core.defaultViewHolderProducer

fun RecyclerView.setupWithPrefetchViewPool(
    defaultMaxRecycledViews: Int = PrefetchViewPool.DEFAULT_MAX_RECYCLED_VIEWS,
    viewHolderProducer: ViewHolderProducer = defaultViewHolderProducer,
    builder: PrefetchViewPool.() -> Unit = {}
): PrefetchViewPool {
    val viewHolderSupplier = ExecutorViewHolderSupplier(context, viewHolderProducer)
    val prefetchViewPool = PrefetchViewPool(defaultMaxRecycledViews, viewHolderSupplier).apply(builder)
    setRecycledViewPool(prefetchViewPool)
    return prefetchViewPool
}