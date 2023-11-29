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


data class TaskSaveModel (
    val task: String,
    val taskStatus: String
)
