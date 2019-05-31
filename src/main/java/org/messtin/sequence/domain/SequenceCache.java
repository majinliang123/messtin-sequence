package org.messtin.sequence.domain;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class SequenceCache {

    private final long minId;
    private final long maxId;
    private final AtomicLong currentId;
    private final Date updateTime;
    private final String sequenceName;

    private SequenceCache(long minId, long maxId, Date updateTime, String sequenceName) {
        this.minId = minId;
        this.maxId = maxId;
        this.updateTime = updateTime;
        this.sequenceName = sequenceName;
        this.currentId = new AtomicLong(this.minId);
    }

    public static SequenceCacheBuilder builder() {
        return new SequenceCacheBuilder();
    }

    public static class SequenceCacheBuilder {
        private long minId;
        private long maxId;
        private Date updateTime;
        private String sequenceName;

        public SequenceCacheBuilder minId(long minId) {
            this.minId = minId;
            return this;
        }

        public SequenceCacheBuilder maxId(long maxId) {
            this.maxId = maxId;
            return this;
        }

        public SequenceCacheBuilder updateTime(Date updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public SequenceCacheBuilder sequenceName(String sequenceName) {
            this.sequenceName = sequenceName;
            return this;
        }

        public SequenceCache build() {
            return new SequenceCache(minId, maxId, updateTime, sequenceName);
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public String getSequenceName() {
        return sequenceName;
    }
}
