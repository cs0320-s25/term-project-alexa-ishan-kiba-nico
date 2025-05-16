# QUIZ WHIZ: TERM PROJECT

## Project Details

### Description
Quiz Whiz is a trivia game which uses a Large Language Model (LLM) to generate trivia questions. The game includes two modes: a "daily game" mode, and an "endless" mode. In the "daily game" mode, the LLM picks a word, and this word is used by the LLM to generate ten questions on this unique topic. The mode can only be played once per day. In "endless" mode, you pick a topic and the LLM generates questions until you get one wrong. In both game modes, scoring is based on both timing and accuracy; the faster you answer a question, the more points you get. 

This project uses OpenAI's Chat Completions API for question generation, Clerk for authentication purposes, and Firebase to store information. Please see "External Resources" section for additional integrated components. 

### GitHub Repository
- **URL:** https://github.com/cs0320-s25/term-project-alexa-ishan-kiba-nico

### Team Members
- cs login: **anshield**
- cs login: **ikhuran1**
- cs login: **nmromero**
- cs login: **scbhutia**

### Collaborators

OpenAI. (2025). ChatGPT (Dec 24 version) [Large language model].https://chat.openai.com/chat

ChatGPT was used with the prompt "explain how an Elo system works, and suggest a method by which it can be integrated into a trivia game" to plan for an implementation of an Elo rating system for use in Quiz Whiz. 



## Design Choices

### External Resources
- OpenAI's Chat Completions API
- Firebase
- Clerk
- Moshi
- React

### Key Classes

#### Front End

- `Dashboard.tsx:` Serves as the user's main landing page with options to start the daily or endless trivia games
- `DailyTrivia.tsx:` Implements the timed 10-question daily trivia game using a dynamic topic and scoring system
- `EndlessTrivia.tsx:` Allows users to continuously answer questions on a chosen topic to build a streak until they get one wrong
- `Leaderboard.tsx:` Provides navigation to view both the daily and topic-based leaderboards
- `DailyLeaderboard.tsx:` Displays a ranked list of users based on their daily trivia performance
- ` TopicLeaderboard.tsx:` Displays a ranked list of users based on their longest streaks in endless trivia by topic

#### Back End

- ` Server.java:` sets up server and routes enpoint calls to their respective handler. Each endpoint and handler is described below. 

- `DailyTriviaLeaderboardHandler.java:` Handles "/dailyleaderboard" endpoint; handles HTTP requests to generate and return the daily leaderboard for a given username
-` DailyTriviaHandler.java:` Handles "/daily" endpoint; serves daily trivia questions for a specified topic from either OpenAI's Chat Completions API, or from the cache
- `EndlessTriviaHandler.java:` Handles "/endless" endpoint; generates a single trivia question on a given topic using OpenAI’s API and returns it as JSON
- `PlayedHandler.java:` Handles "/played" endpoint; processes get/set requests for whether a user has played the daily trivia using their UID
- `PointsHandler.java:` Handles "/points" endpoint; calculates and returns a player's updated score based on their current score and time taken to answer a question
- `RandomWordHandler.java:` Handles "/random" endpoint; returns a daily random word for trivia, generating a new one via OpenAI if not already stored for the current date
- `ScoreHandler.java:` Handles "/score" endpoint; updates a user's score (Elo) in storage based on provided uid and score query parameters
-` TopicHandler.java:` Handles "/topic" endpoint; deals with tracking and updating the highest user streaks for each trivia category.
- `TopicLeaderboardHandler.java:` Handles "/topicleaderboard" endpoint; returns the top streak holder for each trivia category from stored leaderboard data.
- `UserHandler.java:` Handles "/user" endpoing; creates or updates a user with a daily reset of their gameplay state and ELO score

- `Ranker.java:` Ranks users by ELO for the current day and returns a leaderboard including the top 10 and the requesting user if outside the top 10
- `User.java:` Class to hold user information including username, current ELO score, date of last activity, and whether they've played the daily game
- `RankedUser.java:` Represents a user on the leaderboard with their rank, username, and ELO score
- `StorageInterface.java:`  Interacts with Firebase to provide methods to add, retrieve, and manage user data, category data, and daily trivia content


### Relationships Between Classes and Front/Back End

- The `ScoreHandler` uses `StorageInterface` to update a user's ELO score based on query parameters.
- The `TopicHandler` interacts with `StorageInterface` to update or initialize streak leader data for a specific category.
- The `TopicLeaderboardHandler` accesses category data via `StorageInterface` to build and return a leaderboard.
- The `UserHandler` reads and writes user data using `StorageInterface` to track usernames, ELOs, and daily activity.
- The `Ranker` retrieves all users from `StorageInterface`, sorts them by ELO, and assigns ranks using the User and `RankedUser` classes.
- The `RankedUser` class is used by `Ranker` to represent a user with an associated rank and ELO.
- The `User` class encapsulates user-specific data such as name, ELO score, date, and whether they’ve played today.
- The `StorageInterface` serves as a shared abstraction that all handlers and the `Ranker` class rely on for data persistence and retrieval.
- The front end sends HTTP requests with query parameters (e.g. uid, username, score) to specific backend routes handled by classes like `UserHandler`, `ScoreHandler`, and `TopicHandler`.
- The back end handlers parse these parameters using Spark’s Request API and respond with JSON-encoded results using Moshi.
- The front end receives these JSON responses and uses them to dynamically update UI elements such as leaderboards, user profiles, or streak tables.
- When a user submits their quiz score or completes a streak, the front end calls the appropriate endpoint (/score, /topic, etc.), which persists the update through StorageInterface.
- The front end retrieves leaderboard data by making GET requests to endpoints like /rankings or /topic-leaderboard, which are served by Ranker or TopicLeaderboardHandler.

## Errors and Bugs

N/A

## Tests

Testing is done both with mocked data and with actual API calls. This includes integration testing, end-to-end testing, and unit testing. For more information regarding tests, please see `MockData.java`, `MockedScorage.java`, and `RankerTest.java`.

## Running the Program

**To run the program: run the back end by starting the `Server.java` file. Start the front end with the "npm start" command in the terminal, from the "client" folder. To run test cases, type "npm run test:e2e".**

