package org.messtin.sequence.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messtin.sequence.domain.SequenceCache;
import org.messtin.sequence.exception.SequenceException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class SequenceRepository {
    private static final Logger logger = LogManager.getLogger(SequenceRepository.class);

    private final String sequenceName;
    private final DataSource dataSource;

    private static final String UPDATE = "UPDATE sequence SET current_offset = current_offset + ? WHERE sequence_name = ?";
    private static final String INSERT_IF_NOT_EXIST = "INSERT INTO sequence (sequence_name, current_offset) SELECT ?,? from dual WHERE NOT EXISTS (SELECT id FROM sequence WHERE sequence_name = ?)";
    private static final String SELECT = "SELECT current_offset FROM sequence WHERE sequence_name = ?";
    private static final int DEFAULT_OFFSET = 0;
    private static final long STEP = 500;

    public SequenceRepository(DataSource dataSource, String sequenceName) {
        this.sequenceName = sequenceName;
        this.dataSource = dataSource;
    }

    public SequenceCache nextSequenceCache() {
        logger.info("Start generate sequence for {}.", sequenceName);
        Connection conn = null;
        PreparedStatement insertIfNotExistStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement selectStmt = null;
        ResultSet selectResult = null;
        long currentOffset = 0;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            insertIfNotExistStmt = assemblePreparedStmt(conn, INSERT_IF_NOT_EXIST, Arrays.asList(sequenceName, DEFAULT_OFFSET, sequenceName));
            int rows = insertIfNotExistStmt.executeUpdate();

            updateStmt = assemblePreparedStmt(conn, UPDATE, Arrays.asList(STEP, sequenceName));
            int updateRows = updateStmt.executeUpdate();

            selectStmt = assemblePreparedStmt(conn, SELECT, Arrays.asList(sequenceName));
            selectResult = selectStmt.executeQuery();
            while (selectResult.next()) {
                currentOffset = selectResult.getLong(1);
                break;
            }

            conn.commit();

            return SequenceCache.builder()
                    .maxId(currentOffset)
                    .minId(currentOffset - STEP)
                    .sequenceName(sequenceName)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to generate sequence for {}, start rollback.", sequenceName, e);
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                logger.error("Failed to rollback for {}.", sequenceName);
            }

            throw new SequenceException(String.format("Failed to generate sequence for %s.", sequenceName));
        } finally {
            closeConnection(conn);
            closeStatement(insertIfNotExistStmt);
            closeStatement(updateStmt);
            closeStatement(selectStmt);
            closeResultSet(selectResult);
        }
    }

    private PreparedStatement assemblePreparedStmt(Connection conn, String sql, List<Object> params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
        return stmt;
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("Failed to close connection.", e);
        }
    }

    private void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            logger.error("Failed to close statement.", e);
        }
    }

    private void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.error("Failed to close result set.", e);
        }
    }
}
