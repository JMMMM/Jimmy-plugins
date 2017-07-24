package com.jimmy.plugins.mysql.actions

import com.mysql.jdbc.Connection
import com.mysql.jdbc.PreparedStatement
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import groovy.sql.Sql
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.sql.ResultSet

/**
 * Created by jimmy on 2017/7/24.
 */
class MysqlGenerator extends DefaultTask {
    @TaskAction
    void generator() {
        sql = Sql.newInstance("jdbc:mysql://192.168.0.102:3306/crm?useUnicode=true&characterEncoding=utf8", "root", "root","com.mysql.jdbc.driver")
        sql.eachRow("select * from companys limit 0,10"){
            row->println row.name
        }
    }
}
