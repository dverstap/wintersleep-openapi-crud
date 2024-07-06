import { Admin, Resource, ShowGuesser } from "react-admin";
import { Layout } from "./Layout";
import { dataProvider } from "./dataProvider";
import { UserList } from "./users";
import {
  CompanyCreate,
  CompanyEdit,
  CompanyList,
  CompanyShow,
} from "./companies";
import { EmployeeEdit, EmployeeList, EmployeeShow } from "./employees";

export const App = () => (
  <Admin layout={Layout} dataProvider={dataProvider}>
    <Resource
      name="companies"
      list={CompanyList}
      create={CompanyCreate}
      show={CompanyShow}
      edit={CompanyEdit}
    />
    <Resource
      name="employees"
      list={EmployeeList}
      show={EmployeeShow}
      edit={EmployeeEdit}
    />
    <Resource
      name="users"
      list={UserList}
      recordRepresentation="displayName"
      show={ShowGuesser}
    />
  </Admin>
);
