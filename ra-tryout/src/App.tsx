import {
  Admin,
  Resource,
  ListGuesser,
  EditGuesser,
  ShowGuesser,
} from "react-admin";
import { Layout } from "./Layout";
import { dataProvider } from "./dataProvider";

export const App = () => (
  <Admin layout={Layout} dataProvider={dataProvider}>
    <Resource name="companies" list={ListGuesser} />
    <Resource name="employees" list={ListGuesser} />
    <Resource name="users" list={ListGuesser} />
  </Admin>
);
