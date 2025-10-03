package com.emergency.mesh.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emergency.mesh.R
import com.emergency.mesh.data.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageText)
        private val locationTextView: TextView = itemView.findViewById(R.id.messageCoords) // Changed to match layout
        private val timestampTextView: TextView = itemView.findViewById(R.id.messageTimestamp) // Changed to match layout

        fun bind(message: Message) {
            messageTextView.text = message.text
            locationTextView.text = String.format("Lat: %.4f, Lon: %.4f", message.latitude, message.longitude)
            timestampTextView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(message.timestamp))
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}