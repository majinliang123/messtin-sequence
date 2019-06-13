package org.messtin.sequence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messtin.sequence.domain.SequenceCache;
import org.messtin.sequence.repository.SequenceRepository;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicLong;

public class Sequence {

    private static final Logger logger = LogManager.getLogger(Sequence.class);

    private volatile SequenceCache sequenceCache;
    private SequenceRepository repository;


    public Sequence(DataSource dataSource, String sequenceName) {
        repository = new SequenceRepository(dataSource, sequenceName);
        logger.info("Sequence is initialized for sequenceName={}.", sequenceName);
    }

    public long nextId() {
        for (; ; ) {
            updateSequenceCache();

            SequenceCache cache = sequenceCache;
            long maxId = cache.getMaxId();
            AtomicLong currentId = cache.getCurrentId();
            long id = currentId.getAndIncrement();
            if (id < maxId) {
                logger.info("Get id {}.", id);
                return id;
            }
        }
    }

    private void updateSequenceCache() {
        if (sequenceCache == null || sequenceCache.getCurrentId().get() > sequenceCache.getMaxId()) {
            synchronized (this) {
                if (sequenceCache == null || sequenceCache.getCurrentId().get() > sequenceCache.getMaxId()) {
                    sequenceCache = repository.nextSequenceCache();
                }
            }
        }
    }
}
