package com.mapnadigital.insights.data.model.view;

import java.io.Serializable;
import java.sql.Timestamp;


public interface DataProjection extends Serializable {

    Double getValue();
    Timestamp getDateTime();

}
