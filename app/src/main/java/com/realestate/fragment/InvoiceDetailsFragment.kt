package com.realestate.fragment

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.WebColors
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.AreaBreakType
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.realestate.BuildConfig
import com.realestate.R
import com.realestate.adapter.PdfDocumentAdapter
import com.realestate.model.InvoiceModel
import com.realestate.utils.Constant
import kotlinx.android.synthetic.main.fragment_invoice_details.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [InvoiceDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InvoiceDetailsFragment : Fragment() {
    private var invoiceModel: InvoiceModel? = null
    private val PERMISSION_REQUEST_CODE = 200
    private var pageHeight = 1120
    private var pagewidth = 792
    private var isPrint: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        invoiceModel = bundle?.getParcelable("invoice_details")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invoice_details, container, false)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().runOnUiThread(Runnable {
            try {
                requireActivity().txtViewToolbarTitle.text =
                    activity?.resources?.getString(R.string.invoice_details)
                activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                activity?.imgViewBack?.tag = "back"

            } catch (e: Exception) {

            }
        })
        activity?.refresh_img?.visibility = View.GONE
        activity?.add_img?.visibility = View.GONE
        activity?.filter_spinner?.visibility = View.GONE

        invoice_pdf_generate.setOnClickListener {
            if (checkPermission()) {
                //generatePDF();
                isPrint = false
                //generateInvoicePDF()
            } else {
                requestPermission();
            }
        }
        invoice_pdf_print.setOnClickListener {
            if (checkPermission()) {
                //generatePDF();
                isPrint = true
                //generateInvoicePDF()
            } else {
                requestPermission();
            }
        }

        activity?.refresh_img?.visibility = View.GONE
        activity?.add_img?.visibility = View.GONE
        invoice_id.text = invoiceModel?.invoiceNo
        invoice_bill_to_name.text = invoiceModel?.trips?.dealer?.dealerName
        invoice_bill_to_address.text = invoiceModel?.trips?.dealer?.address
        invoice_date.text = Constant.convertEndStringToTime(invoiceModel?.invoiceDate!!, true)
        invoice_material_name.text = invoiceModel?.trips?.company?.material?.materialName
        invoice_customer_id.text = invoiceModel?.trips?.dealerId?.toString()
        invoice_material_unit_price.text = invoiceModel?.trips?.company?.material?.price?.toString()+"/-"
        invoice_material_total_price.text = invoiceModel?.trips?.totalMaterialCost?.toString()+"/-"
        invoice_material_total_price1.text = invoiceModel?.trips?.totalMaterialCost?.toString()+"/-"
        invoice_trip_total_cost.text = invoiceModel?.trips?.tripCost?.toString()+"/-"
        invoice_total_cost.text = (invoiceModel?.trips?.totalMaterialCost!! + invoiceModel?.trips?.tripCost!!).toString()+"/-"
        invoice_trip_advance_cost.text = invoiceModel?.trips?.advanceAmount?.toString()+"/-"
        val subTotal = (invoiceModel?.trips?.totalMaterialCost!! + invoiceModel?.trips?.tripCost!! - invoiceModel?.trips?.advanceAmount!!)
        invoice_sub_total.text = String.format("%.2f", subTotal)+"/-"
        invoice_tax_rate.text = "4.25%"
        val tax = (subTotal*4.25)/100.0
        invoice_tax.text = String.format("%.2f", tax)+"/-"
        invoice_total.text = String.format("%.2f", (tax+subTotal))+"/-"
    }

    companion object {
        @JvmStatic
        fun newInstance() = InvoiceDetailsFragment()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun generateInvoicePDF(){


    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun printPDF(filePath: String){


    }

    private fun openPDF(pdfName: String){
        Toast.makeText(requireActivity(), "PDF generated successfully", Toast.LENGTH_LONG).show()
        val file = File(Environment.getExternalStorageDirectory().toString() + "/RealEstate")
        if (file.exists()){
            val file1 = File(file.absoluteFile, "$pdfName.pdf")
            val path: Uri = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID+".provider", file1)
            val pdfOpenintent = Intent(Intent.ACTION_VIEW)
            pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pdfOpenintent.setDataAndType(path, "application/pdf")
            try {
                startActivity(pdfOpenintent)
            } catch (e: Exception) {
                Log.e("TAGG", "PDF open error: "+e.message)
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(requireActivity(),"File doesn't exist", Toast.LENGTH_LONG).show()
        }


        /*val file = File(
            Environment.getExternalStorageDirectory().toString()+"",
            pdfName+".pdf"
        )*/

    }


    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 = ContextCompat.checkSelfPermission(
            requireActivity(),
            WRITE_EXTERNAL_STORAGE
        )
        val permission2 = ContextCompat.checkSelfPermission(
            requireActivity(),
            READ_EXTERNAL_STORAGE
        )
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                val writeStorage =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage =
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage) {
                    Toast.makeText(requireActivity(), "Permission Granted..", Toast.LENGTH_SHORT).show()
                    //generatePDF()
                    generateInvoicePDF()
                } else {
                    Toast.makeText(requireActivity(), "Permission Denined.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}