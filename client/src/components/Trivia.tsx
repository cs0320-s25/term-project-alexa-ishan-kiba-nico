import React, { useEffect, useState } from 'react';
import '../styles/Trivia.css';
import { useNavigate } from 'react-router-dom';


export function Trivia() {
   const [question, setQuestion] = useState<String>("");
   const [choices, setChoices] = useState<String[]>([]);
   const [answer, setAnswer] = useState<String>("");
   const [count, setCount] = useState<number>(1);
   const [correctAnswer, setCorrectAnswer] = useState<Boolean>(false);
   const [wrongAnswer, setWrongAnswer] = useState<Boolean>(false);
   const [isAnswered, setIsAnswered] = useState<boolean>(false);
   const navigate = useNavigate();


   async function fetchQuestionInformation() {
       try {
           const response = await fetch("http://localhost:3232/question?elo=30&topic=Brown University");
           if (!response.ok) {
               throw new Error("Failed to fetch question data");
           }
           const data = await response.json();
           if (data.result === "success") {
               setQuestion(data.question);
               setChoices(data.options);
               setAnswer(data.answer);
               
           }
       } catch (error: any) {
           console.log(error);
       }
   }


   function updateCount() {
       setCount(count => count + 1);
   }


   function returnToHome() {
       navigate("/dashboard");
   }


   function compareAnswer(choice: any) {
       if (answer == choice) {
        setCorrectAnswer(true);
           } else {
            setWrongAnswer(true);
           }
       }



   useEffect(() => {
       fetchQuestionInformation();
   }, []);


   return (
       <div className="trivia-container">
           <div className="counter-display">
               {count}/10
           </div>


           <div className="question-box">
               {question}
           </div>


           <div className="choices-grid">
               {choices.map((choice, index) => (
                   <button onClick={() => {compareAnswer(choice); console.log(answer); console.log(choice); setIsAnswered(true)}}key={index} disabled={isAnswered} className="choice-box">
                       {choice}
                   </button>
               ))}
           </div> 
           <div className="answer-buttons-container">
            {correctAnswer && (
            <button className="correct-button">Correct Answer</button> ) }
            {wrongAnswer && (
            <button className="wrong-button">Wrong Answer</button>
            )}
            </div>


           {/* New "Next Question" Button */}
           {count !== 10 ? (
               <div className="next-button-container">
                   <button onClick={() => { fetchQuestionInformation(); updateCount(); setCorrectAnswer(false); setWrongAnswer(false); setIsAnswered(false); }} className="next-button">Next Question</button>
               </div>
           ) : (
               <div className="next-button-container">
                   <button onClick={returnToHome} className="next-button">Return Home</button>
               </div>
           )}
       </div>
   );
}


export default Trivia;



