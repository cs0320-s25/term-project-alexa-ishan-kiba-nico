import { Link, Outlet } from 'react-router-dom';

export function Leaderboard() {
    
    return (
        <div>
            <div>
                <Link to="daily">
                    <button>Daily Leaderboard</button>
                </Link>
                <Link to="topic">
                    <button>Topic Leaderboard</button>
                </Link>
            </div>
            <Outlet />
        </div>
    )
}