import axios from 'axios';

/**
 * Validate all services are healthy on startup
 */
export async function validateServiceHealth() {
  const services = [
    { name: 'Auth Service', url: process.env.AUTH_SERVICE_URL || 'http://localhost:8081' },
    { name: 'User Service', url: process.env.USER_SERVICE_URL || 'http://localhost:8082' },
    { name: 'Employee Service', url: process.env.EMPLOYEE_SERVICE_URL || 'http://localhost:8083' },
    { name: 'Payroll Service', url: process.env.PAYROLL_SERVICE_URL || 'http://localhost:8084' }
  ];

  console.log('\n📊 Validating service health...\n');

  for (const service of services) {
    try {
      const response = await axios.get(`${service.url}/actuator/health`, {
        timeout: 5000
      });
      console.log(`✅ ${service.name} - Available at ${service.url}`);
    } catch (error) {
      console.log(`⚠️  ${service.name} - Unavailable (will retry on request)`);
      console.log(`   URL: ${service.url}`);
    }
  }

  console.log('\n');
}
