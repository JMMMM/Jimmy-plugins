package com.jimmy.plugins.projects

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

/**
 * Created by jimmy on 2017/7/24.
 */
class ProjectStructurePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println "project plugins"
    }
}
