package com.realestate.utils

import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.realestate.fragment.*

/**
 * Created by Chandan on 26/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */

class FragmentProvider {
    companion object {

        private val fragmentMap: HashMap<String, String> = HashMap()

        init {
            fragmentMap[FragmentTag.INSURANCE_FRAGMENT] = InsuranceFragment::class.java.name
            fragmentMap[FragmentTag.MATERIALS_FRAGMENT] = MaterialFragment::class.java.name
            fragmentMap[FragmentTag.TRIP_FRAGMENT] = TripFragment::class.java.name
            fragmentMap[FragmentTag.USERS_FRAGMENT] = UserFragment::class.java.name
            fragmentMap[FragmentTag.VEHICLES_FRAGMENT] = VehiclesFragment::class.java.name
            fragmentMap[FragmentTag.BRANCH_FRAGMENT] = BranchFragment::class.java.name
            fragmentMap[FragmentTag.COMPANIES_FRAGMENT] = CompaniesFragment::class.java.name
            fragmentMap[FragmentTag.DEALER_FRAGMENT] = DealerFragment::class.java.name
            fragmentMap[FragmentTag.INVOICE_FRAGMENT] = InvoiceFragment::class.java.name
            fragmentMap[FragmentTag.INVOICE_DETAILS_FRAGMENT] = InvoiceDetailsFragment::class.java.name
            fragmentMap[FragmentTag.PERMIT_FRAGMENT] = PermitFragment::class.java.name
            fragmentMap[FragmentTag.POLLUTION_FRAGMENT] = PollutionFragment::class.java.name
            fragmentMap[FragmentTag.ROLES_FRAGMENT] = RolesFragment::class.java.name
            fragmentMap[FragmentTag.TRIP_DETAILS_FRAGMENT] = TripDetailsFragment::class.java.name
            fragmentMap[FragmentTag.USER_DETAILS_FRAGMENT] = UserDetailsFragment::class.java.name
            fragmentMap[FragmentTag.PROFILE_FRAGMENT] = ProfileFragment::class.java.name
            fragmentMap[FragmentTag.MAP_FRAGMENT] = MapsFragment::class.java.name

        }

        fun getFragment(fragmentTag: String): Fragment? {
            val className = fragmentMap[fragmentTag]
            if (TextUtils.isEmpty(className)) return null
            try {
                return className?.let { Class.forName(it).newInstance() } as Fragment
            } catch (e: InstantiationException) {
            } catch (e: IllegalAccessException) {
            } catch (e: ClassNotFoundException) {
            }
            return null
        }

    }
}