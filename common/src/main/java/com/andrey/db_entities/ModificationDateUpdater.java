package com.andrey.db_entities;

import javax.persistence.PreUpdate;
import java.sql.Timestamp;
import java.util.Date;

public interface ModificationDateUpdater {


    Timestamp updateModificationDate(Timestamp now);

    @PreUpdate
    default Timestamp updateModificationDate() {
        return updateModificationDate(new Timestamp(new Date().getTime()));
    }
}
