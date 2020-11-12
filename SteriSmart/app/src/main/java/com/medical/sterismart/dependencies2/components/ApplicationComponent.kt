package com.medical.sterismart.dependencies2.components

import android.content.Context
import com.medical.sterismart.activities.BaseActivity
import com.medical.sterismart.activities.HomeActivity
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.fragments.*
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun context(): Context

    fun inject(baseActivity: BaseActivity)

    fun inject(HomeActivity: HomeActivity)

    fun inject(HomeFragment: HomeFragment)

    fun inject(SterilizationFragment: SterilizationFragment)

    fun inject(SterilizationPackageFragment: SterilizationPackageFragment)

    fun inject(HelpCenterFragment: HelpCenterFragment)

    fun inject(ManualFragment: ManualFragment)

    fun inject(DocumentationFragment: DocumentationFragment)

    fun inject(ReportingFragment: ReportingFragment)

    fun inject(DailyFragment: DailyFragment)

    fun inject(LabelingFragment: LabelingFragment)

    fun inject(BIListFragment: BIListFragment)

    fun inject(PackageListFragment: PackageListFragment)

    fun inject(DateRangeFragment: DateRangeFragment)

    fun inject(BarcodeFragment: BarcodeFragment)

    fun inject(AuditFragment: AuditFragment)
    
    fun inject(SettingsFragment: SettingsFragment)

    fun inject(AuditTypeFragment: AuditTypeFragment)

    fun inject(OfficeSetupFragment: OfficeSetupFragment)

    fun inject(LicenseInfoFragment: LicenseInfoFragment)

    fun inject(AutoclaveSetupFragment: AutoclaveSetupFragment)

    fun inject(AutoclaveFragment: AutoclaveFragment)

    fun inject(OperatorSetupFragment: OperatorSetupFragment)

    fun inject(CreateNewPackageFragment: CreateNewPackageFragment)

    fun inject(ReportMenuFragment: ReportMenuFragment)

    fun inject(BITestFragment: BITestFragment)

    fun inject(TestStatusFragment: TestStatusFragment)

    fun inject(GenerateReportFragment: GenerateReportFragment)

    fun inject(ResetDataFragment: ResetDataFragment)

    fun inject(ChangeLanguageFragment: ChangeLanguageFragment)
}