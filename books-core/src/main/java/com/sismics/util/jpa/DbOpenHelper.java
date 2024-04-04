package com.sismics.util.jpa;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.sismics.books.core.util.ConfigUtil;
import com.sismics.util.ResourceUtil;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.ConnectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A helper to update the database incrementally.
 *
 * @author jtremeaux
 */

public abstract class DbOpenHelper {
    /**
     * Logger.	
     */
    private static final Logger log = LoggerFactory.getLogger(DbOpenHelper.class);

    private final ConnectionHelper connectionHelper;
    
    private final SqlStatementLogger sqlStatementLogger;
    
    private final List<Exception> exceptions = new ArrayList<Exception>();

    private Formatter formatter;

    private boolean haltOnError;
    
    private Statement stmt;

    public DbOpenHelper(ServiceRegistry serviceRegistry) throws HibernateException {
        final JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
        connectionHelper = new SuppliedConnectionProviderConnectionHelper(jdbcServices.getConnectionProvider());

        sqlStatementLogger = jdbcServices.getSqlStatementLogger();
        formatter = (sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE).getFormatter();
    }
    
    private Connection getConnections() throws SQLException {
        connectionHelper.prepare(true);
        return connectionHelper.getConnection();
    }

    private Integer getOldVersion(Connection connection) {
        try {
        	stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select c.CFG_VALUE_C from T_CONFIG c where c.CFG_ID_C='DB_VERSION'");
            if (result.next()) {
                return Integer.parseInt(result.getString(1));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }

    private void executeScriptsAndUpdateDatabase(Integer oldVersion) throws Exception {
        if (oldVersion == null) {
            log.info("Executing initial schema creation script");
            onCreate();
            oldVersion = 0;
        }

        ResourceBundle configBundle = ConfigUtil.getConfigBundle();
        Integer currentVersion = Integer.parseInt(configBundle.getString("db.version"));
        log.info(MessageFormat.format("Found database version {0}, new version is {1}, executing database incremental update scripts", oldVersion, currentVersion));
        onUpgrade(oldVersion, currentVersion);
    }

    private void handleSQLException(SQLException e) {
        if (e.getMessage().contains("object not found")) {
            log.info("Unable to get database version: Table T_CONFIG not found");
        } else {
            log.error("Unable to get database version", e);
            exceptions.add(e);
        }
    }

    private void handleException(Exception e) {
        exceptions.add(e);
        log.error("Unable to complete schema update", e);
    }

    private void closeResources(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                exceptions.add(e);
                log.error("Unable to close resource", e);
            }
        }
    }

    public void open() {
        log.info("Opening database and executing incremental updates");

        Connection connection = null;
        Writer outputFileWriter = null;

        exceptions.clear();
        
        try {
            connection = getConnections();

            Integer oldVersion = getOldVersion(connection);

            executeScriptsAndUpdateDatabase(oldVersion);
            
            log.info("Database upgrade complete");
        } catch (Exception e) {
            handleException(e);
        } finally {
            closeResources(connection);
            closeResources(outputFileWriter);
        }

    }

    /**
     * Execute all upgrade scripts in ascending order for a given version.
     * 
     * @param version Version number
     * @throws Exception
     */
    protected void executeAllScript(final int version) throws Exception {
        List<String> fileNameList = ResourceUtil.list(getClass(), "/db/update/", new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String versionString = String.format("%03d", version);
                return name.matches("dbupdate-" + versionString + "-\\d+\\.sql");
            }
        });
        Collections.sort(fileNameList);
        
        for (String fileName : fileNameList) {
            if (log.isInfoEnabled()) {
                log.info(MessageFormat.format("Executing script: {0}", fileName));
            }
            InputStream is = getClass().getResourceAsStream("/db/update/" + fileName);
            executeScript(is);
        }
    }
    
    /**
     * Execute a SQL script. All statements must be one line only.
     * 
     * @param inputScript Script to execute
     * @throws IOException
     * @throws SQLException
     */
    protected void executeScript(InputStream inputScript) throws IOException, SQLException {
        List<String> lines = CharStreams.readLines(new InputStreamReader(inputScript));
        
        for (String sql : lines) {
            if (shouldExecuteSql(sql)) {
                executeSqlStatement(sql);
            }
        }
    }

    private boolean shouldExecuteSql(String sql) {
        return !Strings.isNullOrEmpty(sql) && !sql.startsWith("--");
    }

    private void executeSqlStatement(String sql) throws SQLException {
        String formattedSql = formatter.format(sql);
        try {
            log.debug(formattedSql);
            stmt.executeUpdate(formattedSql);
        } catch (SQLException e) {
            handleSqlExecutionError(sql, e);
        }
    }

    private void handleSqlExecutionError(String sql, SQLException e) throws SQLException {
        if (haltOnError) {
            closeStatement();
            throw new JDBCException("Error during script execution", e);
        }
        exceptions.add(e);
        logSqlExecutionError(sql, e);
    }

    private void closeStatement() throws SQLException {
        if (stmt != null) {
            stmt.close();
            stmt = null;
        }
    }

    private void logSqlExecutionError(String sql, SQLException e) {
        if (log.isErrorEnabled()) {
            log.error("Error executing SQL statement: {}", sql);
            log.error(e.getMessage());
        }
    }


    public abstract void onCreate() throws Exception;
    
    public abstract void onUpgrade(int oldVersion, int newVersion) throws Exception;
    
    /**
     * Returns a List of all Exceptions which occured during the export.
     *
     * @return A List containig the Exceptions occured during the export
     */
    public List<?> getExceptions() {
        return exceptions;
    }

    public void setHaltOnError(boolean haltOnError) {
        this.haltOnError = haltOnError;
    }

    /**
     * Format the output SQL statements.
     * 
     * @param format True to format
     */
    public void setFormat(boolean format) {
        this.formatter = (format ? FormatStyle.DDL : FormatStyle.NONE).getFormatter();
    }
}


