package com.medical.sterismart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.medical.sterismart.R
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import kotlinx.android.synthetic.main.fragment_manual.*

class ManualFragment : BaseFragment(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener {
    companion object {
        fun getCallingFragment(): Fragment {
            return ManualFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manual, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()

        pdf_view.fromAsset("UserManual.pdf").defaultPage(0).onPageChange(this)
            .enableAnnotationRendering(true).onLoad(this).scrollHandle(DefaultScrollHandle(requireContext()))
            .spacing(0) // in dp
            .onPageError(this).pageFitPolicy(FitPolicy.WIDTH).load()
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    override fun loadComplete(nbPages: Int) {

    }

    override fun onPageError(page: Int, t: Throwable?) {

    }
}