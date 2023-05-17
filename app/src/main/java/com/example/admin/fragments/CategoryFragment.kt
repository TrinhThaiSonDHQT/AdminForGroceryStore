package com.example.admin.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.adapter.ProductAdapter
import com.example.admin.databinding.FragmentCategoryBinding
import com.example.admin.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class CategoryFragment : Fragment(), AdapterView.OnItemSelectedListener,
    ProductAdapter.ItemClickListener {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var spinner: Spinner
    private lateinit var listProducts: ArrayList<Product>
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var adapterProducts: ProductAdapter
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutHeader: LinearLayout
    private lateinit var layoutBody: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        recyclerViewProducts = binding.listProducts
        listProducts = ArrayList()
        adapterProducts = ProductAdapter(listProducts, requireContext(), this)
        recyclerViewProducts.adapter = adapterProducts

        progressBar = binding.progressBarProducts
        layoutHeader = binding.LayoutHeaderProduct
        layoutBody = binding.layoutBodyProduct

        firebaseFirestore = Firebase.firestore

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getProducts()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}


    @SuppressLint("NotifyDataSetChanged")
    private fun getProducts() {
        firebaseFirestore.collection("AllProducts").get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val product = document.toObject<Product>()
                    listProducts.add(product)
                }
                adapterProducts.notifyDataSetChanged()

                getLayout()
            }
            .addOnFailureListener { }
    }

    private fun getLayout() {
        layoutHeader.visibility = View.VISIBLE
        layoutBody.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        spinner = binding.orderByPrice
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.order_price_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    override fun onItemClick(product: Product) {

    }

}