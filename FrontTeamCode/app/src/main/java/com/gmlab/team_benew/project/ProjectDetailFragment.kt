package com.gmlab.team_benew.project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.gmlab.team_benew.R
import com.gmlab.team_benew.project.projectgetdetail.GetProjectDeatilResponse
import com.gmlab.team_benew.project.projectgetdetail.ProjectDetailService
import com.gmlab.team_benew.project.projectgetdetail.ProjectDetailView
import com.gmlab.team_benew.project.projectgetdetail.ProjectDetailViewModel
import com.gmlab.team_benew.project.projectgetmember.ProjectGetMemberViewModel
import com.gmlab.team_benew.project.projectgetmember.ProjectMemberListAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class ProjectDetailFragment:Fragment(), ProjectDetailView {

    private lateinit var projectDetailService: ProjectDetailService
    private val projectDetailViewModel: ProjectDetailViewModel by viewModels()
    private val projectGetMemberViewModel: ProjectGetMemberViewModel by viewModels()


    private lateinit var projectNameTextView: TextView
    private lateinit var projectStartDateTextView: TextView
    private lateinit var projectEndDateTextView: TextView
    private lateinit var projectOneLineIntroTextView: TextView
    private lateinit var projectIntroTextView: TextView
    private lateinit var projectProgressBar: ProgressBar
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var projectDetailContent: View
    private lateinit var memberViewPager: ViewPager2



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_project_detail, container, false)

        projectDetailContent = view.findViewById(R.id.project_detail_content)
        projectNameTextView = view.findViewById(R.id.tv_project_title_name_of_detail)
        projectStartDateTextView = view.findViewById(R.id.tv_project_detail_start_date_data)
        projectEndDateTextView = view.findViewById(R.id.tv_project_detail_end_date_data)
        projectOneLineIntroTextView = view.findViewById(R.id.tv_project_detail_summary_introduction_info_data)
        projectIntroTextView = view.findViewById(R.id.tv_project_detail_explain_introduction_info_data)
        projectProgressBar = view.findViewById(R.id.pb_project_detail_state_data)
        loadingIndicator = view.findViewById(R.id.project_detail_loading_indicator)
        memberViewPager = view.findViewById(R.id.viewPager_team_members)

        projectDetailService = ProjectDetailService(requireContext())
        projectDetailService.setProjectDetailView(this)

        projectDetailViewModel.projectDetail.observe(viewLifecycleOwner, Observer { projectDetail ->
            updateUI(projectDetail)
        })

        projectDetailViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                loadingIndicator.visibility = View.VISIBLE
                projectDetailContent.visibility = View.GONE
            } else {
                loadingIndicator.visibility = View.GONE
                projectDetailContent.visibility = View.VISIBLE
            }
        })

        projectGetMemberViewModel.projectMembers.observe(viewLifecycleOwner, Observer { members ->
            memberViewPager.adapter = ProjectMemberListAdapter(members)

        })

        projectGetMemberViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                loadingIndicator.visibility = View.VISIBLE
                projectDetailContent.visibility = View.GONE
            } else {
                loadingIndicator.visibility = View.GONE
                projectDetailContent.visibility = View.VISIBLE
            }
        })

        projectGetMemberViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                // 에러 처리 로직을 추가할 수 있습니다.
                Log.e("ProjectDetailFragment", errorMessage)
            }
        })

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //번들에서 projectId가져옴
        val projectId = arguments?.getInt("projectId")

        //project를 UI에 표시하거나 로그에 출력
        if(projectId != null) {
            // 로그에 출력
            projectDetailViewModel.setLoading(true)
            projectDetailService.getProjectDetail(projectId)
            projectGetMemberViewModel.getProjectMembers(requireContext(), projectId)  // Context를 전달하여 getProjectMembers 호출
            Log.d("ProjectDetailFragment", "프로젝트 id: $projectId")
        }
        else{
            Log.e("ProjectDetailFragment", "No projectId")
        }

    }

    override fun onProjectDetailSuccess(projectDetail: GetProjectDeatilResponse) {
        projectDetailViewModel.setLoading(false)
        projectDetailViewModel.setProjectDetail(projectDetail)
        Log.d("ProjectDetailFragment", "프로젝트 상세 정보를 불러오는데 성공!")
    }

    override fun onProjectDetailFailure(statusCode: Int) {
        projectDetailViewModel.setLoading(false)
        Log.e("ProjectDetailFragment", "프로젝트 상세 정보를 불러오는데 실패 $statusCode")
    }

    private fun updateUI(projectDetail: GetProjectDeatilResponse) {
        projectNameTextView.text = "${projectDetail.projectName} 프로젝트"
        projectStartDateTextView.text = projectDetail.projectStartDate
        projectEndDateTextView.text = projectDetail.projectDeadlineDate
        projectOneLineIntroTextView.text = projectDetail.projectOneLineIntroduction
        projectIntroTextView.text = projectDetail.projectIntroduction

    }

}