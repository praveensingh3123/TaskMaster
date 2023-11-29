package edu.bu.taskmaster.utils

import com.google.firebase.database.DataSnapshot

data class ToDoData(var taskId:String, var task:String,var taskStatus:String){

        companion object {
                fun fromSnapshot(snapshot: DataSnapshot): ToDoData {
                        // Implement custom deserialization logic
                        return ToDoData(
                                snapshot.child("taskId").getValue(String::class.java) ?: "",
                                snapshot.child("task").getValue(String::class.java) ?: "",
                                snapshot.child("taskStatus").getValue(String::class.java) ?: ""
                                // other properties
                        )
                }
        }
}
