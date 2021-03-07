package com.fd.bestpracticetry_codinginflow05.features.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fd.bestpracticetry_codinginflow05.data.NewsArticle
import com.fd.bestpracticetry_codinginflow05.data.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository : NewsRepository
) : ViewModel(){


    val bookmarks = repository.getAllBookmarkedArticle()
        .stateIn(viewModelScope, SharingStarted.Lazily,null)

    fun onBookmarkClick(article: NewsArticle){
        val currentlyBookmarked = article.isBookmarked
        val updateArticle = article.copy ( isBookmarked =  !currentlyBookmarked)
        viewModelScope.launch {
            repository.updateArticle(updateArticle)
        }

    }

    fun onDeleteAllBookmarks(){
        viewModelScope.launch {
            repository.resetAllBookmarks()
        }
    }


}