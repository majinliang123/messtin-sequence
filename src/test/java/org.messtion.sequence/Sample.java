package org.messtion.sequence;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.messtin.sequence.Sequence;
import org.mockito.runners.MockitoJUnitRunner;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(MockitoJUnitRunner.class)
public class Sample {
    private static final Logger logger = LogManager.getLogger(Sample.class);

    ExecutorService pool = Executors.newCachedThreadPool();

    private ComboPooledDataSource dataSource = new ComboPooledDataSource();

    private static final String SQL = "CREATE TABLE IF NOT EXISTS `sequence` (\n" +
            "    `id` SMALLINT NOT NULL auto_increment,\n" +
            "    `sequence_name` varchar(60) NOT NULL,\n" +
            "    `current_offset` BIGINT(20) UNSIGNED NOT NULL,\n" +
            "    UNIQUE (`sequence_name`)\n" +
            ");";

    @Before
    public void setUpDataSource() throws PropertyVetoException {
        dataSource.setDriverClass("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:./test");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        dataSource.setInitialPoolSize(3);
        dataSource.setMaxPoolSize(10);
        dataSource.setMinPoolSize(3);
        dataSource.setAcquireIncrement(3);
    }

    @Before
    public void setUpSequenceTable() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(SQL);
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    @Test
    public void testWithSingleThread() {
        Sequence sequence = new Sequence(dataSource, "abc");
        loop400Times(sequence);
    }

    @Test
    public void testWithMultiThread() throws InterruptedException {
        Sequence sequence = new Sequence(dataSource, "abc");
        CountDownLatch countDownLatch = new CountDownLatch(2);
        pool.execute(() -> {
            loop400Times(sequence);
            countDownLatch.countDown();
        });
        pool.execute(() -> {
            loop400Times(sequence);
            countDownLatch.countDown();
        });
        countDownLatch.await();
    }

    @Test
    public void testWithMultiSequence() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        pool.execute(() -> {
            loop400Times(new Sequence(dataSource, "abc"));
            countDownLatch.countDown();
        });
        pool.execute(() -> {
            loop400Times(new Sequence(dataSource, "abc"));
            countDownLatch.countDown();
        });
        countDownLatch.await();
    }

    private void loop400Times(Sequence sequence) {
        for (int i = 0; i < 400; i++) {
            logger.info("Current id is {}.", sequence.nextId());
        }
    }
}
