package jw.adamiak.hangout.ui.profile.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import jw.adamiak.hangout.data.remote.ChatMessage
import jw.adamiak.hangout.databinding.ItemChatMessageBinding

class ChatAdapter(
	private val options: FirebaseRecyclerOptions<ChatMessage>
): FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder>(options) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return ChatViewHolder(
			ItemChatMessageBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: ChatMessage) {
		(holder as ChatViewHolder).bind(model)
	}

	inner class ChatViewHolder(private val binding: ItemChatMessageBinding)
		: RecyclerView.ViewHolder(binding.root) {
		fun bind(message: ChatMessage) {
			binding.tvMessage.text = message.text
			binding.tvMessageUsername.text = message.username
			binding.tvMessageDate.text = message.toTimeString()
		}
	}

}