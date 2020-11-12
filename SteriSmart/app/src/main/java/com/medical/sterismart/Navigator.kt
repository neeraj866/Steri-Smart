package com.medical.sterismart

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.medical.data.tables.DataModel
import com.medical.sterismart.activities.PdfReaderActivity
import com.medical.sterismart.fragments.*
import javax.inject.Inject

class Navigator @Inject constructor() {

    fun loadPdfReaderActivity(context: FragmentActivity, fileName: String) {
        val intentToLaunch = PdfReaderActivity.getCallingIntent(context)
        intentToLaunch.putExtra("pdf_file", fileName)
        context.startActivity(intentToLaunch)
    }

    fun loadHomeFragment(context: FragmentActivity) {
        val fragmentToLoad = HomeFragment.getCallingFragment()
        replaceFragment(context, fragmentToLoad)
    }

    fun loadSterilizationFragment(context: FragmentActivity) {
        val fragmentToLoad = SterilizationFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadBIListFragment(context: FragmentActivity) {
        val fragmentToLoad = BIListFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadBITestFragment(
        context: FragmentActivity,
        id: Int,
        testUpdateListener: BITestFragment.TestUpdateListener
    ) {
        val fragmentToLoad = BITestFragment()
        val bundle = Bundle()
        bundle.putInt("process_id", id)
        fragmentToLoad.arguments = bundle
        fragmentToLoad.initializeTestListener(testUpdateListener)
        addFragment(context, fragmentToLoad)
    }

    fun loadReportMenuFragment(context: FragmentActivity) {
        val fragmentToLoad = ReportMenuFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadReportingFragment(context: FragmentActivity) {
        val fragmentToLoad = ReportingFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadHelpCenterFragment(context: FragmentActivity) {
        val fragmentToLoad = HelpCenterFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadAboutUsFragment(context: FragmentActivity) {
        val fragmentToLoad = AboutUsFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadChangeLanguageFragment(context: FragmentActivity) {
        val fragmentToLoad = ChangeLanguageFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadSettingsFragment(context: FragmentActivity) {
        val fragmentToLoad = SettingsFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadOfficeSetupFragment(context: FragmentActivity) {
        val fragmentToLoad = OfficeSetupFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadLicenseInfoFragment(context: FragmentActivity) {
        val fragmentToLoad = LicenseInfoFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadAutoclaveSetupFragment(
        context: FragmentActivity,
        id: Int,
        onAutoclaveSetupComplete: AutoclaveSetupFragment.OnAutoclaveSetupComplete
    ) {
        val fragmentToLoad = AutoclaveSetupFragment()
        val bundle = Bundle()
        bundle.putInt("autoclave_id", id)
        fragmentToLoad.arguments = bundle
        fragmentToLoad.initializeListener(onAutoclaveSetupComplete)
        addFragment(context, fragmentToLoad)
    }

    fun loadAutoclaveFragment(context: FragmentActivity) {
        val fragmentToLoad = AutoclaveFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadSterilizationPackageFragment(context: FragmentActivity) {
        val fragmentToLoad = SterilizationPackageFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadLabelFragment(context: FragmentActivity) {
        val fragmentToLoad = LabelingFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }


    fun loadOperatorSetupFragment(context: FragmentActivity) {
        val fragmentToLoad = OperatorSetupFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }


    fun loadPackageListFragment(context: FragmentActivity) {
        val fragmentToLoad = PackageListFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadResetDateFragment(context: FragmentActivity) {
        val fragmentToLoad = ResetDataFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    fun loadAuditFragment(context: FragmentActivity) {
        val fragmentToLoad = AuditFragment.getCallingFragment()
        addFragment(context, fragmentToLoad)
    }

    private fun addFragment(context: FragmentActivity, fragmentToLoad: Fragment) {
        val manager = context.supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        transaction.add(R.id.main_container, fragmentToLoad)
            .addToBackStack(fragmentToLoad.javaClass.name)
        transaction.commit()
    }

    private fun replaceFragment(context: FragmentActivity, fragmentToLoad: Fragment) {
        val manager = context.supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        transaction.replace(R.id.main_container, fragmentToLoad)
        transaction.commit()
    }

    fun loadHelpFragment(context: FragmentActivity) {
        val fragmentToLoad = HelpFragment.getCallingFragment()
        loadFragmentToHelpCenter(context, fragmentToLoad)
    }

    fun loadDocumentationFragment(context: FragmentActivity) {
        val fragmentToLoad = DocumentationFragment.getCallingFragment()
        loadFragmentToHelpCenter(context, fragmentToLoad)
    }

    fun loadManualFragment(context: FragmentActivity) {
        val fragmentToLoad = ManualFragment.getCallingFragment()
        loadFragmentToHelpCenter(context, fragmentToLoad)
    }

    fun loadTrainingFragment(context: FragmentActivity) {
        val fragmentToLoad = TrainingFragment.getCallingFragment()
        loadFragmentToHelpCenter(context, fragmentToLoad)
    }

    private fun loadFragmentToHelpCenter(context: FragmentActivity, fragmentToLoad: Fragment) {
        val manager = context.supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.help_center_container, fragmentToLoad)
        transaction.commit()
    }

    fun loadDailyFragment(context: FragmentActivity) {
        val fragmentToLoad = DailyFragment.getCallingFragment()
        loadFragmentToReporting(context, fragmentToLoad)
    }

    fun loadDateRangeFragment(context: FragmentActivity) {
        val fragmentToLoad = DateRangeFragment.getCallingFragment()
        loadFragmentToReporting(context, fragmentToLoad)
    }

    fun loadBarcodeFragment(context: FragmentActivity) {
        val fragmentToLoad = BarcodeFragment.getCallingFragment()
        loadFragmentToReporting(context, fragmentToLoad)
    }

    fun loadTestReportFragment(context: FragmentActivity, startDate: String, endDate: String) {
        val fragmentToLoad = TestStatusFragment.getCallingFragment()
        val bundle = Bundle()
        bundle.putString("startDate", startDate)
        bundle.putString("endDate", endDate)
        fragmentToLoad.arguments = bundle
        loadFragmentToReporting(context, fragmentToLoad)
    }

    fun loadAuditTypeFragment(
        context: FragmentActivity,
        typeOfAudit: String,
        startDate: String,
        endDate: String
    ) {
        val fragmentToLoad = AuditTypeFragment.getCallingFragment()
        val bundle = Bundle()
        bundle.putString("type", typeOfAudit)
        bundle.putString("startDate", startDate)
        bundle.putString("endDate", endDate)
        fragmentToLoad.arguments = bundle
        loadFragmentToReporting(context, fragmentToLoad)
    }


    private fun loadFragmentToReporting(context: FragmentActivity, fragmentToLoad: Fragment) {
        val manager = context.supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.reporting_container, fragmentToLoad)
        transaction.commit()
    }

    fun loadReportFragment(
        context: FragmentActivity,
        reportList: List<DataModel>,
        initials: String,
        isAudit: Boolean,
        isFullAuditDetails: Boolean,
        isPassed: Boolean, isUniqueCode: Boolean, uniqueCode: String
    ) {
        val fragmentToLoad = GenerateReportFragment()
        fragmentToLoad.initializeList(
            reportList,
            initials,
            isAudit,
            isFullAuditDetails,
            isPassed,
            isUniqueCode,
            uniqueCode
        )
        addFragment(context, fragmentToLoad)
    }

    private fun loadFragmentToInternalReporting(
        context: FragmentActivity,
        fragmentToLoad: Fragment
    ) {
        val manager = context.supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.report_container, fragmentToLoad)
        transaction.commit()
    }

    fun loadCreateNewPackageFragment(
        context: FragmentActivity,
        onPackageChangeListener: CreateNewPackageFragment.OnPackageChangeListener,
        id: Int
    ) {
        val fragmentToLoad = CreateNewPackageFragment()
        val bundle = Bundle()
        bundle.putInt("package_id", id)
        fragmentToLoad.arguments = bundle
        fragmentToLoad.initializeListener(onPackageChangeListener)
        addFragment(context, fragmentToLoad)
    }
}