package one.coffee.antispam;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetectionRequest {
    public static final String TYPE = "type";
    private static final String TIMESTAMP_JSON_PROPERTY = "timestamp";
    private static final String TASKS_JSON_PROPERTY = "tasks";
    private static final String CONTENT_JSON_PROPERTY = "content";
    private static final String URL_JSON_PROPERTY = "url";
    private static final String TEXT_JSON_PROPERTY = "text";
    @JsonProperty(TIMESTAMP_JSON_PROPERTY)
    private final long timestamp;
    @JsonProperty(TASKS_JSON_PROPERTY)
    private final Set<String> tasks;
    @JsonProperty(URL_JSON_PROPERTY)
    private final String url;
    @JsonProperty(CONTENT_JSON_PROPERTY)
    private final String content;
    @JsonProperty(TEXT_JSON_PROPERTY)
    private final String text;

    @JsonProperty(TYPE)
    private final String type;

    @JsonCreator
    public DetectionRequest(@JsonProperty(value = TIMESTAMP_JSON_PROPERTY, required = true) final long timestamp,
                            @JsonProperty(value = TASKS_JSON_PROPERTY, required = true) final Set<String> tasks,
                            @JsonProperty(URL_JSON_PROPERTY) final String url,
                            @JsonProperty(CONTENT_JSON_PROPERTY) final String content,
                            @JsonProperty(TEXT_JSON_PROPERTY) final String text,
                            @JsonProperty(TYPE) final String type) {
        this.type = type;
        Validate.notEmpty(tasks);
        Validate.isTrue(StringUtils.isNotBlank(url) ||
                        StringUtils.isNotBlank(content) ||
                        StringUtils.isNotBlank(text),
                "One of [url, content, text] should be not empty");

        this.timestamp = timestamp;
        this.tasks = Collections.unmodifiableSet(new HashSet<>(tasks));
        this.url = url;
        this.content = content;
        this.text = text;
    }

    private DetectionRequest(final long timestamp,
                             final Set<String> tasks,
                             final String url,
                             final String text,
                             String type) {
        this.timestamp = timestamp;
        this.tasks = Collections.unmodifiableSet(new HashSet<>(tasks));
        this.url = url;
        this.type = type;
        this.content = "";
        this.text = text;
    }

    public DetectionRequest requestWithoutContent() {
        return new DetectionRequest(getTimestamp(), getTasks(), getUrl(), getText(), type);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Set<String> getTasks() {
        return tasks;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DetectionRequest that = (DetectionRequest) o;
        return timestamp == that.timestamp &&
                Objects.equals(tasks, that.tasks) &&
                Objects.equals(url, that.url) &&
                Objects.equals(content, that.content) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, tasks, url, content, text);
    }

    @Override
    public String toString() {
        return "DetectionRequest{" +
                "timestamp=" + timestamp +
                ", tasks=" + tasks +
                ", url='" + url + '\'' +
                ", content=base64[" + (content == null ? null : Integer.toString(content.length())) + ']' +
                ", text='" + text + '\'' +
                '}';
    }
}
