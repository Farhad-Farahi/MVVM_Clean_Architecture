package com.fd.bestpracticetry_codinginflow05.features.breakingnews



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fd.bestpracticetry_codinginflow05.data.NewsArticle
import com.fd.bestpracticetry_codinginflow05.data.NewsRepository
import com.fd.bestpracticetry_codinginflow05.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    var pendingScrollToTopAfterRefresh = false

    val breakingNews = refreshTrigger.flatMapLatest {refresh ->
        repository.getBreakingNews(
            refresh == Refresh.FORCE,
            onFetchSuccess = {
                pendingScrollToTopAfterRefresh = true
            },
            onFetchFailed = { t ->
                viewModelScope.launch { eventChannel.send(Event.ShowErrorMessage(t)) }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            repository.deleteNonBookmarkedArticlesOlderThan(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            )
        }
    }

    fun onStart() {
        if (breakingNews.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.NORMAL)
            }
        }
    }

    fun onManualRefresh() {
        if (breakingNews.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.FORCE)
            }
        }
    }

    /**
     *
     * get the article
     * get value of article.isBookmarked
     * set isBookmarked value opposite
     *
     * then update article
     */
    fun onBookmarkClick(article: NewsArticle){
        val currentlyBookmarked = article.isBookmarked
        val updateArticle = article.copy ( isBookmarked =  !currentlyBookmarked)
            viewModelScope.launch {
                repository.updateArticle(updateArticle)
            }

        }



    enum class Refresh {
        FORCE, NORMAL
    }
    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }
}