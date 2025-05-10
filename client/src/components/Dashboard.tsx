import { Link } from "react-router-dom";
import { updatePlayerStatus } from "../utils/api";
import { useUser } from "@clerk/clerk-react";

export function Dashboard() {
    const {user} = useUser();
    const userId = user?.id


    return (
        <div>
            <Link to="/trivia">

                <button onClick={()=>{
                    if (userId) 
                    updatePlayerStatus(userId, "")}}>Daily Game</button>
            </Link>
        </div>
    )
}