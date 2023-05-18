package com.example.admin.fragments

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.adapter.ProductAdapter
import com.example.admin.databinding.FragmentCategoryBinding
import com.example.admin.model.Product
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

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
    private lateinit var searchBox: EditText
    private lateinit var buttonAddItem: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        recyclerViewProducts = binding.listProducts
        listProducts = ArrayList()
        adapterProducts = ProductAdapter(listProducts, requireContext(), this)
        recyclerViewProducts.adapter = adapterProducts

        progressBar = binding.progressBarProducts
        layoutHeader = binding.LayoutHeaderProduct
        layoutBody = binding.layoutBodyProduct

        buttonAddItem = binding.buttonAddItems
        searchBox = binding.searchProducts

        firebaseFirestore = Firebase.firestore

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getProducts()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position == 0) listProducts.sortBy {product: Product ->   product.price}
        else if(position == 1) listProducts.sortByDescending {product: Product ->   product.price}
        else listProducts.sortBy {product: Product ->   product.type}
        adapterProducts.notifyDataSetChanged()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) { getAllProducts() }


    @SuppressLint("NotifyDataSetChanged")
    private fun getProducts() {
        firebaseFirestore.collection("AllProducts")
            .addSnapshotListener { snapshot, error ->
                listProducts.clear()
                for (s in snapshot!!) {
                    val product = s.toObject<Product>()
                    listProducts.add(product)
                }
                adapterProducts.notifyDataSetChanged()
            }
        getLayout()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllProducts() {
        firebaseFirestore.collection("AllProducts")
            .get()
            .addOnSuccessListener { documents ->
                listProducts.clear()
                for (document in documents) {
                    val product = document.toObject<Product>()
                    listProducts.add(product)
                }
                adapterProducts.notifyDataSetChanged()
            }
        getLayout()
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

        buttonAddItem.setOnClickListener { addNewItem() }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) searchProducts(s.toString())
                else getAllProducts()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    @SuppressLint("MissingInflatedId")
    private fun addNewItem() {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_item, null)
        builder.setView(view)
        val dialog = builder.create()
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()

        view.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<Button>(R.id.buttonAddItem).setOnClickListener {
            val itemName =
                view.findViewById<TextInputLayout>(R.id.itemName).editText!!.text.toString()
            val itemPrice =
                view.findViewById<TextInputLayout>(R.id.itemPrice).editText!!.text.toString()
            val itemDescription =
                view.findViewById<TextInputLayout>(R.id.itemDescription).editText!!.text.toString()
            val itemImage =
                view.findViewById<TextInputLayout>(R.id.itemImage).editText!!.text.toString()

            val type = if (view.findViewById<RadioButton>(R.id.radio_button_1).isChecked) "fish"
            else if (view.findViewById<RadioButton>(R.id.radio_button_2).isChecked) "egg"
            else if (view.findViewById<RadioButton>(R.id.radio_button_3).isChecked) "fruit"
            else if (view.findViewById<RadioButton>(R.id.radio_button_4).isChecked) "vegetable"
            else "milk"

            if (itemName.isNotEmpty() && itemPrice.isNotEmpty() && itemDescription.isNotEmpty() && type.isNotEmpty()) {
                val product = Product(itemName, itemImage, type, itemPrice.toInt(), itemDescription)
                firebaseFirestore.collection("AllProducts")
                    .add(product)
                    .addOnSuccessListener {
                        dialog.dismiss()
                    }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchProducts(string: String) {
        firebaseFirestore.collection("AllProducts")
            .whereGreaterThanOrEqualTo("name", string)
            .whereLessThanOrEqualTo("name", string + "\uF8FF")
            .get()
            .addOnSuccessListener { documents ->
                listProducts.clear()
                for (document in documents) {
                    val product = document.toObject<Product>()
                    listProducts.add(product)
                }
                adapterProducts.notifyDataSetChanged()
            }
            .addOnFailureListener { }
    }

    override fun onItemClick(product: Product) {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_item, null)
        builder.setView(view)
        val dialog = builder.create()
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()

        view.findViewById<TextInputLayout>(R.id.itemName).editText!!.setText(product.name.toString())
        view.findViewById<TextInputLayout>(R.id.itemPrice).editText!!.setText(product.price.toString())
        view.findViewById<TextInputLayout>(R.id.itemDescription).editText!!.setText(product.description.toString())
        when (product.type.toString()) {
            "fish" -> view.findViewById<RadioButton>(R.id.radio_button_1).isChecked = true
            "egg" -> view.findViewById<RadioButton>(R.id.radio_button_2).isChecked = true
            "fruit" -> view.findViewById<RadioButton>(R.id.radio_button_3).isChecked = true
            "vegetable" -> view.findViewById<RadioButton>(R.id.radio_button_4).isChecked = true
            else -> view.findViewById<RadioButton>(R.id.radio_button_5).isChecked = true
        }

        view.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<Button>(R.id.buttonDeleteItem).setOnClickListener {
            firebaseFirestore.collection("AllProducts")
                .whereEqualTo("name", product.name.toString())
                .whereEqualTo("type", product.type.toString())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        firebaseFirestore.collection("AllProducts").document(document.id)
                            .delete()
                    }
                }
            dialog.dismiss()
        }
    }

}
