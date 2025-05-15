/**
 * Leaderboard.tsx
 *
 * This component displays links to both the Daily and Topic leaderboards.
 * It uses nested routes to render the selected leaderboard view below the buttons.
 */

import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

export function Leaderboard() {
  const navigate = useNavigate();

  // Keyboard shortcut: 'd' returns to dashboard
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'd') {
        navigate('/dashboard');
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
