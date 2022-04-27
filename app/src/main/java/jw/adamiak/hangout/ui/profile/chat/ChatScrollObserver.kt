package jw.adamiak.hangout.ui.profile.chat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatScrollObserver(
	private val recycler: RecyclerView,
	private val adapter: ChatAdapter,
	private val manager: LinearLayoutManager
) : RecyclerView.AdapterDataObserver() {
	override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
		super.onItemRangeInserted(positionStart, itemCount)
		val count = adapter.itemCount
		val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
		val loading = lastVisiblePosition == -1
		val atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
		if (loading || atBottom) {
			recycler.scrollToPosition(positionStart)
		}
	}
}
