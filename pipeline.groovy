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
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Azure Login') {
            steps {
                withCredentials([file(credentialsId: 'AZURE_CREDENTIALS', variable: 'AZURE_CREDS_FILE')]) {
                    sh '''
                        az login --service-principal --username $(jq -r .clientId $AZURE_CREDS_FILE) \
                            --password $(jq -r .clientSecret $AZURE_CREDS_FILE) \
                            --tenant $(jq -r .tenantId $AZURE_CREDS_FILE)
                        az account set --subscription $(jq -r .subscriptionId $AZURE_CREDS_FILE)
                    '''
                }
            }
        }

        stage('ACR Login') {
            steps {
                sh 'az acr login --name $ACR_NAME'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def imageTag = "${env.BUILD_NUMBER}"
                    sh "docker build -t $REGISTRY/$IMAGE_NAME:${imageTag} ."
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    def imageTag = "${env.BUILD_NUMBER}"
                    sh "docker push $REGISTRY/$IMAGE_NAME:${imageTag}"
                }
            }
        }

        stage('Set AKS Context') {
            steps {
                sh '''
                    az aks get-credentials --resource-group $AKS_RESOURCE_GROUP --name $AKS_CLUSTER_NAME --overwrite-existing
                '''
            }
        }

        stage('Deploy to AKS') {
            steps {
                script {
                    def imageTag = "${env.BUILD_NUMBER}"
                    sh """
                        kubectl set image deployment/$IMAGE_NAME $IMAGE_NAME=$REGISTRY/$IMAGE_NAME:${imageTag} --record || true
                        kubectl rollout status deployment/$IMAGE_NAME || true
                    """
                }
            }
        }
    }
}
