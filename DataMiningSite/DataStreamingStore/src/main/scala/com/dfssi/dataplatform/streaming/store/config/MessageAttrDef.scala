package com.dfssi.dataplatform.streaming.store.config

import com.dfssi.dataplatform.streaming.store.utils.XmlUtils
import StreamingStoreConfig._
import scala.xml.Elem

class MessageAttrDef extends Serializable {
  var name: String = _
  var toFieldName: String = _

  def this(hiveTableEl: Elem) {
    this()
    loadDefFromEl(hiveTableEl)
  }

  def loadDefFromEl(hiveTableEl: Elem): Unit = {
    if (hiveTableEl == null) return

    name = XmlUtils.getAttrValue(hiveTableEl, CONFIG_ATTR_TAG_NAME)
    toFieldName =
      XmlUtils.getAttrValue(hiveTableEl, CONFIG_ATTR_TAG_TOFIELDNAME)
  }
}
