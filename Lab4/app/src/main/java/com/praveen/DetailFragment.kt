package com.praveen

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController


class DetailFragment : Fragment(R.layout.fragment_detail) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projTitle = view.findViewById<TextView>(R.id.projTitle)
        val projDesc = view.findViewById<TextView>(R.id.projDesc)
        val projAuthor = view.findViewById<TextView>(R.id.authorsTextView)
        val projLinks = view.findViewById<TextView>(R.id.linksTextView)
        val projKeywords = view.findViewById<TextView>(R.id.keywordsTextView)
        val editProj = view.findViewById<ImageButton>(R.id.editProj)

        val position: Int = arguments?.getInt("projId") ?: 1

        // Set the title and description as you were doing
        projTitle.text = Project.projects[position].title
        projDesc.text = Project.projects[position].description

        // Set author, links, and keywords
        val project = Project.projects[position]
        projAuthor.text = "Author: ${project.authors.joinToString(", ")}"
        projLinks.text = "Links: ${project.links.joinToString(", ")}"
        projKeywords.text = "Keywords: ${project.keywords.joinToString(", ")}"

        editProj.setOnClickListener {
            val action = DetailFragmentDirections
                .actionDetailFragmentToEditFragment(position)
            view.findNavController().navigate(action)
        }
    }
}
