package com.gmlab.team_benew.matching

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R

class CardStackAdapter(val context: Context, items: List<Profile>) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>(){
    var items: List<Profile> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardStackAdapter.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profile = items[position]
        holder.binding(profile)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_matching_profile_username_data)
        private val roleTextView: TextView = itemView.findViewById(R.id.tv_matching_profile_role_data)
        private val instructionTextView: TextView = itemView.findViewById(R.id.tv_matching_profile_instruction_data)
        private val peerReviewTextView: TextView = itemView.findViewById((R.id.tv_matching_peerReview_data))
        private val projectExperienceTextView: TextView = itemView.findViewById(R.id.tv_matching_project_booleanCheck_data)
//        private val profileImageView: ImageView = itemView.findViewById(R.id.civ_matching_profile_user_image)

        fun binding(profile: Profile){
            nameTextView.text = profile.member.name
            roleTextView.text = profile.role
            instructionTextView.text = profile.instruction
            peerReviewTextView.text = profile.peer.toString()
            projectExperienceTextView.text = if (profile.projectExperience) "있음" else "없음"

            // 프로필 이미지 로딩 (Glide 라이브러리 등 사용)
            // 예시: Glide.with(itemView).load(profile.photo).into(profileImageView)
        }
    }

}