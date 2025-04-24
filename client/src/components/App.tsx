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
        <SignedIn>
          <div className="user-button-wrapper">
            <UserButton />
          </div>
        </SignedIn>
      </div>
      <SignedOut>
        <SignInButton />
      </SignedOut>
    </div>
  )
}

export default App
