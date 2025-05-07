import React, { useEffect, useState } from 'react';
import '../styles/Trivia.css';
import { useNavigate } from 'react-router-dom';

export function Trivia() {
    const [count, setCount] = useState<number>(0);
    const [correctAnswer, setCorrectAnswer] = useState<Boolean>(false);
    const [wrongAnswer, setWrongAnswer] = useState<Boolean>(false);
    const [isAnswered, setIsAnswered] = useState<boolean>(false);
    const [questions, setQuestions] = useState<Question[]>([]);
    const navigate = useNavigate();

    type Question = {
        question: string;
        answer: string;
        options: string[];
    };

    async function fetchQuestionInformation() {
        try {
            const response = await fetch("http://localhost:3232/test?elo=30&topic=Basketball");
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

    function updateCount() {
        setCount((count) => count + 1);
    }

    function returnToHome() {
        navigate("/dashboard");
    }

    function compareAnswer(choice: string) {
        if (currentQuestion?.answer == choice) {
            setCorrectAnswer(true);
        } else {
            setWrongAnswer(true);
        }
    }

    useEffect(() => {
        fetchQuestionInformation();
    }, []);

    const currentQuestion = questions[count];

    return (
        <div className="trivia-container">
            <div className="counter-display">{count + 1}/10</div>

            {!currentQuestion ? (
                <div>Loading...</div>
            ) : (
                <>
                    <div className="question-box">{currentQuestion.question}</div>

                    <div className="choices-grid">
                        {currentQuestion.options.map((choice, index) => (
                            <button
                                onClick={() => {
                                    compareAnswer(choice);
                                    setIsAnswered(true);
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
                        {wrongAnswer && <button className="wrong-button">Wrong Answer</button>}
                    </div>

                    {count < 9 ? (
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
                            <button onClick={returnToHome} className="next-button">
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
