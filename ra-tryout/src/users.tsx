import { Datagrid, List, TextField } from "react-admin";

export const UserList = () => (
  <List>
    <Datagrid>
      <TextField source="id" />
      <TextField source="email" />
      <TextField source="displayName" />
      <TextField source="firstName" />
      <TextField source="lastName" />
    </Datagrid>
  </List>
);
