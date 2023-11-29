package edu.bu.taskmaster.viewModels

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import edu.bu.taskmaster.R
import edu.bu.taskmaster.utils.ToDoData


class TaskViewModel : ViewModel() {
    val homeTaskList: MutableList<ToDoData> = mutableListOf()
    val inProgressTaskList: MutableList<ToDoData> = mutableListOf()

    fun addHomeTask(todoTask: ToDoData) {
        homeTaskList.add(todoTask)
    }

    fun addInProgressTask(todoTask: ToDoData) {
        inProgressTaskList.add(todoTask)
    }

    fun removeHomeTask(todoTask: ToDoData) {
        homeTaskList.remove(todoTask)
    }
    fun removeInProgressTask(task: ToDoData) {
        Log.d("TaskViewModel", "Removing task")
        inProgressTaskList.remove(task)
    }


    fun updateInProgressTask(todoTask: ToDoData) {
        val index = inProgressTaskList.indexOfFirst { it.taskId == todoTask.taskId }
        if (index != -1) {
            inProgressTaskList[index] = todoTask
        } else {
            inProgressTaskList.add(todoTask)
        }
    }
}