import {
  AutocompleteInput,
  BooleanField,
  BooleanInput,
  Create,
  Datagrid,
  DateField,
  DateTimeInput,
  Edit,
  List,
  NullableBooleanInput,
  ReferenceField,
  ReferenceInput,
  required,
  SaveButton,
  Show,
  SimpleForm,
  SimpleShowLayout,
  TextField,
  TextInput,
  Toolbar,
  useEditContext,
  useNotify,
  useRedirect,
} from "react-admin";

const filters = [
  <TextInput source="q" label="Search" alwaysOn />,
  <NullableBooleanInput source="active" label="Active" defaultValue={null} />,
];

export const EmployeeList = () => (
  <List filters={filters}>
    {/*<Datagrid sort={{ field: "id", order: "ASC" }}>*/}
    <Datagrid>
      <TextField source="id" />
      <ReferenceField source="userId" reference="users" />
      {/*<TextField source="userDisplayName" />*/}
      <ReferenceField source="companyId" reference="companies" />
      {/*<TextField source="companyName"/>*/}
      <BooleanField source="active" />
      <DateField source="lastActivatedAt" showTime={true} />
      <DateField source="lastDeActivatedAt" showTime={true} />
    </Datagrid>
  </List>
);

export const EmployeeShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="id" />
      <ReferenceField source="userId" reference="users" />
      <ReferenceField source="companyId" reference="companies" />
      <BooleanField source="active" />
      <DateField source="lastActivatedAt" showTime={true} />
      <DateField source="lastDeActivatedAt" showTime={true} />
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
  return (
    <Edit mutationMode={"pessimistic"}>
      <SimpleForm toolbar={<EditToolbar />}>
        <TextInput source="id" disabled={true} />
        <ReferenceInput
          source="userId"
          reference="users"
          format={(user) => user.displayName}
        >
          <AutocompleteInput name={"userId"} disabled />
        </ReferenceInput>
        <ReferenceInput source="companyId" reference="companies">
          <AutocompleteInput name={"companyId"} disabled />
        </ReferenceInput>
        <BooleanInput source="active" />
        <DateTimeInput source="lastActivatedAt" disabled={true} />
        <DateTimeInput source="lastDeActivatedAt" disabled={true} />
      </SimpleForm>
    </Edit>
  );
};

export const EmployeeCreate = () => (
  <Create>
    <SimpleForm>
      <ReferenceInput source="userId" reference="users">
        <AutocompleteInput name={"userId"} validate={required("companyId")} />
      </ReferenceInput>
      <ReferenceInput source="companyId" reference="companies">
        <AutocompleteInput
          name={"companyId"}
          validate={required("companyId")}
        />
      </ReferenceInput>
      <BooleanInput source="active" />
    </SimpleForm>
  </Create>
);
