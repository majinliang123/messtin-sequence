package org.messtin.sequence.repository;

import org.messtin.sequence.domain.SequenceCache;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.sql.DataSource;
import java.util.Date;

public class SequenceRepository {

    private final String sequenceName;
    private final DataSource dataSource;
    private final JedisPool pool;


    private static final String UPDATE = "";
    private static final String SELECT = "";
    private static final String SEQUENCE_KEY = "SEQUENCE_KEY";
    private static final long STEP = 50;

    public SequenceRepository(DataSource dataSource, String sequenceName) {
        this.sequenceName = sequenceName;
        this.dataSource = dataSource;
        pool = new JedisPool(new JedisPoolConfig(), "localhost");
    }

    public SequenceCache nextSequenceCache() {

        try (Jedis jedis = pool.getResource()) {
            long maxId = jedis.incrBy(sequenceName, STEP);
            long minId = maxId - STEP;

            return SequenceCache.builder()
                    .maxId(maxId)
                    .minId(minId)
                    .updateTime(new Date())
                    .sequenceName(sequenceName)
                    .build();
        }
    }
}
