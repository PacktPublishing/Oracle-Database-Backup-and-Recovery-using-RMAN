package com.dom.benchmarking.swingbench.storedprocedures;


import com.dom.benchmarking.swingbench.event.JdbcTaskEvent;
import com.dom.benchmarking.swingbench.kernel.DatabaseTransaction;
import com.dom.benchmarking.swingbench.kernel.SwingBenchException;
import com.dom.benchmarking.swingbench.kernel.SwingBenchTask;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Map;
import java.util.logging.Logger;

import oracle.jdbc.OracleTypes;


public class newStoredProcedure extends DatabaseTransaction {
    private static final Logger logger = Logger.getLogger(newStoredProcedure.class.getName());
    int arraySize = 10;

    public newStoredProcedure() {
    }

    public void close() {
    }

    public void init(Map params) {
        arraySize = Integer.parseInt((String)params.get("ARRAYSIZE"));
    }

    public void execute(Map params) throws SwingBenchException {
        Connection connection = (Connection)params.get(SwingBenchTask.JDBC_CONNECTION);

        long executeStart = System.nanoTime();
        int[] dmlArray = null;

        try {
            long start = System.nanoTime();

            try {
                CallableStatement cs = connection.prepareCall("{? = call SB_SQLApply.UBS(?)}");
                cs.registerOutParameter(1, OracleTypes.INTEGER);
                cs.setInt(2, arraySize);
                cs.executeUpdate();
                logger.fine("number of rows inserted = " + cs.getInt(1));
                cs.close();
            } catch (SQLException se) {
                throw new SwingBenchException(se.getMessage());
            }

            processTransactionEvent(new JdbcTaskEvent(this, getId(), (System.nanoTime() - executeStart), true, 0, 0, 0, 0, 0, 0, 0));
        } catch (SwingBenchException ex) {
            processTransactionEvent(new JdbcTaskEvent(this, getId(), (System.nanoTime() - executeStart), false, 0, 0, 0, 0, 0, 0));
            throw new SwingBenchException(ex);
        } finally {
        }
    }
}
