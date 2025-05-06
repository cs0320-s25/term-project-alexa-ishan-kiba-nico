const HOST = "http://localhost:3232";

async function queryAPI(
  endpoint: string,
  query_params: Record<string, string>
) {
  // query_params is a dictionary of key-value pairs that gets added to the URL as query parameters
  // e.g. { foo: "bar", hell: "o" } becomes "?foo=bar&hell=o"
  const paramsString = new URLSearchParams(query_params).toString();
  const url = `${HOST}/${endpoint}?${paramsString}`;
  const response = await fetch(url);
  console.log(response)
  if (!response.ok) {
    console.error(response.status, response.statusText);
  }
  return response.json();
}

export async function addUser(uid: string, username: string) {
  return await queryAPI("user", {
    uid: uid,
    username: username,
  });
}

export async function getDailyLeaderboard(username: string) {
  return await queryAPI("dailyleaderboard", {
    username: username,
  });
}

export async function getTopicLeaderboard() {
  return await queryAPI("topicleaderboard", {});
}
