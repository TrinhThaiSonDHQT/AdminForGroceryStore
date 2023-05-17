package com.example.admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.R
import com.example.admin.model.Product

class ProductAdapter(
    private val list: ArrayList<Product>,
    private val context: Context,
    private val clickListener: ItemClickListener
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageProduct: ImageView
        val nameProduct: TextView
        val typeProduct: TextView
        val priceProduct: TextView
        val descriptionProduct: TextView
        val tableRow: TableRow

        init {
            imageProduct = view.findViewById(R.id.itemImage)
            nameProduct = view.findViewById(R.id.itemName)
            typeProduct = view.findViewById(R.id.itemType)
            priceProduct = view.findViewById(R.id.itemPrice)
            descriptionProduct = view.findViewById(R.id.itemDescription)
            tableRow = view.findViewById(R.id.tableRow)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_products, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        Glide.with(context).load(product.img_url).into(holder.imageProduct)
        holder.nameProduct.text = product.name.toString()
        holder.typeProduct.text = product.type.toString()
        holder.priceProduct.text = product.price.toString()
        holder.descriptionProduct.text = product.description.toString()
        holder.tableRow.setOnClickListener{
            clickListener.onItemClick(Product(
                product.name,
                product.img_url,
                product.type,
                product.price,
                product.description
            ))
        }
    }

    override fun getItemCount() = list.size

    interface ItemClickListener {
        fun onItemClick(product: Product)
    }
}