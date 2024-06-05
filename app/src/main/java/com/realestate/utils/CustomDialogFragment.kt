package com.realestate.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.realestate.R
import com.realestate.adapter.spinner.*
import com.realestate.app.RealEstateApp
import com.realestate.callback.AddUpdateListener
import com.realestate.model.*
import com.realestate.restapi.Payload
import kotlinx.android.synthetic.main.add_branch_dialog_fragment.*
import kotlinx.android.synthetic.main.add_branch_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_company_dialog_fragment.*
import kotlinx.android.synthetic.main.add_company_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_dealer_dialog_fragment.*
import kotlinx.android.synthetic.main.add_dealer_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_insurance_dialog_fragment.*
import kotlinx.android.synthetic.main.add_insurance_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_invoice_dialog_fragment.*
import kotlinx.android.synthetic.main.add_invoice_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_material_dialog_fragment.*
import kotlinx.android.synthetic.main.add_permit_dialog_fragment.*
import kotlinx.android.synthetic.main.add_permit_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_pollution_dialog_fragment.*
import kotlinx.android.synthetic.main.add_pollution_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_role_dialog_fragment.*
import kotlinx.android.synthetic.main.add_trip_dialog_fragment.*
import kotlinx.android.synthetic.main.add_trip_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_user_dialog_fragment.*
import kotlinx.android.synthetic.main.add_user_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_user_dialog_fragment.view.add_txt
import kotlinx.android.synthetic.main.add_user_dialog_fragment.view.cancel_txt
import kotlinx.android.synthetic.main.add_user_dialog_fragment.view.title_txt
import kotlinx.android.synthetic.main.add_vehicle_dialog_fragment.*
import kotlinx.android.synthetic.main.add_vehicle_dialog_fragment.view.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Chandan on 29/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class CustomDialogFragment : DialogFragment() {


    companion object {

        val TAG = "CustomDialogFragment"
        private val KEY_TITLE = "KEY_TITLE"
        private val KEY_UPDATE = "KEY_UPDATE"
        private var title = ""
        private var update: String? = null
        private lateinit var roleAdapter: SpinnerRoleAdapter
        private lateinit var userAdapter: SpinnerUserAdapter
        private lateinit var dealerAdapter: SpinnerDealerAdapter
        private lateinit var driverAdapter: SpinnerDriverAdapter
        private lateinit var companyAdapter: SpinnerCompanyAdapter
        private lateinit var vehicleAdapter: SpinnerVehicleAdapter
        private lateinit var materialAdapter: SpinnerMaterialAdapter
        private lateinit var stateAdapter: SpinnerStateAdapter
        private lateinit var tripAdapter: SpinnerTripAdapter
        private var role_id: Int = -1
        private var user_id: Int = -1
        private var dealer_id: Int = -1
        private var material_id: Int = -1
        private var company_id: Int = -1
        private var vehicle_id: Int = -1
        private var trip_id: Int = -1
        private var stateName: String = ""
        private var gender = ""

        var style = STYLE_NO_TITLE
        private var context: Activity? = null
        private var resource: Int? = null
        private var listener: AddUpdateListener? = null
        private var bundle: Bundle? = null
        private var genericData: Any? = null

        fun newInstance(
            context: Activity,
            listener: AddUpdateListener,
            bundle: Bundle?,
            resource: Int,
            title: String,
            update: String?
        ): CustomDialogFragment {
            this.context = context
            this.listener = listener
            this.resource = resource
            this.title = title
            this.update = update
            //val args = Bundle()
            //args.putString(KEY_TITLE, title)
            //args.putString(KEY_UPDATE, update)
            //Log.d("TAG", "args "+ args)
            val fragment = CustomDialogFragment()
            //fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(style, R.style.MyDialog)
        bundle = arguments
        Log.d("TAG", "bundle: $bundle")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (resource != null)
            return inflater.inflate(resource!!, container, false)

        return inflater.inflate(R.layout.custom_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupClickListeners(view)
    }

    override fun onStart() {
        super.onStart()
        if (resource == R.layout.add_invoice_dialog_fragment || resource == R.layout.add_dealer_dialog_fragment || resource == R.layout.add_role_dialog_fragment || resource == R.layout.add_material_dialog_fragment || resource == R.layout.add_company_dialog_fragment || resource == R.layout.add_permit_dialog_fragment || resource == R.layout.add_branch_dialog_fragment || resource == R.layout.add_vehicle_dialog_fragment || resource == R.layout.add_insurance_dialog_fragment || resource == R.layout.add_pollution_dialog_fragment) {
            dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        } else {
            dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }

    }

    /*override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        return dialog
    }*/

    private fun setupView(view: View) {

        view.title_txt.text = title
        if (update != null) {
            view.add_txt.text = update
        }

        if (resource == R.layout.add_user_dialog_fragment) {
            var rolesList = bundle?.getSerializable("role_list") as ArrayList<RolesModel>

            Log.d("TAG", "role_list " + rolesList)

            roleAdapter = SpinnerRoleAdapter(
                context!!,
                rolesList
            )
            view.user_role_spinner.setAdapter(roleAdapter)
            view.user_role_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var roleModel = rolesList.get(p2)
                    //Toast.makeText(this, "Click $roleModel", Toast.LENGTH_LONG).show()

                    if (roleModel.roleType != "Select role") {
                        role_id = roleModel.id
                    } else {
                        role_id = -1
                    }
                }

            })
            if (title.equals("Update User", ignoreCase = true)) {
                genericData = bundle?.getParcelable<UserModel>("user_data")
                Log.d("TAG", "user_data " + genericData)
                if (genericData != null) {

                    user_first_name.setText((genericData as UserModel).firstName)
                    user_last_name.setText((genericData as UserModel).lastName)
                    user_email.setText((genericData as UserModel).email)
                    user_phone_no.setText((genericData as UserModel).phoneNumber)
                    user_alternate_phone_no.setText((genericData as UserModel).alternateNumber)
                    user_aadhar_no.setText((genericData as UserModel).aadharNumber)
                    user_address.setText((genericData as UserModel).address)
                }
                var position = rolesList.indexOf((genericData as UserModel)?.role)
                view.user_role_spinner.setSelection(position)
                role_id = (genericData as UserModel)?.role?.id!!
                var index = -1
                if ((genericData as UserModel)?.gender.equals("Male", ignoreCase = true)) {
                    index = 0
                    gender = "Male"
                }
                if ((genericData as UserModel)?.gender.equals("Female", ignoreCase = true)) {
                    index = 1
                    gender = "Female"
                }

                if ((genericData as UserModel)?.gender.equals("Other", ignoreCase = true)) {
                    index = 2
                    gender = "Other"
                }
                (view.user_gender.getChildAt(index) as RadioButton).setChecked(true)
            }
            view.user_gender.setOnCheckedChangeListener(object :
                RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                    if (p1 == R.id.male) {
                        gender = "Male"
                    } else if (p1 == R.id.female) {
                        gender = "Female"
                    } else {
                        gender = "Other"
                    }
                }

            })
        }

        if (resource == R.layout.add_trip_dialog_fragment) {

            var startDate = ""
            var endDate = ""

            var dealerList = bundle?.getSerializable("dealer_list") as ArrayList<DealerModel>
            var driverList = bundle?.getSerializable("driver_list") as ArrayList<DriverModel>
            var companyList = bundle?.getSerializable("company_list") as ArrayList<CompanyModel>
            var vehicleList = bundle?.getSerializable("vehicle_list") as ArrayList<VehicleModel>

            dealerAdapter = SpinnerDealerAdapter(
                context!!,
                dealerList
            )
            view.trip_dealer_no_spinner.setAdapter(dealerAdapter)
            driverAdapter = SpinnerDriverAdapter(
                context!!,
                driverList
            )
            view.trip_driver_name_spinner.setAdapter(driverAdapter)
            companyAdapter =
                SpinnerCompanyAdapter(
                    context!!,
                    companyList
                )
            view.trip_company_name_spinner.setAdapter(companyAdapter)
            vehicleAdapter =
                SpinnerVehicleAdapter(
                    context!!,
                    vehicleList
                )
            view.trip_vehicle_reg_spinner.setAdapter(vehicleAdapter)

            view.trip_dealer_no_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var roleModel = dealerList.get(p2)

                    if (roleModel.dealerNo != "Select dealer no") {
                        dealer_id = roleModel.id
                    } else {
                        dealer_id = -1
                    }
                }

            })

            view.trip_driver_name_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var roleModel = driverList.get(p2)

                    if ((roleModel.firstName + " " + roleModel.lastName) != "Select driver") {
                        role_id = roleModel.roleId!!
                        user_id = roleModel.id!!
                    } else {
                        role_id = -1
                        user_id = -1
                    }
                }

            })

            view.trip_company_name_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var roleModel = companyList.get(p2)

                    if (roleModel.name != "Select company name") {
                        material_id = roleModel.materialId
                        company_id = roleModel.id
                    } else {
                        material_id = -1
                        company_id = -1
                    }
                }

            })

            view.trip_vehicle_reg_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var roleModel = vehicleList.get(p2)

                    if (roleModel.vehicleRegisteredNo != "Select Registration no") {
                        vehicle_id = roleModel.id
                    } else {
                        vehicle_id = -1
                    }
                }

            })

            trip_start_time.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    when (event?.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            val myCalendar = Calendar.getInstance()

                            val mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                            val mMinute = myCalendar.get(Calendar.MINUTE);
                            val date =
                                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                    myCalendar[Calendar.YEAR] = year
                                    myCalendar[Calendar.MONTH] = monthOfYear
                                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                    val myFormat = "yyyy-MM-dd" //In which you need put here

                                    val sdf = SimpleDateFormat(myFormat, Locale.US)

                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        OnTimeSetListener { view, hourOfDay, minute ->
                                            //txtTime.setText("$hourOfDay:$minute")
                                            startDate = sdf.format(myCalendar.time)
                                            var dd =
                                                sdf.format(myCalendar.time) + " " + hourOfDay + ":" + minute
                                            trip_start_time.setText(dd)
                                        },
                                        mHour,
                                        mMinute,
                                        false
                                    )
                                    timePickerDialog.show()

                                }

                            var datePicker = DatePickerDialog(
                                context!!, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            )
                            var format = SimpleDateFormat("yyyy-MM-dd")
                            if (endDate.isNotEmpty()) {
                                var date = format.parse(endDate)
                                var cal = Calendar.getInstance()
                                cal.setTime(date!!)
                                datePicker.datePicker.maxDate = cal.timeInMillis
                            }

                            datePicker.show()
                        }
                    }

                    return false
                }
            })

            trip_end_time.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    when (event?.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            val myCalendar = Calendar.getInstance()
                            val mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                            val mMinute = myCalendar.get(Calendar.MINUTE);
                            val date =
                                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                    myCalendar[Calendar.YEAR] = year
                                    myCalendar[Calendar.MONTH] = monthOfYear
                                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                    val myFormat = "yyyy-MM-dd" //In which you need put here

                                    val sdf = SimpleDateFormat(myFormat, Locale.US)

                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        OnTimeSetListener { view, hourOfDay, minute ->
                                            //txtTime.setText("$hourOfDay:$minute")
                                            endDate = sdf.format(myCalendar.time)
                                            var dd =
                                                sdf.format(myCalendar.time) + " " + hourOfDay + ":" + minute
                                            trip_end_time.setText(dd)
                                        },
                                        mHour,
                                        mMinute,
                                        false
                                    )
                                    timePickerDialog.show()

                                }
                            var datePicker = DatePickerDialog(
                                context!!, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            )

                            var format = SimpleDateFormat("yyyy-MM-dd")
                            if (startDate.isNotEmpty()) {
                                var date = format.parse(startDate)
                                var cal = Calendar.getInstance()
                                cal.setTime(date!!)
                                datePicker.datePicker.minDate = cal.timeInMillis
                            }
                            datePicker.show()
                        }
                    }

                    return false
                }
            })

            if (title.equals("Add Trip")) {
                text_input_trip_end_time.visibility = View.GONE
                text_input_trip_end_distance.visibility = View.GONE
                text_input_trip_toll_charge.visibility = View.GONE
                text_input_trip_operation_cost.visibility = View.GONE

            } else {
                genericData = bundle?.getParcelable<TripModel>("trip_data")
                text_input_trip_end_time.visibility = View.VISIBLE
                text_input_trip_end_distance.visibility = View.VISIBLE
                text_input_trip_toll_charge.visibility = View.VISIBLE
                text_input_trip_operation_cost.visibility = View.VISIBLE
                trip_start_location.setText((genericData as TripModel).startLocation)
                trip_end_location.setText((genericData as TripModel).endLocation)
                trip_start_time.setText((genericData as TripModel).startTime)
                startDate = trip_start_time.text?.substring(0, 10)!!
                if ((genericData  as TripModel).tripStatus.equals("Ended", ignoreCase = true)){
                    trip_end_time.setText((genericData as TripModel)?.endTime)
                    endDate = trip_end_time.text?.substring(0, 10)!!
                    trip_end_distance.setText((genericData as TripModel)?.endDistance.toString())
                    trip_toll_charge.setText((genericData as TripModel)?.tollCharge.toString())
                    trip_operation_cost.setText((genericData as TripModel)?.operationCost.toString())
                }
                trip_start_distance.setText((genericData as TripModel)?.startDistance.toString())
                trip_fuel_quantity.setText((genericData as TripModel)?.fuelQuantity.toString())
                trip_fuel_price.setText((genericData as TripModel)?.fuelPrice.toString())
                trip_advance_amount.setText((genericData as TripModel)?.advanceAmount.toString())
                trip_weight.setText((genericData as TripModel)?.weight.toString())
                var position = dealerList.indexOf((genericData as TripModel)?.dealer)
                view.trip_dealer_no_spinner.setSelection(position)

                position = driverList.indexOf((genericData as TripModel)?.user)
                view.trip_driver_name_spinner.setSelection(position)

                position = companyList.indexOf((genericData as TripModel)?.company)
                view.trip_company_name_spinner.setSelection(position)

                position = vehicleList.indexOf((genericData as TripModel)?.vehicle)
                view.trip_vehicle_reg_spinner.setSelection(position)

            }

        }

        if (resource == R.layout.add_branch_dialog_fragment) {

            var userList = bundle?.getSerializable("user_list") as List<UserModel>

            userAdapter = SpinnerUserAdapter(
                context!!,
                userList
            )
            view.branch_user_spinner.setAdapter(userAdapter)
            view.branch_user_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var roleModel = userList.get(p2)
                    //Toast.makeText(this, "Click $roleModel", Toast.LENGTH_LONG).show()

                    if (roleModel.userName != "Select user name") {
                        user_id = roleModel.id
                        role_id = roleModel.roleId
                    } else {
                        user_id = -1
                        role_id = -1
                    }
                }

            })
            if (title.equals("Update Branch")) {
                genericData = bundle?.getParcelable<BranchModel>("branch_data") as BranchModel
                branch_name.setText((genericData as BranchModel).branchName)
                branch_address.setText((genericData as BranchModel)?.branchAddress)
                branch_phone_no.setText((genericData as BranchModel)?.phoneNumber)
                branch_email.setText((genericData as BranchModel)?.emailId)
                var position = userList.indexOf((genericData as BranchModel)?.user)
                view.branch_user_spinner.setSelection(position)
            }
        }

        if (resource == R.layout.add_vehicle_dialog_fragment) {
            if (title.equals("Update Vehicle")) {
                genericData = bundle?.getParcelable<VehicleModel>("vehicle_data") as VehicleModel
                vehicle_owner_name.setText((genericData as VehicleModel)?.owner)
                vehicle_reg_no.setText((genericData as VehicleModel)?.vehicleRegisteredNo)
                vehicle_reg_office.setText((genericData as VehicleModel)?.registeredOffice)
                vehicle_reg_date.setText((genericData as VehicleModel)?.registeredYear)
                vehicle_chasis_no.setText((genericData as VehicleModel)?.chasisNo)
            }
            view.vehicle_reg_date.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    when (event?.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            val myCalendar = Calendar.getInstance()
                            val mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                            val mMinute = myCalendar.get(Calendar.MINUTE);
                            val date =
                                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                    myCalendar[Calendar.YEAR] = year
                                    myCalendar[Calendar.MONTH] = monthOfYear
                                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                    val myFormat = "yyyy-MM-dd" //In which you need put here

                                    val sdf = SimpleDateFormat(myFormat, Locale.US)

                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        OnTimeSetListener { view, hourOfDay, minute ->
                                            //txtTime.setText("$hourOfDay:$minute")
                                            var dd =
                                                sdf.format(myCalendar.time) + " " + hourOfDay + ":" + minute
                                            vehicle_reg_date.setText(dd)
                                        },
                                        mHour,
                                        mMinute,
                                        false
                                    )
                                    timePickerDialog.show()

                                }
                            DatePickerDialog(
                                context!!, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    }

                    return false
                }
            })

        }

        if (resource == R.layout.add_insurance_dialog_fragment) {
            var startDate = ""
            var endDate = ""
            var vehicleList = bundle?.getSerializable("vehicle_list") as ArrayList<VehicleModel>
            vehicleAdapter =
                SpinnerVehicleAdapter(
                    context!!,
                    vehicleList
                )
            view.insurance_vehicle_reg_spinner.setAdapter(vehicleAdapter)
            if (title.equals("Update Insurance")) {
                genericData =
                    bundle?.getParcelable<InsuranceModel>("insurance_data") as InsuranceModel
                view.insurance_no.setText((genericData as InsuranceModel).insuranceNo)
                view.insurance_start_date.setText((genericData as InsuranceModel).startDate)
                view.insurance_end_date.setText((genericData as InsuranceModel).endDate)
                startDate = insurance_start_date.text?.substring(0, 10)!!
                endDate = insurance_end_date.text?.substring(0, 10)!!
                view.insurance_provider.setText((genericData as InsuranceModel).provider)
                view.insurance_premium.setText((genericData as InsuranceModel).premium.toString())
                val position = vehicleList.indexOf((genericData as InsuranceModel).vehicle)
                view.insurance_vehicle_reg_spinner.setSelection(position)
            }

            view.insurance_start_date.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    when (event?.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            val myCalendar = Calendar.getInstance()
                            val mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                            val mMinute = myCalendar.get(Calendar.MINUTE);
                            val date =
                                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                    myCalendar[Calendar.YEAR] = year
                                    myCalendar[Calendar.MONTH] = monthOfYear
                                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                    val myFormat = "yyyy-MM-dd" //In which you need put here

                                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                                    startDate = sdf.format(myCalendar.time)

                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        OnTimeSetListener { view, hourOfDay, minute ->
                                            //txtTime.setText("$hourOfDay:$minute")
                                            var dd =
                                                sdf.format(myCalendar.time) + " " + hourOfDay + ":" + minute
                                            insurance_start_date.setText(dd)
                                        },
                                        mHour,
                                        mMinute,
                                        false
                                    )
                                    timePickerDialog.show()

                                }
                            var datePicker = DatePickerDialog(
                                context!!, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            )

                            var format = SimpleDateFormat("yyyy-MM-dd")
                            if (endDate.isNotEmpty()) {
                                var date = format.parse(endDate)
                                var cal = Calendar.getInstance()
                                cal.setTime(date!!)
                                datePicker.datePicker.maxDate = cal.timeInMillis
                            }
                            datePicker.show()
                        }
                    }

                    return false
                }

            })

            view.insurance_end_date.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    when (event?.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            val myCalendar = Calendar.getInstance()
                            val mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                            val mMinute = myCalendar.get(Calendar.MINUTE);
                            val date =
                                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                    myCalendar[Calendar.YEAR] = year
                                    myCalendar[Calendar.MONTH] = monthOfYear
                                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                    val myFormat = "yyyy-MM-dd" //In which you need put here

                                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                                    endDate = sdf.format(myCalendar.time)

                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        OnTimeSetListener { view, hourOfDay, minute ->
                                            //txtTime.setText("$hourOfDay:$minute")
                                            var dd =
                                                sdf.format(myCalendar.time) + " " + hourOfDay + ":" + minute
                                            insurance_end_date.setText(dd)
                                        },
                                        mHour,
                                        mMinute,
                                        false
                                    )
                                    timePickerDialog.show()

                                }
                            var datePicker = DatePickerDialog(
                                context!!, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            )

                            var format = SimpleDateFormat("yyyy-MM-dd")
                            if (startDate.isNotEmpty()) {
                                var date = format.parse(startDate)
                                var cal = Calendar.getInstance()
                                cal.setTime(date!!)
                                datePicker.datePicker.minDate = cal.timeInMillis
                            }
                            datePicker.show()
                        }
                    }

                    return false
                }

            })

            view.insurance_vehicle_reg_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var roleModel = vehicleList.get(p2)

                    if (roleModel.vehicleRegisteredNo != "Select Vehicle reg. no") {
                        vehicle_id = roleModel.id
                    } else {
                        vehicle_id = -1
                    }
                }

            })

        }

        if (resource == R.layout.add_permit_dialog_fragment) {

            var startDate = ""
            var endDate = ""
            var vehicleList = bundle?.getSerializable("vehicle_list") as ArrayList<VehicleModel>
            vehicleAdapter =
                SpinnerVehicleAdapter(
                    context!!,
                    vehicleList
                )
            view.permit_vehicle_reg_spinner.setAdapter(vehicleAdapter)

            if (title.equals("Update Permit")) {
                genericData = bundle?.getParcelable<PermitModel>("permit_data") as PermitModel
                view.permit_no.setText((genericData as PermitModel)?.permitNo)
                view.permit_start_date.setText((genericData as PermitModel)?.startDate)
                view.permit_end_date.setText((genericData as PermitModel)?.endDate)
                startDate = permit_start_date.text?.substring(0, 10)!!
                endDate = permit_end_date.text?.substring(0, 10)!!
                view.permit_premium.setText((genericData as PermitModel)?.premium.toString())
                view.permit_type.setText((genericData as PermitModel)?.permitType)
                val position = vehicleList.indexOf((genericData as PermitModel)?.vehicle)
                view.permit_vehicle_reg_spinner.setSelection(position)
            }

            permit_start_date.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    when (event?.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            val myCalendar = Calendar.getInstance()
                            val mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                            val mMinute = myCalendar.get(Calendar.MINUTE);
                            val date =
                                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                    myCalendar[Calendar.YEAR] = year
                                    myCalendar[Calendar.MONTH] = monthOfYear
                                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                    val myFormat = "yyyy-MM-dd" //In which you need put here

                                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                                    startDate = sdf.format(myCalendar.time)
                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        OnTimeSetListener { view, hourOfDay, minute ->
                                            //txtTime.setText("$hourOfDay:$minute")
                                            var dd =
                                                sdf.format(myCalendar.time) + " " + hourOfDay + ":" + minute
                                            permit_start_date.setText(dd)
                                        },
                                        mHour,
                                        mMinute,
                                        false
                                    )
                                    timePickerDialog.show()

                                }
                            var datePicker = DatePickerDialog(
                                context!!, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            )

                            var format = SimpleDateFormat("yyyy-MM-dd")
                            if (endDate.isNotEmpty()) {
                                var date = format.parse(endDate)
                                var cal = Calendar.getInstance()
                                cal.setTime(date!!)
                                datePicker.datePicker.maxDate = cal.timeInMillis
                            }
                            datePicker.show()
                        }
                    }

                    return false
                }

            })

            permit_end_date.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    when (event?.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            val myCalendar = Calendar.getInstance()
                            val mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                            val mMinute = myCalendar.get(Calendar.MINUTE);
                            val date =
                                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                    myCalendar[Calendar.YEAR] = year
                                    myCalendar[Calendar.MONTH] = monthOfYear
                                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                    val myFormat = "yyyy-MM-dd" //In which you need put here

                                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                                    endDate = sdf.format(myCalendar.time)
                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        OnTimeSetListener { view, hourOfDay, minute ->
                                            //txtTime.setText("$hourOfDay:$minute")
                                            var dd =
                                                sdf.format(myCalendar.time) + " " + hourOfDay + ":" + minute
                                            permit_end_date.setText(dd)
                                        },
                                        mHour,
                                        mMinute,
                                        false
                                    )
                                    timePickerDialog.show()

                                }
                            var datePicker = DatePickerDialog(
                                context!!, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            )

                            var format = SimpleDateFormat("yyyy-MM-dd")
                            if (startDate.isNotEmpty()) {
                                var date = format.parse(startDate)
                                var cal = Calendar.getInstance()
                                cal.setTime(date!!)
                                datePicker.datePicker.minDate = cal.timeInMillis
                            }
                            datePicker.show()
                        }
                    }

                    return false
                }

            })

            view.permit_vehicle_reg_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var roleModel = vehicleList.get(p2)

                    if (roleModel.vehicleRegisteredNo != "Select Vehicle reg. no") {
                        vehicle_id = roleModel.id
                    } else {
                        vehicle_id = -1
                    }
                }

            })

        }

        if (resource == R.layout.add_pollution_dialog_fragment) {
            var startDate = ""
            var endDate = ""
            var vehicleList = bundle?.getSerializable("vehicle_list") as ArrayList<VehicleModel>
            vehicleAdapter =
                SpinnerVehicleAdapter(
                    context!!,
                    vehicleList
                )
            view.pollution_vehicle_reg_spinner.setAdapter(vehicleAdapter)
            if (title.equals("Update Pollution")) {
                genericData =
                    bundle?.getParcelable<PollutionModel>("pollution_data") as PollutionModel
                view.pollution_no.setText((genericData as PollutionModel)?.pollutionNo)
                view.pollution_start_date.setText((genericData as PollutionModel)?.startDate)
                view.pollution_end_date.setText((genericData as PollutionModel)?.endDate)
                startDate = pollution_start_date.text?.substring(0, 10)!!
                endDate = pollution_end_date.text?.substring(0, 10)!!
                view.pollution_price.setText((genericData as PollutionModel)?.price.toString())
                val position = vehicleList.indexOf((genericData as PollutionModel)?.vehicle)
                view.pollution_vehicle_reg_spinner.setSelection(position)
            }

            pollution_start_date.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    when (event?.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            val myCalendar = Calendar.getInstance()
                            val mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                            val mMinute = myCalendar.get(Calendar.MINUTE);
                            val date =
                                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                    myCalendar[Calendar.YEAR] = year
                                    myCalendar[Calendar.MONTH] = monthOfYear
                                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                    val myFormat = "yyyy-MM-dd" //In which you need put here

                                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                                    startDate = sdf.format(myCalendar.time)

                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        OnTimeSetListener { view, hourOfDay, minute ->
                                            //txtTime.setText("$hourOfDay:$minute")
                                            var dd =
                                                sdf.format(myCalendar.time) + " " + hourOfDay + ":" + minute
                                            pollution_start_date.setText(dd)
                                        },
                                        mHour,
                                        mMinute,
                                        false
                                    )
                                    timePickerDialog.show()

                                }
                            var datePicker = DatePickerDialog(
                                context!!, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            )

                            var format = SimpleDateFormat("yyyy-MM-dd")
                            if (endDate.isNotEmpty()) {
                                var date = format.parse(endDate)
                                var cal = Calendar.getInstance()
                                cal.setTime(date!!)
                                datePicker.datePicker.maxDate = cal.timeInMillis
                            }
                            datePicker.show()
                        }
                    }

                    return false
                }

            })

            pollution_end_date.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    when (event?.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            val myCalendar = Calendar.getInstance()
                            val mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                            val mMinute = myCalendar.get(Calendar.MINUTE);
                            val date =
                                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                    myCalendar[Calendar.YEAR] = year
                                    myCalendar[Calendar.MONTH] = monthOfYear
                                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                    val myFormat = "yyyy-MM-dd" //In which you need put here

                                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                                    endDate = sdf.format(myCalendar.time)

                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        OnTimeSetListener { view, hourOfDay, minute ->
                                            //txtTime.setText("$hourOfDay:$minute")
                                            var dd =
                                                sdf.format(myCalendar.time) + " " + hourOfDay + ":" + minute
                                            pollution_end_date.setText(dd)
                                        },
                                        mHour,
                                        mMinute,
                                        false
                                    )
                                    timePickerDialog.show()

                                }
                            var datePicker = DatePickerDialog(
                                context!!, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            )

                            var format = SimpleDateFormat("yyyy-MM-dd")
                            if (startDate.isNotEmpty()) {
                                var date = format.parse(startDate)
                                var cal = Calendar.getInstance()
                                cal.setTime(date!!)
                                datePicker.datePicker.minDate = cal.timeInMillis
                            }
                            datePicker.show()
                        }
                    }

                    return false
                }

            })

            view.pollution_vehicle_reg_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var roleModel = vehicleList.get(p2)

                    if (roleModel.vehicleRegisteredNo != "Select Registration no") {
                        vehicle_id = roleModel.id
                    } else {
                        vehicle_id = -1
                    }
                }

            })

        }

        if (resource == R.layout.add_invoice_dialog_fragment) {

            var tripList = bundle?.getSerializable("trip_list") as ArrayList<TripModel>
            tripAdapter =
                SpinnerTripAdapter(
                    context!!,
                    tripList
                )
            view.invoice_trip_spinner.setAdapter(tripAdapter)
            if (title.equals("Update Invoice")) {
                genericData = bundle?.getParcelable<InvoiceModel>("invoice_data") as InvoiceModel
                invoice_date.setText((genericData as InvoiceModel)?.invoiceDate)
                invoice_order_id.setText((genericData as InvoiceModel)?.orderId)
                var position = tripList.indexOf((genericData as InvoiceModel)?.trips)
                invoice_trip_spinner.setSelection(position)
            }
            invoice_date.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    when (event?.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            val myCalendar = Calendar.getInstance()
                            val mHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                            val mMinute = myCalendar.get(Calendar.MINUTE);
                            val date =
                                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                    myCalendar[Calendar.YEAR] = year
                                    myCalendar[Calendar.MONTH] = monthOfYear
                                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                    val myFormat = "yyyy-MM-dd" //In which you need put here

                                    val sdf = SimpleDateFormat(myFormat, Locale.US)

                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        OnTimeSetListener { view, hourOfDay, minute ->
                                            //txtTime.setText("$hourOfDay:$minute")
                                            var dd =
                                                sdf.format(myCalendar.time) + " " + hourOfDay + ":" + minute
                                            invoice_date.setText(dd)
                                        },
                                        mHour,
                                        mMinute,
                                        false
                                    )
                                    timePickerDialog.show()

                                }
                            DatePickerDialog(
                                context!!, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    }

                    return false
                }

            })

            view.invoice_trip_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var trip = tripList.get(p2)

                    if (trip.tripsNo != "Select trip") {
                        trip_id = trip.id
                    } else {
                        trip_id = -1
                    }
                }

            })

        }

        if (resource == R.layout.add_company_dialog_fragment) {
            var materialList = bundle?.getSerializable("material_list") as ArrayList<MaterialModel>
            materialAdapter =
                SpinnerMaterialAdapter(
                    context!!,
                    materialList
                )
            view.company_material_spinner.setAdapter(materialAdapter)
            if (title.equals("Update Company")) {
                genericData = bundle?.getParcelable<CompanyModel>("company_data") as CompanyModel
                company_name.setText((genericData as CompanyModel)?.name)
                company_address.setText((genericData as CompanyModel)?.address)
                val position = materialList.indexOf((genericData as CompanyModel)?.material)
                view.company_material_spinner.setSelection(position)
            }
            view.company_material_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var material = materialList.get(p2)

                    if (material.materialName != "Select material") {
                        material_id = material.id
                    } else {
                        material_id = -1
                    }
                }

            })

        }

        if (resource == R.layout.add_material_dialog_fragment) {

            if (title.equals("Update Material")) {
                genericData = bundle?.getParcelable<MaterialModel>("material_data") as MaterialModel
                material_name.setText((genericData as MaterialModel)?.materialName)
                material_price.setText((genericData as MaterialModel)?.price.toString())
            }

        }

        if (resource == R.layout.add_dealer_dialog_fragment) {
            val stateList = bundle?.getSerializable("state_list") as ArrayList<String>
            stateAdapter = SpinnerStateAdapter(RealEstateApp.context, stateList)
            view.dealer_state_spinner.adapter = stateAdapter
            if (title.equals("Update Dealer")) {
                genericData = bundle?.getParcelable<DealerModel>("dealer_data") as DealerModel
                dealer_name.setText((genericData as DealerModel).dealerName)
                dealer_phone_no.setText((genericData as DealerModel)?.phoneNumber)
                dealer_address.setText((genericData as DealerModel)?.address)
                dealer_pincode.setText((genericData as DealerModel)?.pincode.toString())
                var position = stateList.indexOf((genericData as DealerModel)?.state)
                view.dealer_state_spinner.setSelection(position)
            }
            view.dealer_state_spinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var state = stateList.get(p2)

                    if (state != "Select state") {
                        stateName = state
                    } else {
                        stateName = ""
                    }
                }

            })
        }

        if (resource == R.layout.add_role_dialog_fragment) {
            if (title.equals("Update Role")) {
                genericData = bundle?.getParcelable<RolesModel>("role_data")
                role_type.setText((genericData as RolesModel)?.roleType)
                role_normalized_name.setText((genericData as RolesModel)?.normalizedName)
            }
        }
    }

    private fun setupClickListeners(view: View) {
        view.cancel_txt.setOnClickListener {
            listener?.onCancel(true)
            dismiss()
        }
        view.add_txt.setOnClickListener {

            if (resource == R.layout.add_user_dialog_fragment) {
                var email = user_email.text.toString().trim()
                var first_name = user_first_name.text.toString().trim()
                var last_name = user_last_name.text.toString().trim()
                var phone_no = user_phone_no.text.toString().trim()
                var alt_phone_no = user_alternate_phone_no.text.toString().trim()
                var aadhar_no = user_aadhar_no.text.toString().trim()
                var address = user_address.text.toString().trim()


                var emailEmpty: Boolean
                var firstNameEmpty: Boolean
                var lastNameEmpty: Boolean
                var phoneEmpty: Boolean
                var genderEmpty: Boolean
                var aadharEmpty: Boolean
                var addressEmpty: Boolean

                if (first_name.isEmpty()) {
                    text_input_user_first_name.error = "Field must not be empty"
                    firstNameEmpty = true
                } else {
                    text_input_user_first_name.error = ""
                    firstNameEmpty = false
                }

                if (last_name.isEmpty()) {
                    text_input_user_last_name.error = "Field must not be empty"
                    lastNameEmpty = true
                } else {
                    text_input_user_last_name.error = ""
                    lastNameEmpty = false
                }

                if (phone_no.isEmpty()) {
                    text_input_user_phone_no.error = "Field must not be empty"
                    phoneEmpty = true
                } else {
                    text_input_user_phone_no.error = ""
                    phoneEmpty = false
                }

                if (email.isEmpty()) {
                    text_input_user_email.error = "Field must not be empty"
                    emailEmpty = true
                } else {
                    text_input_user_email.error = ""
                    emailEmpty = false
                }

                if (aadhar_no.isEmpty()) {
                    text_input_user_aadhar_no.error = "Field must not be empty"
                    aadharEmpty = true
                } else {
                    text_input_user_aadhar_no.error = ""
                    aadharEmpty = false
                }

                if (address.isEmpty()) {
                    text_input_user_address.error = "Field must not be empty"
                    addressEmpty = true
                } else {
                    text_input_user_address.error = ""
                    addressEmpty = false
                }

                if (gender.isEmpty()) {
                    Toast.makeText(context, "Please select your gender", Toast.LENGTH_SHORT).show()
                    genderEmpty = true
                } else {
                    genderEmpty = false
                }

                if (emailEmpty || firstNameEmpty || lastNameEmpty || phoneEmpty || genderEmpty || aadharEmpty || addressEmpty) {

                } else {
                    if (Constant.isValidEmail(email)) {
                        Log.d("phone_no.length ", "phone_no " + phone_no.length)
                        if (phone_no.length >= 10) {
                            if (role_id != -1) {
                                val payload = Payload()
                                payload.add("firstName", first_name)
                                payload.add("lastName", last_name)
                                payload.add("gender", gender)
                                payload.add("email", email)
                                payload.add("phoneNumber", phone_no)
                                payload.add("alternateNumber", alt_phone_no)
                                payload.add("address", address)
                                payload.add("aadharNumber", aadhar_no)
                                payload.add("roleId", role_id)
                                if (title.equals("Update User")) {
                                    payload.add("id", (genericData as UserModel).id)
                                }
                                if (NetworkUtils.isOnline) {
                                    listener?.onAddOrUpdate(true, payload)
                                    dismiss()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Network connection error",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            } else {
                                Toast.makeText(context, "Please select role", Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            text_input_user_phone_no.error = "Invalid phone number"
                        }
                    } else {
                        text_input_user_email.error = "Invalid email format"
                    }
                }

            }

            if (resource == R.layout.add_trip_dialog_fragment) {
                var start_location = trip_start_location.text.toString().trim()
                var end_location = trip_end_location.text.toString().trim()
                var start_time = trip_start_time.text.toString().trim()
                //var end_time = Constant.convertTimeToString(trip_end_time.text.toString().trim())
                var end_time = trip_end_time.text.toString().trim()
                var start_distance = trip_start_distance.text.toString().trim()
                var end_distance = trip_end_distance.text.toString().trim()
                var fuel_quantity = trip_fuel_quantity.text.toString().trim()
                var fuel_price = trip_fuel_price.text.toString().trim()
                var toll_charge = trip_toll_charge.text.toString().trim()
                var operation_cost = trip_operation_cost.text.toString().trim()
                var advance_amount = trip_advance_amount.text.toString().trim()
                var weight = trip_weight.text.toString().trim()

                var start_locationEmpty: Boolean
                var end_locationEmpty: Boolean
                var start_timeEmpty: Boolean
                var end_timeEmpty: Boolean
                var start_distanceEmpty: Boolean
                var end_distanceEmpty: Boolean
                var fuel_quantityEmpty: Boolean
                var fuel_priceEmpty: Boolean
                var toll_chargeEmpty: Boolean
                var operation_costEmpty: Boolean
                var advance_amountEmpty: Boolean
                var weightEmpty: Boolean
                var dealer_idEmpty: Boolean
                var company_idEmpty: Boolean
                var driver_idEmpty: Boolean
                var vehicle_reg_idEmpty: Boolean

                if (start_location.isEmpty()) {
                    text_input_trip_start_location.error = "Field must not be empty"
                    start_locationEmpty = true
                } else {
                    text_input_trip_start_location.error = ""
                    start_locationEmpty = false
                }

                if (end_location.isEmpty()) {
                    text_input_trip_end_location.error = "Field must not be empty"
                    end_locationEmpty = true
                } else {
                    text_input_trip_end_location.error = ""
                    end_locationEmpty = false
                }

                if (start_time.isEmpty()) {
                    text_input_trip_start_time.error = "Field must not be empty"
                    start_timeEmpty = true
                } else {
                    text_input_trip_start_time.error = ""
                    start_timeEmpty = false
                }

                if (start_distance.isEmpty()) {
                    text_input_trip_start_distance.error = "Field must not be empty"
                    start_distanceEmpty = true
                } else {
                    text_input_trip_start_distance.error = ""
                    start_distanceEmpty = false
                }

                if (fuel_quantity.isEmpty()) {
                    text_input_trip_fuel_quantity.error = "Field must not be empty"
                    fuel_quantityEmpty = true
                } else {
                    text_input_trip_fuel_quantity.error = ""
                    fuel_quantityEmpty = false
                }

                if (fuel_price.isEmpty()) {
                    text_input_trip_fuel_price.error = "Field must not be empty"
                    fuel_priceEmpty = true
                } else {
                    text_input_trip_fuel_price.error = ""
                    fuel_priceEmpty = false
                }

                if (advance_amount.isEmpty()) {
                    text_input_trip_advance_amount.error = "Field must not be empty"
                    advance_amountEmpty = true
                } else {
                    text_input_trip_advance_amount.error = ""
                    advance_amountEmpty = false
                }

                if (weight.isEmpty()) {
                    text_input_trip_weight.error = "Field must not be empty"
                    weightEmpty = true
                } else {
                    text_input_trip_weight.error = ""
                    weightEmpty = false
                }

                if (dealer_id == -1) {
                    Toast.makeText(context, "Please select dealer no", Toast.LENGTH_SHORT)
                        .show()
                    dealer_idEmpty = true
                } else {
                    dealer_idEmpty = false
                }

                if (role_id == -1) {
                    Toast.makeText(context, "Please select driver name", Toast.LENGTH_SHORT)
                        .show()
                    driver_idEmpty = true
                } else {
                    driver_idEmpty = false
                }

                if (company_id == -1) {
                    Toast.makeText(context, "Please select company name", Toast.LENGTH_SHORT)
                        .show()
                    company_idEmpty = true
                } else {
                    company_idEmpty = false
                }

                if (vehicle_id == -1) {
                    Toast.makeText(
                        context,
                        "Please select vehicle registration no",
                        Toast.LENGTH_SHORT
                    ).show()
                    vehicle_reg_idEmpty = true
                } else {
                    vehicle_reg_idEmpty = false
                }

                if (title.equals("Update Trip")) {
                    if (end_time.isEmpty()) {
                        text_input_trip_end_time.error = "Field must not be empty"
                        end_timeEmpty = true
                    } else {
                        text_input_trip_end_time.error = ""
                        end_timeEmpty = false
                    }

                    if (end_distance.isEmpty()) {
                        text_input_trip_end_distance.error = "Field must not be empty"
                        end_distanceEmpty = true
                    } else {
                        text_input_trip_end_distance.error = ""
                        end_distanceEmpty = false
                    }
                    if (operation_cost.isEmpty()) {
                        text_input_trip_operation_cost.error = "Field must not be empty"
                        operation_costEmpty = true
                    } else {
                        text_input_trip_operation_cost.error = ""
                        operation_costEmpty = false
                    }
                    if (toll_charge.isEmpty()) {
                        text_input_trip_toll_charge.error = "Field must not be empty"
                        toll_chargeEmpty = true
                    } else {
                        text_input_trip_toll_charge.error = ""
                        toll_chargeEmpty = false
                    }
                    if (start_locationEmpty || end_locationEmpty || start_timeEmpty || start_distanceEmpty || fuel_quantityEmpty || fuel_priceEmpty || advance_amountEmpty || weightEmpty || dealer_idEmpty || driver_idEmpty || company_idEmpty || vehicle_reg_idEmpty || end_distanceEmpty || end_timeEmpty || operation_costEmpty || toll_chargeEmpty) {

                        return@setOnClickListener
                    } else {
                        val payload = Payload()
                        payload.add("startLocation", start_location)
                        payload.add("endLocation", end_location)
                        payload.add("startTime", start_time)
                        payload.add("startDistance", start_distance.toDouble())
                        payload.add("fuelQuantity", fuel_quantity.toDouble())
                        payload.add("fuelPrice", fuel_price.toDouble())
                        payload.add("advanceAmount", advance_amount.toDouble())
                        payload.add("weight", weight.toDouble())
                        payload.add("vehicleId", vehicle_id)
                        payload.add("dealerId", dealer_id)
                        payload.add("materialId", material_id)
                        payload.add("companyId", company_id)
                        payload.add("userId", user_id)
                        payload.add("roleId", role_id)
                        payload.add("endTime", end_time)
                        payload.add("endDistance", end_distance.toDouble())
                        payload.add("tollCharge", toll_charge.toDouble())
                        payload.add("operationCost", operation_cost.toDouble())
                        payload.add("id", (genericData as TripModel).id)
                        if (NetworkUtils.isOnline) {
                            listener?.onAddOrUpdate(true, payload)
                            dismiss()
                        } else {
                            Toast.makeText(
                                context,
                                "Network connection error",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                    }

                } else {

                    if (start_locationEmpty || end_locationEmpty || start_timeEmpty || start_distanceEmpty || fuel_quantityEmpty || fuel_priceEmpty || advance_amountEmpty || weightEmpty || dealer_idEmpty || driver_idEmpty || company_idEmpty || vehicle_reg_idEmpty) {
                        return@setOnClickListener
                    } else {
                        val payload = Payload()
                        payload.add("startLocation", start_location)
                        payload.add("endLocation", end_location)
                        payload.add("startTime", start_time)
                        payload.add("startDistance", start_distance.toDouble())
                        payload.add("fuelQuantity", fuel_quantity.toDouble())
                        payload.add("fuelPrice", fuel_price.toDouble())
                        payload.add("advanceAmount", advance_amount.toDouble())
                        payload.add("weight", weight.toDouble())
                        payload.add("vehicleId", vehicle_id)
                        payload.add("dealerId", dealer_id)
                        payload.add("materialId", material_id)
                        payload.add("companyId", company_id)
                        payload.add("userId", user_id)
                        payload.add("roleId", role_id)
                        if (NetworkUtils.isOnline) {
                            listener?.onAddOrUpdate(true, payload)
                            dismiss()
                        } else {
                            Toast.makeText(
                                context,
                                "Network connection error",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }

                }
            }

            if (resource == R.layout.add_branch_dialog_fragment) {
                var branch_name = branch_name.text.toString().trim()
                var branch_address = branch_address.text.toString().trim()
                var branch_phone_no = branch_phone_no.text.toString().trim()
                var branch_email = branch_email.text.toString().trim()

                var branch_nameEmpty: Boolean
                var branch_addressEmpty: Boolean
                var branch_phone_noEmpty: Boolean
                var branch_emailEmpty: Boolean
                var branch_user_nameEmpty: Boolean

                if (branch_name.isEmpty()) {
                    text_input_branch_name.error = "Field must not be empty"
                    branch_nameEmpty = true
                } else {
                    text_input_branch_name.error = ""
                    branch_nameEmpty = false
                }

                if (branch_address.isEmpty()) {
                    text_input_branch_address.error = "Field must not be empty"
                    branch_addressEmpty = true
                } else {
                    text_input_branch_address.error = ""
                    branch_addressEmpty = false
                }

                if (branch_phone_no.isEmpty()) {
                    text_input_branch_phone_no.error = "Field must not be empty"
                    branch_phone_noEmpty = true
                } else {
                    text_input_branch_phone_no.error = ""
                    branch_phone_noEmpty = false
                }
                if (branch_email.isEmpty()) {
                    text_input_branch_email.error = "Field must not be empty"
                    branch_emailEmpty = true
                } else {
                    text_input_branch_email.error = ""
                    branch_emailEmpty = false
                }

                if (user_id == -1) {
                    Toast.makeText(context, "Please select user name", Toast.LENGTH_SHORT)
                        .show()
                    branch_user_nameEmpty = true
                } else {
                    branch_user_nameEmpty = false
                }

                if (branch_nameEmpty || branch_addressEmpty || branch_phone_noEmpty || branch_emailEmpty || branch_user_nameEmpty) {
                    return@setOnClickListener
                } else {
                    if (Constant.isValidEmail(branch_email)) {
                        text_input_branch_email.error = ""
                        if (branch_phone_no.length >= 10) {
                            text_input_branch_phone_no.error = ""
                            var payload = Payload()
                            payload.add("branchName", branch_name)
                            payload.add("branchAddress", branch_address)
                            payload.add("phoneNumber", branch_phone_no)
                            payload.add("emailId", branch_email)
                            payload.add("userId", user_id)
                            payload.add("roleId", role_id)

                            if (title.equals("Update Branch"))
                                payload.add("id", (genericData as BranchModel)?.id)
                            if (NetworkUtils.isOnline) {
                                listener?.onAddOrUpdate(true, payload)
                                dismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Network connection error",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        } else {
                            text_input_branch_phone_no.error = "Inalid phone number"
                        }
                    } else {
                        text_input_branch_email.error = "Invalid email format"
                    }
                }
            }

            if (resource == R.layout.add_vehicle_dialog_fragment) {
                var vehicle_owner_name = vehicle_owner_name.text.toString().trim()
                var vehicle_reg_no = vehicle_reg_no.text.toString().trim()
                var vehicle_reg_office = vehicle_reg_office.text.toString().trim()
                var vehicle_reg_date = vehicle_reg_date.text.toString().trim()
                var vehicle_chasis_no = vehicle_chasis_no.text.toString().trim()

                var vehicle_owner_nameEmpty: Boolean
                var vehicle_reg_noEmpty: Boolean
                var vehicle_reg_officeEmpty: Boolean
                var vehicle_reg_dateEmpty: Boolean
                var vehicle_chasis_noEmpty: Boolean

                if (vehicle_owner_name.isEmpty()) {
                    text_input_vehicle_owner_name.error = "Field must not be empty"
                    vehicle_owner_nameEmpty = true
                } else {
                    text_input_vehicle_owner_name.error = ""
                    vehicle_owner_nameEmpty = false
                }

                if (vehicle_reg_no.isEmpty()) {
                    text_input_vehicle_reg_no.error = "Field must not be empty"
                    vehicle_reg_noEmpty = true
                } else {
                    text_input_vehicle_reg_no.error = ""
                    vehicle_reg_noEmpty = false
                }

                if (vehicle_reg_office.isEmpty()) {
                    text_input_vehicle_reg_office.error = "Field must not be empty"
                    vehicle_reg_officeEmpty = true
                } else {
                    text_input_vehicle_reg_office.error = ""
                    vehicle_reg_officeEmpty = false
                }

                if (vehicle_reg_date.isEmpty()) {
                    text_input_vehicle_reg_date.error = "Field must not be empty"
                    vehicle_reg_dateEmpty = true
                } else {
                    text_input_vehicle_reg_date.error = ""
                    vehicle_reg_dateEmpty = false
                }

                if (vehicle_chasis_no.isEmpty()) {
                    text_input_vehicle_chasis_no.error = "Field must not be empty"
                    vehicle_chasis_noEmpty = true
                } else {
                    text_input_vehicle_chasis_no.error = ""
                    vehicle_chasis_noEmpty = false
                }

                if (vehicle_owner_nameEmpty || vehicle_reg_noEmpty || vehicle_reg_officeEmpty || vehicle_reg_dateEmpty || vehicle_chasis_noEmpty) {
                    return@setOnClickListener
                } else {
                    var payload = Payload()
                    payload.add("owner", vehicle_owner_name)
                    payload.add("vehicleRegisteredNo", vehicle_reg_no)
                    payload.add("registeredOffice", vehicle_reg_office)
                    payload.add("registeredYear", vehicle_reg_date)
                    payload.add("chasisNo", vehicle_chasis_no)
                    if (title.equals("Update Vehicle"))
                        payload.add("id", (genericData as VehicleModel).id)

                    if (NetworkUtils.isOnline) {
                        listener?.onAddOrUpdate(true, payload)
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Network connection error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

            }

            if (resource == R.layout.add_insurance_dialog_fragment) {
                var insurance_no = insurance_no.text.toString().trim()
                var insurance_start_date = insurance_start_date.text.toString().trim()
                var insurance_end_date = insurance_end_date.text.toString().trim()
                var insurance_provider = insurance_provider.text.toString().trim()
                var insurance_premium = insurance_premium.text.toString().trim()

                var insurance_noEmpty: Boolean
                var insurance_start_dateEmpty: Boolean
                var insurance_end_dateEmpty: Boolean
                var insurance_providerEmpty: Boolean
                var insurance_premiumEmpty: Boolean
                var vehicle_reg_idEmpty: Boolean

                if (insurance_no.isEmpty()) {
                    text_input_insurance_no.error = "Field must not be empty"
                    insurance_noEmpty = true
                } else {
                    text_input_insurance_no.error = ""
                    insurance_noEmpty = false
                }

                if (insurance_start_date.isEmpty()) {
                    text_input_insurance_start_date.error = "Field must not be empty"
                    insurance_start_dateEmpty = true
                } else {
                    text_input_insurance_start_date.error = ""
                    insurance_start_dateEmpty = false
                }

                if (insurance_end_date.isEmpty()) {
                    text_input_insurance_end_date.error = "Field must not be empty"
                    insurance_end_dateEmpty = true
                } else {
                    text_input_insurance_end_date.error = ""
                    insurance_end_dateEmpty = false
                }

                if (insurance_provider.isEmpty()) {
                    text_input_insurance_provider.error = "Field must not be empty"
                    insurance_providerEmpty = true
                } else {
                    text_input_insurance_provider.error = ""
                    insurance_providerEmpty = false
                }

                if (insurance_premium.isEmpty()) {
                    text_input_insurance_premium.error = "Field must not be empty"
                    insurance_premiumEmpty = true
                } else {
                    text_input_insurance_premium.error = ""
                    insurance_premiumEmpty = false
                }
                if (vehicle_id == -1) {
                    Toast.makeText(
                        context,
                        "Please select vehicle registration no",
                        Toast.LENGTH_SHORT
                    ).show()
                    vehicle_reg_idEmpty = true
                } else {
                    vehicle_reg_idEmpty = false
                }

                if (insurance_noEmpty || insurance_start_dateEmpty || insurance_end_dateEmpty || insurance_providerEmpty || insurance_premiumEmpty || vehicle_reg_idEmpty) {

                } else {
                    var payload = Payload()
                    payload.add("insuranceNo", insurance_no)
                    payload.add("startDate", insurance_start_date)
                    payload.add("endDate", insurance_end_date)
                    payload.add("provider", insurance_provider)
                    payload.add("premium", insurance_premium.toDouble())
                    payload.add("vehicleId", vehicle_id)
                    if (title.equals("Update Insurance"))
                        payload.add("id", (genericData as InsuranceModel).id)
                    if (NetworkUtils.isOnline) {
                        listener?.onAddOrUpdate(true, payload)
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Network connection error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

            }

            if (resource == R.layout.add_permit_dialog_fragment) {
                var permit_no = permit_no.text.toString().trim()
                var permit_start_date = permit_start_date.text.toString().trim()
                var permit_end_date = permit_end_date.text.toString().trim()
                var permit_premium = permit_premium.text.toString().trim()
                var permit_type = permit_type.text.toString().trim()

                var permit_noEmpty: Boolean
                var permit_start_dateEmpty: Boolean
                var permit_end_dateEmpty: Boolean
                var permit_premiumEmpty: Boolean
                var permit_typeEmpty: Boolean
                var vehicle_reg_idEmpty: Boolean

                if (permit_no.isEmpty()) {
                    text_input_permit_no.error = "Field must not be empty"
                    permit_noEmpty = true
                } else {
                    text_input_permit_no.error = ""
                    permit_noEmpty = false
                }

                if (permit_start_date.isEmpty()) {
                    text_input_permit_start_date.error = "Field must not be empty"
                    permit_start_dateEmpty = true
                } else {
                    text_input_permit_start_date.error = ""
                    permit_start_dateEmpty = false
                }

                if (permit_end_date.isEmpty()) {
                    text_input_permit_end_date.error = "Field must not be empty"
                    permit_end_dateEmpty = true
                } else {
                    text_input_permit_end_date.error = ""
                    permit_end_dateEmpty = false
                }

                if (permit_premium.isEmpty()) {
                    text_input_permit_premium.error = "Field must not be empty"
                    permit_premiumEmpty = true
                } else {
                    text_input_permit_premium.error = ""
                    permit_premiumEmpty = false
                }

                if (permit_type.isEmpty()) {
                    text_input_permit_type.error = "Field must not be empty"
                    permit_typeEmpty = true
                } else {
                    text_input_permit_type.error = ""
                    permit_typeEmpty = false
                }

                if (vehicle_id == -1) {
                    Toast.makeText(
                        context,
                        "Please select vehicle registration no",
                        Toast.LENGTH_SHORT
                    ).show()
                    vehicle_reg_idEmpty = true
                } else {
                    vehicle_reg_idEmpty = false
                }

                if (permit_noEmpty || permit_start_dateEmpty || permit_end_dateEmpty || permit_premiumEmpty || permit_typeEmpty || vehicle_reg_idEmpty) {

                } else {
                    var payload = Payload()
                    payload.add("permitNo", permit_no)
                    payload.add("startDate", permit_start_date)
                    payload.add("endDate", permit_end_date)
                    payload.add("premium", permit_premium.toDouble())
                    payload.add("permitType", permit_type)
                    payload.add("vehicleId", vehicle_id)
                    if (title.equals("Update Permit"))
                        payload.add("id", (genericData as PermitModel).id)
                    if (NetworkUtils.isOnline) {
                        listener?.onAddOrUpdate(true, payload)
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Network connection error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }


            }

            if (resource == R.layout.add_pollution_dialog_fragment) {
                var pollution_no = pollution_no.text.toString().trim()
                var pollution_start_date = pollution_start_date.text.toString().trim()
                var pollution_end_date = pollution_end_date.text.toString().trim()
                var pollution_price = pollution_price.text.toString().trim()

                var pollution_noEmpty: Boolean
                var pollution_start_dateEmpty: Boolean
                var pollution_end_dateEmpty: Boolean
                var pollution_priceEmpty: Boolean
                var vehicle_reg_idEmpty: Boolean

                if (pollution_no.isEmpty()) {
                    text_input_pollution_no.error = "Field must not be empty"
                    pollution_noEmpty = true
                } else {
                    text_input_pollution_no.error = ""
                    pollution_noEmpty = false
                }

                if (pollution_start_date.isEmpty()) {
                    text_input_pollution_start_date.error = "Field must not be empty"
                    pollution_start_dateEmpty = true
                } else {
                    text_input_pollution_start_date.error = ""
                    pollution_start_dateEmpty = false
                }

                if (pollution_end_date.isEmpty()) {
                    text_input_pollution_end_date.error = "Field must not be empty"
                    pollution_end_dateEmpty = true
                } else {
                    text_input_pollution_end_date.error = ""
                    pollution_end_dateEmpty = false
                }

                if (pollution_price.isEmpty()) {
                    text_input_pollution_price.error = "Field must not be empty"
                    pollution_priceEmpty = true
                } else {
                    text_input_pollution_price.error = ""
                    pollution_priceEmpty = false
                }

                if (vehicle_id == -1) {
                    Toast.makeText(
                        context,
                        "Please select vehicle registration no",
                        Toast.LENGTH_SHORT
                    ).show()
                    vehicle_reg_idEmpty = true
                } else {
                    vehicle_reg_idEmpty = false
                }

                if (pollution_noEmpty || pollution_start_dateEmpty || pollution_end_dateEmpty || pollution_priceEmpty || vehicle_reg_idEmpty) {
                    return@setOnClickListener
                } else {
                    var payload = Payload()
                    payload.add("pollutionNo", pollution_no)
                    payload.add("startDate", pollution_start_date)
                    payload.add("endDate", pollution_end_date)
                    payload.add("price", pollution_price)
                    payload.add("vehicleId", vehicle_id)
                    if (title.equals("Update Pollution"))
                        payload.add("id", (genericData as PollutionModel).id)
                    if (NetworkUtils.isOnline) {
                        listener?.onAddOrUpdate(true, payload)
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Network connection error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

            }

            if (resource == R.layout.add_invoice_dialog_fragment) {

                var invoice_date = invoice_date.text.toString().trim()
                var invoice_order_id = invoice_order_id.text.toString().trim()

                var invoice_dateEmpty = false
                var invoice_order_idEmpty = false
                var invoice_tripEmpty = false

                if (invoice_date.isEmpty()) {
                    text_input_invoice_date.error = "Field must not be empty"
                    invoice_dateEmpty = true
                } else {
                    text_input_invoice_date.error = ""
                    invoice_dateEmpty = false
                }

                if (invoice_order_id.isEmpty()) {
                    text_input_invoice_order_id.error = "Field must not be empty"
                    invoice_order_idEmpty = true
                } else {
                    text_input_invoice_order_id.error = ""
                    invoice_order_idEmpty = false
                }

                if (trip_id == -1) {
                    Toast.makeText(RealEstateApp.context, "Please select trip", Toast.LENGTH_LONG)
                        .show()
                    invoice_tripEmpty = true
                } else {
                    invoice_tripEmpty = false
                }

                if (invoice_dateEmpty || invoice_order_idEmpty || invoice_tripEmpty) {
                    return@setOnClickListener
                } else {
                    var payload = Payload()
                    payload.add("invoiceDate", invoice_date)
                    payload.add("orderId", invoice_order_id)
                    payload.add("tripId", trip_id)
                    if (title.equals("Update Invoice"))
                        payload.add("id", (genericData as InvoiceModel).id)
                    if (NetworkUtils.isOnline) {
                        listener?.onAddOrUpdate(true, payload)
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Network connection error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

            }

            if (resource == R.layout.add_company_dialog_fragment) {
                var company_name = company_name.text.toString().trim()
                var company_address = company_address.text.toString().trim()
                var company_nameEmpty: Boolean
                var company_addressEmpty: Boolean
                var material_idEmpty: Boolean

                if (company_name.isEmpty()) {
                    text_input_company_name.error = "Field must not be empty"
                    company_nameEmpty = true
                } else {
                    text_input_company_name.error = ""
                    company_nameEmpty = false
                }

                if (company_address.isEmpty()) {
                    text_input_company_address.error = "Field must not be empty"
                    company_addressEmpty = true
                } else {
                    text_input_company_address.error = ""
                    company_addressEmpty = false
                }

                if (material_id == -1) {
                    Toast.makeText(context, "Please select material name", Toast.LENGTH_SHORT)
                        .show()
                    material_idEmpty = true
                } else {
                    material_idEmpty = false
                }

                if (company_nameEmpty || company_addressEmpty || material_idEmpty) {

                } else {
                    var payload = Payload()
                    payload.add("name", company_name)
                    payload.add("address", company_address)
                    payload.add("materialId", material_id)
                    if (title.equals("Update Company"))
                        payload.add("id", (genericData as CompanyModel).id)
                    if (NetworkUtils.isOnline) {
                        listener?.onAddOrUpdate(true, payload)
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Network connection error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

            }

            if (resource == R.layout.add_material_dialog_fragment) {
                var material_name = material_name.text.toString().trim()
                var material_price = material_price.text.toString().trim()
                var material_nameEmpty: Boolean
                var material_priceEmpty: Boolean
                if (material_name.isEmpty()) {
                    text_input_material_name.error = "Field must not be empty"
                    material_nameEmpty = true
                } else {
                    text_input_material_name.error = ""
                    material_nameEmpty = false
                }

                if (material_price.isEmpty()) {
                    text_input_material_price.error = "Field must not be empty"
                    material_priceEmpty = true
                } else {
                    text_input_material_price.error = ""
                    material_priceEmpty = false
                }

                if (material_nameEmpty || material_priceEmpty) {
                    return@setOnClickListener
                } else {
                    var payload = Payload()
                    payload.add("materialName", material_name)
                    payload.add("price", material_price)
                    if (title.equals("Update Material"))
                        payload.add("id", (genericData as MaterialModel).id)
                    if (NetworkUtils.isOnline) {
                        listener?.onAddOrUpdate(true, payload)
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Network connection error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }

            if (resource == R.layout.add_dealer_dialog_fragment) {

                var dealer_name = dealer_name.text.toString().trim()
                var dealer_phone_no = dealer_phone_no.text.toString().trim()
                var dealer_address = dealer_address.text.toString().trim()
                var dealer_pincode = dealer_pincode.text.toString().trim()

                var dealer_nameEmpty = false
                var dealer_phone_noEmpty = false
                var dealer_addressEmpty = false
                var dealer_pincodeEmpty = false
                var dealer_stateEmpty = false

                if (dealer_name.isEmpty()) {
                    text_input_dealer_name.error = "Field must not be empty"
                    dealer_nameEmpty = true
                } else {
                    text_input_dealer_name.error = ""
                    dealer_nameEmpty = false
                }

                if (dealer_phone_no.isEmpty()) {
                    text_input_dealer_phone_no.error = "Field must not be empty"
                    dealer_nameEmpty = true
                } else {
                    text_input_dealer_phone_no.error = ""
                    dealer_phone_noEmpty = false
                }

                if (dealer_address.isEmpty()) {
                    text_input_dealer_address.error = "Field must not be empty"
                    dealer_addressEmpty = true
                } else {
                    text_input_dealer_address.error = ""
                    dealer_addressEmpty = false
                }

                if (dealer_pincode.isEmpty()) {
                    text_input_dealer_pincode.error = "Field must not be empty"
                    dealer_pincodeEmpty = true
                } else {
                    text_input_dealer_pincode.error = ""
                    dealer_pincodeEmpty = false
                }

                if (stateName.isEmpty()) {
                    Toast.makeText(RealEstateApp.context, "Please select state", Toast.LENGTH_LONG)
                        .show()
                    dealer_stateEmpty = true
                } else {
                    dealer_stateEmpty = false
                }

                if (dealer_nameEmpty || dealer_addressEmpty || dealer_phone_noEmpty || dealer_pincodeEmpty || dealer_stateEmpty) {
                    return@setOnClickListener
                } else {
                    var payload = Payload()
                    payload.add("dealerName", dealer_name)
                    payload.add("phoneNumber", dealer_phone_no)
                    payload.add("state", stateName)
                    payload.add("address", dealer_address)
                    payload.add("pincode", dealer_pincode.toInt())
                    if (title.equals("Update Dealer"))
                        payload.add("id", (genericData as DealerModel).id)
                    if (NetworkUtils.isOnline) {
                        listener?.onAddOrUpdate(true, payload)
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Network connection error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }

            if (resource == R.layout.add_role_dialog_fragment) {
                var role_type = role_type.text.toString().trim()
                var role_normalized_name = role_normalized_name.text.toString().trim()
                var role_typeEmpty: Boolean
                var role_normalized_nameEmpty: Boolean

                if (role_type.isEmpty()) {
                    text_input_role_type.error = "Field must not be empty"
                    role_typeEmpty = true
                } else {
                    text_input_role_type.error = ""
                    role_typeEmpty = false
                }

                if (role_normalized_name.isEmpty()) {
                    text_input_role_normalized_name.error = "Field must not be empty"
                    role_normalized_nameEmpty = true
                } else {
                    text_input_role_normalized_name.error = ""
                    role_normalized_nameEmpty = false
                }

                if (role_typeEmpty || role_normalized_nameEmpty) {
                    return@setOnClickListener
                } else {
                    var payload = Payload()
                    payload.add("roleType", role_type)
                    payload.add("normalizedName", role_normalized_name.toUpperCase())
                    if (title.equals("Update Role"))
                        payload.add("id", (genericData as RolesModel).id)
                    if (NetworkUtils.isOnline) {
                        listener?.onAddOrUpdate(true, payload)
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Network connection error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }

        }
    }

}