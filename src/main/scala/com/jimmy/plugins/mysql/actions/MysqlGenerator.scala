package com.jimmy.plugins.mysql.actions

import java.io.{File, FileOutputStream}
import java.sql.{Connection, ResultSet}
import java.text.SimpleDateFormat
import java.util.Date

import com.jimmy.plugins.mysq.beans.ColumnInfo
import com.jimmy.plugins.utils.StringUtils._
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by Jimmy on 2017/7/25.
  */
object MysqlGenerator /*extends DefaultTask */ {

  val lineSeparator: String = System.getProperty("line.separator", "\n")


  var hostIp = "192.168.99.100"
  var port = 3306
  var userName = "root"
  var password = "root"
  var tableName = "companys"
  var database = "crm"
  var packageName = "com.jimmy.plugins.temp"

  val importList = List("java.math.BigDecimal", "javax.persistence.*", "java.util.Date", "java.sql.Timestamp")
  var getterAndSetter = ListBuffer.newBuilder[String]

  //  @TaskAction
  def generator(): Unit = {
    val conn = getConnection()
    val ps = conn.prepareStatement("select * from information_schema.columns where `TABLE_SCHEMA`=? and `TABLE_NAME`=?")
    ps.setString(1, database)
    ps.setString(2, tableName)
    val rs = ps.executeQuery()
    val columnInfos = createColumnInfos(rs)
    val entity = createAll(columnInfos)
    val fos = new FileOutputStream(createEntityFile())
    fos.write(entity.getBytes())
    fos.close()
  }

  private def getConnection(): Connection = {
    val dataSource = new MysqlDataSource()
    dataSource.setUrl(s"jdbc:mysql://${hostIp}:${port}/information_schema?useUnicode=true&characterEncoding=utf8&user=${userName}&password=${password}")
    dataSource.getConnection
  }

  /**
    * 返回 column_name -> (column_type,column_key)
    *
    * @param rs
    * @return
    */
  private def createColumnInfos(rs: ResultSet): List[ColumnInfo] = {
    val columnInfos: mutable.Builder[ColumnInfo, ListBuffer[ColumnInfo]] = ListBuffer.newBuilder[ColumnInfo]
    while (rs.next()) {
      val columnInfo = new ColumnInfo()
      columnInfo.columnKey = rs.getString("COLUMN_KEY")
      columnInfo.columnName = rs.getString("COLUMN_NAME")
      columnInfo.dataType = rs.getString("DATA_TYPE")
      columnInfo.extra = rs.getString("EXTRA")
      columnInfo.columnComment = rs.getString("COLUMN_COMMENT")
      val yesOrNo = rs.getString("IS_NULLABLE")
      columnInfo.isNullAble = if (yesOrNo == "YES") true else false
      columnInfos += columnInfo
    }
    columnInfos.result().toList
  }

  private def createEntityFile() = /*new File(getProject.getPath +"/src/main/java/" + packageName.replaceAll(".", File.pathSeparator))*/ new File(s"./${tableName.toClassCamel}.java")

  private def createAll(columnInfos: List[ColumnInfo]): String =
    s"""
       |package ${packageName};
       |
       |${createImportList}
       |${author}
       |@Entity
       |@Table(name="${tableName}")
       |public class ${tableName.toClassCamel} implements java.io.Serializable {
       |${createColumnFields(columnInfos)}
       |
       |${getterAndSetter.result().mkString(lineSeparator)}
       |}
       |""".stripMargin

  private def createColumnFields(columnInfos: List[ColumnInfo]): String = {
    columnInfos.map(columnInfo => {
      fieldNameBuilder(columnInfo)
    }).mkString("")
  }

  private def mysqlTypeConverter(dataType: String): String = {
    dataType match {
      case "varchar" => "String"
      case "int" => "Integer"
      case "smallint" => "Integer"
      case "char" => "String"
      case "decimal" => "BigDecimal"
      case "datetime" => "Date"
      case "date" => "Date"
      case "timestamp" => "Timestamp"
      case "tinyint" => "Integer"
      case _ => ""
    }
  }

  private def fieldNameBuilder(columnInfo:ColumnInfo): String = {
    val fieldType = mysqlTypeConverter(columnInfo.dataType)

    val columnFieldText = columnInfo.columnKey match {
      case "PRI" =>
        s"""
           |@Id
           |@GeneratedValue
           |@Column(name = "${columnInfo.columnName}",unique=true${if(!columnInfo.isNullAble) ",nullable = false" else ""})
           |private ${fieldType} ${columnInfo.columnName.toFieldCamel};
           |"""
      case "UNI" =>
        s"""
           |@Column(name = "${columnInfo.columnName}",unique=true${if(!columnInfo.isNullAble) ",nullable = false" else ""})
           |private ${fieldType} ${columnInfo.columnName.toFieldCamel};
         """
      case _ =>
        s"""
           |@Column(name = "${columnInfo.columnName}"${if(!columnInfo.isNullAble) ",nullable = false" else ""})
           |private ${fieldType} ${columnInfo.columnName.toFieldCamel};
         """
    }
    getterAndSetter += createGetterAndSetter(columnInfo.columnName, fieldType)
    fieldComment(columnInfo.columnComment) + columnFieldText.replaceAll("\\|", "\t").replace("   ", "")
  }

  private def fieldComment(columnComment: String): String = {
    s"""
       |\t/**
       |\t  * ${columnComment}
       |\t  */
     """.stripMargin
  }

  private def createGetterAndSetter(columnName: String, fieldType: String): String = {
    val t1 = columnName.toClassCamel
    val t2 = columnName.toFieldCamel
    s"""
       |${"\t"}public void set${t1}(${fieldType} ${t2}){
       |${"\t\t"}this.${t2}=${t2};
       |${"\t"}}
       |
       |${"\t"}public ${fieldType} get${t1}(){
       |${"\t\t"}return this.${t2};
       |${"\t"}}
     """.stripMargin
  }

  private def createImportList(): String = {
    importList.map(x => s"import ${x};").mkString(lineSeparator)
  }

  /**
    *
    */
  private def author(): String = {
    s"""
       |/**
       |  *
       |  * @author jimmy plugin
       |  * created_at ${new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())}
       |  *
       |  */
     """.stripMargin
  }

  def main(args: Array[String]): Unit = {
    generator()
  }
}
