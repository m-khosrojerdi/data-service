package com.mapnadigital.insights.data.api;

public interface ApiClient {

    /** USER **/
    String USER_GET_BY_PHONE = "user-get-by-phone";
    String USER_GET_ALL_SERIAL_NUMBER_BY_ACCESS_LEVEL = "user-get-all-serial-by-access-level";
    String USER_GET_ALL_ACCESS_LEVEL = "user-get-all-access-level";
    String USER_GET_COMPANY = "user-get-company";
    String USER_GET_COMPANY_BY_ID = "user-get-company-by-id";
    String USER_PRINCIPALS = "user-principals";
    String USER_PROFILES = "user-profiles";
    String GROUP_GET_ALL = "group-get-all";

    /** SMS **/
    String SMS_PASSWORD = "sms-password";
    String SMS_OTP = "sms-otp";
    String SMS_INQUIRY_OPT = "sms-inquiry-otp";

    /** DEVICE **/
    String DEVICE_FIND_BY_SERIAL = "device-get-by-serial";
    String DEVICE_GET_BY_ID = "device-get-by-id";
    String DEVICE_GET_ALL_BY_DEVICE_MODEL_IDS = "device-get-all-by-device-model-ids";
    String DEVICE_GET_ALL_BY_DEVICE_TYPE_NAMES = "device-get-all-by-device-type-names";
    String DEVICE_GET_ALL_BY_IDS = "device-get-all-by-ids";
    String DEVICE_GET_ALL_BY_HUB_IDS = "device-get-all-by-hub-ids";
    String DEVICE_USER_COMPANY_DEVICES = "device-user-company-devices";

    /** DEVICE_TYPE **/
    String DEVICE_TYPE_GET_ALL_BY_SAFETY_LOCK = "device-type-get-all-by-safety-lock";
    String DEVICE_TYPE_GET_ALL_BY_IDS = "device-type-get-all-by-ids";
    String DEVICE_TYPE_GET_BY_ID = "device-type-get-by-id";
    String DEVICE_TYPE_GET_ALL_NAMES = "device-type-get-all-names";

    /** LORA_WAN **/
    String LORAWAN_SAVE_DEVICE_TYPE = "lorawan-save-device-type";
    String LORAWAN_UPDATE_DEVICE_TYPE = "lorawan-update-device-type";
    String LORAWAN_UPDATE_DEVICE = "lorawan-update-device";
    String LORAWAN_SAVE_DEVICES = "lorawan-save-devices";
    String LORAWAN_DELETE_DEVICE = "lorawan-delete";
    String LORAWAN_GET_DEVICE = "lorawan-get-device";


    /** USER_DEVICE **/
    String USER_DEVICE_GET_BY_ID = "user-device-get-by-id";
    String USER_DEVICE_FIND_ALL = "user-device-get-all";
    String USER_DEVICE_FIND_ALL_BY_USER_AND_DEVICE_IDS = "user-device-get-all-by-devices";
    String USER_DEVICE_GET_ALL_REFS = "user-device-get-all-refs";

    /** WALLET **/
    String WALLET_GET = "wallet-get";

    /** TOPIC **/
    String TOPIC_TREE_GET_BY_ID = "topic-tree-getById";
    String TOPIC_TREE_GET_ALL_BY_IDS = "topic-tree-get-all-by-ids";

    /** DEVICE MODEL **/
    String DEVICE_MODEL_GET = "device-model-get";

}
