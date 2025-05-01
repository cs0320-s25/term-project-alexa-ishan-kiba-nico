import { useEffect, useState } from 'react';
import '../styles/Leaderboard.css';
import { getLeaderboard } from "../utils/api";
import { useUser } from '@clerk/clerk-react';

interface LeaderboardEntry {
  rank: number;
  username: string;
  elo: number;
}

export function Leaderboard() {
  const [leaderboardData, setLeaderboardData] = useState<LeaderboardEntry[]>([]);
  const { user } = useUser();

  async function fetchLeaderboard() {
    getLeaderboard(user?.id).then((json) => {
      if (json.result === "success") {
        setLeaderboardData(json.leaderboard);
      }
    });
  }

  useEffect(() => {
    if (user?.id) {
      fetchLeaderboard();
    }
  }, [user]);

  return (
    <div className="leaderboard-container">
      <h2 aria-label="leaderboard-header">Leaderboard</h2> 

      <table className="leaderboard-table">
        <thead>
          <tr>
            <th>Rank</th>
            <th>Username</th>
            <th>Elo</th>
          </tr>
        </thead>
        <tbody>
          {leaderboardData.map((user, index) => (
            <tr key={user.username}>
              <td tabIndex={0} aria-label={`Row ${index + 1}, Column 1: ${user.rank}`}>
                {user.rank}
              </td>
              <td tabIndex={0} aria-label={`Row ${index + 1}, Column 2: ${user.username}`}>
                {user.username}
              </td>
              <td tabIndex={0} aria-label={`Row ${index + 1}, Column 3: ${user.elo}`}>
                {user.elo}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}