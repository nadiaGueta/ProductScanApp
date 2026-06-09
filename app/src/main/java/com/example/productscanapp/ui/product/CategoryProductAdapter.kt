package com.example.productscanapp.ui.product

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productscanapp.R
import com.example.productscanapp.domain.Product

class CategoryProductAdapter(
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var products: List<Product> = emptyList()
    private var loading: Boolean = false

    fun submit(
        products: List<Product>,
        loading: Boolean
    ) {
        this.products = products
        this.loading = loading
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return products.size + if (loading) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < products.size) {
            TYPE_PRODUCT
        } else {
            TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PRODUCT -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.item_category_product,
                        parent,
                        false
                    )

                ProductViewHolder(
                    view = view,
                    onProductClick = onProductClick
                )
            }

            else -> {
                val progressBar = ProgressBar(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        72.dp(parent)
                    )
                    isIndeterminate = true
                    setPadding(16, 16, 16, 16)
                }

                LoadingViewHolder(progressBar)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (holder is ProductViewHolder) {
            holder.bind(products[position])
        }
    }

    private class ProductViewHolder(
        view: View,
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val productName: TextView =
            view.findViewById(R.id.product_name)

        private val productBrand: TextView =
            view.findViewById(R.id.product_brand)

        private val productScore: TextView =
            view.findViewById(R.id.product_score)

        private var currentProduct: Product? = null

        init {
            view.setOnClickListener {
                currentProduct?.let(onProductClick)
            }
        }

        fun bind(product: Product) {
            currentProduct = product

            productName.text = product.name
            productBrand.text = product.brand

            val score = product.nutriScore?.uppercase() ?: "?"

            productScore.text = "NutriScore $score"
            productScore.setTextColor(nutriScoreColor(score))
        }

        private fun nutriScoreColor(score: String): Int {
            return when (score) {
                "A" -> Color.rgb(58, 181, 71)
                "B" -> Color.rgb(133, 187, 47)
                "C" -> Color.rgb(245, 166, 35)
                "D" -> Color.rgb(224, 123, 57)
                "E" -> Color.rgb(230, 57, 70)
                else -> Color.rgb(189, 189, 189)
            }
        }
    }

    private class LoadingViewHolder(
        progressBar: ProgressBar
    ) : RecyclerView.ViewHolder(progressBar)

    companion object {
        private const val TYPE_PRODUCT = 0
        private const val TYPE_LOADING = 1

        private fun Int.dp(parent: ViewGroup): Int {
            return (
                    this * parent.resources.displayMetrics.density
                    ).toInt()
        }
    }
}