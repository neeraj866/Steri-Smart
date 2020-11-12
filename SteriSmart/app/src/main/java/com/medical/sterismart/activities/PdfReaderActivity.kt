package com.medical.sterismart.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.medical.sterismart.R
import kotlinx.android.synthetic.main.activity_pdf_reader.*

class PdfReaderActivity : BaseActivity(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener {

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, PdfReaderActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_reader)
        val fileName: String = intent?.getStringExtra("pdf_file") as String
        pdf_view.fromAsset(fileName).defaultPage(0).onPageChange(this)
            .enableAnnotationRendering(true).onLoad(this).scrollHandle(DefaultScrollHandle(this))
            .spacing(0) // in dp
            .onPageError(this).pageFitPolicy(FitPolicy.WIDTH).load()

        back.setOnClickListener {
            finish()
        }
    }


    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    override fun loadComplete(nbPages: Int) {

    }

    override fun onPageError(page: Int, t: Throwable?) {

    }
}