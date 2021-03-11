package vivid.money.prefetchviewpool.core

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.attachToPreventFromClearing
import androidx.recyclerview.widget.factorInCreateTime
import kotlin.math.max

/**
 * An implementation of [RecyclerView.RecycledViewPool] that is able to trigger item creation
 * before they are actually required by [RecyclerView]
 *
 * @param defaultMaxRecycledViews — minimum amount of view holders of any view type that will be kept in view pool
 * @param viewHolderSupplier — instance of a [ViewHolderSupplier] that will handle the actual item creation
 */
class PrefetchViewPool(
    private val defaultMaxRecycledViews: Int,
    private val viewHolderSupplier: ViewHolderSupplier
) : RecyclerView.RecycledViewPool() {

    private val recycledViewsBounds = mutableMapOf<Int, Int>()

    init {
        attachToPreventFromClearing()
        viewHolderSupplier.viewHolderConsumer = ::putViewFromSupplier
        viewHolderSupplier.start()
    }

    /**
     * Trigger actual item creation
     *
     * @param viewType — view type of the required view holders
     * @param count — required amount of the view holders; it is used as max amount of recycled views of [viewType]
     * in case it exceeds [defaultMaxRecycledViews]
     */
    fun setPrefetchBound(viewType: Int, count: Int) {
        recycledViewsBounds[viewType] = max(defaultMaxRecycledViews, count)
        viewHolderSupplier.setPrefetchBound(viewType, count)
    }

    override fun putRecycledView(scrap: RecyclerView.ViewHolder) {
        val viewType = scrap.itemViewType
        val maxRecycledViews = recycledViewsBounds.getOrPut(viewType) { defaultMaxRecycledViews }
        setMaxRecycledViews(viewType, maxRecycledViews)
        super.putRecycledView(scrap)
    }

    override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
        val holder = super.getRecycledView(viewType)
        if (holder == null) viewHolderSupplier.onItemCreatedOutside(viewType)
        return holder
    }

    override fun clear() {
        super.clear()
        viewHolderSupplier.stop()
    }

    /**
     * Notifying [RecyclerView.RecycledViewPool] about item creation time is required for
     * [androidx.recyclerview.widget.GapWorker] work, so this method does that before putting
     * item, created in [viewHolderSupplier], into the pool
     */
    private fun putViewFromSupplier(scrap: RecyclerView.ViewHolder, creationTimeNanos: Long) {
        factorInCreateTime(scrap.itemViewType, creationTimeNanos)
        putRecycledView(scrap)
    }

    companion object {

        const val DEFAULT_MAX_RECYCLED_VIEWS = 5
    }
}