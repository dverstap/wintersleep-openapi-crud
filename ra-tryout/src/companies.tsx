import {
  BooleanField,
  BooleanInput,
  Create,
  Datagrid,
  DateField,
  DateTimeInput,
  Edit,
  List,
  NullableBooleanInput,
  required,
  Show,
  SimpleForm,
  SimpleShowLayout,
  TextField,
  TextInput,
  UrlField,
  Validator,
} from "react-admin";
import { checkVAT, countries } from "jsvat";

const filters = [
  <TextInput source="q" label="Search" alwaysOn />,
  <TextInput source="name" label="Name" />,
  <TextInput source="vatNumber" label="VAT" />,
  <TextInput source="externalId" label="External ID" />,
  <NullableBooleanInput
    source="verified"
    label="Verified"
    defaultValue={null}
  />,
];

export const CompanyList = () => (
  <List filters={filters}>
    <Datagrid>
      <TextField source="id" />
      <TextField source="name" />
      <TextField source="vatNumber" />
      <TextField source="externalId" />
      <BooleanField source="verified" />
    </Datagrid>
  </List>
);

export const CompanyShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="id" />
      <TextField source="name" />
      <TextField source="vatNumber" />
      <TextField source="externalId" />
      <UrlField source="url" target="_blank" />
      <BooleanField source="verified" />
      <DateField source="lastVerifiedAt" showTime={true} />
      <DateField source="lastUnVerifiedAt" showTime={true} />
    </SimpleShowLayout>
  </Show>
);

const validateVat: Validator[] = [
  required(),
  (vat: string) => {
    let result = checkVAT(vat, countries);
    if (result.isValid) {
      return null;
    } else if (result.isValidFormat) {
      return "VAT number is in the right format, but the checksum failed";
    } else if (!result.isSupportedCountry) {
      //return "Country is not supported: " + result.country
      return null; // Just don't validate
    } else {
      return "Invalid VAT number: " + vat;
    }
  },
];

const validateUrl: Validator[] = [
  (url) => {
    if (url === undefined || url === null || url === "") {
      return null;
    }
    if (URL.canParse(url)) {
      if (url.startsWith("https://")) {
        return null;
      }
      return "URL must start with https://";
    } else {
      return "Invalid URL: " + url;
    }
  },
];

export const CompanyEdit = () => (
  <Edit mutationMode={"pessimistic"}>
    <SimpleForm>
      <TextInput source="id" disabled={true} />
      <TextInput source="name" />
      <TextInput source="vatNumber" disabled={true} />
      <TextInput source="externalId" label="External ID" disabled={true} />
      <TextInput source="url" validate={validateUrl} />
      <BooleanInput source="verified" />
      <DateTimeInput source="lastVerifiedAt" disabled={true} />
      <DateTimeInput source="lastUnVerifiedAt" disabled={true} />
    </SimpleForm>
  </Edit>
);

export const CompanyCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="name" />
      <TextInput source="vatNumber" validate={validateVat} />
      <TextInput source="externalId" label="External ID" />
      <TextInput source="url" validate={validateUrl} />
      <BooleanInput source="verified" />
    </SimpleForm>
  </Create>
);
