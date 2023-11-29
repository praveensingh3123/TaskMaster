package com.praveen

data class Project(val id: Int, var title: String, var description: String, var authors: List<String>, var links: List<String>, var keywords: List<String>, var isFavorite: Boolean){
    companion object {
        // val project = Project(0, "Weather Forecast", "Weather Forcast is an app ...")
        val projects = mutableListOf(
            Project(0, "Weather Forecast", "Weather Forcast is an app ...", listOf("Praveen", "Singh"), listOf("www.github.com"), listOf("weather"), true ),
            Project(1, "Connect Me", "Connect Me is an app ... ", listOf("Praveen", "Singh"), listOf("www.github.com"), listOf("Connection"), true),
            Project(2, "What to Eat", "What to Eat is an app ...",  listOf("Praveen", "Singh"), listOf("www.github.com"), listOf("Food"), true),
            Project(3, "Project Portal", "Project Portal is an app ...",  listOf("Praveen", "Singh"), listOf("www.github.com"), listOf("Project"), true))
    }
}