package com.gmlab.team_benew.matching

import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        private val peerReviewData: ImageView = itemView.findViewById((R.id.iv_matching_peerReview_data))
        private val projectExperienceTextView: TextView = itemView.findViewById(R.id.tv_matching_project_booleanCheck_data)
        private val profileImageView: ImageView = itemView.findViewById(R.id.civ_matching_profile_user_image)

        fun binding(profile: Profile){
            // 프로필 이미지 로딩 (Glide 사용)
            profile.photo?.let {
                val imageByteArray = Base64.decode(it, Base64.DEFAULT)
                Glide.with(context)
                    .load(imageByteArray)
                    .into(profileImageView)
            } ?: run {
                // 프로필 사진이 null일 경우 기본 이미지 설정
                profileImageView.setImageResource(R.drawable.male_avatar) // 예시로 기본 이미지를 설정
            }

            nameTextView.text = profile.member.name
            roleTextView.text = profile.role
            instructionTextView.text = profile.instruction
            val drawableResource = when (profile.peer) {
                in 0..19 -> context.getDrawable(R.drawable.profilecard_detail_peer0_19)
                in 20..39 -> context.getDrawable(R.drawable.profilecard_detail_peer20_39)
                in 40..59 -> context.getDrawable(R.drawable.profilecard_detail_peer40_59)
                in 60..79 -> context.getDrawable(R.drawable.profilecard_detail_peer60_79)
                in 80..100 -> context.getDrawable(R.drawable.profilecard_detail_peer80_100)
                else -> null // 범위 밖의 값에 대한 처리
            }

            peerReviewData.setImageDrawable(drawableResource)

            projectExperienceTextView.text = if (profile.projectExperience) "유" else "무"

            // 프로필 이미지 로딩 (Glide 라이브러리 등 사용)
            // 예시: Glide.with(itemView).load(profile.photo).into(profileImageView)
        }
    }

}