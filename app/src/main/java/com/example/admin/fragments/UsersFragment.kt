package com.example.admin.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.adapter.UserManagementAdapter
import com.example.admin.databinding.FragmentUsersBinding
import com.example.admin.model.Product
import com.example.admin.model.UserManagement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

private fun <E> ArrayList<E>.add(element: Product) {

}

class UsersFragment : Fragment(), AdapterView.OnItemSelectedListener,
    UserManagementAdapter.OnDeleteClickListener {

    private lateinit var binding: FragmentUsersBinding
    private lateinit var listUser: ArrayList<UserManagement>
    private lateinit var recyclerViewUserManagement: RecyclerView
    private lateinit var adapterUser: UserManagementAdapter
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater, container, false)

        recyclerViewUserManagement = binding.listUser
        listUser = ArrayList()
        adapterUser = UserManagementAdapter(listUser, requireContext(), this)
        recyclerViewUserManagement.adapter = adapterUser

        progressBar = binding.progressbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseFirestore = Firebase.firestore
        getUserManagement()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getUserManagement() {
        firebaseFirestore.collection("useraccount").get()
            .addOnSuccessListener { documents ->
                listUser.clear()
                for (document in documents) {
                    val useraccount = document.toObject<UserManagement>()
                    listUser.add(useraccount)
                }
                adapterUser.notifyDataSetChanged()
            }
            .addOnFailureListener { }
        progressBar.visibility = View.GONE
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // implement your logic for item selection
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        // implement your logic when nothing is selected
    }

    override fun onDeleteClick(user: UserManagement) {
        // implement your logic for delete action
    }
}