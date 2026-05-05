# GitHub Secrets Setup

This guide explains how to set up GitHub Secrets for the Employee Service CI/CD pipeline.

## Required Secrets

### 1. Docker Hub Credentials

```
DOCKER_USERNAME: Your Docker Hub username
DOCKER_PASSWORD: Your Docker Hub access token (not your password)
```

#### How to create Docker access token:

1. Go to https://hub.docker.com/settings/security
2. Click "New Access Token"
3. Give it a descriptive name (e.g., "GitHub Actions")
4. Copy the token and use it as `DOCKER_PASSWORD`

### 2. Kubernetes Configurations

#### Development Cluster (KUBE_CONFIG_DEV):

```bash
# Get kubeconfig and encode it
cat ~/.kube/config | base64 -w 0 > /tmp/kube-config-dev.b64
cat /tmp/kube-config-dev.b64
```

Copy the output and set as `KUBE_CONFIG_DEV` secret.

#### Staging Cluster (KUBE_CONFIG_STAGING):

```bash
# If using a different context
KUBECONFIG=/path/to/staging/config cat ~/.kube/config | base64 -w 0
```

#### Production Cluster (KUBE_CONFIG_PROD):

```bash
# For production, ensure it's a restricted kubeconfig
KUBECONFIG=/path/to/prod/config cat ~/.kube/config | base64 -w 0
```

## Setting Secrets in GitHub

### Via Web UI:

1. Go to your repository on GitHub
2. Settings → Secrets and variables → Actions
3. Click "New repository secret"
4. Enter secret name and value
5. Click "Add secret"

### Via GitHub CLI:

```bash
# Docker secrets
gh secret set DOCKER_USERNAME --body "your-docker-username"
gh secret set DOCKER_PASSWORD --body "your-docker-token"

# Kubernetes configs
gh secret set KUBE_CONFIG_DEV --body "$(cat ~/.kube/config | base64 -w 0)"
gh secret set KUBE_CONFIG_STAGING --body "$(cat ~/.kube/staging/config | base64 -w 0)"
gh secret set KUBE_CONFIG_PROD --body "$(cat ~/.kube/prod/config | base64 -w 0)"
```

### Via Script:

```bash
#!/bin/bash

# Set all secrets at once
SECRETS=(
  "DOCKER_USERNAME:your-username"
  "DOCKER_PASSWORD:your-token"
  "KUBE_CONFIG_DEV:$(cat ~/.kube/config | base64 -w 0)"
  "KUBE_CONFIG_STAGING:$(cat ~/.kube/staging/config | base64 -w 0)"
  "KUBE_CONFIG_PROD:$(cat ~/.kube/prod/config | base64 -w 0)"
)

for secret in "${SECRETS[@]}"; do
  IFS=':' read -r name value <<< "$secret"
  gh secret set "$name" --body "$value"
  echo "Set $name"
done
```

## Kubeconfig Security Best Practices

1. **Use context-specific kubeconfigs**: Don't put production credentials in dev kubeconfig
2. **Restrict permissions**: Use service accounts with minimal permissions
3. **Rotate regularly**: Update tokens/credentials periodically
4. **Audit access**: Monitor who/what accesses your clusters
5. **Use separate accounts**: Different kubeconfigs for dev/staging/prod

## Verifying Secrets

```bash
# List all secrets (without values)
gh secret list

# Check if a specific secret is set
gh secret list | grep DOCKER_USERNAME
```

## Troubleshooting

### Secret not found in workflow:

```yaml
# Make sure you're using correct syntax:
${{ secrets.SECRET_NAME }}

# Not:
${{ SECRET_NAME }}
${{ GITHUB_SECRETS.SECRET_NAME }}
```

### Kubeconfig auth fails:

1. Verify base64 encoding: `echo "encoded-value" | base64 -d`
2. Check kubeconfig validity: `kubectl config view --kubeconfig=/tmp/test-config`
3. Ensure cluster is accessible from GitHub Actions runners

### Docker push fails:

1. Verify credentials: `docker login`
2. Verify Docker Hub API access token (not password)
3. Check repository permissions
4. Ensure repository visibility settings allow pushing

## Environment-Specific Secrets (Optional)

You can also create environment-specific secrets:

```bash
# Development environment
gh secret set DOCKER_USERNAME --body "dev-username" --env development

# Staging environment
gh secret set DOCKER_USERNAME --body "staging-username" --env staging

# Production environment
gh secret set DOCKER_USERNAME --body "prod-username" --env production
```

Then reference in workflow:

```yaml
jobs:
  deploy-dev:
    environment: development
    # Can use environment-specific secrets
```

## Security Audit

```bash
# View which secrets are being used in workflows
grep -r "secrets\." .github/workflows/

# Check for hardcoded values
grep -r "password" .github/workflows/
grep -r "token" .github/workflows/
```

## Cleanup

Remove unused secrets:

```bash
# Via web UI: Settings → Secrets → Delete
# Via CLI: Not directly supported in gh, use web UI
```
