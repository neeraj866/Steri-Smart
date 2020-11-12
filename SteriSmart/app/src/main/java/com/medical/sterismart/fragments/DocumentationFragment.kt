package com.medical.sterismart.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.medical.sterismart.R
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import kotlinx.android.synthetic.main.fragment_documentation.*

class DocumentationFragment : BaseFragment(), AdapterView.OnItemSelectedListener {

    private var selectedDocumentName: String = ""

    companion object {
        fun getCallingFragment(): Fragment {
            return DocumentationFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_documentation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        document_spinner.onItemSelectedListener = this
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(activity!!.application)).build()
        applicationComponent.inject(this)
        open_document.setOnClickListener {
            navigator.loadPdfReaderActivity(activity!!, selectedDocumentName)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 2) {
            selectedDocumentName =
                "IPAC Checklist for Dental Practice Reporting of Dental or Medical Equipment or Devices.pdf"
        } else {
            selectedDocumentName =
                "${activity?.resources?.getStringArray(R.array.documentation)!![position]}.pdf"
        }
    }
}