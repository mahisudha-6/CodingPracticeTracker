package dao;

import exception.DatabaseException;
import model.CodingProblem;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for 'coding_problems' table.
 */
public class CodingProblemDAO {

    /**
     * Maps a ResultSet row to a CodingProblem model.
     */
    private CodingProblem mapRow(ResultSet rs) throws SQLException {
        CodingProblem cp = new CodingProblem();
        cp.setId(rs.getInt("id"));
        cp.setUserId(rs.getInt("user_id"));
        cp.setTitle(rs.getString("title"));
        cp.setPlatform(rs.getString("platform"));
        cp.setDifficulty(rs.getString("difficulty"));
        cp.setTopicId(rs.getInt("topic_id"));
        cp.setStatus(rs.getString("status"));
        cp.setDateSolved(rs.getDate("date_solved"));
        cp.setNotes(rs.getString("notes"));
        cp.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Handle optional topic_name join
        try {
            cp.setTopicName(rs.getString("topic_name"));
        } catch (SQLException e) {
            // column not in result set, ignore
        }
        
        return cp;
    }

    /**
     * Inserts a new coding problem.
     */
    public boolean add(CodingProblem problem) throws DatabaseException {
        String sql = "INSERT INTO coding_problems (user_id, title, platform, difficulty, topic_id, status, date_solved, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, problem.getUserId());
            ps.setString(2, problem.getTitle());
            ps.setString(3, problem.getPlatform());
            ps.setString(4, problem.getDifficulty());
            ps.setInt(5, problem.getTopicId());
            ps.setString(6, problem.getStatus());
            ps.setDate(7, problem.getDateSolved());
            ps.setString(8, problem.getNotes());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add coding problem: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing coding problem.
     */
    public boolean update(CodingProblem problem) throws DatabaseException {
        String sql = "UPDATE coding_problems SET title = ?, platform = ?, difficulty = ?, topic_id = ?, status = ?, date_solved = ?, notes = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, problem.getTitle());
            ps.setString(2, problem.getPlatform());
            ps.setString(3, problem.getDifficulty());
            ps.setInt(4, problem.getTopicId());
            ps.setString(5, problem.getStatus());
            ps.setDate(6, problem.getDateSolved());
            ps.setString(7, problem.getNotes());
            ps.setInt(8, problem.getId());
            ps.setInt(9, problem.getUserId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update coding problem: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a coding problem for security validation (matching user_id).
     */
    public boolean delete(int id, int userId) throws DatabaseException {
        String sql = "DELETE FROM coding_problems WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete coding problem: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches a coding problem by ID.
     */
    public CodingProblem getById(int id, int userId) throws DatabaseException {
        String sql = "SELECT cp.*, t.name AS topic_name FROM coding_problems cp JOIN topics t ON cp.topic_id = t.id WHERE cp.id = ? AND cp.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.setInt(2, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve coding problem: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Performs dynamic search and filtering.
     */
    public List<CodingProblem> searchAndFilter(int userId, String searchTitle, String difficulty, Integer topicId) throws DatabaseException {
        List<CodingProblem> problems = new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            "SELECT cp.*, t.name AS topic_name FROM coding_problems cp " +
            "JOIN topics t ON cp.topic_id = t.id " +
            "WHERE cp.user_id = ?"
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
            sb.append(" AND cp.title LIKE ?");
            params.add("%" + searchTitle.trim() + "%");
        }
        if (difficulty != null && !difficulty.equalsIgnoreCase("All")) {
            sb.append(" AND cp.difficulty = ?");
            params.add(difficulty);
        }
        if (topicId != null && topicId > 0) {
            sb.append(" AND cp.topic_id = ?");
            params.add(topicId);
        }
        sb.append(" ORDER BY cp.created_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    problems.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error filtering coding problems: " + e.getMessage(), e);
        }
        return problems;
    }

    /**
     * Gets statistics counts for Dashboard cards.
     * Returns keys: "Total", "Solved", "Pending", "Revising"
     */
    public Map<String, Integer> getDashboardStats(int userId) throws DatabaseException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Total", 0);
        stats.put("Solved", 0);
        stats.put("Pending", 0);
        stats.put("Revising", 0);

        String sql = "SELECT status, COUNT(*) AS count FROM coding_problems WHERE user_id = ? GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                int total = 0;
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("count");
                    total += count;
                    
                    if (status.equalsIgnoreCase("Solved")) {
                        stats.put("Solved", count);
                    } else if (status.equalsIgnoreCase("Pending")) {
                        stats.put("Pending", count);
                    } else if (status.equalsIgnoreCase("Revising")) {
                        stats.put("Revising", count);
                    }
                }
                stats.put("Total", total);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch dashboard stats: " + e.getMessage(), e);
        }
        return stats;
    }

