package com.mapnadigital.insights.data.documentation;

/**
 * Reusable Swagger/OpenAPI texts of the data REST API.
 *
 * <p>Every data controller answers with the platform envelope {@code {code, message, body}} and, because the
 * handlers return {@code ResponseBody} instead of {@code ResponseEntity}, always with HTTP 200: business outcomes
 * are reported in {@code code} rather than in the HTTP status. A successful call carries {@code code} 200, a
 * rejected payload carries {@code code} 400, a caller without one of the accepted roles carries {@code code} 403,
 * and a failed look-up carries one of the {@code 9001}-{@code 9012} business codes. Only framework level failures
 * change the HTTP status: a missing or invalid JWT is answered with HTTP 401 and an unexpected exception with
 * HTTP 500, which is why those two descriptions are shared by every operation instead of being repeated in each
 * controller.</p>
 *
 * <p>The security schemes referenced by the controllers ({@code JWT Token}, {@code Language} and {@code sys}) are
 * declared once by {@code com.mapnadigital.insights.web.configuration.SwaggerConfig} in {@code map-web}.</p>
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
public interface ApiDocumentation {

    /* ------------------------------------------------------------------ tags */

    String DATA_TAG = "Telemetry data";
    String DATA_TAG_DESCRIPTION = "Historical and aggregated values of the signals of the devices. A signal is "
            + "addressed by its MQTT topic (`<deviceType>/<serial>/data/<param>`), by the serial number and the "
            + "parameter of the device (`/data/get-data`), or by the compact `topic:isProperty` list "
            + "(`stringSignals`). The values are read through the `data_history.fnc_get_aggr_data` database function, so "
            + "the aggregation and the interval are resolved by the database. Every endpoint expects the JWT access "
            + "token, the `Accept-Language` header and the `sys` realm header, and answers with the "
            + "`{code, message, body}` envelope; business errors are returned with HTTP 200 and reported in `code`.";

    String TOPIC_TREE_TAG = "Topic tree";
    String TOPIC_TREE_TAG_DESCRIPTION = "Hierarchy of the signals of the devices. The tree is stored in "
            + "`data_history.topic_tree`: the root nodes are the device types, their children are the serial numbers and "
            + "the leaves are the `data` nodes that carry the unit and the description of a signal. The read paths "
            + "apply the visibility rules of the caller (the devices granted to the caller are resolved over REST "
            + "from `user-service` and `device-service`), so the same endpoint returns a different tree for an "
            + "administrator, a manufacturer and a customer. Every endpoint expects the JWT access token, the "
            + "`Accept-Language` header and the `sys` realm header, and answers with the `{code, message, body}` "
            + "envelope; business errors are returned with HTTP 200 and reported in `code`.";

    String BASELINE_TAG = "Baselines";
    String BASELINE_TAG_DESCRIPTION = "Baseline value of a signal, used by the charts to draw the reference line "
            + "of a comparison. A baseline is identified by the device, the topic tree node and the interval, which "
            + "is the unique key of `data_history.device_baseline_values`. The device is resolved from the topic over REST "
            + "(`device-service`), so the module stores only its identifier. Every endpoint expects the JWT access "
            + "token and the `Accept-Language` header, and answers with the `{code, message, body}` envelope; "
            + "business errors are returned with HTTP 200 and reported in `code`.";

    String OFFLINE_DATA_TAG = "Offline data";
    String OFFLINE_DATA_TAG_DESCRIPTION = "Ingestion of the samples a client collected while it was offline. The "
            + "payload is forwarded to the `data_history.fnc_save_data` database function, which stores it exactly like a "
            + "live MQTT sample, so the offline samples take part in the aggregations and the exports. Every "
            + "endpoint expects the JWT access token and the `Accept-Language` header, and answers with the "
            + "`{code, message, body}` envelope; business errors are returned with HTTP 200 and reported in `code`.";

    /* ------------------------------------------------------- shared responses */

    String ENVELOPE_NOTE = "The response is wrapped in the standard envelope `{code, message, body}`; business "
            + "errors are returned with HTTP 200 and reported in `code`.";

    String VALIDATION_400 = "The request payload is rejected by the bean validation of the DTO; the message lists "
            + "the violated constraints and `code` is 400";

    String UNAUTHORIZED_401 = "Missing or invalid JWT access token";

    String FORBIDDEN_403 = "The caller does not have one of the roles accepted by the operation";

    String INTERNAL_ERROR_500 = "Unexpected server error";

    /* ------------------------------------------------------------- examples */

    String SIGNAL_SELECTOR_REQUEST_EXAMPLE = """
            {
              "startDate": "2026-11-01 00:00:00",
              "endDate": "2026-11-02 00:00:00",
              "signals": [
                {
                  "topic": "lw_boiler/LW-0000000001042/data/temperature",
                  "isProperty": "0",
                  "interval": "1 hour",
                  "aggregation": "avg",
                  "sampleCount": 24
                }
              ]
            }
            """;

    String SIGNAL_SELECTOR_STRING_REQUEST_EXAMPLE = """
            {
              "startDate": "2026-11-01 00:00:00",
              "endDate": "2026-11-02 00:00:00",
              "stringSignals": "lw_boiler/LW-0000000001042/data/temperature:0,lw_boiler/LW-0000000001042/data/pressure:0"
            }
            """;

    String SIGNAL_SELECTOR_RESPONSE_EXAMPLE = """
            {
              "code": 200,
              "message": "The receiving get signal operation was completed successfully",
              "body": {
                "startDate": "2026-11-01 00:00:00",
                "endDate": "2026-11-02 00:00:00",
                "values": [
                  {
                    "name": "lw_boiler/LW-0000000001042/data/temperature",
                    "effectiveInterval": "1 hour",
                    "unit": "C",
                    "baselineValue": 21.5,
                    "data": [
                      { "x": "2026-11-01 00:00:00.0", "y": 20.4 },
                      { "x": "2026-11-01 01:00:00.0", "y": 20.9 }
                    ]
                  }
                ]
              }
            }
            """;

    String SIGNAL_SELECTOR_EXPORT_RESPONSE_EXAMPLE = """
            {
              "code": 200,
              "message": "The exporting signal data was completed successfully",
              "body": {
                "startDate": "2026-11-01 00:00:00",
                "endDate": "2026-11-02 00:00:00",
                "exportUuid": "3f1c9a52-6a1e-4a0e-9d0f-2b7c8d9e0f11"
              }
            }
            """;

    String BASELINE_REQUEST_EXAMPLE = """
            {
              "topic": "lw_boiler/LW-0000000001042/data/temperature",
              "interval": "1 hour",
              "value": 21.5
            }
            """;

    String BASELINE_RESPONSE_EXAMPLE = """
            {
              "code": 200,
              "message": "The baseline get was completed successfully",
              "body": {
                "id": 17,
                "topic": "lw_boiler/LW-0000000001042/data/temperature",
                "interval": "1 hour",
                "value": 21.5
              }
            }
            """;

    String BASELINE_DELETE_REQUEST_EXAMPLE = """
            {
              "topic": "lw_boiler/LW-0000000001042/data/temperature",
              "interval": "1 hour"
            }
            """;

    String OFFLINE_DATA_REQUEST_EXAMPLE = """
            {
              "topic": "lw_boiler/LW-0000000001042/data/temperature",
              "payload": "{\\"value\\": 20.4}",
              "date": "2026-11-01 08:30:00"
            }
            """;

    String TOPIC_TREE_LIST_EXAMPLE = """
            {
              "code": 200,
              "message": "Requested signal tree sent",
              "body": [
                {
                  "id": 12,
                  "code": 12,
                  "topic": "lw_boiler",
                  "path": "lw_boiler",
                  "parentId": null,
                  "isProperty": 0,
                  "description": "Boiler",
                  "isVisible": true,
                  "unit": null,
                  "hasChildren": true,
                  "children": [
                    {
                      "id": 1042,
                      "code": 1042,
                      "topic": "LW-0000000001042",
                      "path": "lw_boiler/LW-0000000001042",
                      "parentId": 12,
                      "isProperty": 0,
                      "description": "Boiler of the plant",
                      "isVisible": true,
                      "unit": null,
                      "hasChildren": true,
                      "children": []
                    }
                  ]
                }
              ]
            }
            """;

    String TOPIC_TREE_EXAMPLE = """
            {
              "code": 200,
              "message": "Requested signal tree sent",
              "body": {
                "id": 31,
                "code": 31,
                "topic": "temperature",
                "path": "lw_boiler/LW-0000000001042/data/temperature",
                "parentId": 30,
                "isProperty": 1,
                "description": "Water temperature",
                "isVisible": true,
                "unit": "C",
                "hasChildren": false,
                "children": []
              }
            }
            """;

    String TOPIC_TREE_UPDATE_REQUEST_EXAMPLE = """
            {
              "id": 31,
              "topic": "temperature",
              "description": "Water temperature",
              "unit": "C",
              "isVisible": true
            }
            """;

    String DATE_EXAMPLE = """
            {
              "code": 200,
              "message": "Operation successfully done",
              "body": {
                "date": "2026-11-08"
              }
            }
            """;

}
