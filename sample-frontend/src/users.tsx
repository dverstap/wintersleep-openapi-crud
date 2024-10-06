import {
  Datagrid,
  Edit,
  List,
  ReferenceField,
  ReferenceManyField,
  Show,
  SimpleForm,
  SimpleShowLayout,
  TextField,
  TextInput,
} from "react-admin";

const filters = [
  <TextInput source="q" label="Search" alwaysOn />,
  <TextInput source="email" label="Email" />,
  <TextInput source="firstName" label="First name" />,
  <TextInput source="lastName" label="Last name" />,
  <TextInput source="displayName" label="Display name" />,
];

export const UserList = () => (
  <List filters={filters}>
    <Datagrid>
      <TextField source="id" />
      <TextField source="email" />
      <TextField source="displayName" />
      <TextField source="firstName" />
      <TextField source="lastName" />
    </Datagrid>
  </List>
);

export const UserShow = () => (
  <Show>
    <SimpleShowLayout>
      <h2>User</h2>
      <TextField source="id" />
      <TextField source="email" />
      <TextField source="displayName" />
      <TextField source="firstName" />
      <TextField source="lastName" />
      <h2>Employees</h2>
      <ReferenceManyField reference="employees" target="userId">
        <Datagrid>
          <ReferenceField reference="employees" source="id" />
          <ReferenceField reference="companies" source="companyId" />
          <TextField source="active" />
        </Datagrid>
      </ReferenceManyField>
    </SimpleShowLayout>
  </Show>
);

export const UserEdit = () => {
  return (
    <Edit mutationMode={"pessimistic"}>
      <SimpleForm>
        <TextInput source="id" disabled={true} />
        <TextInput source="email" disabled={true} />
        <TextInput source="firstName" required={true} />
        <TextInput source="lastName" />
        <TextInput source="displayName" disabled={true} />
      </SimpleForm>
    </Edit>
  );
};
