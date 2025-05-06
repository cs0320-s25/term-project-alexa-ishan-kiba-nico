import { useEffect, useState } from 'react';
import '../styles/Leaderboard.css';
import { getDailyLeaderboard } from "../utils/api";
import { useUser } from '@clerk/clerk-react';

interface LeaderboardEntry {
  rank: number;
  username: string;
  elo: number;
}

export function DailyLeaderboard() {
  const [dailyLeaderboardData, setDailyLeaderboardData] = useState<LeaderboardEntry[]>([]);
  const { user } = useUser();

  useEffect(() => {
    if (user?.username) {
        getDailyLeaderboard(user.username).then((json) => {
            if (json.result === "success") {
                setDailyLeaderboardData(json.leaderboard)
            }
        })
    }
  }, [user])

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