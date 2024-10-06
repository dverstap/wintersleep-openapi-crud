import simpleRestProvider from "ra-data-json-server";

let URL = "http://localhost:2080"; // Caddy
//let URL = "http://localhost:8080"; // Tomcat

export const dataProvider = simpleRestProvider(URL);
