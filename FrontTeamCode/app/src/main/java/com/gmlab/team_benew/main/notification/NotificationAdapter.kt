package com.gmlab.team_benew.main.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R

class NotificationAdapter(private var items: MutableList<NotificationMatchingResponse>,
                          private val onAccept: (NotificationMatchingResponse) -> Unit,
                          private val onReject: (NotificationMatchingResponse) -> Unit)
    : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onAccept, onReject)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val message: TextView = itemView.findViewById(R.id.tv_notification_message)
        private val acceptButton: TextView = itemView.findViewById(R.id.tv_notification_access)
        private val rejectButton: TextView = itemView.findViewById(R.id.tv_notification_reject)

        fun bind(notificationMatchingResponse: NotificationMatchingResponse,
                 onAccept: (NotificationMatchingResponse) -> Unit,
                 onReject: (NotificationMatchingResponse) -> Unit) {
            message.text = notificationMatchingResponse.message

            acceptButton.setOnClickListener { onAccept(notificationMatchingResponse) }
            rejectButton.setOnClickListener { onReject(notificationMatchingResponse) }
        }
    }
    // 아이템을 제거하는 메소드
    fun removeItem(notification: NotificationMatchingResponse) {
        val position = items.indexOf(notification)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
