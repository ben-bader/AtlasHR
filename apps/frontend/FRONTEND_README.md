# AtlasHR Frontend - React + Next.js + Tailwind CSS + shadcn/ui

A modern, production-ready HR Management System frontend built with Next.js 16, React 19, TypeScript, and Tailwind CSS.

## 🎯 Project Overview

This is the frontend application for the AtlasHR microservices platform, providing:

- ✅ **User Authentication** - JWT-based login and registration
- ✅ **Protected Routes** - Role-based access control
- ✅ **Employee Management** - Complete CRUD operations
- ✅ **Responsive Design** - Works on desktop, tablet, and mobile
- ✅ **Type-Safe** - Full TypeScript support with strict mode
- ✅ **API Integration** - Centralized axios client with token refresh
- ✅ **Form Validation** - React Hook Form + Zod
- ✅ **State Management** - Zustand for global state, React Query for server state
- ✅ **Error Handling** - Comprehensive error states and user feedback

## 📋 Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Environment Variables](#environment-variables)
- [Features](#features)
- [API Integration](#api-integration)
- [Component Architecture](#component-architecture)
- [Authentication Flow](#authentication-flow)
- [Employee Management](#employee-management)
- [Development Guidelines](#development-guidelines)
- [Troubleshooting](#troubleshooting)

## 🛠️ Tech Stack

### Core Framework
- **Next.js 16.2** - React framework with built-in optimization
- **React 19.2** - UI library
- **TypeScript 5** - Type safety and development experience

### UI & Styling
- **Tailwind CSS 4** - Utility-first CSS framework
- **shadcn/ui** - High-quality React component library
- **Lucide React** - Beautiful icon library

### State Management
- **Zustand 4** - Lightweight state management for authentication
- **React Query (TanStack Query) 5** - Server state management and caching

### Form Handling
- **React Hook Form 7** - Performant form handling
- **Zod** - TypeScript-first schema validation
- **@hookform/resolvers** - Integration between React Hook Form and Zod

### HTTP Client
- **Axios** - Promise-based HTTP client with interceptors

## 📁 Project Structure

```
src/
├── app/                          # Next.js app router pages
│   ├── dashboard/               # Protected dashboard routes
│   │   ├── employees/           # Employee management pages
│   │   │   ├── page.tsx        # List employees
│   │   │   ├── create/         # Create employee
│   │   │   └── [id]/           # View/edit employee
│   │   └── page.tsx            # Dashboard overview
│   ├── login/                   # Authentication pages
│   ├── register/
│   ├── page.tsx                 # Home (redirects to login)
│   ├── layout.tsx               # Root layout
│   ├── globals.css              # Global styles
│   └── providers.tsx            # Context providers
│
├── components/                   # Reusable React components
│   ├── ui/                      # shadcn/ui component library
│   │   ├── button.tsx
│   │   ├── input.tsx
│   │   ├── dialog.tsx
│   │   ├── form.tsx
│   │   ├── table.tsx
│   │   └── ...
│   ├── employees/               # Employee-specific components
│   │   ├── employee-list-view.tsx
│   │   ├── employee-form-view.tsx
│   │   └── employee-details-view.tsx
│   ├── login-form.tsx           # Authentication forms
│   ├── register-form.tsx
│   ├── app-sidebar.tsx          # Navigation sidebar
│   └── ...
│
├── hooks/                        # Custom React hooks
│   ├── useAuth.ts               # Authentication hook
│   ├── useEmployee.ts           # Employee CRUD hook
│   ├── useProtectedRoute.ts     # Route protection
│   └── use-mobile.ts
│
├── lib/                         # Utility functions and configuration
│   ├── api/                     # API clients
│   │   ├── axios.ts             # Axios instance with interceptors
│   │   ├── auth.ts              # Authentication API
│   │   └── employee.ts          # Employee API
│   ├── store/                   # Zustand stores
│   │   └── auth.ts              # Auth state management
│   ├── types/                   # TypeScript types
│   │   └── index.ts             # Centralized type definitions
│   ├── utils/                   # Utility functions
│   │   ├── token.ts             # Token management
│   │   └── utils.ts             # Common utilities
│   └── validators/              # Zod schemas
│       ├── auth.ts              # Auth validation schemas
│       └── employee.ts          # Employee validation schemas
│
└── public/                       # Static assets
    └── placeholder.svg
```

## 🚀 Installation

### Prerequisites

- **Node.js** 18.x or higher
- **npm** or **yarn**
- Backend services running (Auth Service, Employee Service)

### Setup Steps

1. **Clone and navigate to the project:**
```bash
cd apps/frontend
```

2. **Install dependencies:**
```bash
npm install
# or
yarn install
```

3. **Create environment variables:**
```bash
cp .env.example .env.local
```

4. **Configure environment variables** (see [Environment Variables](#environment-variables))

## 🏃 Running the Application

### Development Mode

```bash
npm run dev
# or
yarn dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser.

### Build for Production

```bash
npm run build
npm run start
```

### Linting

```bash
npm run lint
```

## 🔧 Environment Variables

Create a `.env.local` file in the `apps/frontend` directory:

```env
# API Configuration
NEXT_PUBLIC_API_URL=http://localhost:8080/api

# Auth Service (usually same as API_URL)
NEXT_PUBLIC_AUTH_URL=http://localhost:8080/api

# Employee Service
NEXT_PUBLIC_EMPLOYEE_URL=http://localhost:8080/api

# Feature Flags
NEXT_PUBLIC_ENABLE_ANALYTICS=true
NEXT_PUBLIC_ENVIRONMENT=development
```

### Notes on Environment Variables

- All `NEXT_PUBLIC_*` variables are exposed to the browser
- Don't put secrets in these variables
- Update `API_URL` to match your backend service URL
- Default API URL is `http://localhost:8080/api`

## ✨ Features

### 1. Authentication
- **Login** - Username/password authentication with JWT tokens
- **Registration** - New user account creation with validation
- **Token Refresh** - Automatic token refresh on expiration
- **Protected Routes** - Unauthorized access redirects to login
- **Logout** - Clear session and tokens

**Files:**
- `src/components/login-form.tsx` - Login form component
- `src/components/register-form.tsx` - Registration form component
- `src/lib/api/auth.ts` - Authentication API client
- `src/lib/store/auth.ts` - Authentication state (Zustand)
- `src/hooks/useAuth.ts` - Authentication hook
- `src/hooks/useProtectedRoute.ts` - Route protection hook

### 2. Employee Management
- **List Employees** - View all employees with pagination and search
- **Create Employee** - Add new employees with form validation
- **View Details** - See complete employee information
- **Edit Employee** - Update employee details
- **Delete Employee** - Remove employees with confirmation
- **Search & Filter** - Find employees by various criteria

**Files:**
- `src/components/employees/employee-list-view.tsx` - List with pagination
- `src/components/employees/employee-form-view.tsx` - Create/edit form
- `src/components/employees/employee-details-view.tsx` - Details view
- `src/lib/api/employee.ts` - Employee API client
- `src/hooks/useEmployee.ts` - Employee CRUD hooks

### 3. UI Components
All components built with shadcn/ui and Tailwind CSS:
- **Button** - With variants (default, outline, ghost, destructive, etc.)
- **Input** - Text and other input types
- **Table** - Sortable, responsive data table
- **Dialog** - Modal dialogs with animations
- **Form** - Form layout and error handling
- **Breadcrumb** - Navigation breadcrumbs
- **Sidebar** - Collapsible navigation sidebar
- **Avatar** - User avatars
- **Skeleton** - Loading placeholders

## 🔌 API Integration

### Axios Configuration

The application uses a centralized Axios instance with:
- Automatic token attachment to requests
- Token refresh on 401 responses
- Error handling and logging

**File:** `src/lib/api/axios.ts`

```typescript
// Usage
import api from "@/lib/api/axios"

const response = await api.get("/employees")
```

### API Endpoints

#### Authentication
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/refresh` - Refresh access token
- `GET /auth/me` - Get current user

#### Employees
- `GET /employees` - List employees (with pagination)
- `GET /employees/{id}` - Get single employee
- `POST /employees` - Create employee
- `PUT /employees/{id}` - Update employee
- `DELETE /employees/{id}` - Delete employee
- `GET /employees/search?q=query` - Search employees
- `GET /employees/department/{dept}` - Get by department

## 🏗️ Component Architecture

### Component Hierarchy

```
RootLayout
├── Providers (QueryClient, Zustand)
├── Page Routes
│   ├── /login → LoginPage → LoginForm
│   ├── /register → RegisterPage → RegisterForm
│   ├── /dashboard → DashboardPage
│   │   └── SidebarProvider
│   │       ├── AppSidebar
│   │       ├── NavMain
│   │       └── Dashboard Content
│   └── /dashboard/employees
│       ├── EmployeeListView
│       │   └── Table, Dialog, Buttons
│       ├── /create → EmployeeFormView
│       ├── /[id] → EmployeeDetailsView
│       └── /[id]/edit → EmployeeFormView (edit mode)
```

### UI Component Examples

```tsx
// Button
<Button variant="outline" onClick={() => {}}>
  Click me
</Button>

// Form with validation
<Form onSubmit={handleSubmit(onSubmit)}>
  <FormGroup>
    <FormLabel>Email</FormLabel>
    <Input {...register("email")} />
    {errors.email && <FormMessage>{errors.email.message}</FormMessage>}
  </FormGroup>
</Form>

// Table
<Table>
  <TableHeader>
    <TableRow>
      <TableHead>Name</TableHead>
    </TableRow>
  </TableHeader>
  <TableBody>
    {data.map(item => (
      <TableRow key={item.id}>
        <TableCell>{item.name}</TableCell>
      </TableRow>
    ))}
  </TableBody>
</Table>
```

## 🔐 Authentication Flow

### Login Flow
1. User enters credentials
2. Submit to `/auth/login` API
3. Receive `token` and `refreshToken`
4. Store tokens in localStorage
5. Redirect to dashboard
6. On each request, token is attached via interceptor

### Token Refresh Flow
1. Make API request with access token
2. If 401 response, automatically refresh token
3. Retry original request with new token
4. If refresh fails, redirect to login

### Protected Route Flow
1. Component calls `useProtectedRoute()`
2. Check if token exists in localStorage
3. If no token and not authenticated, redirect to `/`
4. If token exists, fetch current user
5. Render component content

**Files:**
- `src/lib/api/axios.ts` - Token attach and refresh logic
- `src/hooks/useProtectedRoute.ts` - Route protection logic
- `src/lib/utils/token.ts` - Token storage helpers

## 👥 Employee Management

### Employee Data Model

```typescript
interface Employee {
  id: string
  firstName: string
  lastName: string
  email: string
  phone?: string
  department?: string
  designation?: string
  joinDate?: string
  dateOfBirth?: string
  address?: string
  city?: string
  state?: string
  zipCode?: string
  country?: string
  employmentType?: string  // FULL_TIME, PART_TIME, CONTRACT, TEMPORARY
  status?: string          // ACTIVE, INACTIVE, ON_LEAVE, TERMINATED
  manager?: string
  salary?: number
  createdAt?: string
  updatedAt?: string
}
```

### CRUD Operations

```typescript
import { useEmployeeList, useEmployee, useCreateEmployee, useUpdateEmployee, useDeleteEmployee } from "@/hooks/useEmployee"

// List employees
const { employees, total, page, isLoading } = useEmployeeList({
  page: 1,
  pageSize: 10,
  search: "john"
})

// Get single employee
const { employee, isLoading, error } = useEmployee(employeeId)

// Create employee
const { createEmployee, isLoading, error } = useCreateEmployee()
createEmployee({ firstName: "John", lastName: "Doe", email: "john@example.com" })

// Update employee
const { updateEmployee, isLoading } = useUpdateEmployee(employeeId)
updateEmployee({ firstName: "Jane" })

// Delete employee
const { deleteEmployee, isLoading } = useDeleteEmployee()
deleteEmployee(employeeId)
```

## 📝 Development Guidelines

### Component Best Practices

1. **Use functional components** with hooks
2. **Keep components focused** - single responsibility
3. **Extract custom hooks** for reusable logic
4. **Use TypeScript** - specify all types
5. **Avoid prop drilling** - use context for shared state
6. **Memoize** expensive components with `React.memo`

### Form Validation

Use Zod schemas for validation:

```typescript
import { z } from "zod"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"

const schema = z.object({
  name: z.string().min(1, "Name required"),
  email: z.string().email("Invalid email"),
})

const { register, formState: { errors } } = useForm({
  resolver: zodResolver(schema),
})
```

### API Calls

Always use hooks for API calls:

```typescript
import { useEmployeeList } from "@/hooks/useEmployee"

function MyComponent() {
  const { employees, isLoading, error } = useEmployeeList()
  
  if (isLoading) return <Spinner />
  if (error) return <ErrorMessage error={error} />
  return <EmployeeTable employees={employees} />
}
```

### Error Handling

Display errors consistently:

```tsx
{error && (
  <div className="p-4 bg-red-50 border border-red-200 rounded text-sm text-red-800">
    {error}
  </div>
)}
```

## 🐛 Troubleshooting

### Common Issues

#### "Cannot GET /dashboard"
- Ensure you're authenticated (token in localStorage)
- Check if backend services are running
- Verify `NEXT_PUBLIC_API_URL` environment variable

#### "API requests returning 401"
- Token may have expired
- Check if refresh token is valid
- Clear localStorage and login again

#### "Components not rendering"
- Check browser console for errors
- Verify TypeScript types
- Ensure required props are passed
- Check if data is loading

#### "Form validation not working"
- Verify Zod schema is correct
- Check `zodResolver` integration
- Ensure form field names match schema

#### "Sidebar not showing"
- Check if `SidebarProvider` wraps content
- Verify `AppSidebar` component is included
- Check CSS classes are applied correctly

### Debug Mode

Enable verbose logging:

```typescript
// In axios.ts
api.interceptors.request.use((config) => {
  console.log("Request:", config)
  return config
})
```

## 📚 Additional Resources

- [Next.js Documentation](https://nextjs.org/docs)
- [React Documentation](https://react.dev)
- [Tailwind CSS](https://tailwindcss.com)
- [shadcn/ui](https://ui.shadcn.com)
- [React Query Docs](https://tanstack.com/query/latest)
- [Zod Validation](https://zod.dev)
- [React Hook Form](https://react-hook-form.com)

## 📄 License

This project is part of the AtlasHR system and is maintained by the development team.

---

**Last Updated:** May 7, 2026  
**Version:** 1.0.0  
**Status:** Production Ready ✅
