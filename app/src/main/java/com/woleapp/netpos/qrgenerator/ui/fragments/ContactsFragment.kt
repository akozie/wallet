package com.woleapp.netpos.qrgenerator.ui.fragments

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.ContactAdapter
import com.woleapp.netpos.qrgenerator.adapter.QrAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentContactsBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentMyTallyBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutInviteToTallyBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutSetPasswordPrefBinding
import com.woleapp.netpos.qrgenerator.model.ContactModel
import com.woleapp.netpos.qrgenerator.utils.showToast


class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    private val PICK_CONTACT_REQUEST = 10
    private lateinit var contactsList: MutableList<ContactModel>
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var selectedItems : ArrayList<String>
    private lateinit var resultString : String
    private lateinit var inviteToTallyDialog: AlertDialog
    private lateinit var inviteToTallyBinding: LayoutInviteToTallyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readContacts()
        contactListSetUp()

        inviteToTallyBinding = LayoutInviteToTallyBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        ).apply {
            lifecycleOwner = this@ContactsFragment
            executePendingBindings()
        }

        inviteToTallyDialog = AlertDialog.Builder(requireContext())
            .setView(inviteToTallyBinding.root)
            .create()


        binding.shareButton.setOnClickListener {
            getSelectedItems()
            inviteToTallyDialog.show()
            inviteToTallyBinding.contactEdittext.setText(resultString)
        }

        inviteToTallyBinding.proceed.setOnClickListener {
            showToast(inviteToTallyBinding.contactEdittext.text.toString())
        }
    }

    private fun getSelectedItems(): String {
         selectedItems = arrayListOf()

        for (item in 0 until contactsList.size) {
            if (contactsList[item].selected) {
                selectedItems.add(contactsList[item].phoneNumber)
            }
        }
         resultString = selectedItems.joinToString(", ") { it.replace(" ", "") }
        Log.d("SELECTED", resultString)

        return resultString
    }

    fun selectContacts() {
        val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

        // Allow multiple selections (if supported)
        contactPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        startActivityForResult(contactPickerIntent, PICK_CONTACT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val selectedContacts = mutableListOf<String>()

                if (data.clipData != null) {
                    // Multiple contacts selected
                    val clipData = data.clipData!!
                    for (i in 0 until clipData.itemCount) {
                        val contactUri = clipData.getItemAt(i).uri
                        val cursor: Cursor? = requireActivity().contentResolver.query(contactUri, null, null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val contactName: String =
                                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                                selectedContacts.add(contactName)
                            }
                        }
                    }
                } else {
                    // Single contact selected
                    val contactUri: Uri = data.data!!
                    val cursor: Cursor? = requireActivity().contentResolver.query(
                        contactUri, null, null, null, null
                    )
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val contactName: String =
                                it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                            selectedContacts.add(contactName)
                        }
                    }
                }

                // Process the selectedContacts list (e.g., display or use them)
                Toast.makeText(requireContext(), "Selected Contacts: $selectedContacts", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun readContacts() {
         contactsList = mutableListOf()

        // Define the columns you want to retrieve
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        // Query contacts using a cursor
        val cursor: Cursor? = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        // Process the cursor and retrieve phone numbers
        cursor?.use {
            while (it.moveToNext()) {
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add(ContactModel("$name", "$phoneNumber", false))
                Log.d("LLLKKKKIOJJJKK", "$name: $phoneNumber")
            }
        }

        // Do something with the contactsList (e.g., display it in your app)
    }

    private fun contactListSetUp() {
        contactAdapter = ContactAdapter(contactsList){show -> showShareMenu(show)}
        binding.contactRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.contactRecycler.adapter = contactAdapter
    }

    private fun showShareMenu(show: Boolean){
        binding.shareButton.visibility = View.VISIBLE
    }

}