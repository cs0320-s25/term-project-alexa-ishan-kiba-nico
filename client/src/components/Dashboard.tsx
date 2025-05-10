import { Link } from "react-router-dom";

export function Dashboard() {

    return (
        <div>
            <Link to="/trivia">
                <button >Daily Game</button>
            </Link>
        </div>
    )
}