package vivid.money.prefetchviewpool.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.RecycledViewPool.bindToLifecycle(lifecycleOwner: LifecycleOwner) {
    val observer = object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            this@bindToLifecycle.clear()
        }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
}