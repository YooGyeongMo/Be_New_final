package com.gmlab.team_benew.main.notification

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R

class NotificationFragment: Fragment(), NotificationView, NotificationReadView,MatchingAlarmsPatchView {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationService: NotificationMatchingService
    private lateinit var notificationReadService: NotificationReadService
    private lateinit var matchingPatchService: MatchingPatchService

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
        // 알람 리스트 불러오는 로직
        notificationService = NotificationMatchingService()
        notificationService.setNotificationView(this)
        notificationService.getNotificationList(requireContext())
        // 알람 읽는 로직
        notificationReadService = NotificationReadService()
        notificationReadService.setNotificationReadView(this)

        // 알람 수락,거절 하는로직
        matchingPatchService = MatchingPatchService()
        matchingPatchService.setMatchingAlarmsPatchView(this)


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

        // 매칭 수락 관련 로직 처리
        val receiverId =  getIdFromSharedPreferences(requireContext())?.toLong() ?: -1L // 현재 사용자의 ID
        val senderId =  notification.senderUserId // 알림의 발신자 ID

        notificationReadService.alarmsRead(requireContext(), notification.id)
        matchingPatchService.acceptMatch(requireContext(), senderId, receiverId)

        // 알람 API 수락 관련 로직 처리
        notificationReadService.alarmsRead(requireContext(), notification.id)
        (recyclerView.adapter as? NotificationAdapter)?.removeItem(notification)
    }

    private fun handleReject(notification: NotificationMatchingResponse) {

        // 매칭 거절 관련 로직 처리
        val receiverId =  getIdFromSharedPreferences(requireContext())?.toLong() ?: -1L // 현재 사용자의 ID
        val senderId =  notification.senderUserId // 알림의 발신자 ID

        notificationReadService.alarmsRead(requireContext(), notification.id)
        matchingPatchService.rejectMatch(requireContext(), senderId, receiverId)

        // 알람API 거절 관련 로직 처리
        notificationReadService.alarmsRead(requireContext(), notification.id)
        (recyclerView.adapter as? NotificationAdapter)?.removeItem(notification)
    }


    override fun onNotificationSuccess() {
        Log.d("NOTIFICATION/LIST/GET/SUCCESS","유저에 대한 알람 리스트 성공")
    }

    override fun onNotificationFailure() {
        Log.e("NOTIFICATION/LIST/GET/FAILURE","401 인증 에러 유저에 대한 알람 리스트 실패")
    }

    override fun onNotificationReadSuccess() {
        Log.d("NOTIFICATION/READ/PUT/SUCCESS","유저에 대한 알람 읽기 성공")
    }

    override fun onNotificationReadFailure() {
        findNavController().navigateUp()
        // AlertDialog 생성 및 표시
        AlertDialog.Builder(requireContext()).apply {
            setTitle("네트워크 오류")
            setMessage("네트워크 요청이 실패했습니다.")
            setPositiveButton("확인") { dialog, which ->
                // 여기서 아무 것도 하지 않음
            }
            create()
            show()
        }
    }




    override fun onMatchingAlarmsAccessSuccess() {
        Log.d("NOTIFICATION/USER/매칭/수락","유저에 대한 매칭 수락 성공")
    }

    override fun onMatchingAlarmsAccessFailure() {
        findNavController().navigateUp()
        // AlertDialog 생성 및 표시
        AlertDialog.Builder(requireContext()).apply {
            setTitle("네트워크 오류")
            setMessage("네트워크 요청이 실패했습니다.")
            setPositiveButton("확인") { dialog, which ->
                // 여기서 아무 것도 하지 않음
            }
            create()
            show()
        }
    }

    override fun onMatchingAlarmsRejectSuccess() {
        Log.d("NOTIFICATION/USER/매칭/거절","유저에 대한 매칭 거절 성공")
    }

    override fun onMatchingAlarmsRejectFailure() {
        findNavController().navigateUp()
        // AlertDialog 생성 및 표시
        AlertDialog.Builder(requireContext()).apply {
            setTitle("네트워크 오류")
            setMessage("네트워크 요청이 실패했습니다.")
            setPositiveButton("확인") { dialog, which ->
                // 여기서 아무 것도 하지 않음
            }
            create()
            show()
        }
    }


    private fun getIdFromSharedPreferences(context: Context): Int? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("loginId", -1).takeIf { it != -1 }
    }
}