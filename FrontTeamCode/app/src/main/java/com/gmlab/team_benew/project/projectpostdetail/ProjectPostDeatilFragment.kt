package com.gmlab.team_benew.project.projectpostdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gmlab.team_benew.databinding.FragmentMyProjectDetailBinding
import com.gmlab.team_benew.databinding.FragmentMyprojectPostBinding

class ProjectPostDeatilFragment : Fragment() {

    private var _binding: FragmentMyprojectPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyprojectPostBinding.inflate(inflater, container, false)
        return binding.root
    }
}