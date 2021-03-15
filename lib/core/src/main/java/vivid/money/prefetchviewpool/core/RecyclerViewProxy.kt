@file:Suppress("PackageDirectoryMismatch")

package androidx.recyclerview.widget

import vivid.money.prefetchviewpool.core.PrefetchViewPool

internal fun RecyclerView.RecycledViewPool.attachToPreventFromClearing() {
    attach()
}

internal fun RecyclerView.ViewHolder.setItemViewType(viewType: Int) {
    mItemViewType = viewType
}

internal fun PrefetchViewPool.factorInCreateTime(viewType: Int, creationTimeNanos: Long) {
    (this as RecyclerView.RecycledViewPool).factorInCreateTime(viewType, creationTimeNanos)
}