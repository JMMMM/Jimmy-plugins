package com.jimmy.plugins.projects

import com.jimmy.plugins.projects.actions.HelloTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by jimmy on 2017/7/24.
 */
class ProjectStructurePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.getTasks().create("hello",HelloTask.class)
    }
}
