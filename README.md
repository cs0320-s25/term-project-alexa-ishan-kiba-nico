# QUIZ WHIZ: TERM PROJECT

## Project Details

### Description
Quiz Whiz is a trivia game which uses a Large Language Model (LLM) to generate trivia questions. The game includes two modes: a "daily game" mode, and an "endless" mode. In the "daily game" mode, the LLM picks a word, and this word is used by the LLM to generate ten questions on this unique topic. The mode can only be played once per day. In "endless" mode, you pick a topic and the LLM generates questions until you get one wrong. In both game modes, scoring is based on both timing and accuracy; the faster you answer a question, the more points you get. 

### Team Members
-cs login: **anshield**
-cs login: **ikhuran1**
-cs login: **nmromero**
-cs login: **scbhutia**

### Collaborators

### GitHub Repository
-**URL:** https://github.com/cs0320-s25/term-project-alexa-ishan-kiba-nico

## Design Choices

### Key Classes

#### Front End

- **Dashboard.tsx:** Serves as the user's main landing page with options to start the daily or endless trivia games
- **DailyTrivia.tsx:** Implements the timed 10-question daily trivia game using a dynamic topic and scoring system
- **EndlessTrivia.tsx:** Allows users to continuously answer questions on a chosen topic to build a streak until they get one wrong
- **Leaderboard.tsx:** Provides navigation to view both the daily and topic-based leaderboards
- **DailyLeaderboard.tsx:** Displays a ranked list of users based on their daily trivia performance
- **TopicLeaderboard.tsx:** Displays a ranked list of users based on their longest streaks in endless trivia by topic

#### Back End

Handlers: 
- **DailyTriviaLeaderboardHandler.java:** Handles "/dailyleaderboard" endpoint; handles HTTP requests to generate and return the daily leaderboard for a given username
- **DailyTriviaHandler.java:** Handles "/daily" endpoint; serves daily trivia questions for a specified topic from either OpenAI's Chat Completions API, or from the cache
- **EndlessTriviaHandler.java:** Handles "/endless" endpoint; generates a single trivia question on a given topic using OpenAI’s API and returns it as JSON
- **PlayedHandler.java:** Handles "/played" endpoint; processes get/set requests for whether a user has played the daily trivia using their UID
- **PointsHandler.java:** Handles "/points" endpoint; calculates and returns a player's updated score based on their current score and time taken to answer a question
- **RandomWordHandler.java:** Handles "/random" endpoint; returns a daily random word for trivia, generating a new one via OpenAI if not already stored for the current date
- **ScoreHandler.java:** Handles "/score" endpoint; updates a user's score (Elo) in storage based on provided uid and score query parameters
- **TopicHandler.java:** Handles "/topic" endpoint; deals with tracking and updating the highest user streaks for each trivia category.
- **TopicLeaderboardHandler.java:** Handles "/topicleaderboard" endpoint; returns the top streak holder for each trivia category from stored leaderboard data.
- **UserHandler.java**: Handles "/user" endpoing; creates or updates a user with a daily reset of their gameplay state and ELO score


### Relationships Between Classes/Interfaces
- The `Select` component holds an array of `histEntry` that represents the history of all submitted datasets.
- The `SelectInput` component updates this state by adding a new `histEntry` when a dataset is selected and submitted.
- The `SelectHistory` component reads the state and displays each dataset as a  table.

## Errors and Bugs

None

## Tests

Testing includes end to end tests with playwright that: 
- Assert that the dropdown and submit buttons appear only after logging in.
- Assert that after submission, the table displays the expected header and data from the mocked data.
- Assert that switching between datasets multiple times updates the displayed tables correctly.
- Assert that multiple tables are rendered and are scrollable. 
- Assert that logging out removes all loaded tables and that logging back in resets 
- Assert that error handling works by verifying that invalid datasets trigger appropriate error messages in bar chart view.
- Assert that a valid dataset renders a bar chart with a visible canvas element.
- Assert that switching between view types (tabular and bar chart) functions properly


**To run the program, simply type "npm start" into the terminal. To run test cases type "npm run test:e2e".**