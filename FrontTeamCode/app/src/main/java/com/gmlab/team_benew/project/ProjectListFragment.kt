package com.gmlab.team_benew.project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmlab.team_benew.databinding.FragmentIntroMyprojectBinding

class ProjectListFragment : Fragment(), ProjectListView {

    private var _binding: FragmentIntroMyprojectBinding? = null
    private val binding get() = _binding!!
    private lateinit var projectListAdapter: ProjectListAdapter
    private val projectListViewModel: ProjectListViewModel by viewModels()
    private lateinit var projectListService: ProjectListService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIntroMyprojectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 프로젝트리스트 서비스 초기화
        projectListService = ProjectListService(requireContext())
        projectListService.setProjectListView(this)

        // 어뎁터랑 리사이클러뷰 초기화
        projectListAdapter = ProjectListAdapter(emptyList(), findNavController())
        binding.myRecyclerViewProjectList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = projectListAdapter
        }

        // Observe 뷰모델 데이터
        projectListViewModel.projects.observe(viewLifecycleOwner, Observer { projects ->
            projectListAdapter = ProjectListAdapter(projects,  findNavController())
            binding.myRecyclerViewProjectList.adapter = projectListAdapter
        })

        projectListViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.myProjectLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Load project data
        projectListViewModel.loadProjects(projectListService)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onProjectListSuccess(projects: List<ProjectResponse>) {
        projectListViewModel.setProjects(projects)
        Log.d("ProjectListFragment", "프로젝트 전체 리스트 불러오는데 성공! 200")
    }

    override fun onProjectListEmpty() {
        projectListViewModel.setProjects(emptyList())
        Log.e("ProjectListFragment", "프로젝트 전체 리스트가 비어있습니다.")
    }

    override fun onProjectListFailure() {
        Log.e("ProjectListFragment", "프로젝트 전체 리스트 불러오는데 실패 401에러")
    }

    override fun onProjectListForbidden() {
        Log.e("ProjectListFragment", "접근 금지 403에러")
    }

    override fun onProjectListNotFound() {
        Log.e("ProjectListFragment", "프로젝트 전체 리스트를 찾을수 없음 404에러")
    }
}