package org.openmrs.mobile.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.openmrs.mobile.models.Datatype
import org.openmrs.mobile.models.Resource


@Entity(tableName = "concepts")
class ConceptEntity : Resource() {
    @PrimaryKey
    override var id: Long? = null
    @Embedded(prefix = "datatype_")
    var datatype: Datatype? = null
    @ColumnInfo(name = "name")
    var name: String? = null

}
