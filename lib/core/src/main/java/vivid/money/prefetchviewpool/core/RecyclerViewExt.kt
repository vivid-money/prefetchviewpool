package vivid.money.prefetchviewpool.core

import androidx.recyclerview.widget.RecyclerView

inline val RecyclerView.defaultViewHolderProducer: ViewHolderProducer
    get() {
        val adapter = requireNotNull(adapter) { "You have to set RecyclerView's adapter first" }
        return adapter::createViewHolder
    }