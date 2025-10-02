package com.example.usermanagement.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Tiny utility to remove failed Flyway migrations (success = 0) from flyway_schema_history.
 * Usage (from project root):
 *   ./mvnw -q compile exec:java -Dexec.mainClass=com.example.usermanagement.tools.FlywayDbRepairTool -Ddb.url="jdbc:mysql://localhost:3306/user_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" -Ddb.user=root -Ddb.password=
 */
public class FlywayDbRepairTool {
    public static void main(String[] args) {
        String url = System.getProperty("db.url");
        String user = System.getProperty("db.user");
        String pass = System.getProperty("db.password");

        if (url == null || user == null) {
            System.err.println("Missing required system properties: db.url and db.user");
            System.exit(2);
        }

        System.out.println("Connecting to DB: " + url + " as user=" + user);

        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            // Show current recent entries
            System.out.println("Current flyway_schema_history (latest 10):");
            try (PreparedStatement ps = c.prepareStatement("SELECT installed_rank, version, description, success, installed_on FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 10")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.printf("rank=%d, version=%s, desc=%s, success=%s, installed_on=%s\n",
                                rs.getInt("installed_rank"), rs.getString("version"), rs.getString("description"), rs.getBoolean("success"), rs.getTimestamp("installed_on"));
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not read flyway_schema_history: " + e.getMessage());
            }

            // Delete failed rows (success = 0)
            System.out.println("Deleting failed Flyway rows (success = 0)...");
            try (PreparedStatement del = c.prepareStatement("DELETE FROM flyway_schema_history WHERE success = 0")) {
                int deleted = del.executeUpdate();
                System.out.println("Deleted rows: " + deleted);
            }

            // Show entries again
            System.out.println("flyway_schema_history after repair (latest 10):");
            try (PreparedStatement ps2 = c.prepareStatement("SELECT installed_rank, version, description, success, installed_on FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 10")) {
                try (ResultSet rs = ps2.executeQuery()) {
                    while (rs.next()) {
                        System.out.printf("rank=%d, version=%s, desc=%s, success=%s, installed_on=%s\n",
                                rs.getInt("installed_rank"), rs.getString("version"), rs.getString("description"), rs.getBoolean("success"), rs.getTimestamp("installed_on"));
                    }
                }
            }

            System.out.println("Repair complete.");

        } catch (Exception ex) {
            System.err.println("Error during repair: " + ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(3);
        }
    }
}
