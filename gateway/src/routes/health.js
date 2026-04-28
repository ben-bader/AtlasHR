import express from 'express';
import axios from 'axios';

const router = express.Router();

/**
 * Gateway health check
 */
router.get('/', (req, res) => {
  res.json({
    status: 'ok',
    timestamp: new Date().toISOString(),
    uptime: process.uptime()
  });
});

/**
 * Services health check
 */
router.get('/services', async (req, res) => {
  const services = [
    { name: 'auth-service', url: process.env.AUTH_SERVICE_URL || 'http://localhost:8081' },
    { name: 'user-service', url: process.env.USER_SERVICE_URL || 'http://localhost:8082' },
    { name: 'employee-service', url: process.env.EMPLOYEE_SERVICE_URL || 'http://localhost:8083' },
    { name: 'payroll-service', url: process.env.PAYROLL_SERVICE_URL || 'http://localhost:8084' }
  ];

  const healthStatus = [];

  for (const service of services) {
    try {
      const response = await axios.get(`${service.url}/actuator/health`, {
        timeout: 5000
      });
      healthStatus.push({
        name: service.name,
        status: response.status === 200 ? 'up' : 'down',
        url: service.url
      });
    } catch (error) {
      healthStatus.push({
        name: service.name,
        status: 'down',
        url: service.url,
        error: error.message
      });
    }
  }

  const allHealthy = healthStatus.every(s => s.status === 'up');
  res.status(allHealthy ? 200 : 503).json({
    status: allHealthy ? 'healthy' : 'degraded',
    services: healthStatus,
    timestamp: new Date().toISOString()
  });
});

export default router;
