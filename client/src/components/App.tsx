import { useLocation, BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { Leaderboard } from './Leaderboard';
import { Dashboard } from './Dashboard';
import { addUser } from "../utils/api";
import '../styles/App.css';
import { SignedIn, SignedOut, SignInButton, SignOutButton, UserButton, useUser } from '@clerk/clerk-react';
import { DailyLeaderboard } from './DailyLeaderboard';
import { TopicLeaderboard } from './TopicLeaderboard';
import Trivia from './DailyTrivia';
import { EndlessTrivia } from './EndlessTrivia';

function MainApp() {
  const { user, isSignedIn } = useUser();
  const navigate = useNavigate();
  const location = useLocation();
  const currentPath = location.pathname;

  async function syncUser() {
    if (user?.id && user.username) {
      await addUser(user.id, user.username);
    }
  }

  useEffect(() => {
    if (user) {
      syncUser();
      navigate('/dashboard');
    }
  }, [user]);

  // Keyboard shortcuts: D = Dashboard, L = Leaderboard, T = Trivia, E = Endless, Q = Logout
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (!isSignedIn) return;

      switch (event.key.toLowerCase()) {
        case 'd':
          navigate('/dashboard');
          break;
        case 'l':
          navigate('/leaderboard');
          break;
        case 't':
          navigate('/trivia');
          break;
        case 'e':
          navigate('/endless');
          break;
        case 'q':
          document.getElementById('logout-btn')?.click();
          break;
        default:
          break;
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [navigate, isSignedIn]);

  const showHeader =
    currentPath.startsWith("/dashboard") || currentPath.startsWith("/leaderboard");

  return (
    <div className='App'>
      {showHeader && (
        <div className='App-header'>
          <SignedIn>
            {currentPath.startsWith("/dashboard") ? (
              <button className="leaderboard-button" onClick={() => navigate('/leaderboard')}>
                Leaderboard
              </button>
            ) : currentPath.startsWith("/leaderboard") ? (
              <button className="dashboard-button" onClick={() => navigate('/dashboard')}>
                Dashboard
              </button>
            ) : null}
          </SignedIn>
          <h1 aria-label='Quiz Whiz Header'>Quiz Whiz</h1>
          <SignedIn>
            <div className="user-button-wrapper">
              <UserButton />
              <SignOutButton>
                <button id="logout-btn" style={{ display: "none" }} />
              </SignOutButton>
            </div>
          </SignedIn>
        </div>
      )}

      <SignedOut>
        <SignInButton />
      </SignedOut>

      <SignedIn>
        <Routes>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/leaderboard" element={<Leaderboard />}>
            <Route path="daily" element={<DailyLeaderboard />} />
            <Route path="topic" element={<TopicLeaderboard />} />
          </Route>
          <Route path="/trivia" element={<Trivia />} />
          <Route path="/endless" element={<EndlessTrivia />} />
        </Routes>
      </SignedIn>
    </div>
  );
}

export default function App() {
  return (
    <Router>
      <MainApp />
    </Router>
  );
}
