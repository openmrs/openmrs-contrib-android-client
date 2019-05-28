package org.openmrs.mobile.databases.entities;

import org.openmrs.mobile.models.Resource;

import androidx.annotation.Nullable;
import androidx.room.Entity;

@Entity(tableName = "concepts")
public class ConceptEntity extends Resource {

    public ConceptEntity() { }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof ConceptEntity)) {
            return false;
        }
        ConceptEntity conceptEntity = (ConceptEntity) obj;
        return conceptEntity.uuid.equals(uuid)
                && conceptEntity.display.equals(display);
    }
}
