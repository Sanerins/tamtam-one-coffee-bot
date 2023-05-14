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
public class DetectionLabel {
    private static final String TAGS_JSON_PROPERTY = "tags";
    private static final String TEXT_JSON_PROPERTY = "text";
    private static final String STATS_JSON_PROPERTY = "stats";
    private static final String SCORED_TAGS_JSON_PROPERTY = "scored_tags";

    @JsonProperty(TAGS_JSON_PROPERTY)
    private final List<String> tags;
    @JsonProperty(value = SCORED_TAGS_JSON_PROPERTY)
    private final Map<String, Float> scoredTags;
    @JsonProperty(TEXT_JSON_PROPERTY)
    private final String text;
    @JsonProperty(STATS_JSON_PROPERTY)
    private final Map<String, Object> stats;

    @JsonCreator
    public DetectionLabel(@JsonProperty(TAGS_JSON_PROPERTY) final List<String> tags,
                          @JsonProperty(TEXT_JSON_PROPERTY) final String text,
                          @JsonProperty(STATS_JSON_PROPERTY) final Map<String, Object> stats,
                          @JsonProperty(value = SCORED_TAGS_JSON_PROPERTY) final Map<String, Float> scoredTags) {
        this.tags = tags == null ? null : Collections.unmodifiableList(new ArrayList<>(tags));
        this.text = text;
        this.stats = stats == null ? null : Collections.unmodifiableMap(new HashMap<>(stats));
        this.scoredTags = scoredTags == null ? null : Collections.unmodifiableMap(new HashMap<>(scoredTags));
    }

    public static DetectionLabel tagsLabel(final List<String> tags) {
        Validate.notNull(tags);

        return new DetectionLabel(tags, null, null, null);
    }

    public static DetectionLabel textLabel(final String text) {
        Validate.notBlank(text);

        return new DetectionLabel(null, text, null, null);
    }

    public List<String> getTags() {
        return tags;
    }

    public String getText() {
        return text;
    }

    public Map<String, Float> getScoredTags() {
        return scoredTags;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DetectionLabel that = (DetectionLabel) o;
        return Objects.equals(tags, that.tags) &&
                Objects.equals(text, that.text) &&
                Objects.equals(stats, that.stats) &&
                Objects.equals(scoredTags, that.scoredTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tags, text, stats, scoredTags);
    }

    @Override
    public String toString() {
        return "DetectionLabel{" +
                "tags=" + tags +
                ", text='" + text + '\'' +
                ", stats=" + stats +
                ", scoredTags=" + scoredTags +
                '}';
    }

    public Map<String, Object> getStats() {
        return stats;
    }
}

