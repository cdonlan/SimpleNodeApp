# Jenkins to GitHub Actions Migration

This document outlines the migration from Jenkins pipeline to GitHub Actions for Azure Container Registry and AKS deployment.

## Overview

The Jenkins pipeline in `pipeline.groovy` has been converted to a GitHub Actions workflow in `.github/workflows/azure-deploy.yml`.

## Required GitHub Repository Configuration

### Secrets (Repository Settings > Secrets and variables > Actions > Secrets)

Add the following repository secrets:

1. **AZURE_CREDENTIALS** - Azure service principal credentials in JSON format:
   ```json
   {
     "clientId": "your-client-id",
     "clientSecret": "your-client-secret", 
     "subscriptionId": "your-subscription-id",
     "tenantId": "your-tenant-id"
   }
   ```

2. **ACR_USERNAME** - Azure Container Registry username (usually the same as clientId)
3. **ACR_PASSWORD** - Azure Container Registry password (usually the same as clientSecret)

### Variables (Repository Settings > Secrets and variables > Actions > Variables)

Add the following repository variables:

1. **CONTAINER_IMAGE_NAME** - Name of your container image (e.g., "simplenodeapp")
2. **ACR_NAME** - Name of your Azure Container Registry (without .azurecr.io)
3. **AKS_RESOURCE_GROUP** - Azure resource group containing your AKS cluster
4. **AKS_CLUSTER_NAME** - Name of your AKS cluster

### Environment

Create an environment named "production" in your repository settings if you want deployment protection rules.

## Key Differences from Jenkins

| Jenkins Feature | GitHub Actions Equivalent |
|----------------|---------------------------|
| `BUILD_NUMBER` | `github.run_number` |
| Jenkins credentials | GitHub secrets |
| Jenkins environment variables | GitHub variables |
| `checkout scm` | `actions/checkout@v4` |
| Manual Azure CLI login | `azure/login@v2` action |
| Manual ACR login | `azure/docker-login@v2` action |
| Docker build/push commands | `docker/build-push-action@v6` |
| kubectl context setup | `azure/k8s-set-context@v4` action |

## Workflow Triggers

The GitHub Actions workflow triggers on:
- Push to `main` or `master` branch
- Manual workflow dispatch (via GitHub UI)

You can modify the triggers in the workflow file as needed.

## Advantages of GitHub Actions

1. **Better Integration**: Native integration with GitHub repository
2. **Action Marketplace**: Reusable actions for common tasks
3. **Improved Security**: Built-in secret management
4. **Cost Effective**: Included minutes for public repositories
5. **Better Logging**: Enhanced workflow visualization and logging
6. **Matrix Builds**: Easy parallel job execution
7. **Environments**: Built-in deployment environments and protection rules

## Migration Checklist

- [ ] Set up Azure service principal with appropriate permissions
- [ ] Add required secrets to GitHub repository
- [ ] Add required variables to GitHub repository  
- [ ] Create production environment (optional)
- [ ] Test the workflow with a manual trigger
- [ ] Remove or archive the Jenkins pipeline file
- [ ] Update any documentation referencing Jenkins builds

## Troubleshooting

1. **Azure login fails**: Verify AZURE_CREDENTIALS secret format and service principal permissions
2. **ACR login fails**: Check ACR_USERNAME and ACR_PASSWORD secrets
3. **AKS deployment fails**: Verify service principal has AKS permissions and cluster access
4. **Image not found**: Ensure ACR_NAME and CONTAINER_IMAGE_NAME variables are correct