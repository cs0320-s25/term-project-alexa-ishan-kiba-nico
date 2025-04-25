import { useState } from 'react';
import { Leaderboard } from './Leaderboard';
import { Dashboard } from './Dashboard';
import '../styles/App.css'
import { SignedIn, SignedOut, SignInButton, UserButton } from '@clerk/clerk-react';

function App() {
  const [leaderboard, setLeaderboard] = useState<boolean>(false)

  return (
    <div className='App'>
      <div className='App-header'>
        <SignedIn>
          {!leaderboard ? (
            <button className="leaderboard-button" onClick={() => 
              setLeaderboard(true)
              }>Leaderboard</button>
             ) : (
            <button className="dashboard-button" onClick={() => 
              setLeaderboard(false)
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
      {leaderboard ? <Leaderboard /> : <Dashboard />}
    </div>
  )
}

export default App
