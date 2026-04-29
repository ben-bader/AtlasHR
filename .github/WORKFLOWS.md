# GitHub Workflows Setup

## Overview

Automated CI/CD pipelines configured for API Gateway and Frontend services using GitHub Actions.

## Workflows

### 1. API Gateway Workflow (`.github/workflows/api-gateway.yaml`)

**Triggers:**
- Push to `main` or `develop` branches with changes in `gateway/` directory
- Pull requests to `main` or `develop` with changes in `gateway/` directory

**Jobs:**

#### Build
- **Node.js 20** setup with npm caching
- Install dependencies
- Run ESLint (optional, no fail)
- **Main branch only**: 
  - Login to Docker Hub
  - Build and push Docker image with tags:
    - `latest` (always)
    - `<commit-sha>` (for version tracking)
- **PR only**:
  - Build Docker image (no push) for testing

#### Security Scan (Main branch only)
- Scan Docker image with Trivy for vulnerabilities
- Upload results to GitHub Security tab

### 2. Frontend Workflow (`.github/workflows/frontend.yaml`)

**Triggers:**
- Push to `main` or `develop` branches with changes in `apps/frontend/` directory
- Pull requests to `main` or `develop` with changes in `apps/frontend/` directory

**Jobs:**

#### Build
- **Node.js 20** setup with npm caching
- Install dependencies
- Run ESLint
- Build Next.js application
- TypeScript type checking
- **Main branch only**:
  - Login to Docker Hub
  - Build and push Docker image with tags:
    - `latest` (always)
    - `<commit-sha>` (for version tracking)
- **PR only**:
  - Build Docker image (no push) for testing

#### Security Scan (Main branch only)
- Scan Docker image with Trivy for vulnerabilities
- Upload results to GitHub Security tab

#### Dependency Check
- Check npm packages for vulnerabilities using `npm audit`

## Prerequisites

### GitHub Secrets Required

Add these secrets to your GitHub repository settings (`Settings > Secrets and variables > Actions`):

```
DOCKER_USERNAME     - Docker Hub username
DOCKER_PASSWORD     - Docker Hub access token (not password!)
```

### Docker Hub Setup

1. Create Docker Hub account if you don't have one
2. Generate access token:
   - Go to Docker Hub account settings
   - Select "Security" → "Access Tokens"
   - Create new token with read/write permissions
   - Copy the token value

3. Add to GitHub Secrets:
   - Repository → Settings → Secrets and variables → Actions
   - New repository secret: `DOCKER_USERNAME`
   - New repository secret: `DOCKER_PASSWORD` (paste the token)

## Image Tags Strategy

### Production (Main Branch)
```
docker.io/YOUR_USERNAME/api-gateway:latest
docker.io/YOUR_USERNAME/api-gateway:abc123def456  # commit SHA

docker.io/YOUR_USERNAME/frontend:latest
docker.io/YOUR_USERNAME/frontend:abc123def456     # commit SHA
```

### Pull Request
```
api-gateway:pr-123    # Local build, no push
frontend:pr-456       # Local build, no push
```

## How It Works

### On Push to Main

1. **Code changes detected** in `gateway/` or `apps/frontend/`
2. **Checkout** repository code
3. **Install dependencies** and run tests
4. **Docker login** with Hub credentials
5. **Build Docker image** from Dockerfile
6. **Push to Docker Hub** with multiple tags
7. **Scan image** for vulnerabilities
8. **Report results** to GitHub Security tab

### On Pull Request

1. **Code changes detected** in `gateway/` or `apps/frontend/`
2. **Checkout** repository code
3. **Install dependencies** and run tests
4. **Build Docker image** (no push)
5. **Report build status** on PR

## Using Built Images

After successful workflow run on main branch, use images in docker-compose:

