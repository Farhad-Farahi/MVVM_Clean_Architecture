package com.fd.bestpracticetry_codinginflow05.shared

import androidx.recyclerview.widget.DiffUtil
import com.fd.bestpracticetry_codinginflow05.data.NewsArticle

class NewsArticleComparator : DiffUtil.ItemCallback<NewsArticle>() {
    override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
         return oldItem == newItem
    }
}