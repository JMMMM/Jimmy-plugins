package com.jimmy.plugins.mysql

import com.jimmy.plugins.mysql.actions.MysqlGenerator
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by jimmy on 2017/7/24.
 */
class MysqlConnectPlugins implements Plugin<Project>{
    @Override
    void apply(Project project) {
        project.getTasks().create("generator",MysqlGenerator.class)
    }
}
