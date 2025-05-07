import React, { useEffect, useState } from 'react';
import '../styles/Trivia.css';
import { useNavigate } from 'react-router-dom';


export function Trivia() {
   const [question, setQuestion] = useState<String>("");
   const [choices, setChoices] = useState<String[]>([]);
   const [answer, setAnswer] = useState<String>("");
   const [count, setCount] = useState<number>(1);
   const [screen, setScreen] = useState<String>("white-screen");
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
               console.log(question)
               console.log(data)
               console.log(choices)
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
       if (answer === choice) {
           setScreen("green-screen");
           setTimeout(() => {
               setScreen("white-screen");
           }, 1000); // Flash green for 1 second
       } else {
           setScreen("red-screen");
           setTimeout(() => {
               setScreen("white-screen");
           }, 1000); // Flash red for 1 second
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
                   <button  key={index} className="choice-box">
                       {choice}
                   </button>
               ))}
           </div>


           {/* New "Next Question" Button */}
           {count !== 10 ? (
               <div className="next-button-container">
                   <button onClick={() => { fetchQuestionInformation(); updateCount(); }} className="next-button">Next Question</button>
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



