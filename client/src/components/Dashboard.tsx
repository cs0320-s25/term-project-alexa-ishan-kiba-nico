/**
 * Dashboard.tsx
 *
 * This component displays a "Daily Game" button for the user.
 * It checks if the user has already played the daily trivia game using backend logic.
 * If they haven't, it allows them to start the game; otherwise, it shows an alert.

 */

import { useNavigate } from "react-router-dom";
import { checkPlayerStatus, getDailyWord, updatePlayerStatus } from "../utils/api";
import { useUser, SignOutButton } from "@clerk/clerk-react";
import { useState, useEffect } from "react";

export function Dashboard() {
  const { user } = useUser();
  const userId = user?.id;
  const [word, setWord] = useState<string>("");
  const navigate = useNavigate();

  // Fetch the "daily word" on page load
  useEffect(() => {
    async function fetchWord() {
      const dailyWord = await getDailyWord();
      setWord(dailyWord);
    }

    fetchWord();
  }, []);

  // Scoped keyboard shortcuts for dashboard
  useEffect(() => {
    const handleKeyDown = async (e: KeyboardEvent) => {
      switch (e.key.toLowerCase()) {
        case 'l':
          navigate("/leaderboard");
          break;
        case 'e':
          navigate("/endless");
          break;
        case 't':
          if (userId) {
            const hasPlayed = await checkPlayerStatus(userId);
            if (hasPlayed) {
              alert("Sorry you have already played today's trivia game");
            } else {
              await updatePlayerStatus(userId, "true");
              navigate("/trivia");
            }
          }
          break;
        case 'q':
          document.getElementById('logout-btn')?.click();
          break;
        default:
          break;
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [navigate, userId]);

  const playDailyTriva = async () => {
    if (userId) {
      const hasPlayed = await checkPlayerStatus(userId);
      console.log(hasPlayed);
      if (hasPlayed) {
        alert("Sorry you have already played today's trivia game");
      } else {
        updatePlayerStatus(userId, "true");
        navigate("/trivia");
      }
    }
  };

  const playEndlessTrivia = () => {
    navigate("/endless");
  };

  return (
    <div>
      <button onClick={playDailyTriva}>Daily Game</button>
      <button onClick={playEndlessTrivia}>Endless mode</button>
      <p>Today's Daily Word: {word}</p>

      {/* Hidden logout trigger for keyboard shortcut */}
      <SignOutButton>
        <button id="logout-btn" style={{ display: "none" }} />
      </SignOutButton>
    </div>
  );
}
