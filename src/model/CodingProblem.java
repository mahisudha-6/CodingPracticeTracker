package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model representing a Coding Problem solved or tracked by a user.
 */
public class CodingProblem {
    private int id;
    private int userId;
    private String title;
    private String platform;
    private String difficulty; // Easy, Medium, Hard
    private int topicId;
    private String topicName; // Join field helper
    private String status;     // Solved, Pending, Revising
    private Date dateSolved;
    private String notes;
    private Timestamp createdAt;

    public CodingProblem() {}

    public CodingProblem(int id, int userId, String title, String platform, String difficulty, 
                         int topicId, String topicName, String status, Date dateSolved, 
                         String notes, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.platform = platform;
        this.difficulty = difficulty;
        this.topicId = topicId;
        this.topicName = topicName;
        this.status = status;
        this.dateSolved = dateSolved;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateSolved() {
        return dateSolved;
    }

    public void setDateSolved(Date dateSolved) {
        this.dateSolved = dateSolved;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
