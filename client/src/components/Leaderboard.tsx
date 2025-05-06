import { DailyLeaderboard } from "./DailyLeaderboard";
import { TopicLeaderboard } from "./TopicLeaderboard";
import { useState } from 'react';

enum Section {
    DAILY = "DAILY",
    TOPIC = "TOPIC",
    NONE = "NONE",
  }

export function Leaderboard() {
    const [section, setSection] = useState<Section>(Section.NONE);
    
    return (
        <div>
            <button onClick={() => setSection(Section.DAILY)}>
                Daily Leaderboard
            </button>
            <button onClick={() => setSection(Section.TOPIC)}>
                Topic Leaderboard
            </button>
            {section === Section.DAILY ? <DailyLeaderboard /> : null}
            {section === Section.TOPIC ? <TopicLeaderboard /> : null}
        </div>
    )
}