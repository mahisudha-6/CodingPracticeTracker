package service;

import dao.CodingProblemDAO;
import dao.TopicDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.CodingProblem;
import model.Topic;
import util.InputValidator;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Service layer coordinating validation logic and DB actions for Coding Problems and Topics.
 */
public class ProblemService {
    private final CodingProblemDAO problemDAO = new CodingProblemDAO();
    private final TopicDAO topicDAO = new TopicDAO();

    // -------------------------------------------------------------
    // Coding Problem Operations
    // -------------------------------------------------------------

    private void validateProblem(CodingProblem cp) throws ValidationException {
        if (InputValidator.isEmpty(cp.getTitle())) {
            throw new ValidationException("Problem Title cannot be empty.");
        }
        if (InputValidator.isEmpty(cp.getPlatform())) {
            throw new ValidationException("Platform cannot be empty.");
        }
        if (cp.getTopicId() <= 0) {
            throw new ValidationException("Please select a valid Topic.");
        }
        
        String diff = cp.getDifficulty();
        if (InputValidator.isEmpty(diff) || 
            (!diff.equalsIgnoreCase("Easy") && !diff.equalsIgnoreCase("Medium") && !diff.equalsIgnoreCase("Hard"))) {
            throw new ValidationException("Difficulty must be one of: Easy, Medium, Hard.");
        }

        String status = cp.getStatus();
        if (InputValidator.isEmpty(status) || 
            (!status.equalsIgnoreCase("Solved") && !status.equalsIgnoreCase("Pending") && !status.equalsIgnoreCase("Revising"))) {
            throw new ValidationException("Status must be one of: Solved, Pending, Revising.");
        }

        // Date Solved validation: must be set if solved, should be empty/null if pending
        if (status.equalsIgnoreCase("Solved") && cp.getDateSolved() == null) {
            throw new ValidationException("Date Solved is required when Status is set to 'Solved'.");
        }
        if (status.equalsIgnoreCase("Pending")) {
            cp.setDateSolved(null); // Force null for pending
        }
    }

    public boolean addProblem(CodingProblem problem) throws ValidationException, DatabaseException {
        validateProblem(problem);
        return problemDAO.add(problem);
    }

    public boolean updateProblem(CodingProblem problem) throws ValidationException, DatabaseException {
        validateProblem(problem);
        return problemDAO.update(problem);
    }

    public boolean deleteProblem(int problemId, int userId) throws DatabaseException {
        return problemDAO.delete(problemId, userId);
    }

    public CodingProblem getProblemById(int problemId, int userId) throws DatabaseException {
        return problemDAO.getById(problemId, userId);
    }

    public List<CodingProblem> searchAndFilterProblems(int userId, String searchTitle, String difficulty, Integer topicId) 
            throws DatabaseException {
        return problemDAO.searchAndFilter(userId, searchTitle, difficulty, topicId);
    }

    public Map<String, Integer> getDashboardStats(int userId) throws DatabaseException {
        return problemDAO.getDashboardStats(userId);
    }

    public List<CodingProblem> getRecentProblems(int userId, int limit) throws DatabaseException {
        return problemDAO.getRecentProblems(userId, limit);
    }

    public List<CodingProblem> getProblemsSolvedThisMonth(int userId) throws DatabaseException {
        return problemDAO.getProblemsSolvedThisMonth(userId);
    }

    public Map<String, Integer> getProblemsCountByTopic(int userId) throws DatabaseException {
        return problemDAO.getProblemsCountByTopic(userId);
    }

    public Map<String, Integer> getProblemsCountByDifficulty(int userId) throws DatabaseException {
        return problemDAO.getProblemsCountByDifficulty(userId);
    }

    public List<CodingProblem> getRevisionList(int userId) throws DatabaseException {
        return problemDAO.getRevisionList(userId);
    }

    /**
     * Gets the count of problems solved today.
     */
    public int getSolvedTodayCount(int userId) throws DatabaseException {
        return problemDAO.getSolvedTodayCount(userId);
    }

    /**
     * Gets the count of problems solved during the current calendar week (Reports screen).
     */
    public int getSolvedThisWeekCount(int userId) throws DatabaseException {
        return problemDAO.getSolvedThisWeekCount(userId);
    }

    /**
     * Gets the statistics count by Platform.
     */
    public Map<String, Integer> getProblemsCountByPlatform(int userId) throws DatabaseException {
        return problemDAO.getProblemsCountByPlatform(userId);
    }

    /**
     * Gets SOLVED-only counts grouped by Difficulty (Reports screen progress rows).
     */
    public Map<String, Integer> getSolvedCountByDifficulty(int userId) throws DatabaseException {
        return problemDAO.getSolvedCountByDifficulty(userId);
    }

    /**
     * Gets SOLVED-only counts grouped by Platform (Reports screen Platform Summary card).
     */
    public Map<String, Integer> getSolvedCountByPlatform(int userId) throws DatabaseException {
        return problemDAO.getSolvedCountByPlatform(userId);
    }

    /**
     * Gets the list of SOLVED problems for one platform (Platform Summary click-through popup).
     */
    public List<CodingProblem> getSolvedProblemsByPlatform(int userId, String platform) throws DatabaseException {
        return problemDAO.getSolvedProblemsByPlatform(userId, platform);
    }

    /**
     * Computes the user's current consecutive-day practice streak: the number
     * of calendar days in a row (ending today or yesterday) with at least one
     * problem solved. Returns 0 if the most recent solve was more than a day
     * ago, so a missed day correctly resets the streak.
     */
    public int getCurrentStreak(int userId) throws DatabaseException {
        List<Date> solvedDates = problemDAO.getDistinctSolvedDates(userId); // most recent first
        if (solvedDates.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate mostRecent = solvedDates.get(0).toLocalDate();

        // If the last solve wasn't today or yesterday, the streak is broken.
        long gapFromToday = ChronoUnit.DAYS.between(mostRecent, today);
        if (gapFromToday > 1) {
            return 0;
        }

        int streak = 1;
        LocalDate expectedPrevious = mostRecent.minusDays(1);
        for (int i = 1; i < solvedDates.size(); i++) {
            LocalDate current = solvedDates.get(i).toLocalDate();
            if (current.equals(expectedPrevious)) {
                streak++;
                expectedPrevious = current.minusDays(1);
            } else if (current.isBefore(expectedPrevious)) {
                break; // gap found — streak ends here
            }
            // if current.equals(mostRecent) somehow (shouldn't happen, dates are distinct), skip
        }
        return streak;
    }

    // -------------------------------------------------------------
    // Topic Operations
    // -------------------------------------------------------------

    public List<Topic> getAllTopics() throws DatabaseException {
        return topicDAO.getAllTopics();
    }

    public int addTopic(String name) throws ValidationException, DatabaseException {
        if (InputValidator.isEmpty(name)) {
            throw new ValidationException("Topic name cannot be empty.");
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2 || trimmed.length() > 50) {
            throw new ValidationException("Topic name must be between 2 and 50 characters.");
        }
        if (!trimmed.matches("^[a-zA-Z0-9&\\s\\-+_]+$")) {
            throw new ValidationException("Topic name contains invalid characters. Use letters, numbers, spaces, and simple symbols (&, -, +, _).");
        }
        return topicDAO.addTopic(trimmed);
    }
}
