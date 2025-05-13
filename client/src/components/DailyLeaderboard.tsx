/**
 * DailyLeaderboard.tsx
 *
 * This component displays the daily leaderboard with player rankings, usernames, and Elo scores.
 * It fetches leaderboard data from the backend using the current user's username.
 * 
 * Dependencies:
 * - useUser() from Clerk for accessing the current authenticated user
 * - getDailyLeaderboard() from utils/api for retrieving leaderboard data
 * - CSS styling from Leaderboard.css
 */

import { useEffect, useState } from 'react';
import '../styles/Leaderboard.css';
import { getDailyLeaderboard } from "../utils/api";
import { useUser } from '@clerk/clerk-react';

// Structure for a leaderboard entry
interface LeaderboardEntry {
  rank: number;
  username: string;
  elo: number;
}

export function DailyLeaderboard() {
  // State to store leaderboard data
  const [dailyLeaderboardData, setDailyLeaderboardData] = useState<LeaderboardEntry[]>([]);
  
  // Get the currently logged-in user
  const { user } = useUser();

  // Fetch leaderboard data when user info is available
  useEffect(() => {
    if (user?.username) {
        getDailyLeaderboard(user.username).then((json) => {
            if (json.result === "success") {
              setDailyLeaderboardData(json.leaderboard);
            }
        });
    }
  }, [user]);

  // Render the leaderboard table
  return (
    <div className="leaderboard-container">
      <h2 aria-label="leaderboard-header">Leaderboard</h2> 

      <table className="leaderboard-table">
        <thead>
          <tr>
            <th tabIndex={0} aria-label='Rank'>Rank</th>
            <th tabIndex={0} aria-label='Username'>Username</th>
            <th tabIndex={0} aria-label='Score'>Score</th>
          </tr>
        </thead>
        <tbody>
          {dailyLeaderboardData.map((user) => (
            <tr key={user.username}>
              <td tabIndex={0} aria-label={`Rank: ${user.rank}`}>
                {user.rank}
              </td>
              <td tabIndex={0} aria-label={`Username: ${user.username}`}>
                {user.username}
              </td>
              <td tabIndex={0} aria-label={`Score: ${user.elo}`}>
                {user.elo}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
