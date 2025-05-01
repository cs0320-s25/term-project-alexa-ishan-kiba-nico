import { useEffect, useState } from 'react';
import { Leaderboard } from './Leaderboard';
import { Dashboard } from './Dashboard';
import { addUser } from "../utils/api"
import '../styles/App.css'
import { SignedIn, SignedOut, SignInButton, UserButton, useUser } from '@clerk/clerk-react';

function App() {
  const [leaderboard, setLeaderboard] = useState<boolean>(false)
  const { user } = useUser();

  const syncUser = async () => {
    if (user) {
      console.log(user.id)
      console.log(user.username)
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
      <SignedIn>
        {leaderboard ? <Leaderboard /> : <Dashboard />}
      </SignedIn>
    </div>
  )
}

export default App
