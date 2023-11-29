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
import androidx.navigation.Navigation
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
import edu.bu.taskmaster.databinding.FragmentHomeBinding
import edu.bu.taskmaster.utils.Controller
import edu.bu.taskmaster.utils.TaskAdapter
import edu.bu.taskmaster.utils.ToDoData
import edu.bu.taskmaster.viewModels.TaskSaveModel
import edu.bu.taskmaster.viewModels.TaskViewModel

class HomeFragment : Fragment(), ToDoDialogFragment.OnDialogNextBtnClickListener,
    TaskAdapter.TaskAdapterInterface {

    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private var frag: ToDoDialogFragment? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private lateinit var toInProgressButton: Button
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var toDoItemList: MutableList<ToDoData>
    private val taskViewModel: TaskViewModel by activityViewModels()



    var text_inprogresss: TextView? = null
    var text_todo: TextView? = null
    var textComplete: TextView? = null
    var ll_inprogress: LinearLayout? = null
    var ll_complete: LinearLayout? = null
    var ll_todo: LinearLayout? = null

    companion object {
        // Static variable
        var type = "0"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root

    }
    private fun initAdapter() {
        taskAdapter = TaskAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.mainRecyclerView.adapter = taskAdapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        getTaskFromFirebase("Pending")


        initAdapter()

        binding.addTaskBtn.setOnClickListener {
            if (frag != null)
                childFragmentManager.beginTransaction().remove(frag!!).commit()
            frag = ToDoDialogFragment()
            frag!!.setListener(this)
            frag!!.show(
                childFragmentManager,
                ToDoDialogFragment.TAG
            )
        }

        binding.logout.setOnClickListener {
            // Call the logout function when the logout button is clicked
            showlogoutDialog()
        }

        toInProgressButton = view.findViewById(R.id.toInProgressButton)

        toInProgressButton.setOnClickListener {

            Navigation.findNavController(view)
                .navigate(R.id.action_homeFragment_to_inProgressFragment)
        }
    }

    private fun signOut() {
        val navController = findNavController()
        auth.signOut()
        // Redirect the user to the login screen or perform any other necessary actions
        // For example, you can use Intent to start a new LoginActivity
        navController.navigate(R.id.action_homeFragment_to_signInFragment)
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
            if (type=="0"){
                map["taskStatus"] = "Inprogress"
            } else if (type=="1"){
                map["taskStatus"] = "Complete"
            }
            database.child(toDoData.taskId).updateChildren(map).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Status Updated", Toast.LENGTH_SHORT).show()
                    if (type=="0"){
                        getTaskFromFirebase("Pending")
                    }else if (type=="1") {
                        getTaskFromFirebase("Inprogress")
                    }
                    /*  // Pass the data to InProgressFragment using Bundle
                      val bundle = Bundle()
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
        if (type=="0"){
            val message  = "Are you sure you want to move task in Inprogress section"
            showDialog(toDoData, position,message)
        } else  if (type=="1"){
            val message  = "Are you sure you want to move task in Complete section"
            showDialog(toDoData, position,message)
        }

    }

    override fun onItemMoved(toDoData: ToDoData, position: Int) {
        TODO("Not yet implemented")
    }

    private fun getTaskFromFirebase(status : String) {

        Controller.dialogStart(context)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Controller.dialogStop()
                val updatedToDoItemList = mutableListOf<ToDoData>()


                for (taskSnapshot in snapshot.children) {
                    val taskId = taskSnapshot.key
                    //val taskValue = taskSnapshot.value.toString()
                    val task = ToDoData.fromSnapshot(taskSnapshot)
                    val taskName = task?.task
                    val taskStatus = task?.taskStatus

                    if (status.equals("Pending")){
                        if (taskStatus.equals("Pending")) {
                            val todoTask = taskId?.let {
                                ToDoData(
                                    it,
                                    taskName.toString(),
                                    taskStatus.toString()
                                )
                            }
                            //val todoTask = taskId?.let { ToDoData(it, taskValue) }
                            if (todoTask != null) {
                                if (!taskMovedToInProgress(todoTask)) {
                                    updatedToDoItemList.add(todoTask)
                                }
                            }
                        }
                    }
                    else if (status.equals("Inprogress")){
                        if (taskStatus.equals("Inprogress")){
                            val todoTask = taskId?.let {
                                ToDoData(
                                    it,
                                    taskName.toString(),
                                    taskStatus.toString()
                                )
                            }
                            //val todoTask = taskId?.let { ToDoData(it, taskValue) }
                            if (todoTask != null) {
                                if (!taskMovedToInProgress(todoTask)) {
                                    updatedToDoItemList.add(todoTask)
                                }
                            }
                        }
                    }
                    else if (status.equals("Complete")){
                        if (taskStatus.equals("Complete")){
                            val todoTask = taskId?.let {
                                ToDoData(
                                    it,
                                    taskName.toString(),
                                    taskStatus.toString()
                                )
                            }
                            //val todoTask = taskId?.let { ToDoData(it, taskValue) }
                            if (todoTask != null) {
                                if (!taskMovedToInProgress(todoTask)) {
                                    updatedToDoItemList.add(todoTask)
                                }
                            }
                        }

                    }
                }

                // Clear the existing list and add the updated tasks
                taskViewModel.homeTaskList.clear()
                taskViewModel.homeTaskList.addAll(updatedToDoItemList)
                Log.d(TAG, "onDataChange: $toDoItemList")

                if (taskViewModel.homeTaskList.size > 0){
                    binding.todoTask.visibility=View.GONE
                    binding.mainRecyclerView.visibility=View.VISIBLE

                }else{
                    binding.todoTask.visibility=View.VISIBLE
                    binding.mainRecyclerView.visibility=View.GONE

                }

                if (::taskAdapter.isInitialized) {
                    // If the adapter is initialized, notify the adapter about the data change
                    taskAdapter.notifyDataSetChanged()
                } else {
                    // If the adapter is not initialized, initialize it and set it to RecyclerView
                    initAdapter()
                }


//                toDoItemList.clear()
//                for (taskSnapshot in snapshot.children) {
//                    val todoTask =
//                        taskSnapshot.key?.let { ToDoData(it, taskSnapshot.value.toString()) }
//
//                    if (todoTask != null) {
//                        toDoItemList.add(todoTask)
//                    }
//
//                }
//                Log.d(TAG, "onDataChange: " + toDoItemList)
//
//                if (::taskAdapter.isInitialized) {
//                    // If the adapter is initialized, notify the adapter about the data change
//                    taskAdapter.notifyDataSetChanged()
//                } else {
//                    // If the adapter is not initialized, initialize it and set it to RecyclerView
//                    initAdapter()
//                }
//                taskAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
    }
    private fun taskMovedToInProgress(todoTask: ToDoData): Boolean {
        // Check if the task is already in the InProgress list
        for (inProgressTask in taskViewModel.inProgressTaskList) {
            if (inProgressTask.taskId == todoTask.taskId) {
                return true
            }
        }
        return false
    }


    private fun init() {

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = Firebase.database.reference.child("Tasks").child(authId)

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize toDoItemList directly with taskViewModel.homeTaskList
        toDoItemList = taskViewModel.homeTaskList

        taskAdapter = TaskAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.mainRecyclerView.adapter = taskAdapter




        if (type == "1") {
            // InProgressFragment.isInprogress = true
            binding.llInprogress.setBackgroundResource(R.drawable.button_tab_bg)
            binding.llComplete.setBackgroundResource(0)
            binding.llTodo.setBackgroundResource(0)
            context?.let { binding.textInprogresss.setTextColor(it.getColor(R.color.text_color)) }
            context?.let { binding.textComplete.setTextColor(it.getColor(R.color.black)) }
            context?.let { binding.textTodo.setTextColor(it.getColor(R.color.black)) }
        }
        else  if (type == "0") {
            // InProgressFragment.isInprogress = true
            binding.llTodo.setBackgroundResource(R.drawable.button_tab_bg)
            binding.llComplete.setBackgroundResource(0)
            binding.llInprogress.setBackgroundResource(0)
            context?.let { binding.textTodo.setTextColor(it.getColor(R.color.text_color)) }
            context?.let { binding.textComplete.setTextColor(it.getColor(R.color.black)) }
            context?.let { binding.textInprogresss.setTextColor(it.getColor(R.color.black)) }

        }
        else if (type == "2") {

            binding.llComplete.setBackgroundResource(R.drawable.button_tab_bg)
            binding.llInprogress.setBackgroundResource(0)
            binding.llTodo.setBackgroundResource(0)
            context?.let { binding.textComplete?.setTextColor(it.getColor(R.color.text_color)) }
            context?.let { binding.textInprogresss?.setTextColor(it.getColor(R.color.black)) }
            context?.let { binding.textTodo?.setTextColor(it.getColor(R.color.black)) }
            // InProgressFragment.isInprogress = false
        }

        binding.llTodo.setOnClickListener(View.OnClickListener {
            if (!type.equals("0")) {
                type ="0"
                // InProgressFragment.isInprogress = true
                Log.e("Type",type)
                binding.llTodo.setBackgroundResource(R.drawable.button_tab_bg)
                binding.llComplete.setBackgroundResource(0)
                binding.llInprogress.setBackgroundResource(0)
                context?.let { binding.textTodo.setTextColor(it.getColor(R.color.text_color)) }
                context?.let { binding.textComplete.setTextColor(it.getColor(R.color.black)) }
                context?.let { binding.textInprogresss.setTextColor(it.getColor(R.color.black)) }
                getTaskFromFirebase("Pending")
            }
        })


        binding.llInprogress.setOnClickListener(View.OnClickListener {
            if (!type.equals("1")) {

                type ="1"
                Log.e("Type",type)
                // InProgressFragment.isInprogress = true
                binding.llInprogress.setBackgroundResource(R.drawable.button_tab_bg)
                binding.llComplete.setBackgroundResource(0)
                binding.llTodo.setBackgroundResource(0)
                context?.let { binding.textInprogresss.setTextColor(it.getColor(R.color.text_color)) }
                context?.let { binding.textComplete.setTextColor(it.getColor(R.color.black)) }
                context?.let { binding.textTodo.setTextColor(it.getColor(R.color.black)) }
                getTaskFromFirebase("Inprogress")
            }

        })

        binding.llComplete.setOnClickListener(View.OnClickListener {
            if (!type.equals("2")) {
                type ="2"
                Log.e("Type",type)
                //  InProgressFragment.isInprogress = false
                binding.llComplete.setBackgroundResource(R.drawable.button_tab_bg)
                binding.llInprogress.setBackgroundResource(0)
                binding.llTodo.setBackgroundResource(0)
                context?.let { binding.textComplete?.setTextColor(it.getColor(R.color.text_color)) }
                context?.let { binding.textInprogresss?.setTextColor(it.getColor(R.color.black)) }
                context?.let { binding.textTodo?.setTextColor(it.getColor(R.color.black)) }
                getTaskFromFirebase("Complete");
            }
        })


    }

    override fun saveTask(todoTask: String, todoEdit: TextInputEditText) {
        val task = TaskSaveModel(todoTask,"Pending")
        database
            .push().setValue(task)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    type ="0"
                    binding.llTodo.setBackgroundResource(R.drawable.button_tab_bg)
                    binding.llComplete.setBackgroundResource(0)
                    binding.llInprogress.setBackgroundResource(0)
                    context?.let { binding.textTodo.setTextColor(it.getColor(R.color.text_color)) }
                    context?.let { binding.textComplete.setTextColor(it.getColor(R.color.black)) }
                    context?.let { binding.textInprogresss.setTextColor(it.getColor(R.color.black)) }


                    Toast.makeText(context, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                    todoEdit.text = null
                    getTaskFromFirebase("Pending")
                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()

    }

    //aman update
    override fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>()
        map["task"] = toDoData.task
          if (type=="0"){
              map["taskStatus"] = "Pending"
          } else if (type=="1"){
            map["taskStatus"] = "Inprogress"
        }else if (type=="2"){
            map["taskStatus"] = "Complete"
        }
        database.child(toDoData.taskId).updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {

                if (type=="0"){
                    getTaskFromFirebase("Pending")
                }else if (type=="1") {
                    getTaskFromFirebase("Inprogress")
                }else if(type=="2") {
                    getTaskFromFirebase("Complete")
                }

                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun onDeleteItemClicked(toDoData: ToDoData, position: Int) {
        val   title = "Are you sure you want to delete?"
        showDeleteDialog(toDoData, position,title)
    }

    private fun showDeleteDialog(toDoData: ToDoData, position: Int,title: String) {

        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.update_status)

        val body = dialog?.findViewById(R.id.txt_message) as TextView
        val titleTop = dialog?.findViewById(R.id.title) as TextView
        body.text = title
        titleTop.text = "Delete Task"

        val yesBtn = dialog.findViewById(R.id.btnyes) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()

            database.child(toDoData.taskId).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    // Notify the adapter about the data change on the main thread

                    if (type=="0"){
                        getTaskFromFirebase("Pending")
                    }else if (type=="1") {
                        getTaskFromFirebase("Inprogress")
                    }else if(type=="2") {
                        getTaskFromFirebase("Complete")
                    }
                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
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

        frag = ToDoDialogFragment.newInstance(toDoData.taskId, toDoData.task ,toDoData.taskStatus)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            ToDoDialogFragment.TAG
        )
    }
}