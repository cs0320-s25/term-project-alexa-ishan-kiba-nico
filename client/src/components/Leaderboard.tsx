/**
 * Leaderboard.tsx
 *
 * This component displays links to both the Daily and Topic leaderboards.
 * It uses nested routes to render the selected leaderboard view below the buttons.
 *
 * Keyboard Shortcuts:
 * - 'a' → Daily Leaderboard
 * - 's' → Topic Leaderboard
 * - 'd' → Back to Dashboard
 */

import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

/**
 * Provides navigation to view both the daily and topic-based leaderboards
 */
export function Leaderboard() {
  const navigate = useNavigate();

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'd') {
        navigate('/dashboard');
      }
      if (e.key === 'a') {
        navigate('/leaderboard/daily');
      }
      if (e.key === 's') {
        navigate('/leaderboard/topic');
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [navigate]);

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
