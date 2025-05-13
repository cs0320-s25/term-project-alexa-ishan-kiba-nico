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
import { checkPlayerStatus, updatePlayerStatus } from "../utils/api";
import { useUser } from "@clerk/clerk-react";

export function Dashboard() {
    const { user } = useUser(); // Gets the current user info from Clerk
    const userId = user?.id;    // Extracts the user ID
    const navigate = useNavigate(); // Allows page navigation

    // Called when the user clicks the "Daily Game" button
    const handleClick = async () => {
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
    };

    return (
        <div>
            <button onClick={handleClick}>Daily Game</button>
        </div>
    );
}
