package one.coffee.antispam;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestMetadata {
    private static final String TYPE_JSON_PROPERTY = "type";
    private static final String OBJECT_METADATA_JSON_PROPERTY = "object_metadata";

    @JsonProperty(TYPE_JSON_PROPERTY)
    private final String type;
    @JsonProperty(OBJECT_METADATA_JSON_PROPERTY)
    private final String metadata;

    @JsonCreator
    public RequestMetadata(@JsonProperty(value = TYPE_JSON_PROPERTY, required = true) String type,
                           @JsonProperty(value = OBJECT_METADATA_JSON_PROPERTY, required = true) String metadata) {
        Validate.notBlank(type);
        Validate.notNull(metadata);

        this.type = type;
        this.metadata = metadata;
    }


    public String getType() {
        return type;
    }


    public String getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RequestMetadata that = (RequestMetadata) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, metadata);
    }

    @Override
    public String toString() {
        return "RequestMetadata{" +
                "type='" + type + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
