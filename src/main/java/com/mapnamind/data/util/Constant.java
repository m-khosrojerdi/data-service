package com.mapnamind.data.util;

import java.util.List;

public interface Constant {

    String SIGNAL_SELECTOR_EXPORT_CONSUMER_QUEUE_NAME = "signal-selector-export";
    String SIGNAL_SELECTOR_EXPORT_CONSUMER_QUEUE_CONCURRENCY = "1-3";
    List<String> SIGNAL_SELECTOR_EXCEL_COLUMNS = List.of("date:تاریخ", "time:زمان", "value:مقدار");
    String SIGNAL_SELECTOR_EXCEL_FILE_NAME = "data.xlsx";
    String SIGNAL_SELECTOR_EXCEL_ZIP_FILE_NAME = "data.zip";
    String SIGNAL_SELECTOR_EXCEL_KILO_TOPICS_REGEX = ".*(ace|appe|reae)$";
}
