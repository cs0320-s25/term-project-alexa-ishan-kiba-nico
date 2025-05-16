import { useEffect, useRef, useState } from 'react';
import '../styles/Trivia.css';
import { useNavigate } from 'react-router-dom';
import { useUser } from '@clerk/clerk-react';
import { addDailyScore } from '../utils/api';

// Create the form for a question

export type Question = {
    question: string;
    answer: string;
    options: string[];
};
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
    



    // calculate the score from the backend base on the current score and time taken

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

    // call backend to generate a random topic or get current daily topic 

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
    
    

    
      


    // generate 10 questions for trivia mode or grab 10 cached questions

    async function fetchQuestionInformation() {
        try {
            const questionTopic = await fetchDailyTopic();
            const response = await fetch(`http://localhost:3232/daily?topic=${questionTopic}`);
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

    // increment the question number the user is on

    function updateCount() {
        setCount((count) => count + 1);
    }

    // allow the user to navigate back to the home page

    function returnToHome() {
        navigate("/dashboard");
    }

    // determine if the user selects the correct answer

    function compareAnswer(choice: string) {
        if (currentQuestion?.answer == choice) {
            setCorrectAnswer(true);
            return true;
        } else {
            setWrongAnswer(true);
            return false;
        }
    }


    // fetch 10 questions

    useEffect(() => {
        console.log("topic: " + topic);
        fetchQuestionInformation();
    }, []);

    // current question index

    const currentQuestion = questions[count];



    
    // build 10 second timer for question
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

    // stop the timer when a user runs out of time or selects an answer

    function stopTimer() {
        if (timerRef.current) {
            clearInterval(timerRef.current);
            timerRef.current = null;
        }
    }
    
      // Keyboard shortcuts:
      // 'n' → Next question (after answering)
      // 'r' → Return home (after game ends)
      useEffect(() => {
        const handleKeyDown = async (e: KeyboardEvent) => {
          if (e.key === 'n' && isAnswered && count < 9) {
            updateCount();
            setCorrectAnswer(false);
            setWrongAnswer(false);
            setIsAnswered(false);
          }
    
          if (e.key === 'r' && isAnswered && count >= 9) {
            if (user?.id) {
              await fetchScore();
              await addDailyScore(user.id, currentScore);
            }
            returnToHome();
          }
        };
    
        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
      }, [isAnswered, count, user, currentScore]);
    
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
                      if (compareAnswer(choice)) await fetchScore();
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
                        await addDailyScore(user.id, currentScore);
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
    