package edu.bu.taskmaster.utils


import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bu.taskmaster.R
import edu.bu.taskmaster.databinding.EachTodoItemBinding
import edu.bu.taskmaster.fragments.HomeFragment
import edu.bu.taskmaster.fragments.InProgressFragment

class TaskAdapter(private val list: MutableList<ToDoData>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private  val TAG = "TaskAdapter"
    private var listener: TaskAdapterInterface? = null
    constructor(list: MutableList<ToDoData>, listener: TaskAdapterInterface) : this(list) {
        this.listener = listener
    }
    fun setListener(listener: TaskAdapterInterface){
        this.listener = listener
    }
    class TaskViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this.task

                Log.d(TAG, "onBindViewHolder: "+this)

                if (HomeFragment.type=="0"){
                    binding.editTask.visibility = View.VISIBLE
                    binding.deleteTask.visibility = View.VISIBLE
                    binding.moveTask.visibility = View.VISIBLE
                }else if (HomeFragment.type=="1"){
                    binding.editTask.visibility = View.VISIBLE
                    binding.deleteTask.visibility = View.VISIBLE
                    binding.moveTask.visibility = View.VISIBLE
                }else if (HomeFragment.type=="2"){
                    binding.editTask.visibility = View.VISIBLE
                    binding.deleteTask.visibility = View.VISIBLE
                    binding.moveTask.visibility = View.GONE
                }

                binding.editTask.setOnClickListener {
                    listener?.onEditItemClicked(this , position)
                }

                binding.deleteTask.setOnClickListener {


                    listener?.onDeleteItemClicked(this , position)
                }

                binding.moveTask.setOnClickListener{
                    listener?.onMoveItemClicked(this, position)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }


    interface TaskAdapterInterface{
        fun onDeleteItemClicked(toDoData: ToDoData , position : Int)
        fun onEditItemClicked(toDoData: ToDoData , position: Int)
        fun onMoveItemClicked(toDoData: ToDoData , position: Int)
        fun onItemMoved(toDoData: ToDoData, position: Int)

    }

}