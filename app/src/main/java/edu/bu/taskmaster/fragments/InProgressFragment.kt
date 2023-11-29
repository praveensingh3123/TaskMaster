package edu.bu.taskmaster.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.bu.taskmaster.R
import edu.bu.taskmaster.databinding.FragmentInProgressBinding
import edu.bu.taskmaster.fragments.ToDoDialogEditFragment.Companion.TAG
import edu.bu.taskmaster.utils.Controller
import edu.bu.taskmaster.utils.TaskInprogressAdapter
import edu.bu.taskmaster.utils.ToDoData
import edu.bu.taskmaster.viewModels.TaskSaveModel
import edu.bu.taskmaster.viewModels.TaskViewModel

class InProgressFragment : Fragment(), ToDoDialogEditFragment.OnDialogNextBtnClickListener,
    TaskInprogressAdapter.TaskInprogressAdapterInterface {
    private lateinit var binding: FragmentInProgressBinding
    private lateinit var taskAdapter: TaskInprogressAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var authId: String
    private lateinit var toDoItemList: MutableList<ToDoData>
    private var frag: ToDoDialogEditFragment? = null
    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var todoEdit: TextInputEditText

    companion object {
        // Static variable
        var isInprogress = false
    }


    var text_inprogresss: TextView? = null
    var textComplete: TextView? = null

    var ll_inprogress: LinearLayout? = null
    var ll_complete: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = FragmentInProgressBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun initAdapter() {
        taskAdapter = TaskInprogressAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.mainRecyclerView.adapter = taskAdapter
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initAdapter()
        taskViewModel.homeTaskList.clear()
        toDoItemList.clear()
        taskAdapter.notifyDataSetChanged()
        getTaskFromFirebase("Inprogress")

        // Retrieve data from arguments
        val taskId = arguments?.getString("taskId")
        val task = arguments?.getString("task")
        val taskStatus = arguments?.getString("taskStatus" +
                "")
        Log.d("InProgressFragment", "Received taskId: $taskId, task: $task")

        // Add the task to the in-progress list in the ViewModel
        val todoTask = taskId?.let { ToDoData(it, task ?: "",taskStatus?:"") }
        if (todoTask != null) {
            taskViewModel.updateInProgressTask(todoTask)
        }

        // Set up the RecyclerView and adapter
        val layoutManager = LinearLayoutManager(context)
        binding.mainRecyclerView.layoutManager = layoutManager

        // Initialize the TaskAdapter with the in-progress task list from the ViewModel
     //   taskAdapter = TaskAdapter(taskViewModel.inProgressTaskList)
        binding.mainRecyclerView.adapter = taskAdapter


        binding.logout.setOnClickListener {
            // Call the logout function when the logout button is clicked
            showlogoutDialog()
        }
    }

    private fun showlogoutDialog() {

        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.update_status)

        val body = dialog?.findViewById(R.id.txt_message) as TextView
        val title = dialog?.findViewById(R.id.title) as TextView
        body.text = "Are you sure you want to logout?"
        title.text = "Logout"

        val yesBtn = dialog.findViewById(R.id.btnyes) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
            signOut()
        }

        val noBtn = dialog.findViewById(R.id.btn_no) as Button
        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun getTaskFromFirebase(status : String) {
        taskViewModel.homeTaskList.clear()
        toDoItemList.clear()
        Controller.dialogStart(context)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Controller.dialogStop()
                val updatedToDoItemList = mutableListOf<ToDoData>()
                for (taskSnapshot in snapshot.children) {
                    val taskId = taskSnapshot.key
                    val taskValue = taskSnapshot.value.toString()
                   // val todoTask = taskId?.let { ToDoData(it, taskValue) }
                    val task = ToDoData.fromSnapshot(taskSnapshot)
                    val taskName = task?.task
                    val taskStatus = task?.taskStatus

                    if (status.equals("Inprogress")){
                        if (taskStatus.equals("Inprogress")){
                            val todoTask = taskId?.let { ToDoData(it, taskName.toString(),taskStatus.toString()) }
                            //val todoTask = taskId?.let { ToDoData(it, taskValue) }
                            if (todoTask != null) {
                                toDoItemList.add(todoTask)
                            }
                        }
                    }
                   else if (status.equals("Complete")){

                        if (taskStatus.equals("Complete")){
                            val todoTask = taskId?.let { ToDoData(it, taskName.toString(),taskStatus.toString()) }
                            //val todoTask = taskId?.let { ToDoData(it, taskValue) }
                            if (todoTask != null) {
                                toDoItemList.add(todoTask)
                            }
                        }

                    }
                }

                if (toDoItemList.size==0){
                    binding.todoTask.visibility=View.VISIBLE
                    binding.mainRecyclerView.visibility=View.GONE
                }else{
                    binding.todoTask.visibility=View.GONE
                    binding.mainRecyclerView.visibility=View.VISIBLE

                }

                Log.d(TAG, "onDataChange: $updatedToDoItemList")
                if (::taskAdapter.isInitialized) {
                    // If the adapter is initialized, notify the adapter about the data change
                    taskAdapter.notifyDataSetChanged()
                } else {
                    // If the adapter is not initialized, initialize it and set it to RecyclerView
                    initAdapter()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })

    }

    private fun init() {

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = Firebase.database.reference.child("Tasks").child(authId)

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize toDoItemList directly with taskViewModel.homeTaskList
        toDoItemList = taskViewModel.homeTaskList

        taskAdapter = TaskInprogressAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.mainRecyclerView.adapter = taskAdapter

        val type = "1"
        if (type == "1") {
            isInprogress = true
            binding.llInprogress.setBackgroundResource(R.drawable.button_tab_bg)
            binding.llComplete.setBackgroundResource(0)
            context?.let { binding.textInprogresss.setTextColor(it.getColor(R.color.text_color)) }
            context?.let { binding.textComplete.setTextColor(it.getColor(R.color.black)) }
        } else {

            binding.llComplete.setBackgroundResource(R.drawable.button_tab_bg)
            binding.llInprogress.setBackgroundResource(0)
            context?.let { binding.textComplete?.setTextColor(it.getColor(R.color.text_color)) }
            context?.let { binding.textInprogresss?.setTextColor(it.getColor(R.color.black)) }
            isInprogress = false
        }

        binding.llInprogress.setOnClickListener(View.OnClickListener {
            if (!isInprogress) {

                isInprogress = true
                binding.llInprogress.setBackgroundResource(R.drawable.button_tab_bg)
                binding.llComplete.setBackgroundResource(0)
                context?.let { binding.textInprogresss.setTextColor(it.getColor(R.color.text_color)) }
                context?.let { binding.textComplete.setTextColor(it.getColor(R.color.black)) }
                getTaskFromFirebase("Inprogress")
            }
        })

        binding.llComplete.setOnClickListener(View.OnClickListener {
            if (isInprogress) {

                isInprogress = false
                binding.llComplete.setBackgroundResource(R.drawable.button_tab_bg)
                binding.llInprogress.setBackgroundResource(0)
                context?.let { binding.textComplete?.setTextColor(it.getColor(R.color.text_color)) }
                context?.let { binding.textInprogresss?.setTextColor(it.getColor(R.color.black)) }


                getTaskFromFirebase("Complete");
            }
        })


        binding.image.setOnClickListener(View.OnClickListener {
            val navController = findNavController()

            // Navigate back to the previous fragment in the back stack
            navController.popBackStack()

        })


    }

    override fun saveTask(todoTask: String, todoEdit: TextInputEditText) {
        // Not yet implemented
        val task = TaskSaveModel(todoTask,"Inprogress")
        database
            .push().setValue(task)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                    todoEdit.text = null

                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()

    }

    override fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText) {
        taskViewModel.homeTaskList.clear()
        toDoItemList.clear()
        taskAdapter.notifyDataSetChanged()

        val map = HashMap<String, Any>()
        map["task"] = toDoData.task

        if ( isInprogress){
            map["taskStatus"] = "Inprogress"
        }else{
            map["taskStatus"] = "Complete"
        }
        database.child(toDoData.taskId).updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                taskViewModel.homeTaskList.clear()
                toDoItemList.clear()
                taskAdapter.notifyDataSetChanged()
                if (isInprogress){
                    getTaskFromFirebase("Inprogress")
                }else{
                    getTaskFromFirebase("Complete")
                }

            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun onDeleteItemClicked(toDoData: ToDoData, position: Int) {
      val   title = "Are you sure you want to delete?"
        showDeleteDialog(toDoData, position,title)

        Log.d("InProgressFragment", "onDeleteItemClicked called")
        val navController = findNavController()
      //  taskViewModel.removeInProgressTask(toDoData)

        // Remove the task from the Firebase database

       /* activity?.runOnUiThread {
            taskAdapter.notifyDataSetChanged()
        }
        navController.navigate(R.id.action_inProgressFragment_to_homeFragment)*/
    }


    private fun showDeleteDialog(toDoData: ToDoData, position: Int,title: String) {

        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.update_status)

        val body = dialog?.findViewById(R.id.txt_message) as TextView
        body.text = title
        val titleTop = dialog?.findViewById(R.id.title) as TextView
        titleTop.text = "Delete Task"

        val yesBtn = dialog.findViewById(R.id.btnyes) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()

            database.child(toDoData.taskId).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    // Notify the adapter about the data change on the main thread
                    if (isInprogress){

                    }else{
                        getTaskFromFirebase("Complete")
                    }
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        taskAdapter.notifyDataSetChanged()
                    }

                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        val noBtn = dialog.findViewById(R.id.btn_no) as Button
        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }




    override fun onEditItemClicked(toDoData: ToDoData, position: Int) {

        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()

        frag = ToDoDialogEditFragment.newInstance(toDoData.taskId, toDoData.task,toDoData.task)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            TAG
        )

    }
    private fun signOut() {
        val navController = findNavController()
        auth.signOut()
        // Redirect the user to the login screen or perform any other necessary actions
        // For example, you can use Intent to start a new LoginActivity
        navController.navigate(R.id.action_inProgressFragment_to_signInFragment)
    }

    private fun showDialog(toDoData: ToDoData, position: Int,title: String) {

        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.update_status)

        val body = dialog?.findViewById(R.id.txt_message) as TextView
        body.text = title

        val yesBtn = dialog.findViewById(R.id.btnyes) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
            val map = HashMap<String, Any>()
            map["task"] = toDoData.task
            map["taskStatus"] = "Complete"
            database.child(toDoData.taskId).updateChildren(map).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Complete Successfully", Toast.LENGTH_SHORT).show()
                    isInprogress = false
                    binding.llComplete.setBackgroundResource(R.drawable.button_tab_bg)
                    binding.llInprogress.setBackgroundResource(0)
                    context?.let { binding.textComplete?.setTextColor(it.getColor(R.color.text_color)) }
                    context?.let { binding.textInprogresss?.setTextColor(it.getColor(R.color.black)) }


                    getTaskFromFirebase("Complete");
                    // Pass the data to InProgressFragment using Bundle
                    /*val bundle = Bundle()
                    bundle.putString("taskId", toDoData.taskId)
                    bundle.putString("task", toDoData.task)
                    bundle.putString("taskStatus", toDoData.taskStatus)
                    // Move the task to in-progress list in the ViewModel
                    taskViewModel.addInProgressTask(toDoData)
                    // Remove the task from the home list in the ViewModel
                    taskViewModel.removeHomeTask(toDoData)
                    // Navigate to InProgressFragment
                    findNavController().navigate(R.id.action_homeFragment_to_inProgressFragment, bundle)*/

                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        val noBtn = dialog.findViewById(R.id.btn_no) as Button
        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onMoveItemClicked(toDoData: ToDoData, position: Int) {
        // Not yet implemented


        val message  = "Are you sure you want to move task in Complete Section"
        showDialog(toDoData, position,message)



    }

    override fun onItemMoved(toDoData: ToDoData, position: Int) {
        // Not yet implemented
    }
}