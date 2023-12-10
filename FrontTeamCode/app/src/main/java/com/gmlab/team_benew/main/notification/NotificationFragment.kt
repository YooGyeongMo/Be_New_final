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
import com.gmlab.team_benew.matching.MatchingAlarmResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import retrofit2.Response


class NotificationFragment : Fragment(), NotificationView, NotificationReadView,
    MatchingAlarmsPatchView {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationService: NotificationMatchingService
    private lateinit var notificationReadService: NotificationReadService
    private lateinit var matchingPatchService: MatchingPatchService

    // resquestLock 버튼 중복터치 막기
    private var requestLock = false

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
                Log.d("수락", "수락")
                handleAccept(notification)
            },
            onReject = { notification ->
                // 거절 버튼 클릭 시의 처리
                Log.d("거절", "거절")
                handleReject(notification)
            })
        recyclerView.adapter = adapter
    }


    private fun handleAccept(notification: NotificationMatchingResponse) {

        //비동기 처리 및 버튼 중복 처리
        if (requestLock) return // 이미 요청 중이면 함수반환
        requestLock = true // 요청 시작

        CoroutineScope(Dispatchers.IO).launch {
            val senderId = notification.senderUserId
            val receiverId = getIdFromSharedPreferences(requireContext())?.toLong() ?: -1L
            try {
                val readResponse =
                    notificationReadService.alarmsRead(requireContext(), notification.id)
                Log.d("NETWORK_SUCCESS_ALARMS_READ", "USER_MATCHING_READ_ALARMS_네트워크성공")
                when (readResponse.code()) {
                    200 -> {
                        onNotificationReadSuccess() // 읽기성공
                        val matchResponse =
                            matchingPatchService.acceptMatch(requireContext(), senderId, receiverId)
                        handleMatchSuccessResponse(matchResponse, notification)
                    }

                    401 -> {
                        Log.e("NotificationRead/ERROR", " ${readResponse.code()} ERROR ")
                        onNotificationReadFailure()
                    }

                    500 -> {
                        Log.e("NotificationRead/ERROR", " ${readResponse.code()} ERROR  ")
                        onNotificationReadFailure()
                    }

                    else -> {
                        Log.e("NotificationRead/ERROR", " ${readResponse.code()} ERROR ")
                        onNotificationReadFailure()
                    }
                }
            } catch (e: Exception) {
                Log.e("NETWORK_FAILURE_ALARMS_READ", "USER_MATCHING_READ_ALARMS_네트워크실패")
                onNotificationReadFailure()
            } finally {
                //어쨋든 이 함수하다가 실패하거나 성공안해도 버튼 중복은 false가 되어야하기에
                requestLock = false
            }
        }
    }
//        // 매칭 수락 관련 로직 처리

