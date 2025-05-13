/**
 * TopicLeaderboard.tsx
 *
 * This component displays a leaderboard showing top users by category and their current correct-answer streaks.
 * It fetches leaderboard data from the backend using getTopicLeaderboard().
 *
 * Dependencies:
 * - getTopicLeaderboard() from utils/api for retrieving topic leaderboard data
 * - CSS styling from Leaderboard.css
 */

import { useEffect, useState } from 'react';
import '../styles/Leaderboard.css';
import { getTopicLeaderboard } from "../utils/api";

// Structure for each leaderboard row
interface LeaderboardEntry {
  category: string;
  username: string;
  streak: number;
}

export function TopicLeaderboard() {
  // State to hold fetched leaderboard data
  const [topicLeaderboardData, setTopicLeaderboardData] = useState<LeaderboardEntry[]>([]);

  // Fetch topic leaderboard data once on component mount
  useEffect(() => {
    getTopicLeaderboard().then((json) => {
        if (json.result === "success") {
            setTopicLeaderboardData(json.leaderboard);
        }
    });
  }, []);

  // Render leaderboard as a table
  return (
    <div className="leaderboard-container">
      <h2 aria-label="leaderboard-header">Leaderboard</h2> 

      <table className="leaderboard-table">
        <thead>
          <tr>
            <th tabIndex={0} aria-label='Category'>Category</th>
            <th tabIndex={0} aria-label='Username'>Username</th>
            <th tabIndex={0} aria-label='Streak'>Streak</th>
          </tr>
        </thead>
        <tbody>
          {topicLeaderboardData.map((entry) => (
            <tr key={entry.category}>
              <td tabIndex={0} aria-label={`Category: ${entry.category}`}>
                {entry.category}
              </td>
              <td tabIndex={0} aria-label={`Username: ${entry.username}`}>
                {entry.username}
              </td>
              <td tabIndex={0} aria-label={`Questions Correct: ${entry.streak}`}>
                {entry.streak}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
