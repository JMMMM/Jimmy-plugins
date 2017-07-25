package com.jimmy.plugins.utils

object StringUtils {

  implicit class CamelConverter(from: String) {
    lazy val camelPattern = "([A-Za-z\\d]+)(_)?".r

    def toClassCamel =
      camelPattern.findAllMatchIn(from).map(_.group(0).replace("_", "")).map(str => str.charAt(0).toUpper + str.substring(1).toLowerCase).mkString("")

    def toFieldCamel = {
      val classCamel = toClassCamel
      classCamel.charAt(0).toLower + classCamel.substring(1)
    }
  }

}