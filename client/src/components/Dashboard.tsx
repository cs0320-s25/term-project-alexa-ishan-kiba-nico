import { useNavigate } from "react-router-dom";
import { checkPlayerStatus, getDailyWord, updatePlayerStatus } from "../utils/api";
import { useUser } from "@clerk/clerk-react";
import { useState, useEffect } from "react";

/**
 * Serves as the user's main landing page with options to start the daily or endless trivia games
 */
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
            const hasPlayed = await checkPlayerStatus(userId)
            console.log(hasPlayed)
            if (hasPlayed) {
                
                alert("Sorry you have already played today's trivia game")
                
            } else {
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
    )
}