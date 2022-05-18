# task-manager

This is a tasks manager web app builded using Spring Boot, Hibernate, Vavr, OpenAPI and clean architecture. E2E tests
included.

This app's using current H2 database. H2 console available at: localhost:8080/h2/console

Rest API available at localhost:8080/swagger-ui:

User:

POST /registration - register a new user

PATCH /user/password - change user password

DELETE /user - remove user

PATCH /users/{username}/status - change user status (for admin only)

GET /users - get all users (for admin only)

Task (for logged user):

POST /tasks - add a singe task

PATCH /tasks/{taskId}/content - change task's content

PATCH /tasks/{taskId}/status - change task's status

PATCH /tasks/{taskId}/priority - change task's priority

POST /tasks/

DELETE /tasks/{taskId} - remove task
