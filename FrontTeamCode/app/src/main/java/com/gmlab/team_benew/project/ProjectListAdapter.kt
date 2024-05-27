package com.gmlab.team_benew.project

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.gmlab.team_benew.R
import com.gmlab.team_benew.databinding.ItemProjectListInfoBinding
import com.gmlab.team_benew.databinding.ItemProjectNoListBinding

class ProjectListAdapter(
    private val projectList: List<ProjectResponse>,
    private val navController: NavController
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PROJECT = 0
        private const val VIEW_TYPE_NO_PROJECT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (projectList.isEmpty() || position >= projectList.size) VIEW_TYPE_NO_PROJECT else VIEW_TYPE_PROJECT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_PROJECT) {
            val binding = ItemProjectListInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ProjectViewHolder(binding)
        } else {
            val binding = ItemProjectNoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            NoProjectViewHolder(binding, navController)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ProjectViewHolder && position < projectList.size) {
            val projectItem = projectList[position]
            holder.bind(projectItem)
        }
    }

    override fun getItemCount(): Int = if (projectList.isEmpty()) 1 else projectList.size + 1

    class ProjectViewHolder(private val binding: ItemProjectListInfoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(projectItem: ProjectResponse) {
            binding.tvProjectListProjectTitleInfo.text = projectItem.projectName
            binding.tvProjectListProjectPersonInfo.text = "${projectItem.numberOfMembers}명"
            binding.tvProjectListProjectStartDateInfo.text = projectItem.projectStartDate
        }
    }

    class NoProjectViewHolder(private val binding: ItemProjectNoListBinding, private val navController: NavController) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivBtnIfNoProjectMake.setOnClickListener {
                navController.navigate(R.id.action_navigation_project_list_to_projectPostDeatilFragment)
            }
        }
    }
}