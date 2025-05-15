import { useEffect, useRef, useState } from 'react';
import '../styles/Trivia.css';
import { useNavigate } from 'react-router-dom';
import { useUser } from '@clerk/clerk-react';
import { addDailyScore } from '../utils/api';


export function Trivia() {
    const [count, setCount] = useState<number>(0);
    const [correctAnswer, setCorrectAnswer] = useState<Boolean>(false);
    const [wrongAnswer, setWrongAnswer] = useState<Boolean>(false);
    const [isAnswered, setIsAnswered] = useState<boolean>(false);
    const [questions, setQuestions] = useState<Question[]>([]);
    const [currentScore, setCurrentScore] = useState<string>("0");
    const [timeLeft, setTimeLeft] = useState<number>(10);
    const [timeElasped, setTimeElapsed] = useState<number>(0);
    const timerRef = useRef<NodeJS.Timeout | null>(null);
    const [topic, setTopic] = useState<String>("")
    const { user } = useUser();
    const navigate = useNavigate();
    



    type Question = {
        question: string;
        answer: string;
        options: string[];
    };

    async function fetchScore() {
        try {
            const response = await fetch(`http://localhost:3232/points?currentscore=${currentScore}&time=${timeElasped}`);
            if (!response.ok) {
                throw new Error("Failed to fetch score");
            }
            const data = await response.json();
            if (data.result == "success") {
                const stringScore = data.score;
                setCurrentScore(stringScore)
            }
        } catch (error: any) {
            console.log(error);
        }
    }

    async function fetchDailyTopic() {
        try {
            const response = await fetch("http://localhost:3232/random");
            if (!response.ok) {
                throw new Error("Failed to fetch topic for today");
            }
            const data = await response.json();
            if (data.result == "success") {
                console.log(data.word);
                return data.word;

            }
        } catch (error: any) {
            console.log(error);
        }
    }
    
    

    
      

    async function fetchQuestionInformation() {
        try {
            const questionTopic = await fetchDailyTopic();
            const response = await fetch(`http://localhost:3232/daily?elo=30&topic=${questionTopic}`);
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
            return true;
        } else {
            setWrongAnswer(true);
            return false;
        }
    }



    useEffect(() => {
        console.log("topic: " + topic);
        fetchQuestionInformation();
    }, []);

    const currentQuestion = questions[count];

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

            setTimeElapsed((prev) => {
                return prev + 1;
            })
        }, 1000);
    
        return () => clearInterval(timerRef.current!); 
    }, [currentQuestion]);

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
                                    
                                }
                            }
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
                        {wrongAnswer && 
                            <div>
                            <button className="wrong-button">Wrong Answer</button>
                            <p>The correct answer is {currentQuestion.answer}</p>
                            </div>}
                    </div>

                    {count < 9 && isAnswered? (
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
                            <button onClick={async () => {
                                
                                if (user?.id) {
                                await fetchScore();
                                await addDailyScore(user?.id, currentScore);
                                }
                                returnToHome()

                            }}  className="next-button">
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
