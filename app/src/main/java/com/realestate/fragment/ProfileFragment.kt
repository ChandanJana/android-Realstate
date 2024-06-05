package com.realestate.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.realestate.R
import com.realestate.adapter.spinner.SpinnerRoleAdapter
import com.realestate.callback.ResponseCallback
import com.realestate.model.ProfileModel
import com.realestate.model.RolesModel
import com.realestate.model.UserModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.storage.SharedPreferenceManager
import com.realestate.utils.Constant
import com.realestate.utils.NetworkUtils
import kotlinx.android.synthetic.main.add_user_dialog_fragment.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private val PERMISSION_CODE = 3
    private val GALLERY = 1
    private val CAMERA = 2
    private var role_id = -1
    private var rolesList = mutableListOf<RolesModel>()
    private var roleAdapter: SpinnerRoleAdapter? = null
    private var gender = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roleAdapter = SpinnerRoleAdapter(
            requireActivity(),
            rolesList
        )
        profile_role_edit.setAdapter(roleAdapter)
        requireActivity().runOnUiThread(Runnable {
            try {
                requireActivity().txtViewToolbarTitle.text =
                    activity?.resources?.getString(R.string.profile)
                activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                activity?.imgViewBack?.tag = "back"
            } catch (e: Exception) {

            }
        })

        activity?.refresh_img?.visibility = View.GONE
        activity?.add_img?.visibility = View.GONE
        activity?.filter_spinner?.visibility = View.GONE

        activity?.refresh_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Toast.makeText(requireActivity(), "Profile refresh", Toast.LENGTH_LONG).show()
            }

        })

        getRoles()
        fetchUser()

        capture_img.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                showPictureDialog()
            }

        })
        profile_role_edit.isEnabled = false
        profile_role_edit.isClickable = false
        /*profile_role_edit.setOnItemSelectedListener(object :
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

        })*/

        edit_profile_img.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (edit_profile_img.getTag().equals("edit")) {
                    edit_profile_img.setImageResource(R.drawable.ic_save)
                    edit_profile_img.setTag("save")
                    table_text.visibility = View.GONE
                    table_edit.visibility = View.VISIBLE

                } else {

                    var email = profile_email_edit.text.toString().trim()
                    var first_name = profile_first_name_edit.text.toString().trim()
                    var last_name = profile_last_name_edit.text.toString().trim()
                    var phone_no = profile_contact_edit.text.toString().trim()
                    var alt_phone_no = profile_alternate_contact_edit.text.toString().trim()
                    var aadhar_no = profile_aadhar_no_edit.text.toString().trim()
                    var address = profile_address_edit.text.toString().trim()


                    var emailEmpty: Boolean
                    var firstNameEmpty: Boolean
                    var lastNameEmpty: Boolean
                    var phoneEmpty: Boolean
                    var genderEmpty: Boolean
                    var aadharEmpty: Boolean
                    var addressEmpty: Boolean

                    if (first_name.isEmpty()) {
                        text_input_profile_first_name_edit.error = "Field must not be empty"
                        firstNameEmpty = true
                    } else {
                        text_input_profile_first_name_edit.error = ""
                        firstNameEmpty = false
                    }

                    if (last_name.isEmpty()) {
                        text_input_profile_last_name_edit.error = "Field must not be empty"
                        lastNameEmpty = true
                    } else {
                        text_input_profile_last_name_edit.error = ""
                        lastNameEmpty = false
                    }

                    if (phone_no.isEmpty()) {
                        text_input_profile_contact_edit.error = "Field must not be empty"
                        phoneEmpty = true
                    } else {
                        text_input_profile_contact_edit.error = ""
                        phoneEmpty = false
                    }

                    if (email.isEmpty()) {
                        text_input_profile_email_edit.error = "Field must not be empty"
                        emailEmpty = true
                    } else {
                        text_input_profile_email_edit.error = ""
                        emailEmpty = false
                    }

                    if (aadhar_no.isEmpty()) {
                        text_input_profile_aadhar_no_edit.error = "Field must not be empty"
                        aadharEmpty = true
                    } else {
                        text_input_profile_aadhar_no_edit.error = ""
                        aadharEmpty = false
                    }

                    if (address.isEmpty()) {
                        text_input_profile_address_edit.error = "Field must not be empty"
                        addressEmpty = true
                    } else {
                        text_input_profile_address_edit.error = ""
                        addressEmpty = false
                    }

                    if (gender.isEmpty()) {
                        Toast.makeText(context, "Please select your gender", Toast.LENGTH_SHORT)
                            .show()
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
                                    payload.add(
                                        "id",
                                        SharedPreferenceManager.getInstance(requireActivity())
                                            ?.getInt(SharedPreferenceManager.Key.USER_ID)
                                    )
                                    if (NetworkUtils.isOnline) {
                                        updateProfile(
                                            SharedPreferenceManager.getInstance(
                                                requireActivity()
                                            )?.getInt(SharedPreferenceManager.Key.USER_ID)!!,
                                            payload
                                        )
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

            }

        })

        profile_gender_edit.setOnCheckedChangeListener(object :
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

    private fun updateProfile(id: Int, payload: Payload) {
        Constant.showProgress(requireActivity(), "Updating Profile...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.profileUpdate(id, body)

        RetrofitRequest().enqueue(
            call as Call<UserModel>,
            object : ResponseCallback<UserModel> {
                override fun onSuccess(response: UserModel) {
                    Constant.hideProgress()
                    Toast.makeText(requireActivity(), "Profile updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    edit_profile_img.setImageResource(R.drawable.ic_edit)
                    edit_profile_img.setTag("edit")
                    table_text.visibility = View.VISIBLE
                    table_edit.visibility = View.GONE
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchUser()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    Constant.hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchUser() {
        Constant.showProgress(requireActivity(), "loading...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.userProfile(
            SharedPreferenceManager.getInstance(requireActivity())
                ?.getInt(SharedPreferenceManager.Key.USER_ID)!!
        )

        RetrofitRequest().enqueue(
            call as Call<ProfileModel>,
            object : ResponseCallback<ProfileModel> {
                override fun onSuccess(response: ProfileModel) {
                    Constant.hideProgress()
                    //text
                    profile_user_name.setText(response.userName)
                    profile_full_name.text = response.firstName + " " + response.lastName
                    profile_email.text = response.email
                    profile_address.text = response.address
                    profile_contact.text = response.phoneNumber
                    profile_alt_contact.text = response.alternateNumber
                    profile_aadhar_no.text = response.aadharNumber
                    profile_gender.text = response.gender
                    role_id = response.roleId
                    gender = response.gender

                    rolesList.forEach {
                        if (it.id == response.roleId) {
                            profile_role.text = it.roleType
                        }
                    }

                    profile_aadhar_no.text = response.aadharNumber

                    //edit
                    profile_first_name_edit.setText(response.firstName)
                    profile_last_name_edit.setText(response.lastName)
                    profile_email_edit.setText(response.email)
                    profile_address_edit.setText(response.address)
                    profile_contact_edit.setText(response.phoneNumber)
                    profile_alternate_contact_edit.setText(response.alternateNumber)
                    profile_aadhar_no_edit.setText(response.aadharNumber)
                    var index = -1
                    if (response.gender.equals("Male", ignoreCase = true)) {
                        index = 0
                        gender = "Male"
                    }
                    if (response.gender.equals("Female", ignoreCase = true)) {
                        index = 1
                        gender = "Female"
                    }

                    if (response.gender.equals("Other", ignoreCase = true)) {
                        index = 2
                        gender = "Other"
                    }
                    (profile_gender_edit.getChildAt(index) as RadioButton).setChecked(true)
                }

                override fun onFailure(error: String?) {
                    Constant.hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun getRoles() {
        //Constant.showProgress(requireActivity(), "loading...")
        val retrofit = ApiClient.instance?.getClient(false, requireActivity())

        val call = retrofit?.getAllRole()

        RetrofitRequest().enqueue(
            call as Call<List<RolesModel>>,
            object : ResponseCallback<List<RolesModel>> {
                override fun onSuccess(response: List<RolesModel>) {
                    //Constant.hideProgress()
                    (response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    rolesList.clear()
                    rolesList.addAll(response)
                    roleAdapter?.notifyDataSetChanged()
                }

                override fun onFailure(error: String?) {
                    //Constant.hideProgress()
                    Toast.makeText(requireActivity(), "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(requireActivity())
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun takePhotoFromCamera() {
        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CODE
            )

        } else {
            openCamera()
        }

    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun openCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup was granted
                    openCamera()

                } else {
                    //permission from popup was denied
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, contentURI)
                    //profilePic!!.setImageBitmap(bitmap)
                    var gallerfile: File = File(contentURI?.let { getRealPathFromURI(it) })
                    //sendprofileimagefiletoserver(gallerfile, user_id)
                    saveBitmap(bitmap)
                    var f: File = File(Environment.getExternalStorageDirectory().toString())

                    /*var photo: File? = null
                    for (temp: File in f.listFiles()) {

                        if (temp.name.equals("profile_image.jpg")) {

                            f = temp
                            photo = File(Environment.getExternalStorageDirectory(),"profile_image.jpg")
                            //pic = photo;
                            break
                        }
                    }*/

                    //if (photo != null)
                    Glide.with(requireActivity()).load(bitmap).into(profilePic)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }

        } else if (requestCode == CAMERA) {
            if (data != null) {

                try {

                    val thumbnail = data.extras!!.get("data") as Bitmap
                    var bitmapnew: Bitmap = Bitmap.createScaledBitmap(
                        data.extras!!.get("data") as Bitmap,
                        1080,
                        1920,
                        true
                    )
                    saveBitmap(bitmapnew)

                    var f: File = File(Environment.getExternalStorageDirectory().toString())

                    /*var photo: File? = null
                    for (temp: File in f.listFiles()) {

                        if (temp.name.equals("profile_image.jpg")) {

                            f = temp
                            photo = File(Environment.getExternalStorageDirectory(),"profile_image.jpg")
                            //pic = photo;
                            break
                        }
                    }*/

                    //if (photo != null)
                    Glide.with(requireActivity()).load(bitmapnew).into(profilePic)

                    //sendprofileimagefiletoserver(photo, id)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getRealPathFromURI(contentURI: Uri): String {
        var cursor: Cursor? = activity?.contentResolver?.query(contentURI, null, null, null, null)
        cursor?.moveToFirst()
        var idx: Int? = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return idx?.let { cursor?.getString(it) }!!


    }

    private fun saveBitmap(bitmap: Bitmap): File {
        var extStorageDirectory: String = Environment.getExternalStorageDirectory().toString()
        var outStream: OutputStream? = null
        var file: File = File(extStorageDirectory, "profile_image.jpg")

        if (file.exists()) {
            file.delete()
            file = File(extStorageDirectory, "profile_image.jpg")

        }
        try {
            outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.flush()
            outStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
            //  return null;
        }
        return file


    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ProfileFragment()

    }
}