```yaml
api-gateway:
  image: YOUR_USERNAME/api-gateway:latest
  # or use specific version
  image: YOUR_USERNAME/api-gateway:abc123def456

frontend:
  image: YOUR_USERNAME/frontend:latest
  # or use specific version
  image: YOUR_USERNAME/frontend:abc123def456
```

Or rebuild locally with docker-compose:

```bash
docker-compose build --pull frontend api-gateway
docker-compose up -d
```

## Viewing Workflow Runs

1. Go to repository → **Actions** tab
2. Click on workflow name (API Gateway CI/CD or Frontend CI/CD)
3. View run details:
   - Build logs
   - Security scan results
   - Docker image info
   - Pushed tags

## Troubleshooting

### Build Fails: "npm ci failed"
- Check `package-lock.json` exists and is committed
- Ensure no uncommitted dependency changes
- Run locally: `npm ci`

### Docker Push Fails: "unauthorized"
- Verify `DOCKER_USERNAME` and `DOCKER_PASSWORD` secrets are set
- Check token is still valid in Docker Hub
- Regenerate token if expired

### Image Not Found
- Wait for workflow to complete (check Actions tab)
- Verify image exists: `docker search USERNAME/api-gateway`
- Check Docker Hub account for uploaded images

### Trivy Scan Fails
- Security scan failures don't block workflow (non-blocking)
- Review results in GitHub Security tab
- Update dependencies if critical vulnerabilities found

## Best Practices

1. **Always commit `package-lock.json`**
   - Ensures reproducible builds
   - Speeds up workflow with cache

2. **Use specific commit SHA tags**
   - Easy rollback to previous versions
   - Clear audit trail of deployments

3. **Monitor Security tab**
   - Check for new vulnerabilities regularly
   - Update dependencies proactively

4. **Test locally before push**
   - Build Docker image locally
   - Run tests before committing
   - Prevents unnecessary workflow runs

5. **Keep dependencies updated**
   - Run `npm audit` regularly
   - Update minor/patch versions
   - Test major version updates in develop branch

## File Structure

```
.github/
├── workflows/
│   ├── api-gateway.yaml        # API Gateway CI/CD
│   ├── frontend.yaml            # Frontend CI/CD
│   └── auth-service.yaml        # Auth Service CI/CD (existing)
└── java-upgrade/                # Java upgrade helpers
```

## Sample GitHub Actions Configuration

### Secrets Setup

```bash
# Linux/Mac
gh secret set DOCKER_USERNAME --body "your-docker-username"
gh secret set DOCKER_PASSWORD --body "your-docker-token"

# Or manually:
# 1. Go to Settings → Secrets and variables → Actions
# 2. New repository secret → DOCKER_USERNAME
# 3. New repository secret → DOCKER_PASSWORD (token, not password)
```

## Workflow Status Badges

Add to `README.md`:

```markdown
[![API Gateway CI/CD](https://github.com/YOUR-ORG/AtlasHR/actions/workflows/api-gateway.yaml/badge.svg)](https://github.com/YOUR-ORG/AtlasHR/actions/workflows/api-gateway.yaml)

[![Frontend CI/CD](https://github.com/YOUR-ORG/AtlasHR/actions/workflows/frontend.yaml/badge.svg)](https://github.com/YOUR-ORG/AtlasHR/actions/workflows/frontend.yaml)
```

## Integration with Docker Compose

### Development (Local)

```bash
# Build locally
docker-compose build api-gateway frontend

# Run services
docker-compose up -d
```

### Production (from Hub)

```bash
# Update docker-compose.yml to use hub images
# image: YOUR_USERNAME/api-gateway:latest
# image: YOUR_USERNAME/frontend:latest

docker-compose pull
docker-compose up -d
```

## References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Build Push Action](https://github.com/docker/build-push-action)
- [Trivy Security Scanner](https://github.com/aquasecurity/trivy-action)
- [Docker Hub Registry](https://hub.docker.com)

---

**Last Updated**: April 29, 2026
**Status**: ✅ Production Ready
