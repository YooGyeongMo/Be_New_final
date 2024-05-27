package com.gmlab.team_benew.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gmlab.team_benew.R

class ProjectDetailFragment:Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_project_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //번들에서 projectId가져옴
        val projectId = arguments?.getInt("projectId")

        //project를 UI에 표시하거나 로그에 출력
        if(projectId != null) {
            // 로그에 출력
            Log.d("ProjectDetailFragment", "Received projectId: $projectId")
        }
        else{
            Log.e("ProjectDetailFragment", "No projectId")
        }

    }
}