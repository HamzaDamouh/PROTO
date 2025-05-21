package edu.ezip.ing1.pds.business.server;

import edu.ezip.commons.connectionpool.config.impl.ConnectionPoolImpl;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Saturation {
    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private static volatile boolean locked = false;
    private static Connection heldConnection;

    /** Hold the *given* connection, not a new one from the pool. */
    public static synchronized boolean lockForSaturation(
            ConnectionPoolImpl pool,
            Connection connection,
            int minutes) {
        if (locked) return false;
        if (connection == null) return false;

        locked = true;
        heldConnection = connection;

        scheduler.schedule(() -> {
            try {
                pool.release(heldConnection);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                locked = false;
                heldConnection = null;
            }
        }, minutes, TimeUnit.MINUTES);

        return true;
    }

    public static boolean isLocked() {
        return locked;
    }
}

