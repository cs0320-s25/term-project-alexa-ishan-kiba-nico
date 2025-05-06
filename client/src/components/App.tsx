import { BrowserRouter as Router, Routes, Route, useNavigate, Navigate } from 'react-router-dom';
import { useEffect } from 'react';
import { Leaderboard } from './Leaderboard';
import { Dashboard } from './Dashboard';
import { addUser } from "../utils/api"
import '../styles/App.css'
import { SignedIn, SignedOut, SignInButton, UserButton, useUser } from '@clerk/clerk-react';
import { DailyLeaderboard } from './DailyLeaderboard';
import { TopicLeaderboard } from './TopicLeaderboard';

function MainApp() {
  const { user } = useUser();
  const navigate = useNavigate();
  const currentPath = window.location.pathname;

  async function syncUser() {
    if (user) {
      if (user.id && user.username) {
        await addUser(user.id, user.username)
      }
    }
  }

  useEffect(() => {
    if (user) {
      syncUser();
    }
  }, [user])

  return (
    <div className='App'>
      <div className='App-header'>
        <SignedIn>
          {currentPath === "/dashboard" ? (
            <button className="leaderboard-button" onClick={() => 
              navigate('/leaderboard')
              }>Leaderboard</button>
             ) : (
            <button className="dashboard-button" onClick={() => 
              navigate('/dashboard')
              }>Dashboard</button>
            )}
        </SignedIn>
        <h1 aria-label='Quiz Whiz Header'>Quiz Whiz</h1>
        <SignedIn>
          <div className="user-button-wrapper">
            <UserButton />
          </div>
        </SignedIn>
      </div>
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
          <Route path="*" element={<Dashboard />} />
        </Routes>
      </SignedIn>
    </div>
  )
}

export default function App () {
  return (
    <Router>
      <MainApp />
    </Router>
  );
}
