import '../styles/App.css'
import { SignedIn, SignedOut, SignInButton, UserButton } from '@clerk/clerk-react';

function App() {
  return (
    <div className='App'>
      <div className='App-header'>
        <SignedIn>
          <button className="leaderboard-button">Leaderboard</button>
        </SignedIn>
        <h1 aria-label='Quiz Whiz Header'>Quiz Whiz</h1>
        <div className='Auth-buttons'>
          <SignedOut>
            <SignInButton />
          </SignedOut>
          <SignedIn>
            <div className="user-button-wrapper">
              <UserButton />
            </div>
          </SignedIn>
        </div>
      </div>
    </div>
  )
}

export default App
