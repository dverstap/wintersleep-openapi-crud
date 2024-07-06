import {
  BooleanField,
  BooleanInput,
  Button,
  Datagrid,
  DateField,
  DateTimeInput,
  Edit,
  List,
  ReferenceField,
  ReferenceInput,
  SaveButton,
  Show,
  SimpleForm,
  SimpleShowLayout,
  TextField,
  TextInput,
  Toolbar,
  useEditContext,
  useGetRecordId,
  useNotify,
  useRecordContext,
  useRedirect,
} from "react-admin";

export const EmployeeList = () => (
  <List>
    <Datagrid sort={{ field: "id", order: "ASC" }}>
      <TextField source="id" />
      <DateField source="lastActivatedAt" showTime={true} />
      <DateField source="lastDeActivatedAt" showTime={true} />
      <BooleanField source="active" />
      <ReferenceField source="userId" reference="users" />
      {/*<TextField source="userDisplayName" />*/}
      <ReferenceField source="companyId" reference="companies" />
      {/*<TextField source="companyName"/>*/}
    </Datagrid>
  </List>
);

export const EmployeeShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="id" />
      <DateField source="lastActivatedAt" showTime={true} />
      <DateField source="lastDeActivatedAt" showTime={true} />
      <BooleanField source="active" />
      <ReferenceField source="userId" reference="users" />
      <ReferenceField source="companyId" reference="companies" />
    </SimpleShowLayout>
  </Show>
);

interface EmployeeProps {
  active: boolean;
}

const EditToolbar = () => {
  let context = useEditContext();
  let r: EmployeeProps = context.record;
  const redirect = useRedirect();
  const notify = useNotify();
  if (context.isPending) {
    return null;
  }
  console.log(r);
  return (
    <Toolbar>
      <SaveButton />
      <SaveButton
        label="Re-invite"
        alwaysEnable={!r.active}
        disabled={r.active}
        //onClick={}
        mutationOptions={{
          onSuccess: (data) => {
            notify("TODO", {
              type: "info",
              messageArgs: { smart_count: 1 },
            });
            redirect(false);
          },
        }}
        type="button"
        variant="text"
      />
    </Toolbar>
  );
};

export const EmployeeEdit = () => {
  const recordId = useGetRecordId();
  return (
    <Edit>
      <SimpleForm toolbar={<EditToolbar />}>
        <TextInput source="id" disabled={true} />
        <DateTimeInput source="lastActivatedAt" disabled={true} />
        <DateTimeInput source="lastDeActivatedAt" disabled={true} />
        <BooleanInput source="active" />
        <ReferenceInput source="userId" reference="users" disabled={true} />
        <ReferenceInput
          source="companyId"
          reference="companies"
          disabled={true}
        />
      </SimpleForm>
    </Edit>
  );
};
