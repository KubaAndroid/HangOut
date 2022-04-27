package jw.adamiak.hangout.ui.profile.my_events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.databinding.ListItemBinding

class MyEventsAdapter(private val clickListener: OnEventClickListener):
	RecyclerView.Adapter<MyEventsAdapter.EventsViewHolder>(){

	private var itemsList = mutableListOf<MapObject>()
	fun setItems(events: MutableList<MapObject>) {
		itemsList = events
		notifyDataSetChanged()
	}

	override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
		if(itemsList.size > position) {
			holder.bind(itemsList[position])
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
		return EventsViewHolder(
			ListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	inner class EventsViewHolder(private val binding: ListItemBinding):
		RecyclerView.ViewHolder(binding.root) {
		fun bind(event: MapObject) {
			binding.apply {
				tvEventTitle.text = event.pinTitle
				tvEventType.text = event.eventDay
				tvEventComment.text = event.comment
			}
			binding.clListItem.setOnClickListener {
				clickListener.onEventClicked(event)
			}
		}
	}

	interface OnEventClickListener {
		fun onEventClicked(event: MapObject)
	}

	override fun getItemCount(): Int {
		return itemsList.size
	}

}