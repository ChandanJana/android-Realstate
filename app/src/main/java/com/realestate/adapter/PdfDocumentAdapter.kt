package com.realestate.adapter

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import androidx.annotation.RequiresApi
import java.io.*


/**
 * Created by Chandan on 1/3/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class PdfDocumentAdapter(ctxt: Context?, pathName: String, fileName: String) :
    PrintDocumentAdapter() {
    private var context: Context? = null
    private var pathName = ""
    private var fileName = "file name"
    override fun onLayout(
        printAttributes: PrintAttributes,
        printAttributes1: PrintAttributes,
        cancellationSignal: CancellationSignal,
        layoutResultCallback: LayoutResultCallback,
        bundle: Bundle?
    ) {
        if (cancellationSignal.isCanceled()) {
            layoutResultCallback.onLayoutCancelled()
        } else {
            val builder = PrintDocumentInfo.Builder(fileName)
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build()
            layoutResultCallback.onLayoutFinished(
                builder.build(),
                printAttributes1 != printAttributes
            )
        }
    }

    override fun onWrite(
        pageRanges: Array<PageRange?>?,
        parcelFileDescriptor: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        writeResultCallback: WriteResultCallback
    ) {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            val file = File(pathName)
            `in` = FileInputStream(file)
            out = FileOutputStream(parcelFileDescriptor.fileDescriptor)
            val buf = ByteArray(16384)
            var size: Int = 0
            while (`in`.read(buf).also({ size = it }) >= 0
                && !cancellationSignal.isCanceled()
            ) {
                out.write(buf, 0, size)
            }
            if (cancellationSignal.isCanceled()) {
                writeResultCallback.onWriteCancelled()
            } else {
                writeResultCallback.onWriteFinished(arrayOf<PageRange>(PageRange.ALL_PAGES))
            }
        } catch (e: Exception) {
            writeResultCallback.onWriteFailed(e.message)
            //Logger.logError(e)
        } finally {
            try {
                `in`?.close()
                out?.close()
            } catch (e: IOException) {
                //Logger.logError(e)
            }
        }
    }

    init {
        context = ctxt
        this.pathName = pathName
    }
}