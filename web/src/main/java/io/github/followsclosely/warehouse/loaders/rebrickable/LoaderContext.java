package io.github.followsclosely.warehouse.loaders.rebrickable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.github.followsclosely.warehouse.entity.LegoCategory;
import io.github.followsclosely.warehouse.entity.LegoColor;
import io.github.followsclosely.warehouse.entity.LegoTheme;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class LoaderContext {

    private final List<JobDetails> jobDetails = new ArrayList<>();

    @JsonIgnore
    private MapBackedCache<String, LegoColor> colorCache = new MapBackedCache<>();
    @JsonIgnore
    private MapBackedCache<String, LegoTheme> themeCache = new MapBackedCache<>();
    @JsonIgnore
    private MapBackedCache<String, LegoCategory> categoryCache = new MapBackedCache<>();

    public JobDetails newJob(String jobName) {
        JobDetails job = new JobDetails(jobName);
        jobDetails.add(job);
        return job;
    }

    public static class MapBackedCache<K, V> {
        private final Map<K, V> map = new HashMap<>();

        public MapBackedCache<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public V get(K key) {
            return map.get(key);
        }
    }

    @Getter
    @JsonPropertyOrder({"jobName", "startTime", "endTime", "duration", "counters", "changes"})
    public static class JobDetails {
        public final Counters counters = new Counters();
        private final String jobName;
        private final Instant startTime;
        @JsonProperty
        private final List<ChangeLog> changes = new ArrayList<>();
        private Instant endTime;

        public JobDetails(String jobName) {
            this.jobName = jobName;
            startTime = Instant.now();
        }

        public void logChange(String type, String id, String description) {
            changes.add(LoaderContext.ChangeLog.builder().entityType(type).entityId(id).changeDescription(description).build());
        }

        public void complete() {
            endTime = Instant.now();
        }

        @JsonProperty
        public Duration getDuration() {
            if (endTime != null) {
                return Duration.between(startTime, endTime);
            } else {
                return Duration.between(startTime, Instant.now());
            }
        }
    }

    @Getter
    @Builder
    public static class ChangeLog {
        private String entityId;
        private String entityType;
        private String changeDescription;
    }

    @Getter
    public static class Counters {
        private int processed = 0;
        private int skipped = 0;
        private int updated = 0;
        private int inserted = 0;

        public int incrementProcessed() {
            return ++processed;
        }

        public int incrementSkipped() {
            return ++skipped;
        }

        public int incrementUpdated() {
            return ++updated;
        }

        public int incrementInserted() {
            return ++inserted;
        }
    }
}
