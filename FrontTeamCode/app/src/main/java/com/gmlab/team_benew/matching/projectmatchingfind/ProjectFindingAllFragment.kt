package com.gmlab.team_benew.matching.projectmatchingfind

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R
import com.gmlab.team_benew.matching.SharedViewModel

class ProjectFindingAllFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: ProjectFindingAllViewModel
    private lateinit var adapter: ProjectFindingAllAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_find_project_of_all_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.finding_all_project_loading_indicator)

        val recyclerView = view.findViewById<RecyclerView>(R.id.my_recycler_view_all_finding_project_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProjectFindingAllAdapter()
        recyclerView.adapter = adapter

        // 팩토리를 사용하여 ViewModel 초기화
        val factory = ProjectFindingAllViewModelFactory(sharedViewModel)
        viewModel = ViewModelProvider(this, factory).get(ProjectFindingAllViewModel::class.java)

        viewModel.projects.observe(viewLifecycleOwner, Observer { projects ->
            adapter.submitList(projects)
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        })

        val token = getTokenFromSharedPreferences()
        token?.let {
            viewModel.fetchProjects(it)
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("userToken", null)
    }
}
