package one.coffee.antispam;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetectionResponse {
    private static final String TIMESTAMP_JSON_PROPERTY = "timestamp";
    private static final String METADATA_JSON_PROPERTY = "metadata";
    private static final String RESULT_JSON_PROPERTY = "result";
    private static final String TASK_ERRORS_JSON_PROPERTY = "task_errors";
    private static final String ERRORS_JSON_PROPERTY = "errors";
    private static final String REQUEST_URL_JSON_PROPERTY = "request_url";
    private static final String REQUEST_TEXT_JSON_PROPERTY = "request_text";

    @JsonProperty(TIMESTAMP_JSON_PROPERTY)
    private final long timestamp;
    @JsonProperty(METADATA_JSON_PROPERTY)
    private final RequestMetadata requestMetadata;
    @JsonProperty(REQUEST_URL_JSON_PROPERTY)
    private final String requestUrl;
    @JsonProperty(REQUEST_TEXT_JSON_PROPERTY)
    private final String requestText;
    @JsonProperty(RESULT_JSON_PROPERTY)
    private final Map<String, DetectionLabel> result;
    @JsonProperty(TASK_ERRORS_JSON_PROPERTY)
    private final Map<String, String> taskErrors;
    @JsonProperty(ERRORS_JSON_PROPERTY)
    private final List<String> errors;

    @JsonCreator
    public DetectionResponse(@JsonProperty(TIMESTAMP_JSON_PROPERTY) final long timestamp,
                             @JsonProperty(METADATA_JSON_PROPERTY) final RequestMetadata requestMetadata,
                             @JsonProperty(RESULT_JSON_PROPERTY) final Map<String, DetectionLabel> result,
                             @JsonProperty(REQUEST_URL_JSON_PROPERTY) final String requestUrl,
                             @JsonProperty(REQUEST_TEXT_JSON_PROPERTY) final String requestText,
                             @JsonProperty(TASK_ERRORS_JSON_PROPERTY) final Map<String, String> taskErrors,
                             @JsonProperty(ERRORS_JSON_PROPERTY) final List<String> errors) {
        Validate.notNull(result);

        this.timestamp = timestamp;
        this.requestMetadata = requestMetadata;
        this.result = Collections.unmodifiableMap(new HashMap<>(result));
        this.requestText = requestText;
        this.requestUrl = requestUrl;
        this.taskErrors = taskErrors == null ? null : Collections.unmodifiableMap(new HashMap<>(taskErrors));
        this.errors = errors == null ? null : Collections.unmodifiableList(new ArrayList<>(errors));
    }

    public long getTimestamp() {
        return timestamp;
    }


    public RequestMetadata getRequestMetadata() {
        return requestMetadata;
    }


    public Map<String, DetectionLabel> getResult() {
        return result;
    }


    public String getRequestUrl() {
        return requestUrl;
    }


    public String getRequestText() {
        return requestText;
    }


    public Map<String, String> getTaskErrors() {
        return taskErrors;
    }


    public List<String> getErrors() {
        return errors;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DetectionResponse that = (DetectionResponse) o;
        return timestamp == that.timestamp &&
                Objects.equals(requestMetadata, that.requestMetadata) &&
                Objects.equals(requestUrl, that.requestUrl) &&
                Objects.equals(requestText, that.requestText) &&
                Objects.equals(result, that.result) &&
                Objects.equals(taskErrors, that.taskErrors) &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, requestMetadata, requestUrl, requestText, result, taskErrors, errors);
    }

    @Override
    public String toString() {
        return "DetectionResponse{" +
                "timestamp=" + timestamp +
                ", requestMetadata=" + requestMetadata +
                ", requestUrl='" + requestUrl + '\'' +
                ", requestText='" + requestText + '\'' +
                ", result=" + result +
                ", taskErrors=" + taskErrors +
                ", errors=" + errors +
                '}';
    }
}
