package com.gmlab.team_benew.main.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R

class NotificationFragment: Fragment(), NotificationView {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationService: NotificationMatchingService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // onViewCreated에서 뷰 요소들을 초기화합니다.
        recyclerView = view.findViewById(R.id.my_recycler_view_notificationList)
        recyclerView.layoutManager = LinearLayoutManager(context) // LayoutManager 설정

        notificationService = NotificationMatchingService()
        notificationService.setNotificationView(this)
        notificationService.getNotificationList(requireContext())
    }

    override fun onNotificationListSuccess(notifications: List<NotificationMatchingResponse>) {
        val adapter = NotificationAdapter(notifications.toMutableList(),
            onAccept = { notification ->
                // 수락 버튼 클릭 시의 처리
                Log.d("수락","수락")
                handleAccept(notification)
            },
            onReject = { notification ->
                // 거절 버튼 클릭 시의 처리
                Log.d("거절","거절")
                handleReject(notification)
            })
        recyclerView.adapter = adapter
    }

    private fun handleAccept(notification: NotificationMatchingResponse) {
        // 수락 관련 로직 처리
        // 예: 서버에 수락 요청 보내기
        // ...

        // 아이템 제거
        (recyclerView.adapter as? NotificationAdapter)?.removeItem(notification)
    }

    private fun handleReject(notification: NotificationMatchingResponse) {
        // 거절 관련 로직 처리
        // 예: 서버에 거절 요청 보내기
        // ...

        // 아이템 제거
        (recyclerView.adapter as? NotificationAdapter)?.removeItem(notification)
    }


    override fun onNotificationSuccess() {
        Log.d("NOTIFICATION/LIST/GET/SUCCESS","유저에 대한 알람 리스트 성공")
    }

    override fun onNotificationFailure() {
        Log.e("NOTIFICATION/LIST/GET/FAILURE","401 인증 에러 유저에 대한 알람 리스트 실패")
    }
}