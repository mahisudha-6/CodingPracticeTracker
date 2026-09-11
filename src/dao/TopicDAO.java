package dao;

import exception.DatabaseException;
import model.Topic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for 'topics' table.
 */
public class TopicDAO {

    /**
     * Retrieves all topics sorted by name.
     */
    public List<Topic> getAllTopics() throws DatabaseException {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM topics ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                topics.add(new Topic(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch topics: " + e.getMessage(), e);
        }
        return topics;
    }

    /**
     * Inserts a new topic and returns its generated primary key ID.
     */
    public int addTopic(String name) throws DatabaseException {
        String sql = "INSERT INTO topics (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, name.trim());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            // If topic already exists, let's look up its ID and return it
            if (e.getErrorCode() == 1062) { // Duplicate entry error code for MySQL
                return getTopicIdByName(name);
            }
            throw new DatabaseException("Failed to add topic: " + e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Looks up a topic ID by its exact name.
     */
    public int getTopicIdByName(String name) throws DatabaseException {
        String sql = "SELECT id FROM topics WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search topic by name: " + e.getMessage(), e);
        }
        return -1;
    }
}
