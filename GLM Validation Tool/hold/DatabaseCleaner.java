package hold;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseCleaner {

    private Connection con;
    private List<String> tableNames = new ArrayList<String>();
    private static int iterations=2;
    
    public DatabaseCleaner() {
    }

    public DatabaseCleaner clear() {
        try {
            establishConnection();

            analyseDatabase();

            boolean entriesLeft = clearDatabase();

            if (entriesLeft) {
                System.err.println("The specified amount of " + iterations + " iterations was not enough to clear the whole database.");
            }
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
               }
            }
        }

        return this;
    }

    private void establishConnection() throws SQLException {
    	
        try {
//            DataSource dataSource = (DataSource) new InitialContext()
//                    .lookup("jdbc/odr");
//            con = dataSource.getConnection();
    	    Class.forName("org.postgresql.Driver"); 

    		String url = "jdbc:postgresql://54.83.58.23/glm_vv"; 
    		con = DriverManager.getConnection(url, "postgres", "password"); 
    		con.setAutoCommit(false);
        } catch (SQLException ex) {
            throw new SQLException("An exception occured while trying to"
                    + "connect to the database.", ex);
        }
//        } catch (NamingException ex) {
//            throw new SQLException("An exception occured while trying to"
//                    + "connect to the database.", ex);
//        } 
        catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }

    private void analyseDatabase() throws SQLException {
        ResultSet result = null;
        try {
            DatabaseMetaData metaData = con.getMetaData();

            result = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (result.next()) {
                String tableName = result.getString("TABLE_NAME");
                
                // skip all tables
                tableNames.add(tableName);
//                if (!shouldBeSkipped(tableName)) {
//                    tableNames.add(tableName);
//                }
            }
        } catch (SQLException e) {
            throw new SQLException("An exception occured while trying to"
                    + "analyse the database.", e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
                }
            }

        }
    }

//    private boolean shouldBeSkipped(String name) {
//        for (String skipTableName : skipTables) {
//            if (skipTableName.equalsIgnoreCase(name)) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    private boolean clearDatabase() {
        boolean entriesLeft = true;

        for (int i = 0; i < iterations && entriesLeft; i++) {
            try {
				entriesLeft = clearTables();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            if (!entriesLeft) {
                System.err.println("No database entries left after" +
                        (i + 1) + "iteration(s)");
            }
        }

        return entriesLeft;
    }

    private boolean clearTables() throws SQLException {
        boolean entriesLeft = false;

        for (String tableName : tableNames) {
            entriesLeft = clearSingleTable(tableName) || entriesLeft;
        }

        return entriesLeft;
    }

    private boolean clearSingleTable(String tableName) throws SQLException {
        ResultSet result = null;
        try {
            boolean entriesLeft = false;

            result = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE).
                    executeQuery("SELECT * FROM ".concat(tableName));

            while (!result.isClosed() && result.next()) {
                entriesLeft = deleteRow(result) || entriesLeft;
            }


            return entriesLeft;
        } catch (SQLException ex) {
            throw new SQLException("Can't read table contents from table "
                    .concat(tableName), ex);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
                }
            }
        }
    }

    private boolean deleteRow(ResultSet result) {
        try {
            result.deleteRow();

            return false;
        } catch (SQLException ex) {
            return true;
        }
    }

    public DatabaseCleaner dropAllTables() {
        clear();

        try {
            try {
				establishConnection();

				analyseDatabase();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            dropTables();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                }
            }
        }

        return this;
    }

    private void dropTables() {

        for (int i = 0; i < iterations && !tableNames.isEmpty(); i++) {
            Iterator<String> tableNamesIt = tableNames.iterator();

            while (tableNamesIt.hasNext()) {
                try {
                    con.createStatement().executeUpdate("DROP TABLE "
                            .concat(tableNamesIt.next()));
                    tableNamesIt.remove();
                } catch (SQLException ex) {
                }
            }

        }
    }

    public static void bruteForceCleanup() {
        new DatabaseCleaner().clear();
    }

    public static void main(String[] args) {
        new DatabaseCleaner().clear();
    }
}