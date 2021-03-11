package vivid.money.prefetchviewpool.executor

import android.content.Context
import vivid.money.prefetchviewpool.core.ViewHolderProducer
import vivid.money.prefetchviewpool.core.ViewHolderSupplier
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ExecutorViewHolderSupplier(
    context: Context,
    viewHolderProducer: ViewHolderProducer
) : ViewHolderSupplier(context, viewHolderProducer) {

    private val executorService: ExecutorService =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    override fun start() = Unit

    override fun enqueueItemCreation(viewType: Int) {
        executorService.submit { createItem(viewType) }
    }

    override fun stop() {
        executorService.shutdown()
    }
}