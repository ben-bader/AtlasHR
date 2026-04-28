import express from 'express';
import axios from 'axios';

const router = express.Router();
const AUTH_SERVICE_URL = process.env.AUTH_SERVICE_URL || 'http://localhost:8081';

/**
 * Forward login request to auth service
 */
router.post('/login', async (req, res, next) => {
  try {
    const response = await axios.post(`${AUTH_SERVICE_URL}/api/auth/login`, req.body);
    res.json(response.data);
  } catch (error) {
    handleAuthError(error, res, next);
  }
});

/**
 * Forward register request to auth service
 */
router.post('/register', async (req, res, next) => {
  try {
    const response = await axios.post(`${AUTH_SERVICE_URL}/api/auth/register`, req.body);
    res.json(response.data);
  } catch (error) {
    handleAuthError(error, res, next);
  }
});

/**
 * Verify token
 */
router.post('/verify', async (req, res, next) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    if (!token) {
      return res.status(401).json({ error: 'No token provided' });
    }

    const response = await axios.post(
      `${AUTH_SERVICE_URL}/api/auth/verify`,
      { token },
      { headers: { Authorization: `Bearer ${token}` } }
    );
    res.json(response.data);
  } catch (error) {
    handleAuthError(error, res, next);
  }
});

/**
 * Refresh token
 */
router.post('/refresh', async (req, res, next) => {
  try {
    const response = await axios.post(`${AUTH_SERVICE_URL}/api/auth/refresh`, req.body);
    res.json(response.data);
  } catch (error) {
    handleAuthError(error, res, next);
  }
});

/**
 * Error handler for auth routes
 */
function handleAuthError(error, res, next) {
  if (error.response) {
    return res.status(error.response.status).json(error.response.data);
  }
  next(error);
}

export default router;
