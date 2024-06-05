package com.gmlab.team_benew.main.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R
import com.gmlab.team_benew.main.notification.projectnotiget.GetProjectNotiResponse

class NotificationAdapter(
    private var items: MutableList<NotificationMatchingResponse>,
    private val onAccept: (NotificationMatchingResponse) -> Unit,
    private val onReject: (NotificationMatchingResponse) -> Unit,
    private val onProjectPreview: (Int) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onAccept, onReject, onProjectPreview)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val message: TextView = itemView.findViewById(R.id.tv_notification_message)
        private val acceptButton: TextView = itemView.findViewById(R.id.tv_notification_access)
        private val rejectButton: TextView = itemView.findViewById(R.id.tv_notification_reject)
        private val projectPreviewButton: Button = itemView.findViewById(R.id.btn_project_preview)

        fun bind(
            notificationMatchingResponse: NotificationMatchingResponse,
            onAccept: (NotificationMatchingResponse) -> Unit,
            onReject: (NotificationMatchingResponse) -> Unit,
            onProjectPreview: (Int) -> Unit
        ) {
            message.text = notificationMatchingResponse.message

            acceptButton.setOnClickListener { onAccept(notificationMatchingResponse) }
            rejectButton.setOnClickListener { onReject(notificationMatchingResponse) }
            // 프로젝트 ID를 추출하여 요청을 보냅니다.
            val projectId = extractProjectId(notificationMatchingResponse.message)
            projectPreviewButton.setOnClickListener { onProjectPreview(projectId.toInt()) }
        }
        private fun extractProjectId(message: String): Int {
            // 정규식을 사용하여 프로젝트 ID를 추출합니다.
            val regex = Regex("프로젝트.*번호\\s(\\d+)")
            val matchResult = regex.find(message)
            // 추출된 프로젝트 ID를 정수로 변환하여 반환합니다.
            return matchResult?.groupValues?.get(1)?.toInt() ?: -1
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
