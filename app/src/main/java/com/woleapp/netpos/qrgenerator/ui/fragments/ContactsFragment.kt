package com.woleapp.netpos.qrgenerator.ui.fragments

import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.ContactAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentContactsBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutInviteToTallyBinding
import com.woleapp.netpos.qrgenerator.model.ContactModel
import com.woleapp.netpos.qrgenerator.model.referrals.InviteToTallyModel
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.shareAppLink
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.showAlertDialog
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    private lateinit var contactsList: MutableList<ContactModel>
    private lateinit var phoneList: MutableList<ContactModel>
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var selectedItems: ArrayList<String>
    private lateinit var resultString: String
    private lateinit var inviteToTallyDialog: AlertDialog
    private lateinit var inviteToTallyBinding: LayoutInviteToTallyBinding
    private val walletViewModel by activityViewModels<WalletViewModel>()
    private lateinit var loader: android.app.AlertDialog

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    @Named("io-scheduler")
    lateinit var ioScheduler: Scheduler

    @Inject
    @Named("main-scheduler")
    lateinit var mainThreadScheduler: Scheduler


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
        loader = alertDialog(requireContext())
        readContacts()
        contactListSetUp()

        walletViewModel.inviteToTallyMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showAlertDialog(requireContext(), message, "OK") {
                    loader.dismiss()
                }
            }
        }

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

        binding.searchContacts.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val searchResults = searchContacts(phoneList, s.toString())
                if (searchResults.isNotEmpty()) {
                    contactsList.clear()
                    println("Search results for '$searchResults':")
                    contactAdapter.notifyDataSetChanged()
                    for (contact in searchResults) {
                        println("${contact.name}: ${contact.phoneNumber}")
                        contactsList.add(
                            ContactModel(
                                "${contact.name}",
                                "${contact.phoneNumber}",
                                false
                            )
                        )
                        contactAdapter.notifyDataSetChanged()
                    }
                } else {
                    println("No results found for '$searchResults'.")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding.shareButton.setOnClickListener {
            getSelectedItems()
            inviteToTallyDialog.show()
            inviteToTallyBinding.contactEdittext.setText(resultString)
        }

        inviteToTallyBinding.proceed.setOnClickListener {
            if (inviteToTallyBinding.contactEdittext.text.toString().isEmpty()) {
                showToast("Please enter phone number(s)")
            } else {
                inviteToTally()
            }
        }

        inviteToTallyBinding.socialMediaImage.setOnClickListener {
            inviteToTallyDialog.dismiss()
            activity?.let { it1 -> shareAppLink(requireContext(), it1.packageManager) }
        }
    }

    private fun getSelectedItems(): String {
        selectedItems = arrayListOf()

        for (item in 0 until contactsList.size) {
            if (contactsList[item].selected) {
                selectedItems.add(contactsList[item].phoneNumber)
            }
        }
        resultString = selectedItems.joinToString(", ") {
            it.replace(" ", "")
        }.replace("-", "")
        return resultString
    }


    private fun readContacts() {
        contactsList = mutableListOf()
        phoneList = mutableListOf()

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
                phoneList.add(ContactModel("$name", "$phoneNumber", false))
                contactsList.add(ContactModel("$name", "$phoneNumber", false))
                Log.d("LLLKKKKIOJJJKK", "$name: $phoneNumber")
            }
        }

        // Do something with the contactsList (e.g., display it in your app)
    }

    private fun contactListSetUp() {
        contactAdapter = ContactAdapter(contactsList) { show -> showShareMenu(show) }
        binding.contactRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.contactRecycler.adapter = contactAdapter
    }

    private fun showShareMenu(show: Boolean) {
        if (show) {
            binding.shareButton.isEnabled = true
            binding.shareButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            binding.searchContacts.visibility = View.VISIBLE
        } else {
            binding.shareButton.visibility = View.GONE
        }
    }

    private fun inviteToTally() {
        //    loader.show()

        val invitees = inviteToTallyBinding.contactEdittext.text.toString()
        val newInvitees = InviteToTallyModel(invitees)
        walletViewModel.inviteToTallyInFragment(requireContext(), newInvitees)
        observeServerResponse(
            walletViewModel.inViteToTallyResponse,
            loader,
            requireActivity().supportFragmentManager
        ) {
            walletViewModel.inViteToTallyResponse.value?.let {
                //  showToast(it.data.message)
                loader.dismiss()
            }
        }
    }


//    private fun inviteToTally() {
//        loader.show()
//
//        val invitees = inviteToTallyBinding.contactEdittext.text.toString()
//        val newInvitees = InviteToTallyModel(invitees)
//        observeServerResponse(
//            walletViewModel.inviteToTally(requireContext(), newInvitees),
//            loader,
//            compositeDisposable,
//            ioScheduler,
//            mainThreadScheduler,
//        ) {
//            val walletResponse = EncryptedPrefsUtils.getString(requireContext(), WALLET_RESPONSE)
//            showToast(walletResponse.toString())
//            inviteToTallyDialog.dismiss()
//        }
//    }

    fun searchContacts(contacts: List<ContactModel>, query: String): List<ContactModel> {
        // Use filter to search for contacts whose name contains the search query (case-insensitive)
        return contacts.filter { contact ->
            contact.name.contains(query, ignoreCase = true)
        }
    }
}