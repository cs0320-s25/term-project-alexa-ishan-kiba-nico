
import { useEffect, useState } from 'react';
import '../styles/Leaderboard.css';
import { getTopicLeaderboard } from "../utils/api";

interface LeaderboardEntry {
  category: string;
  username: string;
  streak: number;
}

export function TopicLeaderboard() {
  const [topicLeaderboardData, setTopicLeaderboardData] = useState<LeaderboardEntry[]>([]);

  useEffect(() => {
    getTopicLeaderboard().then((json) => {
        if (json.result === "success") {
            setTopicLeaderboardData(json.leaderboard)
        }
    })
  }, [])

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