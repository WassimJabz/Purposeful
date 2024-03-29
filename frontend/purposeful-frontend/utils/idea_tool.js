import fetchWrapper from "./fetch_wrapper";

export async function getDomains() {
  let domains = await fetchWrapper(`/api/domain`);
  let body = await domains.json();
  return body;
}
export async function getTopics() {
  let topics = await fetchWrapper(`/api/topic`);
  let body = await topics.json();
  return body;
}
export async function getTechs() {
  let techs = await fetchWrapper(`/api/tech`);
  let body = await techs.json();
  return body;
}

export async function removeIdea(id) {
  console.log("Removal");
  console.log(`/api/idea/` + id);
  let removal = await fetchWrapper(`/api/idea/` + id, null, "DELETE", null);
  var body = removal.status;
  return body;
}

export async function getIdeaFromID(id) {
  let response = await fetchWrapper(`/api/idea/` + id);
  let body = await response.json();
  return body;
}

export async function getMyIdeas() {
  let ideas = await fetchWrapper(`/api/idea/user`);
  let body = await ideas.json();
  return body;
}
