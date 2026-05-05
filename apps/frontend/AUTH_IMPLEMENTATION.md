# AtlasHR Frontend - Auth Logic & Implementation Guide

## Overview

This frontend implements a **production-ready JWT authentication system** with React 19, Next.js 16, TypeScript, and modern libraries:

- **React Hook Form + Zod**: Robust form handling and schema validation
- **Zustand**: Lightweight auth state management
- **Axios**: HTTP client with automatic token injection and refresh logic
- **React Query**: Data fetching and caching (prepared for APIs)
- **Shadcn/UI**: Professionally styled UI components

---

## Architecture

### 1. **Auth Flow**

```
Login Form (page.tsx)
  ↓
LoginForm Component (uses React Hook Form + Zod)
  ↓
useAuth() Hook (custom hook for auth logic)
  ↓
authAPI.login() (calls /api/auth/login via axios)
  ↓
Zustand Auth Store (manages user + tokens)
  ↓
Token Storage (localStorage)
  ↓
Axios Interceptors (auto-attach token, handle refresh)
  ↓
Dashboard (protected by useProtectedRoute hook)
```

### 2. **File Structure**

```
src/
├── app/
│   ├── layout.tsx              # Root layout with Providers
│   ├── page.tsx                # Login page (redirects if authenticated)
│   ├── providers.tsx           # Query client + providers setup
│   └── dashboard/
│       └── page.tsx            # Protected dashboard with logout
│
├── components/
│   └── login-form.tsx          # Login form (React Hook Form + Zod)
│
├── hooks/
│   ├── useAuth.ts              # Auth logic (login, logout, state)
│   └── useProtectedRoute.ts    # Route protection with auto-redirect
│
└── lib/
    ├── api/
    │   ├── axios.ts            # Axios instance with interceptors
    │   └── auth.ts             # Auth API calls
    ├── store/
    │   └── auth.ts             # Zustand auth store
    ├── validators/
    │   └── auth.ts             # Zod schemas
    └── utils/
        ├── token.ts            # Token storage helpers
        └── utils.ts            # Utility functions (cn)
```

---

## Key Components

### 1. **Login Form** (`src/components/login-form.tsx`)

Uses **React Hook Form** + **Zod** for:
- Field-level validation (username, password)
- Error display
- Loading states during submission
- Automatic redirect to dashboard on success

```tsx
const { register, handleSubmit, formState: { errors, isSubmitting } } = 
  useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    mode: "onBlur",
  });
```

### 2. **Auth Store** (`src/lib/store/auth.ts`)

Zustand store manages:
- User state (id, username)
- Auth tokens (access + refresh)
- Loading and error states
- Login/logout actions

```typescript
export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
  
  login: async (username, password) => { /* ... */ },
  logout: () => { /* ... */ },
  fetchCurrentUser: async () => { /* ... */ },
}));
```

### 3. **Axios Interceptors** (`src/lib/api/axios.ts`)

**Request Interceptor:**
- Automatically adds `Authorization: Bearer {token}` to all requests

**Response Interceptor:**
- Catches 401 errors
- Auto-refreshes token using refresh token
- Retries original request with new token
- Clears tokens on fatal failures

```typescript
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401 && !originalRequest._retry) {
      // Try refresh and retry
    }
  }
);
```

### 4. **Custom Hooks**

**`useAuth()`** - Provides auth logic:
```typescript
const { user, isAuthenticated, isLoading, error, login, logout } = useAuth();
```

**`useProtectedRoute()`** - Protects routes:
```typescript
export function useProtectedRoute() {
  const router = useRouter();
  const { isAuthenticated } = useAuthStore();
  
  useEffect(() => {
    const token = getAccessToken();
    if (!token && !isAuthenticated) {
      router.push("/"); // Redirect to login
    }
  }, []);
}
```

---

## API Contract

### Login Endpoint

**Request:**
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "password123"
}
```

**Response (Success - 200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john.doe",
  "message": "Login successful"
}
```

**Response (Error - 401):**
```json
{
  "message": "Invalid credentials",
  "token": null
}
```

### Refresh Token Endpoint

**Request:**
```
POST /api/auth/refresh
Authorization: Bearer {refreshToken}

{ "refreshToken": "..." }
```

**Response:**
```json
{
  "token": "new_access_token",
  "refreshToken": "new_refresh_token",
  ...
}
```

### Current User Endpoint

**Request:**
```
GET /api/auth/me
Authorization: Bearer {accessToken}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john.doe",
  "email": "john@example.com"
}
```

---

## Validation Schema (Zod)

```typescript
// src/lib/validators/auth.ts
export const loginSchema = z.object({
  username: z.string().min(1, "Username is required"),
  password: z.string().min(6, "Password must be at least 6 characters"),
});
```

---

## Running the Frontend

### Development

```bash
cd apps/frontend
npm install
npm run dev
```

Server runs on `http://localhost:3000`

### Production Build

```bash
npm run build
npm run start
```

---

## Environment Variables

Create `.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

(Gateway URL exposed to client for API calls)

---

## Error Handling

### Form Validation Errors
- Displayed inline under each field
- Updated in real-time on blur

### API Errors
- Captured from backend response
- Displayed in red alert box above form
- Stored in auth store for access elsewhere

### Token Refresh Failures
- Automatically clears tokens
- Redirects to login
- Shows error to user

---

## Security Considerations

1. **JWT Storage**: Tokens stored in `localStorage` (production: consider HTTPOnly cookies with same-site policy)
2. **HTTPS**: Required in production
3. **Token Expiry**: Access tokens expire (backend-controlled); refresh tokens used for renewal
4. **CORS**: Gateway/backend should validate origins
5. **Sensitive Data**: No passwords stored in state or localStorage

---

## Extending the Frontend

### Add Protected Data Fetching

```typescript
// src/hooks/useEmployees.ts
import { useQuery } from "@tanstack/react-query";
import api from "@/lib/api/axios";

export function useEmployees() {
  return useQuery({
    queryKey: ["employees"],
    queryFn: async () => {
      const response = await api.get("/employees");
      return response.data;
    },
  });
}
```

### Add More Auth Endpoints

Extend `src/lib/api/auth.ts`:

```typescript
export const authAPI = {
  register: async (data) => { /* ... */ },
  logout: async () => { /* ... */ },
  // ... other endpoints
};
```

---

## Testing Credentials

Backend auth service (no pre-seeded users in demo):

For testing, create a user via `/api/auth/register`:
```json
{
  "username": "testuser",
  "password": "password123"
}
```

Then login with the same credentials.

---

## Build Output

```
✓ Compiled successfully
✓ Finished TypeScript
✓ Collecting page data
✓ Generating static pages
✓ Finalizing page optimization

Route (app)
┌ ○ /
├ ○ /_not-found
└ ○ /dashboard
```

All pages pre-rendered as static content with dynamic client-side auth logic.
