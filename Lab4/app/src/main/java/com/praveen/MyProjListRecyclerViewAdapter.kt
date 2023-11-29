package com.praveen

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.praveen.databinding.FragmentProjItemBinding



class MyProjListRecyclerViewAdapter(private val projects: List<Project>)
    : RecyclerView.Adapter<MyProjListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentProjItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < projects.size) {
            val project = projects[position]
            holder.idView.text = (project.id + 1).toString()
            holder.contentView.text = project.title
            holder.cardView.setOnClickListener {
                val action = ProjListRecycleViewFragmentDirections
                    .actionProjListRecycleViewFragmentToDetailFragment(project.id)
                it.findNavController().navigate(action)
            }
        } else {
            // Handle the last item (the "+" button)
            holder.idView.text = "" // You can set any text or icon here
            holder.contentView.text = "+"
            holder.cardView.setOnClickListener {
                val action = ProjListRecycleViewFragmentDirections
                    .actionProjListRecycleViewFragmentToAddProjectFragment()
                it.findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount(): Int = projects.size

    inner class ViewHolder(binding: FragmentProjItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.projIdView
        val contentView: TextView = binding.projTitleinCard
        val cardView: ConstraintLayout = binding.projectCard

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }

    }
}
