# PrefetchViewPool

[![](https://jitpack.io/v/vivid-money/prefetchviewpool.svg)](https://jitpack.io/#vivid-money/prefetchviewpool)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## What is it?

A RecyclerView's RecycledViewPool extension that enables you to request specific item view types to be created in advance, prior to them being actually required

## Why?

— Prefetch items while you're loading data for binding

— Shared view pool of the items for use throughout the whole app that could be prefetched as soon as you can get the Activity context

## Usage

If you're using some of the implementation modules(Coroutines, RxJava *, Executor) then the usage will be as simple as following:

In your activity's `onCreate()` or fragment's `onViewCreated()` add


``` Kotlin
recycler.setupWithPrefetchViewPool { setPrefetchBound(viewType = 1, count = 20) }
        .bindToLifecycle(lifecycleOwner = this)
```

and you're done

## Sample

You could see an example of usage in [sample](https://github.com/vivid-money/prefetchviewpool/blob/main/sample/src/main/java/vivid/money/prefetchviewpool/sample/MainActivity.kt)

## Download
Library is distributed through JitPack

#### Add repository in the root build.gradle
``` Gradle
subprojects {
    repositories {
        maven { url("https://jitpack.io") }
    }
}
```

#### Add required modules:

- Core - for core functionality of PrefetchViewPool where you have to provide your own implementation of ViewHolderSupplier

`implementation("com.github.vivid-money.prefetchviewpool:prefetchviewpool-core:{latest-version}")`

- Coroutines - for **Kotlin coroutines** implementation of ViewHolderSupplier

`implementation("com.github.vivid-money.prefetchviewpool:prefetchviewpool-coroutines:{latest-version}")`

- RxJava 3 - for **RxJava 3** implementation of ViewHolderSupplier

`implementation("com.github.vivid-money.prefetchviewpool:prefetchviewpool-rxjava-3:{latest-version}")`

- RxJava 2 - for **RxJava 2** implementation of ViewHolderSupplier

`implementation("com.github.vivid-money.prefetchviewpool:prefetchviewpool-rxjava-2:{latest-version}")`

- Executor - for **ExecutorService** implementation of ViewHolderSupplier

`implementation("com.github.vivid-money.prefetchviewpool:prefetchviewpool-executor:{latest-version}")`

**Note:** all modules except for `core` provide a useful extension to easy setup PrefetchViewPool with RecyclerView