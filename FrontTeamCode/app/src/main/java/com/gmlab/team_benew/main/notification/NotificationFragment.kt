package com.gmlab.team_benew.main.notification

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R
import com.gmlab.team_benew.main.notification.ProjectMemeberPatch.ProjectMemberPatchService
import com.gmlab.team_benew.main.notification.chattingpost.ChattingPostService
import com.gmlab.team_benew.main.notification.chattingpost.ChattingPostView
import com.gmlab.team_benew.main.notification.matchingalarm.MatchingAlarmsPatchView
import com.gmlab.team_benew.main.notification.matchingalarm.MatchingPatchService
import com.gmlab.team_benew.matching.MatchingAlarmResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class NotificationFragment : Fragment(), NotificationView, NotificationReadView, ChattingPostView,
    MatchingAlarmsPatchView {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationService: NotificationMatchingService
    private lateinit var notificationReadService: NotificationReadService
    private lateinit var matchingPatchService: MatchingPatchService
    private lateinit var chattingPostService: ChattingPostService
    private lateinit var projectMemberPatchService: ProjectMemberPatchService

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

        //최종 매칭 성립 조건 채팅방 만들기
        chattingPostService = ChattingPostService()
        chattingPostService.setChattingPostView(this)


        projectMemberPatchService = ProjectMemberPatchService()
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
        if (requestLock) return
        requestLock = true

        CoroutineScope(Dispatchers.IO).launch {
            val senderId = notification.senderUserId
            val receiverId = getIdFromSharedPreferences(requireContext())?.toLong() ?: -1L
            val projectId = extractProjectId(notification.message)

            try {
                val readResponse = notificationReadService.alarmsRead(requireContext(), notification.id)
                if (readResponse.isSuccessful) {
                    Log.d("NotificationFragment", "알람 읽기 성공: ${readResponse.code()}")
                    val matchResponse = matchingPatchService.acceptMatch(requireContext(), senderId, receiverId)
                    if (matchResponse.isSuccessful) {
                        Log.d("NotificationFragment", "매칭 수락 성공: ${matchResponse.code()}")
                        val patchResponse = projectMemberPatchService.addProjectMember(requireContext(), projectId, receiverId)
                        if (patchResponse.isSuccessful) {
                            Log.d("NotificationFragment", "프로젝트 팀원 추가 성공: ${patchResponse.code()}")
                            val chatResponse = chattingPostService.chattingPost(requireContext(), senderId, receiverId)
                            if (chatResponse.isSuccessful) {
                                Log.d("NotificationFragment", "채팅방 생성 성공: ${chatResponse.code()}")
                                withContext(Dispatchers.Main) {
                                    showSuccessDialog("해당 프로젝트에 팀원으로 추가되었습니다.\n해당 프로젝트 채팅방이 생성되었습니다.")
                                    (recyclerView.adapter as? NotificationAdapter)?.removeItem(notification)
                                }
                            } else {
                                Log.e("NotificationFragment", "채팅방 생성 실패: ${chatResponse.code()} - ${chatResponse.errorBody()?.string()}")
                                handleFailure("채팅방 생성에 실패했습니다. 오류 코드: ${chatResponse.code()}")
                            }
                        } else {
                            Log.e("NotificationFragment", "프로젝트 팀원 추가 실패: ${patchResponse.code()} - ${patchResponse.errorBody()?.string()}")
                            handleFailure("프로젝트 팀원 추가에 실패했습니다. 오류 코드: ${patchResponse.code()}")
                        }
                    } else {
                        Log.e("NotificationFragment", "매칭 수락 실패: ${matchResponse.code()} - ${matchResponse.errorBody()?.string()}")
                        handleFailure("매칭 수락에 실패했습니다. 오류 코드: ${matchResponse.code()}")
                    }
                } else {
                    Log.e("NotificationFragment", "알람 읽기 실패: ${readResponse.code()} - ${readResponse.errorBody()?.string()}")
                    handleFailure("알람 읽기에 실패했습니다. 오류 코드: ${readResponse.code()}")
                }
            } catch (e: Exception) {
                Log.e("NotificationFragment", "네트워크 요청에 실패했습니다.", e)
                handleFailure("네트워크 요청에 실패했습니다. ${e.message}")
            } finally {
                requestLock = false
            }
        }
    }

    private fun handleReject(notification: NotificationMatchingResponse) {
        if (requestLock) return
        requestLock = true

        CoroutineScope(Dispatchers.IO).launch {
            val senderId = notification.senderUserId
            val receiverId = getIdFromSharedPreferences(requireContext())?.toLong() ?: -1L

            try {
                val readResponse = notificationReadService.alarmsRead(requireContext(), notification.id)
                if (readResponse.isSuccessful) {
                    val matchResponse = matchingPatchService.rejectMatch(requireContext(), senderId, receiverId)
                    if (matchResponse.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            (recyclerView.adapter as? NotificationAdapter)?.removeItem(notification)
                            showSuccessDialog("매칭 요청을 거절했습니다.")
                        }
                    } else {
                        handleFailure("매칭 거절에 실패했습니다.")
                    }
                } else {
                    handleFailure("알람 읽기에 실패했습니다.")
                }
            } catch (e: Exception) {
                handleFailure("네트워크 요청에 실패했습니다.")
            } finally {
                requestLock = false
            }
        }
    }

    private fun handleFailure(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            showFailureDialog(message)
        }
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("성공")
            setMessage(message)
            setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun showFailureDialog(message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("오류")
            setMessage(message)
            setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun getIdFromSharedPreferences(context: Context): Int? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("loginId", -1).takeIf { it != -1 }
    }

    private fun extractProjectId(message: String): Long {
        val regex = Regex("프로젝트.*번호\\s(\\d+)")
        val matchResult = regex.find(message)
        return matchResult?.groupValues?.get(1)?.toLong() ?: -1L
    }

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
        CoroutineScope(Dispatchers.Main).launch {
            showFailureDialog("네트워크 요청이 실패했습니다.")
        }
    }

    override fun onMatchingAlarmsAccessSuccess() {
        Log.d("NOTIFICATION/USER/매칭/수락", "유저에 대한 매칭 수락 성공")
    }

    override fun onMatchingAlarmsAccessFailure() {
        CoroutineScope(Dispatchers.Main).launch {
            showFailureDialog("매칭 수락 네트워크 오류")
        }
    }

    override fun onMatchingAlarmsRejectSuccess() {
        Log.d("NOTIFICATION/USER/매칭/거절", "유저에 대한 매칭 거절 성공")
    }

    override fun onMatchingAlarmsRejectFailure() {
        CoroutineScope(Dispatchers.Main).launch {
            showFailureDialog("매칭 거절 네트워크 오류")
        }
    }

    override fun onChattingPostSuccess() {
        Log.d("CHATTING/POST/성공", "유저에 대한 채팅방 만들기 성공")
        showSuccessDialog("최종 매칭 성공!")
    }

    override fun onChattingPostFailure() {
        CoroutineScope(Dispatchers.Main).launch {
            showFailureDialog("최종 매칭 네트워크 오류")
        }
    }
}
