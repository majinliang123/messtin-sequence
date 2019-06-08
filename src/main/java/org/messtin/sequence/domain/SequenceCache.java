package org.messtin.sequence.domain;

import java.util.concurrent.atomic.AtomicLong;

public class SequenceCache {

    private final long minId;
    private final long maxId;
    private final AtomicLong currentId;
    private final String sequenceName;

    private SequenceCache(long minId, long maxId, String sequenceName) {
        this.minId = minId;
        this.maxId = maxId;
        this.sequenceName = sequenceName;
        this.currentId = new AtomicLong(this.minId);
    }

    public static SequenceCacheBuilder builder() {
        return new SequenceCacheBuilder();
    }

    public static class SequenceCacheBuilder {
        private long minId;
        private long maxId;
        private String sequenceName;

        public SequenceCacheBuilder minId(long minId) {
            this.minId = minId;
            return this;
        }

        public SequenceCacheBuilder maxId(long maxId) {
            this.maxId = maxId;
            return this;
        }

        public SequenceCacheBuilder sequenceName(String sequenceName) {
            this.sequenceName = sequenceName;
            return this;
        }

        public SequenceCache build() {
            return new SequenceCache(minId, maxId, sequenceName);
        }

    }

    public long getMinId() {
        return minId;
    }

    public long getMaxId() {
        return maxId;
    }

    public AtomicLong getCurrentId() {
        return currentId;
    }

    public String getSequenceName() {
        return sequenceName;
    }
}