    /**
     * Gets the N most recent problems for the Dashboard.
     */
    public List<CodingProblem> getRecentProblems(int userId, int limit) throws DatabaseException {
        List<CodingProblem> problems = new ArrayList<>();
        String sql = "SELECT cp.*, t.name AS topic_name FROM coding_problems cp " +
                     "JOIN topics t ON cp.topic_id = t.id " +
                     "WHERE cp.user_id = ? ORDER BY cp.created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    problems.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch recent problems: " + e.getMessage(), e);
        }
        return problems;
    }

    /**
     * Reports: Problems solved this month.
     */
    public List<CodingProblem> getProblemsSolvedThisMonth(int userId) throws DatabaseException {
        List<CodingProblem> problems = new ArrayList<>();
        String sql = "SELECT cp.*, t.name AS topic_name FROM coding_problems cp " +
                     "JOIN topics t ON cp.topic_id = t.id " +
                     "WHERE cp.user_id = ? AND cp.status = 'Solved' " +
                     "AND MONTH(cp.date_solved) = MONTH(CURRENT_DATE()) " +
                     "AND YEAR(cp.date_solved) = YEAR(CURRENT_DATE()) " +
                     "ORDER BY cp.date_solved DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    problems.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch monthly solved problems: " + e.getMessage(), e);
        }
        return problems;
    }

    /**
     * Reports: Count of problems grouped by Topic.
     */
    public Map<String, Integer> getProblemsCountByTopic(int userId) throws DatabaseException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT t.name AS topic_name, COUNT(cp.id) AS count " +
                     "FROM topics t " +
                     "JOIN coding_problems cp ON t.id = cp.topic_id " +
                     "WHERE cp.user_id = ? " +
                     "GROUP BY t.id, t.name " +
                     "ORDER BY count DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("topic_name"), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch problems count by topic: " + e.getMessage(), e);
        }
        return stats;
    }

    /**
     * Reports: Count of problems grouped by Difficulty.
     */
    public Map<String, Integer> getProblemsCountByDifficulty(int userId) throws DatabaseException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT difficulty, COUNT(*) AS count FROM coding_problems WHERE user_id = ? GROUP BY difficulty";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("difficulty"), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch problems count by difficulty: " + e.getMessage(), e);
        }
        return stats;
    }

    /**
     * Reports: Revision List (problems with status = 'Revising').
     */
    public List<CodingProblem> getRevisionList(int userId) throws DatabaseException {
        List<CodingProblem> problems = new ArrayList<>();
        String sql = "SELECT cp.*, t.name AS topic_name FROM coding_problems cp " +
                     "JOIN topics t ON cp.topic_id = t.id " +
                     "WHERE cp.user_id = ? AND cp.status = 'Revising' " +
                     "ORDER BY cp.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    problems.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch revision list: " + e.getMessage(), e);
        }
        return problems;
    }


    /**
     * Gets the count of problems solved today by the user.
     */
    public int getSolvedTodayCount(int userId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM coding_problems WHERE user_id = ? AND status = 'Solved' AND date_solved = CURRENT_DATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch today solved count: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Gets the count of problems solved during the current calendar week (Reports screen).
     */
    public int getSolvedThisWeekCount(int userId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM coding_problems WHERE user_id = ? AND status = 'Solved' " +
                     "AND YEARWEEK(date_solved, 1) = YEARWEEK(CURRENT_DATE(), 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch this week's solved count: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Gets problem counts grouped by Platform for a user.
     */
    public Map<String, Integer> getProblemsCountByPlatform(int userId) throws DatabaseException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT platform, COUNT(*) AS count FROM coding_problems WHERE user_id = ? GROUP BY platform";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("platform"), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch problems count by platform: " + e.getMessage(), e);
        }
        return stats;
    }

    /**
     * Reports: Count of SOLVED problems grouped by Difficulty (numerator for the
     * "solved / total" progress rows on the Reports screen).
     */
    public Map<String, Integer> getSolvedCountByDifficulty(int userId) throws DatabaseException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT difficulty, COUNT(*) AS count FROM coding_problems WHERE user_id = ? AND status = 'Solved' GROUP BY difficulty";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("difficulty"), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch solved count by difficulty: " + e.getMessage(), e);
        }
        return stats;
    }

    /**
     * Reports: Count of SOLVED problems grouped by Platform (Platform Summary card).
     */
    public Map<String, Integer> getSolvedCountByPlatform(int userId) throws DatabaseException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT platform, COUNT(*) AS count FROM coding_problems WHERE user_id = ? AND status = 'Solved' GROUP BY platform";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("platform"), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch solved count by platform: " + e.getMessage(), e);
        }
        return stats;
    }

    /**
     * Reports: List of SOLVED problems for one specific platform (Platform Summary
     * click-through popup, shows exactly which problems were solved there).
     */
    public List<CodingProblem> getSolvedProblemsByPlatform(int userId, String platform) throws DatabaseException {
        List<CodingProblem> problems = new ArrayList<>();
        String sql = "SELECT cp.*, t.name AS topic_name FROM coding_problems cp " +
                     "JOIN topics t ON cp.topic_id = t.id " +
                     "WHERE cp.user_id = ? AND cp.status = 'Solved' AND cp.platform = ? " +
                     "ORDER BY cp.date_solved DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, platform);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    problems.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch solved problems for platform: " + e.getMessage(), e);
        }
        return problems;
    }

    /**
     * Returns every distinct date the user solved at least one problem,
     * most recent first — used to compute the practice streak.
     */
    public List<Date> getDistinctSolvedDates(int userId) throws DatabaseException {
        List<Date> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT date_solved FROM coding_problems " +
                     "WHERE user_id = ? AND status = 'Solved' AND date_solved IS NOT NULL " +
                     "ORDER BY date_solved DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dates.add(rs.getDate("date_solved"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch distinct solved dates: " + e.getMessage(), e);
        }
        return dates;
    }
}


