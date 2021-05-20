package dev.eyosiyas.smsblocker.util

import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import java.io.FileDescriptor
import java.io.PrintWriter

class MessageLoader : LoaderManager() {
    override fun <D : Any?> initLoader(id: Int, args: Bundle?, callback: LoaderCallbacks<D>): Loader<D> {
        TODO("Not yet implemented")
    }

    override fun <D : Any?> restartLoader(id: Int, args: Bundle?, callback: LoaderCallbacks<D>): Loader<D> {
        TODO("Not yet implemented")
    }

    override fun destroyLoader(id: Int) {
        TODO("Not yet implemented")
    }

    override fun <D : Any?> getLoader(id: Int): Loader<D>? {
        TODO("Not yet implemented")
    }

    override fun markForRedelivery() {
        TODO("Not yet implemented")
    }

    override fun dump(prefix: String?, fd: FileDescriptor?, writer: PrintWriter?, args: Array<out String>?) {
        TODO("Not yet implemented")
    }
}