# Original idea

// c r u d before the dto name sets the default for fields in this dto
crud user {
  id: long: r
  uuid: uuid?: r
  upn: string: r
  first: string #implicit crud, although delete makes no sense
  last: string
  display_name: string
}

crud employee {
  id: id # r by default
  user_id: ref user: r
  company_id: ref company: r
  # predefined fields, read-only:
  created: created # created_at + created_by
  updated: updated # updated_at + updated_by
  deleted: deleted # ... + default cru
}

  
Generate Java interfaces:
UserCreate: only c field
UserRead: only r fiels
UserUpdate
UserDelete: perhaps doesn't make sense?
and a class:
UserAll

# Better idea

Or: generate Java classes (matches better with):
UserCreateDto
UserReadDto
UserUpdateDto
and an interface:
UserCommonDto

We may even only generate the UserCommonDto interface, and use the
x-implements option from:

https://openapi-generator.tech/docs/generators/spring/

and let the openapi generator generate the other fields

There are many other interesting options:

- x-spring-paginated
- useOptional
- useResponseEntity
- useOneOfInterfaces
- generateBuilders
- hateoas
- virtualService

Generate OpenAPI data definitions from this, which should lead to good
TypeScript interfaces.
