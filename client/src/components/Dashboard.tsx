import { useNavigate } from "react-router-dom";
import { checkPlayerStatus, updatePlayerStatus } from "../utils/api";
import { useUser } from "@clerk/clerk-react";

export function Dashboard() {
    const {user} = useUser();
    const userId = user?.id
    const navigate = useNavigate();

    const playDailyTriva = async () => {
        if (userId) {
            const hasPlayed = false;
            // const hasPlayed = await checkPlayerStatus(userId)
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
        </div>
    )
}