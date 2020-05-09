package com.osinniy.school.obj.imp

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.osinniy.school.firebase.Docs
import java.util.*

object ImpMapper {
    fun createMap(imp: Important): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap(7)
        map[Docs.NAME] = imp.name
        map[Docs.TEXT] = imp.text
        map[Docs.CREATION_DATE] = Timestamp(imp.creationDate)
        map[Docs.EDIT_DATE] = Timestamp(imp.editDate)
        map[Docs.UID] = imp.uid
        map[Docs.ID] = imp.id
        map[Docs.IS_CHANGED] = imp.isChanged
        return map
    }

    fun restoreInstance(map: Map<String?, Any>): Important {
        return Important(map[Docs.NAME].toString(), map[Docs.TEXT].toString(),
                (map[Docs.CREATION_DATE] as Timestamp?)!!.toDate(),
                (map[Docs.EDIT_DATE] as Timestamp?)!!.toDate(), map[Docs.ID].toString(), map[Docs.UID].toString(),
                map[Docs.IS_CHANGED] as Boolean
        )
    }

    fun restoreInstance(doc: DocumentSnapshot): Important? {
        return if (doc.data == null) null else Important(
                doc.getString(Docs.NAME),
                doc.getString(Docs.TEXT),
                doc.getTimestamp(Docs.CREATION_DATE)!!.toDate(),
                doc.getTimestamp(Docs.EDIT_DATE)!!.toDate(),
                doc.id,
                doc.getString(Docs.UID),
                doc.getBoolean(Docs.IS_CHANGED)!!
        )
    }
}