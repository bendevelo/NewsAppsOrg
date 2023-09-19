
package com.s.newsapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.s.newsapp.data.model.NewsArticle
import com.s.newsapp.R
import com.s.newsapp.data.model.CategoryArticle
import com.s.newsapp.data.model.SourceArticle
import com.s.newsapp.databinding.ItemCategoryBinding
import com.s.newsapp.databinding.ItemNewsBinding


class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.NewsAdapterViewHolder>() {

    inner class NewsAdapterViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<CategoryArticle>() {
        override fun areItemsTheSame(oldItem: CategoryArticle, newItem: CategoryArticle): Boolean {
            return oldItem.category == newItem.category
        }

        override fun areContentsTheSame(
            oldItem: CategoryArticle,
            newItem: CategoryArticle
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapterViewHolder {
        val binding =
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return NewsAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((CategoryArticle) -> Unit)? = null

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NewsAdapterViewHolder, position: Int) {
        val article = differ.currentList[position]
        with(holder) {

            binding.tvTitle.text = article.category
            binding.tvCountry.text =" Country: "+article.country
            binding.tvlanguage.text =" Language: "+article.language

        }

        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (CategoryArticle) -> Unit) {
        onItemClickListener = listener
    }
}