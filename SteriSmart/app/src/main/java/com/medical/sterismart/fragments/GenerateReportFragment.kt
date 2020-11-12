package com.medical.sterismart.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.medical.data.tables.DataModel
import com.medical.data.tables.OfficesModel
import com.medical.data.tables.SterilizationPackagesModel
import com.medical.sterismart.AppExecutors
import com.medical.sterismart.R
import com.medical.sterismart.custom.Utility
import com.medical.sterismart.dependencies2.components.DaggerApplicationComponent
import com.medical.sterismart.dependencies2.modules.ApplicationModule
import com.medical.sterismart.presenter.GenerateReportPresenter
import com.medical.sterismart.view.GenerateReportView
import kotlinx.android.synthetic.main.fragment_generate_report.*
import kotlinx.android.synthetic.main.item_report.view.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class GenerateReportFragment : BaseFragment(), GenerateReportView {

    @Inject
    lateinit var generateReportPresenter: GenerateReportPresenter

    private var dataList: MutableList<DataModel> = ArrayList()

    private var viewHolder: MutableList<View> = ArrayList()

    private var document: PdfDocument? = null
    private var documentPageNumber: Int = 1

    private var isForSave = false

    private var appExecutors: AppExecutors? = null
    private var targetPdf = ""
    private var pdfName = ""

    private var officeAdded = false
    private var showSignature = false
    private var initials = ""
    private var isAudit: Boolean = false
    private var isUniqueCode: Boolean = false
    private var uniqueCode: String = ""
    private var isFullAuditDetails: Boolean = false
    private var isPassed: Boolean = false

    fun initializeList(
        dataList: List<DataModel>,
        initials: String,
        isAudit: Boolean,
        isFullAuditDetails: Boolean,
        isPassed: Boolean, isUniqueCode: Boolean, uniqueCode: String
    ) {
        this.dataList.clear()
        this.dataList.addAll(dataList)
        this.initials = initials
        this.isAudit = isAudit
        this.isFullAuditDetails = isFullAuditDetails
        this.isPassed = isPassed
        this.isUniqueCode = isUniqueCode
        this.uniqueCode = uniqueCode
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_generate_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseDependency()
        generateReportPresenter.initializeDb(requireContext())
        generateReportPresenter.generateReportView = this
        Thread {
            generateReportPresenter.getOffice()
            requireActivity().runOnUiThread {
                Utility.showProgressDialog(requireActivity())
                for (i in dataList.indices) {
                    when {
                        !TextUtils.isEmpty(initials) && i == dataList.size - 1 -> {
                            showSignature = true
                        }
                    }
                    renderReport(dataList[i])
                    when {
                        !officeAdded -> {
                            officeAdded = true
                        }
                    }
                }
                Utility.dismissDialog()
            }
        }.start()
        back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        save_report.setOnClickListener {
            isForSave = true
            when {
                dataList.size > 0 -> {
                    Utility.showProgressDialog(requireActivity())
                    startMakingPdf()
                }
            }
        }

        container.setOnClickListener {
            Utility.hideKeyboard(container)
        }

        email_report.setOnClickListener {
            isForSave = false
            when {
                dataList.size > 0 -> {
                    Utility.showProgressDialog(requireActivity())
                    startMakingPdf()
                }
            }
        }
    }

    private fun startMakingPdf() {
        documentPageNumber = 1
        document = PdfDocument()
        for (view in viewHolder) {
            getBitmapFromView(view)
            documentPageNumber++
        }
    }

    override fun onAttach(context: Context) {
        appExecutors = AppExecutors()
        super.onAttach(context)
    }


    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        convertBitmapToPdf(bitmap)
        return bitmap
    }

    private fun convertBitmapToPdf(bitmapImage: Bitmap) {
        val bitmap: Bitmap = bitmapImage
        val pageInfo: PdfDocument.PageInfo =
            PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, documentPageNumber).create()
        val page: PdfDocument.Page = document?.startPage(pageInfo)!!
        val canvas: Canvas = page.canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        document?.finishPage(page)
        if (documentPageNumber == viewHolder.size) {
            savePdfDocument()
        }
    }

    private fun savePdfDocument() {
        val directoryPath = requireActivity().getExternalFilesDir(null)?.path + "/mypdf/"
        val file = File(directoryPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val date = Date()
        pdfName = "steri_touch_${date}.pdf"
        targetPdf = directoryPath + pdfName.replace(" ".toRegex(), "_")
        targetPdf = targetPdf.replace(":".toRegex(), "-")
        val filePath = File(targetPdf)
        try {
            document?.writeTo(FileOutputStream(filePath))
            when {
                isForSave -> {
                    Utility.dismissDialog()
                    Snackbar.make(container, "PDF saved successfully", Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    sendEmail()
                }
            }
        } catch (e: Exception) {
            Utility.dismissDialog()
            Snackbar.make(container, "Something wrong $e", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        Utility.dismissDialog()
        val uri = Uri.fromFile(File(targetPdf))
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = "application/pdf"
        share.putExtra(Intent.EXTRA_STREAM, uri)

        startActivityForResult(Intent.createChooser(share, pdfName), 3)
    }

    private fun renderReport(dataModel: DataModel) {
        Thread {
            val cycleUser = generateReportPresenter.getUserSignature(dataModel.cycleUserId.toInt())
            val biUser = generateReportPresenter.getUserSignature(dataModel.biUserId.toInt())
            requireActivity().runOnUiThread {
                val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_report, null)

                view.office_name.text = office?.officeName
                view.street_address.text = office?.streetAddress
                view.city.text = office?.city
                view.postal_code.text = office?.postalCode

                view.report_date.text = Utility.getCurrentDate()
                view.report_time.text = Utility.getCurrentTime()

                when {
                    !TextUtils.isEmpty(dataModel.packageInfo) -> {
                        val packages: List<SterilizationPackagesModel> =
                            Utility.convertPackageStringToJson(dataModel.packageInfo)
                        view.package_count.text = packages.size.toString()
                        when {
                            isUniqueCode -> {
                                loop@ for (items in packages) {
                                    when {
                                        uniqueCode == items.id.toString() -> {
                                            val packageName =
                                                "${items.packageName}(${items.packageType})"
                                            view.package_name.text = packageName
                                            view.unique_number.text = items.id.toString()
                                            view.package_name_text.visibility = View.VISIBLE
                                            view.package_name.visibility = View.VISIBLE
                                            view.unique_number_text.visibility = View.VISIBLE
                                            view.unique_number.visibility = View.VISIBLE
                                            break@loop
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                view.autoclave_id.text = dataModel.autoclave
                view.cycle_operator.text = dataModel.cycleUsername
                view.autoclave_model.text = dataModel.cycleAutoclaveModel
                view.cycle_date.text = dataModel.cycleDate
                view.autoclave_brand.text = dataModel.cycleAutoclaveManufacturer
                view.cycle_time.text = dataModel.cycleTime
                view.sterilization_duration.text = dataModel.runTime
                view.cycle_number.text = dataModel.cycleNumber
                view.maximum_temperature.text = dataModel.cycleTemperature
                view.maximum_pressure.text = dataModel.cyclePressure
                when {
                    dataModel.temperatureResult.contains("P", true) -> {
                        view.maximum_temperature.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.pass
                            )
                        )
                        view.maximum_temperature.setTextColor(Color.BLACK)
                    }
                    else -> {
                        view.maximum_temperature.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        view.maximum_temperature.setTextColor(Color.WHITE)
                    }
                }
                when {
                    dataModel.pressureResult.contains("P", true) -> {
                        view.maximum_pressure.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.pass
                            )
                        )
                        view.maximum_pressure.setTextColor(Color.BLACK)
                    }
                    else -> {
                        view.maximum_pressure.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        view.maximum_pressure.setTextColor(Color.WHITE)
                    }
                }

                view.operator_sign.text = dataModel.cycleUsername
                view.b_i_lot.text = dataModel.biLotNumber
                view.b_i_operator_sign.text = dataModel.biUsername
                view.b_i_spot_test_result.text = dataModel.biResult
                view.type_one_result.text = dataModel.typeOneStatus
                view.type_four_result.text = dataModel.typeFourStatus
                view.type_five_result.text = dataModel.typeFiveStatus

                when {
                    dataModel.biResult.contains("P", true) -> {
                        view.b_i_spot_test_result.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.pass
                            )
                        )
                        view.b_i_spot_test_result.setTextColor(Color.BLACK)
                    }
                    else -> {
                        view.b_i_spot_test_result.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        view.b_i_spot_test_result.setTextColor(Color.WHITE)
                    }
                }

                when {
                    dataModel.typeOneStatus.contains("P", true) -> {
                        view.type_one_result.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.pass
                            )
                        )
                        view.type_one_result.setTextColor(Color.BLACK)
                    }
                    else -> {
                        view.type_one_result.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        view.type_one_result.setTextColor(Color.WHITE)
                    }
                }

                when {
                    dataModel.typeFourStatus.contains("P", true) -> {
                        view.type_four_result.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.pass
                            )
                        )
                        view.type_four_result.setTextColor(Color.BLACK)
                    }
                    else -> {
                        view.type_four_result.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        view.type_four_result.setTextColor(Color.WHITE)
                    }
                }
                when {
                    dataModel.typeFiveStatus.contains("P", true) -> {
                        view.type_five_result.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.pass
                            )
                        )
                        view.type_five_result.setTextColor(Color.BLACK)
                    }
                    else -> {
                        view.type_five_result.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        view.type_five_result.setTextColor(Color.WHITE)
                    }
                }

                when {
                    dataModel.typeFourStatus.contains("P", true) -> {
                        view.type_four_result.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.pass
                            )
                        )
                        view.type_four_result.setTextColor(Color.BLACK)
                    }
                    else -> {
                        view.type_four_result.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        view.type_four_result.setTextColor(Color.WHITE)
                    }
                }

                when {
                    !TextUtils.isEmpty(cycleUser) -> {
                        view.signature_image.setImageBitmap(Utility.stringToBitmap(cycleUser))
                    }
                }
                when {
                    !TextUtils.isEmpty(biUser) -> {
                        view.b_i_signature_image.setImageBitmap(Utility.stringToBitmap(biUser))
                    }
                }

                report_list.addView(view)
                viewHolder.add(view)
            }
        }.start()
    }

    private fun initialiseDependency() {
        val applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(requireActivity().application)).build()
        applicationComponent.inject(this)
    }

    private var office: OfficesModel? = null
    override fun officeDetails(office: OfficesModel) {
        this.office = office
    }
}