//        val senderId = notification.senderUserId
//        val receiverId = getIdFromSharedPreferences(requireContext())?.toLong() ?: -1L
//
//        notificationReadService.alarmsRead(requireContext(), notification.id)
//        matchingPatchService.acceptMatch(requireContext(), senderId, receiverId)
//
//        // 알람 API 수락 관련 로직 처리
//        notificationReadService.alarmsRead(requireContext(), notification.id)
//        (recyclerView.adapter as? NotificationAdapter)?.removeItem(notification)


    private fun handleMatchSuccessResponse(
        matchResponse: Response<MatchingAlarmResponse>,
        notification: NotificationMatchingResponse
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            when (matchResponse.code()) {
                200 -> {
                    (recyclerView.adapter as? NotificationAdapter)?.removeItem(notification)
                    onMatchingAlarmsAccessSuccess() // 예시: 성공 처리
                }

                401 -> {
                    Log.e("NotificationPATCH/수락/ERROR", "401 ERROR ${matchResponse.code()}")
                    onMatchingAlarmsAccessFailure()

                }

                500 -> {
                    Log.e("NotificationPATCH/수락/ERROR", "500 ERROR ${matchResponse.code()}")
                    onMatchingAlarmsAccessFailure()
                }

                else -> {
                    Log.e("NotificationPATCH/수락/ERROR", " ERROR ${matchResponse.code()}")
                    onMatchingAlarmsAccessFailure()
                }
            }
            requestLock = false //요청 잠금해제

        }

    }

    private fun handleReject(notification: NotificationMatchingResponse) {
        //비동기 처리 및 버튼 중복 처리
        if (requestLock) return // 이미 요청 중이면 함수반환
        requestLock = true // 요청 시작

        CoroutineScope(Dispatchers.IO).launch {
            val senderId = notification.senderUserId
            val receiverId = getIdFromSharedPreferences(requireContext())?.toLong() ?: -1L
            try {
                val readResponse =
                    notificationReadService.alarmsRead(requireContext(), notification.id)
                Log.d("NETWORK_SUCCESS_ALARMS_PATCH_수락", "USER_MATCHING_PATCH_수락_네트워크성공")
                when (readResponse.code()) {
                    200 -> {
                        onNotificationReadSuccess() // 읽기성공
                        val matchResponse =
                            matchingPatchService.rejectMatch(requireContext(), senderId, receiverId)
                        handleMatchRejectResponse(matchResponse, notification)
                    }

                    401 -> {
                        Log.e("NotificationRead/ERROR", " ${readResponse.code()} ERROR ")
                        onNotificationReadFailure()
                    }

                    500 -> {
                        Log.e("NotificationRead/ERROR", " ${readResponse.code()} ERROR  ")
                        onNotificationReadFailure()
                    }

                    else -> {
                        Log.e("NotificationRead/ERROR", " ${readResponse.code()} ERROR ")
                        onNotificationReadFailure()
                    }
                }
            } catch (e: Exception) {
                Log.e("NETWORK_FAILURE_ALARMS_READ", "USER_MATCHING_READ_ALARMS_네트워크실패")
                onNotificationReadFailure()
            } finally {
                //어쨋든 이 함수하다가 실패하거나 성공안해도 버튼 중복은 false가 되어야하기에
                requestLock = false
            }
        }

    }

    private fun handleMatchRejectResponse(matchResponse: Response<MatchingAlarmResponse>,
                                          notification: NotificationMatchingResponse
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            when (matchResponse.code()) {
                200 -> {
                    (recyclerView.adapter as? NotificationAdapter)?.removeItem(notification)
                    onMatchingAlarmsRejectSuccess() // 예시: 성공 처리
                }

                401 -> {
                    Log.e("NotificationPATCH/수락/ERROR", "401 ERROR ${matchResponse.code()}")
                    onMatchingAlarmsRejectFailure()

                }

                500 -> {
                    Log.e("NotificationPATCH/수락/ERROR", "500 ERROR ${matchResponse.code()}")
                    onMatchingAlarmsRejectFailure()
                }

                else -> {
                    Log.e("NotificationPATCH/수락/ERROR", " ERROR ${matchResponse.code()}")
                    onMatchingAlarmsRejectFailure()
                }
            }
            requestLock = false //요청 잠금해제

        }
    }

    //비동기로직

//    // 매칭 거절 관련 로직 처리
//    val receiverId = getIdFromSharedPreferences(requireContext())?.toLong() ?: -1L // 현재 사용자의 ID
//    val senderId = notification.senderUserId // 알림의 발신자 ID
//
//    notificationReadService.alarmsRead(requireContext(), notification.id)
//    matchingPatchService.rejectMatch(requireContext(), senderId, receiverId)
//
//    // 알람API 거절 관련 로직 처리
//    notificationReadService.alarmsRead(requireContext(), notification.id)
//    (recyclerView.adapter as? NotificationAdapter)?.removeItem(notification)


    override fun onNotificationSuccess() {
        Log.d("NOTIFICATION/LIST/GET/SUCCESS", "유저에 대한 알람 리스트 성공")
    }

    override fun onNotificationFailure() {
        Log.e("NOTIFICATION/LIST/GET/FAILURE", "401 인증 에러 유저에 대한 알람 리스트 실패")
    }

    override fun onNotificationReadSuccess() {
        Log.d("NOTIFICATION/READ/PUT/SUCCESS", "유저에 대한 알람 읽기 성공")
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
        Log.d("NOTIFICATION/USER/매칭/수락", "유저에 대한 매칭 수락 성공")
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
        Log.d("NOTIFICATION/USER/매칭/거절", "유저에 대한 매칭 거절 성공")
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