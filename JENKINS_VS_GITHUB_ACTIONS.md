# Jenkins vs GitHub Actions Comparison

## Pipeline Overview

### Jenkins Pipeline (pipeline.groovy)
```groovy
pipeline {
    agent any
    environment {
        IMAGE_NAME = "${CONTAINER_IMAGE_NAME}"
        ACR_NAME = credentials('ACR_NAME')
        REGISTRY = "${ACR_NAME}.azurecr.io"
        AZURE_CREDENTIALS = credentials('AZURE_CREDENTIALS')
        AKS_RESOURCE_GROUP = credentials('AKS_RESOURCE_GROUP')
        AKS_CLUSTER_NAME = credentials('AKS_CLUSTER_NAME')
    }
    stages {
        stage('Checkout') { ... }
        stage('Azure Login') { ... }
        stage('ACR Login') { ... }
        stage('Build Docker Image') { ... }
        stage('Push Docker Image') { ... }
        stage('Set AKS Context') { ... }
        stage('Deploy to AKS') { ... }
    }
}
```

### GitHub Actions Workflow (azure-deploy.yml)
```yaml
name: Azure Container and AKS Deployment
on:
  push:
    branches: [ main, master ]
  workflow_dispatch:
env:
  IMAGE_NAME: ${{ vars.CONTAINER_IMAGE_NAME || 'simplenodeapp' }}
  REGISTRY: ${{ vars.ACR_NAME }}.azurecr.io
jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    environment: production
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
    - name: Azure Login
      uses: azure/login@v2
    - name: Login to Azure Container Registry
      uses: azure/docker-login@v2
    - name: Build and push Docker image
      uses: docker/build-push-action@v6
    # ... etc
```

## Key Differences

| Aspect | Jenkins | GitHub Actions |
|--------|---------|----------------|
| **Configuration** | Groovy DSL | YAML |
| **Credentials** | Jenkins credential store | GitHub Secrets |
| **Environment Variables** | Jenkins environment block | GitHub Variables |
| **Build Number** | `env.BUILD_NUMBER` | `github.run_number` |
| **Checkout** | `checkout scm` | `actions/checkout@v4` |
| **Azure Login** | Manual `az login` commands | `azure/login@v2` action |
| **Docker Build** | Shell commands | `docker/build-push-action@v6` |
| **Triggers** | Webhook/polling | Native Git events |

## Benefits of GitHub Actions

1. **Native Git Integration**: No webhooks needed, responds to Git events directly
2. **Marketplace Actions**: Reusable, maintained actions for common tasks
3. **Better Security**: Built-in secret management and OIDC support
4. **Cost Efficiency**: Included minutes for public repos, competitive pricing
5. **Visibility**: Better integration with PR checks and status
6. **Environments**: Built-in deployment environments and protection rules
7. **Matrix Builds**: Easy parallel execution across multiple configurations

## Migration Effort

- **Low Complexity**: Direct 1:1 mapping of most pipeline stages
- **Modern Actions**: Replace manual commands with maintained actions
- **Better Practices**: Improved security and maintainability patterns
- **Documentation**: Comprehensive setup guides and troubleshooting