import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import dotenv from 'dotenv';
import rateLimit from 'express-rate-limit';
import axios from 'axios';

import authRoutes from './routes/auth.js';
import healthRoutes from './routes/health.js';
import errorHandler from './middleware/errorHandler.js';
import requestLogger from './middleware/requestLogger.js';
import { validateServiceHealth } from './utils/serviceValidator.js';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;
const NODE_ENV = process.env.NODE_ENV || 'development';

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

// Health check routes
app.use('/health', healthRoutes);

// API Routes
app.use('/api/auth', authRoutes);

// Generic proxy function
async function proxyRequest(req, res, serviceUrl, serviceName) {
  try {
    const path = req.originalUrl.split(serviceName)[1] || '';
    const fullUrl = `${serviceUrl}${path}`;

    const config = {
      method: req.method,
      url: fullUrl,
      data: req.method !== 'GET' ? req.body : undefined,
      headers: {
        ...req.headers,
        host: new URL(serviceUrl).hostname
      },
      timeout: 10000
    };

    const response = await axios(config);
    res.status(response.status).json(response.data);
  } catch (error) {
    console.error(`Proxy error for ${serviceName}:`, error.message);
    const status = error.response?.status || 503;
    const errorMsg = error.response?.data || { error: `${serviceName} unavailable` };
    res.status(status).json(errorMsg);
  }
}

// Service proxies
app.use('/api/auth-service', (req, res) => 
  proxyRequest(req, res, process.env.AUTH_SERVICE_URL || 'http://localhost:8081', 'auth-service')
);

app.use('/api/users', (req, res) => 
  proxyRequest(req, res, process.env.USER_SERVICE_URL || 'http://localhost:8082', 'users')
);

app.use('/api/employees', (req, res) => 
  proxyRequest(req, res, process.env.EMPLOYEE_SERVICE_URL || 'http://localhost:8083', 'employees')
);

app.use('/api/payroll', (req, res) => 
  proxyRequest(req, res, process.env.PAYROLL_SERVICE_URL || 'http://localhost:8084', 'payroll')
);

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
