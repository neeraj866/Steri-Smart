package com.medical.sterismart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.medical.sterismart.R
import com.medical.sterismart.activities.HomeActivity
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.ReportPresenter
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_change_language.*
import javax.inject.Inject

class ChangeLanguageFragment : BaseFragment() {
    @Inject
    lateinit var reportPresenter: ReportPresenter

    companion object {
        fun getCallingFragment(): Fragment {
            return ChangeLanguageFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_language, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()

        submit.setOnClickListener {
            when {
                english.isChecked -> {
                    Utility.changeLanguage("en", requireActivity())
                    Utility.saveLanguage(requireContext(), "en")
                }
                else -> {
                    Utility.changeLanguage("fr", requireActivity())
                    Utility.saveLanguage(requireContext(), "fr")
                }
            }
            choose_language.text = getString(R.string.choose_language)
            english.text = getString(R.string.english)
            french.text = getString(R.string.french)
            submit.text = getString(R.string.submit)
            (activity as HomeActivity).title_name.text = getString(R.string.change_language)
        }
    }


    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }
}