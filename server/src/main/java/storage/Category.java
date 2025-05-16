package storage;

/**
 * Stores category data for use in the endless mode leaderboard (category chosen, user username,
 * user streak)
 */
public record Category(String category, String username, int streak) {}
