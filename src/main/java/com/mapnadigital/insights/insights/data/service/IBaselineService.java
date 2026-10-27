package com.mapnadigital.insights.insights.data.service;

import com.mapnadigital.insights.insights.data.view.BaselineView;

public interface IBaselineService {

    BaselineView get(String topic, String interval);

    BaselineView save(BaselineView dto);

    void delete(BaselineView dto);

    Double getValue(String topic, String interval);
}
