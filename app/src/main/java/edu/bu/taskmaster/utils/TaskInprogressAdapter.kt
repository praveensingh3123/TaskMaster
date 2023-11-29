package edu.bu.taskmaster.utils


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.bu.taskmaster.databinding.EachTodoItemBinding
import edu.bu.taskmaster.fragments.InProgressFragment

class TaskInprogressAdapter(private val list: MutableList<ToDoData>) : RecyclerView.Adapter<TaskInprogressAdapter.TaskViewHolder>() {

    private  val TAG = "TaskAdapter"
    private var listener: TaskInprogressAdapterInterface? = null
    constructor(list: MutableList<ToDoData>, listener: TaskInprogressAdapterInterface) : this(list) {
        this.listener = listener
    }
    fun setListener(listener: TaskInprogressAdapterInterface){
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

                if (InProgressFragment.isInprogress){
                    binding.editTask.visibility = View.VISIBLE
                    binding.deleteTask.visibility = View.VISIBLE
                    binding.moveTask.visibility = View.VISIBLE
                }else{
                    binding.editTask.visibility = View.VISIBLE
                    binding.deleteTask.visibility = View.VISIBLE
                    binding.moveTask.visibility = View.GONE
                }
                Log.d(TAG, "onBindViewHolder: "+this)
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

    interface TaskInprogressAdapterInterface{
        fun onDeleteItemClicked(toDoData: ToDoData , position : Int)
        fun onEditItemClicked(toDoData: ToDoData , position: Int)
        fun onMoveItemClicked(toDoData: ToDoData , position: Int)
        fun onItemMoved(toDoData: ToDoData, position: Int)

    }

}