package vivid.money.prefetchviewpool.coroutines

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import vivid.money.prefetchviewpool.core.ViewHolderProducer
import vivid.money.prefetchviewpool.core.ViewHolderSupplier
import kotlin.coroutines.CoroutineContext

class CoroutinesViewHolderSupplier(
    context: Context,
    viewHolderProducer: ViewHolderProducer
) : ViewHolderSupplier(context, viewHolderProducer),
    CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Default

    private val channelFlow = Channel<Int>()

    override fun start() {
        launch {
            channelFlow.receiveAsFlow()
                .collect { viewType -> launch { createItem(viewType) } }
        }
    }

    override fun enqueueItemCreation(viewType: Int) {
        launch { channelFlow.send(viewType) }
    }

    override fun stop() {
        cancel()
    }
}