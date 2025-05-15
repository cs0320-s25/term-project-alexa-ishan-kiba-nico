/**
 * Dashboard.tsx
 *
 * This component displays a "Daily Game" button for the user.
 * It checks if the user has already played the daily trivia game using backend logic.
 * If they haven't, it allows them to start the game; otherwise, it shows an alert.
 *
 * Dependencies:
 * - useUser() from Clerk: gets the currently signed-in user's info
 * - useNavigate() from React Router: lets us switch pages programmatically
 * - checkPlayerStatus() from utils/api: asks the server if the user has played
 * - updatePlayerStatus() from utils/api: tells the server the user has now played
 */

import { useNavigate } from "react-router-dom";
import { checkPlayerStatus, getDailyWord, updatePlayerStatus } from "../utils/api";
import { useUser } from "@clerk/clerk-react";
import { useState, useEffect } from "react";
export function Dashboard() {
    const {user} = useUser();
    const userId = user?.id
    const [word, setWord] = useState<string>("");
    const navigate = useNavigate();


    
    useEffect(() => {
        async function fetchWord() {
          const dailyWord = await getDailyWord();
          setWord(dailyWord);
        }
      
        fetchWord();
      }, []); 
      



    

    const playDailyTriva = async () => {
        if (userId) {
            const hasPlayed = await checkPlayerStatus(userId); // Ask server if user already played
            console.log(hasPlayed);
            if (hasPlayed) {
                // Show alert if user already played
                alert("Sorry you have already played today's trivia game");
            } else {
                // Mark user as having played and start game
                updatePlayerStatus(userId, "true");
                navigate("/trivia");
            }
            }
    }

    const playEndlessTrivia = async () => {
        navigate("/endless");
    }

    


    return (
        <div>
            <button onClick={playDailyTriva}>Daily Game</button>
            <button onClick={playEndlessTrivia}>Endless mode</button>
            <p>Today's Daily Word: {word}</p>
            
        </div>
    );
}
