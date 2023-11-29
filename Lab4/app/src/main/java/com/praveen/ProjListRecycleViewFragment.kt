package com.praveen


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.praveen.databinding.FragmentEditBinding
import com.praveen.databinding.FragmentProjListRecyclerViewBinding

/**
 * A fragment representing a list of Items.
 */
class ProjListRecycleViewFragment : Fragment() {
    private lateinit var myButton: Button
    private lateinit var binding: FragmentProjListRecyclerViewBinding

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentProjListRecyclerViewBinding.inflate(inflater,
            container, false)
        return binding.root
        val view = inflater.inflate(R.layout.fragment_proj_list_recycler_view, container, false)
        return inflater.inflate(R.layout.fragment_proj_list_recycler_view, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.projlist?.apply{
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = MyProjListRecyclerViewAdapter(Project.projects)
        }
        myButton = view.findViewById<Button>(R.id.fab)

        myButton.setOnClickListener{
            view.findNavController().navigate(R.id.action_projListRecycleViewFragment_to_addProjectFragment)
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ProjListRecycleViewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}