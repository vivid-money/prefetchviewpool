package vivid.money.prefetchviewpool.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.setItemViewType
import java.util.concurrent.ConcurrentHashMap

typealias ViewHolderProducer = (parent: ViewGroup, viewType: Int) -> RecyclerView.ViewHolder
typealias ViewHolderConsumer = (viewHolder: RecyclerView.ViewHolder, creationTimeNanos: Long) -> Unit

/**
 * This is responsible for scheduling item creation and notifying [viewHolderConsumer] whenever an item was created
 *
 * @param context — used only for creation of [fakeParent]
 * @param viewHolderProducer — is used to actually create item
 * (e.g. [androidx.recyclerview.widget.RecyclerView.Adapter]'s onCreateViewHolder() method)
 */
abstract class ViewHolderSupplier(
    context: Context,
    private val viewHolderProducer: ViewHolderProducer
) {

    /**
     * Invoked once item is created in [createItem]
     */
    internal lateinit var viewHolderConsumer: ViewHolderConsumer

    /**
     * Passed as the parent to [viewHolderProducer]
     */
    private val fakeParent: ViewGroup by lazy { FrameLayout(context) }

    /**
     * Used for notifying [androidx.recyclerview.widget.RecyclerView.RecycledViewPool] of item have been created
     */
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    /**
     * Used for calculation of item creation time
     */
    private val nanoTime: Long get() = System.nanoTime()

    /**
     * Holds info on how many items of certain view type are already created
     */
    private val itemsCreated: MutableMap<Int, Int> = ConcurrentHashMap<Int, Int>()

    /**
     * Holds info on how many items of certain view type are queued for creation
     */
    private val itemsQueued: MutableMap<Int, Int> = ConcurrentHashMap<Int, Int>()

    /**
     * Perform preparation actions required for item creation
     */
    abstract fun start()

    /**
     * Delegate actual item creation
     */
    abstract fun enqueueItemCreation(viewType: Int)

    /**
     * Clear all the resources required for item creation
     */
    abstract fun stop()

    /**
     * Creates view holder with the requested [viewType], if corresponding [itemsQueued] amount is not yet
     * fulfilled.
     *
     * Assumes being called off the main thread so does the notification of [viewHolderConsumer] via
     * main thread [Handler]
     */
    protected fun createItem(viewType: Int) {
        val created = itemsCreated.getOrZero(viewType) + 1
        val queued = itemsQueued.getOrZero(viewType)
        if (created > queued) return

        val holder: RecyclerView.ViewHolder
        val start: Long
        val end: Long

        try {
            start = nanoTime
            holder = viewHolderProducer.invoke(fakeParent, viewType)
            end = nanoTime
        } catch (e: Exception) {
            return
        }
        holder.setItemViewType(viewType)
        itemsCreated[viewType] = itemsCreated.getOrZero(viewType) + 1

        mainHandler.postAtFrontOfQueue { viewHolderConsumer.invoke(holder, end - start) }
    }

    /**
     * Calls [enqueueItemCreation] required amount of times in order to fill the recycled view pool
     */
    internal fun setPrefetchBound(viewType: Int, count: Int) {
        if (itemsQueued.getOrZero(viewType) >= count) return
        itemsQueued[viewType] = count

        val created = itemsCreated.getOrZero(viewType)
        if (created >= count) return

        repeat(count - created) { enqueueItemCreation(viewType) }
    }

    /**
     * One less item is required to be created here
     */
    internal fun onItemCreatedOutside(viewType: Int) {
        itemsCreated[viewType] = itemsCreated.getOrZero(viewType) + 1
    }

    private fun Map<Int, Int>.getOrZero(key: Int) = getOrElse(key) { 0 }
}