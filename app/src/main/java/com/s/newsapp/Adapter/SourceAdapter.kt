
package com.s.newsapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.s.newsapp.data.model.SourceArticle
import com.s.newsapp.databinding.ItemSourceBinding
import java.util.Locale


class SourceAdapter : RecyclerView.Adapter<SourceAdapter.NewsAdapterViewHolder>(), Filterable {

    inner class NewsAdapterViewHolder(val binding: ItemSourceBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<SourceArticle>() {
        override fun areItemsTheSame(oldItem: SourceArticle, newItem: SourceArticle): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: SourceArticle,
            newItem: SourceArticle
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapterViewHolder {
        val binding =
            ItemSourceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return NewsAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((SourceArticle) -> Unit)? = null

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NewsAdapterViewHolder, position: Int) {
        val article = differ.currentList[position]
        with(holder) {

            binding.tvTitle.text = article.name
            binding.tvdesc.text =article.description
            binding.tvCategory.text =article.category

        }

        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (SourceArticle) -> Unit) {
        onItemClickListener = listener
    }

    override fun getFilter(): android.widget.Filter {
        return customFilter
    }

    private val customFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<SourceArticle>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(differ.currentList)
            } else {
                for (item in differ.currentList) {
                    if (item.name.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
            differ.submitList(filterResults?.values as MutableList<SourceArticle>)
        }

    }
}