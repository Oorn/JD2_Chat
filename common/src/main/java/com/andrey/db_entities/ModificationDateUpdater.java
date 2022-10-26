package com.andrey.db_entities;

import javax.persistence.PreUpdate;
import java.sql.Timestamp;
import java.util.Date;

public interface ModificationDateUpdater {


    void updateModificationDate();
}
