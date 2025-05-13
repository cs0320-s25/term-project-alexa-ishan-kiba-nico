import { useEffect, useRef, useState } from 'react';
import '../styles/Trivia.css';
import { useNavigate } from 'react-router-dom';
import { useUser } from '@clerk/clerk-react';
import { addDailyScore } from '../utils/api';

/**
 * Trivia.tsx
 * 
 * This component handles the full logic and interface for a daily trivia game.
 * It fetches questions, manages a countdown timer, processes answers,
 * and submits the player's final score.
 * 
 * Dependencies:
 * - useState, useEffect, useRef for managing game state and timers
 * - useUser from Clerk to get the current user ID
 * - useNavigate for routing back to dashboard
 * - addDailyScore API to save the score
 * - Custom CSS from Trivia.css
 */

export function Trivia() {
    // === State Variables ===
    const [count, setCount] = useState<number>(0); // question index
    const [correctAnswer, setCorrectAnswer] = useState<Boolean>(false);
    const [wrongAnswer, setWrongAnswer] = useState<Boolean>(false);
    const [isAnswered, setIsAnswered] = useState<boolean>(false); // prevents multiple answer clicks
    const [questions, setQuestions] = useState<Question[]>([]);
    const [currentScore, setCurrentScore] = useState<string>("0");
    const [timeLeft, setTimeLeft] = useState<number>(10);
    const [timeElasped, setTimeElapsed] = useState<number>(0);
    const timerRef = useRef<NodeJS.Timeout | null>(null);

    const { user } = useUser();
    const navigate = useNavigate();

    // === Question type structure ===
    type Question = {
        question: string;
        answer: string;
        options: string[];
    };

    // === API: Fetch updated score from server ===
    async function fetchScore() {
        try {
            const response = await fetch(`http://localhost:3232/points?currentscore=${currentScore}&time=${timeElasped}`);
            if (!response.ok) {
                throw new Error("Failed to fetch score");
            }
            const data = await response.json();
            if (data.result == "success") {
                const stringScore = data.score;
                setCurrentScore(stringScore);
            }
        } catch (error: any) {
            console.log(error);
        }
    }

    // === API: Fetch 10 daily trivia questions from backend ===
    async function fetchQuestionInformation() {
        try {
            const response = await fetch("http://localhost:3232/daily?elo=30&topic=NFL");
            if (!response.ok) {
                throw new Error("Failed to fetch question data");
            }
            const data = await response.json();
            if (data.result === "success") {
                setQuestions(data.questions);
            }
        } catch (error: any) {
            console.log(error);
        }
    }

    // === Increment to the next question index ===
    function updateCount() {
        setCount((count) => count + 1);
    }

    // === Navigate back to dashboard ===
    function returnToHome() {
        navigate("/dashboard");
    }

    // === Compare selected answer with correct answer ===
    function compareAnswer(choice: string) {
        if (currentQuestion?.answer == choice) {
            setCorrectAnswer(true);
            return true;
        } else {
            setWrongAnswer(true);
            return false;
        }
    }

    // === On mount, fetch questions ===
    useEffect(() => {
        fetchQuestionInformation();
    }, []);

    const currentQuestion = questions[count];

    // === Reset and manage countdown timer on each question ===
    useEffect(() => {
        setTimeLeft(10);
        setTimeElapsed(0);

        timerRef.current = setInterval(() => {
            if (!currentQuestion) return;

            setTimeLeft((prev) => {
                if (prev <= 1) {
                    clearInterval(timerRef.current!);
                    setIsAnswered(true);
                    setWrongAnswer(true);
                    return 0;
                }
                return prev - 1;
            });

            setTimeElapsed((prev) => prev + 1);
        }, 1000);

        return () => clearInterval(timerRef.current!);
    }, [currentQuestion]);

    // === Stop the timer manually ===
    function stopTimer() {
        if (timerRef.current) {
            clearInterval(timerRef.current);
            timerRef.current = null;
        }
    }

    return (
        <div className="trivia-container">
            <div className="counter-display">{count + 1}/10</div>
            <div className="score-box">Score: {currentScore}</div>
            <div>Time Left: {timeLeft}</div>

            {!currentQuestion ? (
                <div>Please Wait Game is Loading... </div>
            ) : (
                <>
                    <div className="question-box">{currentQuestion.question}</div>

                    <div className="choices-grid">
                        {currentQuestion.options.map((choice, index) => (
                            <button
                                onClick={async () => {
                                    stopTimer();
                                    compareAnswer(choice);
                                    setIsAnswered(true);
                                    if (compareAnswer(choice)) {
                                        await fetchScore();
                                    }
                                }}
                                key={index}
                                disabled={isAnswered}
                                className="choice-box"
                            >
                                {choice}
                            </button>
                        ))}
                    </div>

                    <div className="answer-buttons-container">
                        {correctAnswer && <button className="correct-button">Correct Answer</button>}
                        {wrongAnswer && (
                            <div>
                                <button className="wrong-button">Wrong Answer</button>
                                <p>The correct answer is {currentQuestion.answer}</p>
                            </div>
                        )}
                    </div>

                    {count < 9 && isAnswered ? (
                        <div className="next-button-container">
                            <button
                                onClick={() => {
                                    updateCount();
                                    setCorrectAnswer(false);
                                    setWrongAnswer(false);
                                    setIsAnswered(false);
                                }}
                                className="next-button"
                            >
                                Next Question
                            </button>
                        </div>
                    ) : (
                        <div className="next-button-container">
                            <button
                                onClick={async () => {
                                    if (user?.id) {
                                        await fetchScore();
                                        await addDailyScore(user?.id, currentScore);
                                    }
                                    returnToHome();
                                }}
                                className="next-button"
                            >
                                Return Home
                            </button>
                        </div>
                    )}
                </>
            )}
        </div>
    );
}

export default Trivia;
