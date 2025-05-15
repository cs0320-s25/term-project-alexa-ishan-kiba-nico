import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

export function Leaderboard() {
  const navigate = useNavigate();

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'a') {
        navigate('daily'); // 'a' for daily leaderboard
      }
      if (e.key === 's') {
        navigate('topic'); // 's' for topic leaderboard
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  return (
    <div>
      <div>
        <Link to="daily">
          <button>Daily Leaderboard</button>
        </Link>
        <Link to="topic">
          <button>Topic Leaderboard</button>
        </Link>
      </div>
      <Outlet />
    </div>
  );
}
