import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import dotenv from 'dotenv';
import rateLimit from 'express-rate-limit';
import jwt from 'jsonwebtoken';
import { createProxyMiddleware } from 'http-proxy-middleware';

import authRoutes from './routes/auth.js';
import healthRoutes from './routes/health.js';
import errorHandler from './middleware/errorHandler.js';
import requestLogger from './middleware/requestLogger.js';
import { validateServiceHealth } from './utils/serviceValidator.js';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;
const NODE_ENV = process.env.NODE_ENV || 'development';
const JWT_SECRET = process.env.JWT_SECRET;

if (!JWT_SECRET) {
  console.error('❌ ERROR: JWT_SECRET not configured in .env');
  process.exit(1);
}

// Trust proxy
app.set('trust proxy', 1);

// Security middleware
app.use(helmet());

// CORS Configuration
app.use(cors({
  origin: process.env.FRONTEND_URL || 'http://localhost:3000',
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization'],
  optionsSuccessStatus: 200
}));

// Body parsing middleware
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ limit: '10mb', extended: true }));

// Logging middleware
app.use(morgan(NODE_ENV === 'production' ? 'combined' : 'dev'));
app.use(requestLogger);

// Rate limiting
const limiter = rateLimit({
  windowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS) || 15000,
  max: parseInt(process.env.RATE_LIMIT_MAX_REQUESTS) || 100,
  message: 'Too many requests from this IP, please try again later.'
});
app.use(limiter);

/**
 * JWT Authentication Middleware
 * Verifies Bearer token and extracts user identity
 * Forwards identity to services via headers
 */
const authMiddleware = (req, res, next) => {
  const authHeader = req.headers.authorization;

  if (!authHeader) {
    console.warn('[AUTH] Missing Authorization header on:', req.method, req.path);
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'Missing Authorization header'
    });
  }

  const parts = authHeader.split(' ');
  if (parts.length !== 2 || parts[0] !== 'Bearer') {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'Invalid Authorization header format. Expected: Bearer <token>'
    });
  }

  const token = parts[1];

  try {
    const decoded = jwt.verify(token, JWT_SECRET);

    // Extract user identity
    req.userId = decoded.sub || decoded.userId || decoded.id;
    req.username = decoded.username || decoded.sub;
    req.roles = decoded.roles || [];

    // Forward identity to services via headers
    req.headers['x-user-id'] = req.userId;
    req.headers['x-user-roles'] = JSON.stringify(req.roles);
    req.headers['x-username'] = req.username;

    console.log(`[AUTH] ✓ User authenticated: ${req.username} (${req.userId})`);
    next();
  } catch (error) {
    console.error('[AUTH] ✗ Token verification failed:', error.message);
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'Invalid or expired token',
      details: error.message
    });
  }
};

/**
 * Proxy Middleware Configuration
 * Uses Docker container hostnames for internal communication
 */

// Auth service proxy (no auth required for login/register)
const authServiceProxy = createProxyMiddleware({
  target: 'http://hrms-auth-service:8081',
  changeOrigin: true,
  pathRewrite: {
    '^/api/auth': ''  // Strip /api prefix
  },
  logLevel: 'info',
  on: {
    proxyReq: (proxyReq, req, res) => {
      console.log(`[PROXY→AUTH] ${req.method} ${req.path}`);
    },
    proxyRes: (proxyRes, req, res) => {
      console.log(`[PROXY←AUTH] Status ${proxyRes.statusCode}`);
    },
    error: (err, req, res) => {
      console.error('[PROXY] Auth service error:', err.message);
      res.status(503).json({
        error: 'Service Unavailable',
        message: 'Auth service is not responding'
      });
    }
  }
});

// Employee service proxy (requires authentication)
const employeeServiceProxy = createProxyMiddleware({
  target: 'http://hrms-employee-service:8083',
  changeOrigin: true,
  pathRewrite: {
    '^/api/employees': ''  // Strip /api prefix
  },
  logLevel: 'info',
  on: {
    proxyReq: (proxyReq, req, res) => {
      console.log(`[PROXY→EMPLOYEE] ${req.method} ${req.path} (user: ${req.userId})`);
    },
    proxyRes: (proxyRes, req, res) => {
      console.log(`[PROXY←EMPLOYEE] Status ${proxyRes.statusCode}`);
    },
    error: (err, req, res) => {
      console.error('[PROXY] Employee service error:', err.message);
      res.status(503).json({
        error: 'Service Unavailable',
        message: 'Employee service is not responding'
      });
    }
  }
});

// Health check routes
app.use('/health', healthRoutes);

// ========================
// PUBLIC ROUTES (NO AUTH)
// ========================
app.use('/api/auth', authServiceProxy);

// ========================
// PROTECTED ROUTES (WITH AUTH)
// ========================
app.use('/api/employees', authMiddleware, employeeServiceProxy);

// Health check endpoint
app.get('/health/ready', (req, res) => {
  res.json({
    status: 'ready',
    timestamp: new Date().toISOString(),
    services: {
      auth: 'http://hrms-auth-service:8081',
      employee: 'http://hrms-employee-service:8083'
    }
  });
});

// Root endpoint
app.get('/', (req, res) => {
  res.json({
    name: 'HRMS API Gateway',
    version: '1.0.0',
    status: 'running',
    routes: {
      'auth': '/api/auth (public)',
      'employees': '/api/employees (protected)',
      'health': '/health'
    }
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({ error: 'Route not found' });
});

// Error handling middleware
app.use(errorHandler);

// Start server
app.listen(PORT, () => {
  console.log(`🚀 API Gateway running on port ${PORT} in ${NODE_ENV} mode`);
  console.log(`📡 Frontend URL: ${process.env.FRONTEND_URL}`);
  
  // Validate services on startup
  if (NODE_ENV === 'development') {
    validateServiceHealth();
  }
});

export default app;
