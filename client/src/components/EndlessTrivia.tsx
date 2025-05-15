import { useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { Question } from "./DailyTrivia";
import { addTopicScore } from "../utils/api";
import { useNavigate } from "react-router-dom";

export function EndlessTrivia() {
  const [topic, setTopic] = useState<string>("");
  const [streak, setStreak] = useState<number>(0);
  const [question, setQuestion] = useState<Question | null>(null);
  const [correctAnswer, setCorrectAnswer] = useState<boolean>(false);
  const [wrongAnswer, setWrongAnswer] = useState<boolean>(false);
  const [isAnswered, setIsAnswered] = useState<boolean>(false);

  const { user } = useUser();
  const navigate = useNavigate();

  async function fetchQuestion() {
    try {
      const response = await fetch(`http://localhost:3232/endless?topic=${topic}`);
      if (!response.ok) {
        console.log("Failed to fetch question");
        return;
      }

      const data = await response.json();
      if (data.result === "success") {
        setQuestion(data.question);
      }
    } catch (error) {
      console.log(error);
    }
  }

  function compareAnswer(choice: string): boolean {
    const isCorrect = question?.answer === choice;
    setCorrectAnswer(isCorrect);
    setWrongAnswer(!isCorrect);
    return isCorrect;
  }

  async function endGame() {
    try {
      if (user?.username) {
        await addTopicScore(topic, user.username, streak.toString());
      }
    } catch (error) {
      console.error("Error ending game:", error);
    }
  }

  return (
    <div>
      <button
        onClick={async () => {
          await endGame();
          navigate("/dashboard");
        }}
      >
        End Game
      </button>

      {!question ? (
        <div>
          <input
            placeholder="Please enter a topic"
            value={topic}
            onChange={(e) => setTopic(e.target.value)}
          />
          <button onClick={fetchQuestion}>Play Game</button>
        </div>
      ) : (
        <div>
          <p>Streak: {streak}</p>
          <p>Current Question: {question.question}</p>

          {question.options.map((choice, index) => (
            <div key={index}>
              <button
                onClick={() => {
                  if (!isAnswered) {
                    const isCorrect = compareAnswer(choice);
                    setIsAnswered(true);
                    if (isCorrect) {
                      setStreak((prev) => prev + 1);
                    }
                  }
                }}
                disabled={isAnswered}
                className="choice-box"
              >
                {choice}
              </button>
            </div>
          ))}

          {wrongAnswer && <div>
            <p>Correct Answer: {question.answer}</p>
            <p>Game Over</p>
            
            </div>}

          {correctAnswer && (
            <div>
                <p>Correct!</p>
            <button
              onClick={async () => {
                await fetchQuestion();
                setCorrectAnswer(false);
                setWrongAnswer(false);
                setIsAnswered(false);
              }}
            >
              Next Question
            </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
