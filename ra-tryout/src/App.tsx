import { Admin, Resource, ShowGuesser } from "react-admin";
import { Layout } from "./Layout";
import { dataProvider } from "./dataProvider";
import { UserEdit, UserList, UserShow } from "./users";
import {
  CompanyCreate,
  CompanyEdit,
  CompanyList,
  CompanyShow,
} from "./companies";
import {
  EmployeeCreate,
  EmployeeEdit,
  EmployeeList,
  EmployeeShow,
} from "./employees";

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
      create={EmployeeCreate}
    />
    <Resource
      name="users"
      list={UserList}
      show={UserShow}
      edit={UserEdit}
      recordRepresentation="displayName"
    />
  </Admin>
);
