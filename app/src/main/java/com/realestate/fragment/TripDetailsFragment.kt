package com.realestate.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.realestate.R
import com.realestate.model.TripModel
import com.realestate.utils.Constant
import kotlinx.android.synthetic.main.fragment_trip_details1.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * A simple [Fragment] subclass.
 * Use the [TripDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TripDetailsFragment : Fragment() {

    private var tripModel: TripModel? = null
    var animRightIn: Animation? = null
    var animLeftIn: Animation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        tripModel = bundle?.getParcelable("trip_details")
        animRightIn = AnimationUtils.loadAnimation(
            requireActivity(),
            R.anim.right_to_left
        )
        animLeftIn = AnimationUtils.loadAnimation(
            requireActivity(),
            R.anim.left_to_right
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_details1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().runOnUiThread(Runnable {
            try {
                requireActivity().txtViewToolbarTitle.text =
                    activity?.resources?.getString(R.string.trip_details)
                activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                activity?.imgViewBack?.tag = "back"
            } catch (e: Exception) {

            }
        })

        activity?.refresh_img?.visibility = View.GONE
        activity?.add_img?.visibility = View.GONE
        activity?.filter_spinner?.visibility = View.GONE

        trip_no.text = tripModel?.tripsNo
        trip_no.startAnimation(animRightIn)
        trip_no1.startAnimation(animLeftIn)
        start_location.text = tripModel?.startLocation
        start_location.startAnimation(animLeftIn)
        //start_location1.startAnimation(animLeftIn)
        end_location.text = tripModel?.endLocation
        end_location.startAnimation(animRightIn)
        //end_location1.startAnimation(animLeftIn)
        start_time.text = Constant.convertStartStringToTime(tripModel?.startTime!!, false)
        start_time.startAnimation(animLeftIn)
        //start_time1.startAnimation(animLeftIn)
        end_time.text = Constant.convertEndStringToTime(tripModel?.endTime ?: "", false)
        end_time.startAnimation(animRightIn)
        //end_time1.startAnimation(animLeftIn)
        vehicles_reg_no.text = tripModel?.vehicle?.vehicleRegisteredNo
        vehicles_reg_no.startAnimation(animRightIn)
        vehicles_reg_no1.startAnimation(animLeftIn)
        driver.text = tripModel?.user?.firstName + " " + tripModel?.user?.lastName
        driver.startAnimation(animRightIn)
        driver1.startAnimation(animLeftIn)
        trip_status.text = tripModel?.tripStatus
        trip_status.startAnimation(animRightIn)
        trip_status1.startAnimation(animLeftIn)
        if (tripModel?.tripStatus.equals("Started")) {
            trip_status.setTextColor(activity?.resources?.getColor(R.color.active_color)!!)
        } else if (tripModel?.tripStatus.equals("Ended")) {
            trip_status.setTextColor(activity?.resources?.getColor(R.color.expired_color)!!)
        }
        invoice_no.text = tripModel?.invoice?.invoiceNo
        invoice_no.startAnimation(animRightIn)
        invoice_no1.startAnimation(animLeftIn)
        total_time.text = tripModel?.totalTime
        total_time.startAnimation(animRightIn)
        total_time1.startAnimation(animLeftIn)
        start_distance.text = tripModel?.startDistance.toString()
        start_distance.startAnimation(animLeftIn)
//        start_distance1.startAnimation(animLeftIn)
        end_distance.text = tripModel?.endDistance.toString()
        end_distance.startAnimation(animRightIn)
        //end_distance1.startAnimation(animLeftIn)

        total_distance.text = tripModel?.totalDistance.toString()
        total_distance.startAnimation(animRightIn)
        total_distance1.startAnimation(animLeftIn)

        fuel_price.text = tripModel?.fuelPrice.toString()
        fuel_price.startAnimation(animRightIn)
        fuel_price1.startAnimation(animLeftIn)

        fuel_quantity.text = tripModel?.fuelQuantity.toString()
        fuel_quantity.startAnimation(animRightIn)
        fuel_quantity1.startAnimation(animLeftIn)

        company.text = tripModel?.company?.name
        company.startAnimation(animRightIn)
        company1.startAnimation(animLeftIn)

        material.text = tripModel?.company?.material?.materialName
        material.startAnimation(animRightIn)
        material1.startAnimation(animLeftIn)

        unit_price.text = tripModel?.company?.material?.price.toString()
        unit_price.startAnimation(animRightIn)
        unit_price1.startAnimation(animLeftIn)

        weight.text = tripModel?.weight.toString()
        weight.startAnimation(animRightIn)
        weight1.startAnimation(animLeftIn)

        total_material_cost.text = tripModel?.totalMaterialCost.toString()
        total_material_cost.startAnimation(animRightIn)
        total_material_cost1.startAnimation(animLeftIn)

        fuel_charge.text = tripModel?.fuelCharge.toString()
        fuel_charge.startAnimation(animRightIn)
        fuel_charge1.startAnimation(animLeftIn)

        toll_charge.text = tripModel?.tollCharge.toString()
        toll_charge.startAnimation(animRightIn)
        toll_charge1.startAnimation(animLeftIn)

        operation_cost.text = tripModel?.operationCost.toString()
        operation_cost.startAnimation(animRightIn)
        operation_cost1.startAnimation(animLeftIn)

        trip_cost.text = tripModel?.tripCost.toString()
        trip_cost.startAnimation(animRightIn)
        trip_cost1.startAnimation(animLeftIn)

        advance_amount.text = tripModel?.advanceAmount.toString()
        advance_amount.startAnimation(animRightIn)
        advance_amount1.startAnimation(animLeftIn)

        due.text = tripModel?.due.toString()
        due.startAnimation(animRightIn)
        due1.startAnimation(animLeftIn)

        dealer_no.text = tripModel?.dealerId.toString()
        dealer_no.startAnimation(animRightIn)
        dealer_no1.startAnimation(animLeftIn)

        dealer_phone_no.text = tripModel?.dealer?.phoneNumber.toString()
        dealer_phone_no.startAnimation(animRightIn)
        dealer_phone_no1.startAnimation(animLeftIn)

        dealer_name.text = tripModel?.dealer?.dealerName
        dealer_name.startAnimation(animRightIn)
        dealer_name1.startAnimation(animLeftIn)

        dealer_state.text = tripModel?.dealer?.state
        dealer_state.startAnimation(animRightIn)
        dealer_state1.startAnimation(animLeftIn)

        trip_cost.text = tripModel?.tripCost.toString()
        trip_cost.startAnimation(animRightIn)
        trip_cost1.startAnimation(animLeftIn)

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TripDetailsFragment()
    }
}