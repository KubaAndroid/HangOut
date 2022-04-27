package jw.adamiak.hangout.ui.profile.chat

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.ChatMessage
import jw.adamiak.hangout.databinding.FragmentChatBinding
import jw.adamiak.hangout.ui.events.event_detail.EventDetailViewModel
import jw.adamiak.hangout.utils.Constants.DATABASE_URL
import jw.adamiak.hangout.utils.Helpers.hideKeyboard
import java.lang.Thread.sleep

@AndroidEntryPoint
class ChatFragment: Fragment(R.layout.fragment_chat) {
	private lateinit var binding: FragmentChatBinding
	private lateinit var layoutManager: LinearLayoutManager
	private lateinit var adapter: ChatAdapter

	private lateinit var db: FirebaseDatabase
	private lateinit var chatRoom: String

	private val viewModel: ChatViewModel by viewModels()
	private var userName: String = ""

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentChatBinding.bind(view)
		db = Firebase.database(DATABASE_URL)

		chatRoom = if(arguments != null){
			requireArguments().get("chatRoom") as String
		} else {
			"default"
		}

		switchProgressbar(true)

		viewModel.currentUsername.observe(viewLifecycleOwner){
			userName = it ?: ""
			println("username: $userName")
		}

		val messagesRef = db.reference.child(MESSAGES_CHILD).child(chatRoom)
		val adapterOptions = FirebaseRecyclerOptions.Builder<ChatMessage>()
			.setQuery(messagesRef, ChatMessage::class.java)
			.build()

		if(userName.isNullOrEmpty()){
			userName = viewModel.currentUserEmail.value.toString()
		}

		adapter = ChatAdapter(adapterOptions)
		binding.pbChat.visibility = ProgressBar.INVISIBLE
		layoutManager = LinearLayoutManager(requireContext())
		layoutManager.stackFromEnd = true
		binding.rvChat.layoutManager = layoutManager
		binding.rvChat.adapter = adapter

		adapter.registerAdapterDataObserver(ChatScrollObserver(binding.rvChat, adapter, layoutManager))
		switchProgressbar(false)

		binding.etChatMessage.addTextChangedListener(ChatButtonObserver(binding.ivChatSend))

		binding.ivChatSend.setOnClickListener {
			switchProgressbar(true)
			hideKeyboard(requireActivity())
			val chatMessage = ChatMessage(binding.etChatMessage.text.toString(), userName)
			db.reference.child(MESSAGES_CHILD).child(chatRoom).push().setValue(chatMessage)
			binding.etChatMessage.setText("")
			switchProgressbar(false)
		}
	}

	private fun switchProgressbar(isLoading: Boolean) {
		if(isLoading){
			binding.pbChat.visibility = ProgressBar.VISIBLE
		} else {
			binding.pbChat.visibility = ProgressBar.INVISIBLE
		}
	}

	override fun onPause() {
		adapter.stopListening()
		super.onPause()
	}

	override fun onResume() {
		super.onResume()
		adapter.startListening()
	}

	companion object {
		const val MESSAGES_CHILD = "messages"
	}
}