# Employee Management System

A Java Spring Boot + MySQL REST API with a React/Vite frontend. Current backend modules include employee records, attendance check-in, JWT authentication, role/permission authorization, and monthly payroll generation.

## Requirements
- Java 17 (matches `pom.xml`)
- MySQL 8+
- Node.js and npm

## Database and backend setup

Create the local database:

```sql
CREATE DATABASE employeedb;
```

Configure environment variables before starting the backend. Never commit real credentials.

**Windows PowerShell**
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/employeedb"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-mysql-password"
$env:JWT_SECRET="replace-with-a-random-secret-of-at-least-32-bytes"
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

**macOS/Linux**
```bash
export DB_URL="jdbc:mysql://localhost:3306/employeedb"
export DB_USERNAME="root"
export DB_PASSWORD="your-mysql-password"
export JWT_SECRET="replace-with-a-random-secret-of-at-least-32-bytes"
./mvnw clean test
./mvnw spring-boot:run
```

The API starts on `http://localhost:8082`. Hibernate updates entity tables automatically for local development. Use reviewed migrations in production.

## Frontend setup

```bash
cd "New folder"
npm install
npm run dev
```

Vite normally serves the app at `http://localhost:5173`, which is the origin allowed by the backend CORS configuration.

## Authentication and roles

- `POST /api/auth/register` creates an EMPLOYEE account and returns a JWT.
- `POST /api/auth/login` verifies credentials and returns a JWT.
- Send the token using `Authorization: Bearer <token>` for protected API calls.
- Public registration cannot assign ADMIN or HR roles. A trusted administrator must provision elevated roles through a controlled process.
- For local development only, an administrator can promote a verified account with a controlled SQL update:

```sql
UPDATE user_auth SET role = 'ADMIN'
WHERE userOfficialEmail = 'your-admin-email@example.com';
```

Do not expose role promotion to public endpoints. In production, use a controlled bootstrap/migration procedure instead of ad-hoc SQL.

## API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/auth/hello` | Health/greeting endpoint |
| POST | `/api/auth/register` | Register employee |
| POST | `/api/auth/login` | Login and obtain JWT |
| GET | `/api/employees` | List employees (VIEW_EMP permission) |
| GET | `/api/employees/{id}` | Get one employee |
| POST | `/api/employees` | Create employee (CREATE_EMP permission) |
| PUT | `/api/employees/{id}` | Update employee (UPDATE_EMP permission) |
| DELETE | `/api/employees/{id}` | Delete employee (DELETE_EMP permission) |
| POST | `/api/attendance/checkin/{employeeId}` | Check in an employee |
| GET | `/api/attendance/{employeeId}` | Read attendance |
| POST | `/api/payroll/generate/{employeeId}` | Generate/retrieve current month's payroll (RUN_PAYROLL permission) |

Payroll currently uses the employee's base salary with zero allowances and deductions. Configure and validate a real payroll policy before using it for actual payments.

## Security notes

- Use a fresh, random JWT secret of at least 32 bytes. Do not reuse a placeholder.
- Rotate credentials previously committed to Git history. Removing a secret from the latest file does not erase it from prior commits.
- Use HTTPS in deployment and keep database credentials in a secret manager/environment.
- Public registration always assigns EMPLOYEE; only trusted administrators should assign HR/ADMIN.
- Before production, add integration tests, database migrations, audit logging, and a reviewed role/permission policy. Consider DTOs that never expose sensitive employee fields.
