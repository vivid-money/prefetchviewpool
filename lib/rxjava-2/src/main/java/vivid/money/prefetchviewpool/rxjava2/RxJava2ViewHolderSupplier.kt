package vivid.money.prefetchviewpool.rxjava2

import android.content.Context
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.computation
import io.reactivex.subjects.PublishSubject
import vivid.money.prefetchviewpool.core.ViewHolderSupplier
import vivid.money.prefetchviewpool.core.ViewHolderProducer

class RxJava2ViewHolderSupplier(
    context: Context,
    viewHolderProducer: ViewHolderProducer
) : ViewHolderSupplier(context, viewHolderProducer) {

    private val createSubject = PublishSubject.create<Int>()

    private var disposable: Disposable? = null

    override fun start() {
        disposable = createSubject
            .observeOn(computation())
            .flatMapCompletable { viewType -> createItemAsync(viewType) }
            .subscribe()
    }

    override fun enqueueItemCreation(viewType: Int) {
        createSubject.onNext(viewType)
    }

    override fun stop() {
        disposable?.dispose()
    }

    private fun createItemAsync(viewType: Int): Completable {
        return Completable
            .fromAction { createItem(viewType) }
            .subscribeOn(computation())
    }
}