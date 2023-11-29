package com.praveen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.praveen.databinding.FragmentEditBinding


class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    private lateinit var project: Project

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        project = Project.projects[0]

        val projTitle = binding.projTitleEdit
        val projDesc = binding.projDescEdit
        val authorsEditText = binding.authorsEdit
        val linksEditText = binding.linksEdit
        val keywordsEditText = binding.keywordsEdit
        val favoriteCheckbox = binding.favoriteCheckboxEdit
        val submit = binding.submit
        val cancel = binding.cancel

        projTitle.setText(project.title)
        projDesc.setText(project.description)
        authorsEditText.setText(project.authors.joinToString(", "))
        linksEditText.setText(project.links.joinToString(", "))
        keywordsEditText.setText(project.keywords.joinToString(", "))
        favoriteCheckbox.isChecked = project.isFavorite

        submit.setOnClickListener {
            project.title = projTitle.text.toString()
            project.description = projDesc.text.toString()
            project.authors = authorsEditText.text.toString().split(",").map { it.trim() }
            project.links = linksEditText.text.toString().split(",").map { it.trim() }
            project.keywords = keywordsEditText.text.toString().split(",").map { it.trim() }
            project.isFavorite = favoriteCheckbox.isChecked

            // Navigate back to the DetailFragment
            view.findNavController().navigate(R.id.action_detailFragment_to_editFragment)
        }

        cancel.setOnClickListener {
            // Navigate back to the DetailFragment without saving changes
            view.findNavController().navigate(R.id.action_detailFragment_to_editFragment)
        }
    }
}
