package com.andrey.db_entities;

import java.sql.Timestamp;
import java.util.Date;

public interface ModificationDateUpdater {

    Timestamp UpdateModificationDate(Timestamp now);
    default Timestamp UpdateModificationDate() {
        return UpdateModificationDate(new Timestamp(new Date().getTime()));
    }
}
