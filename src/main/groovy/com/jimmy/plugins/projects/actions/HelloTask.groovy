package com.jimmy.plugins.projects.actions
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by jimmy on 2017/7/24.
 */
class HelloTask extends DefaultTask{
    @TaskAction
    void hello(){
        println "hello..."
    }
}
