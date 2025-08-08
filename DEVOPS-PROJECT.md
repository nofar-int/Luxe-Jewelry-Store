# 🚀 CI/CD & Containers & Webservers - DevOps Project
## Luxe Jewelry Store Microservices Application

## 📖 Project Overview

In this comprehensive DevOps project, you will take the **Luxe Jewelry Store** - a complete microservices e-commerce application - and implement modern CI/CD practices, containerization, and deployment strategies. This project showcases real-world DevOps skills including Docker/Podman containerization, Jenkins CI/CD pipelines, security scanning, testing automation, and deployment orchestration.

While we have provided the Luxe Jewelry Store application as an example, you are not limited to this option. The purpose of this project is to allow you to showcase your skills and creativity, and therefore you have the freedom to choose any app or programming language that aligns with your interests and strengths. You can also use **Podman instead of Docker** if you prefer.

### 🏗️ Application Architecture

The Luxe Jewelry Store consists of three microservices:

1. **React Frontend** (Port 3000) - Modern e-commerce UI with shopping cart and authentication
2. **Main API Service** (Port 8000) - Product catalog and cart management (FastAPI/Python)
3. **Authentication Service** (Port 8001) - User registration, login, JWT authentication (FastAPI/Python)

## 🎯 DevOps Learning Objectives

This project will teach you essential **DevOps and Site Reliability Engineering** skills:

### **Containerization & Orchestration**
- **Docker/Podman**: Container creation, multi-stage builds, optimization
- **Docker Compose**: Multi-container application orchestration
- **Container Registries**: Docker Hub, Nexus repository management
- **Container Security**: Vulnerability scanning with Snyk

### **CI/CD Pipeline Development**
- **Jenkins**: Pipeline as Code with Jenkinsfile
- **Automated Testing**: Unit tests, linting, static code analysis
- **Build Automation**: Docker image building and tagging strategies
- **Deployment Automation**: Container deployment and rollout strategies

### **Infrastructure & Security**
- **Jenkins Agents**: Docker-based build agents
- **Security Scanning**: Container vulnerability assessment
- **Code Quality**: Pylint static analysis and reporting
- **Shared Libraries**: Reusable Jenkins pipeline components

## 🏗️ Project Structure

```
luxe-jewelry-devops/
├── src/                           # Application source code
│   ├── jewelry-store/            # React Frontend (Port 3000)
│   ├── backend/                  # Main API Service (Port 8000)
│   └── auth-service/             # Authentication Service (Port 8001)
├── infra/                        # Infrastructure as Code
│   ├── docker/                   # Dockerfiles and configurations
│   ├── docker-compose.yml        # Local orchestration
│   ├── jenkins/                  # Jenkins configurations
│   └── nexus/                    # Nexus repository setup
├── test/                         # Test suites
│   ├── unit/                     # Unit tests
│   ├── integration/              # Integration tests
│   └── security/                 # Security test configurations
├── Jenkinsfile                   # CI/CD Pipeline definition
├── .pylintrc                     # Python linting configuration
└── README.md                     # Project documentation
```

## 🛠️ Technology Stack

### **Frontend**
- **React 18** - Modern React with hooks and state management
- **CSS3** - Custom styling with glassmorphism effects
- **Fetch API** - HTTP client for API communication

### **Backend Services**
- **FastAPI** - Modern Python web framework
- **Pydantic** - Data validation and serialization
- **JWT** - JSON Web Tokens for authentication
- **Bcrypt** - Password hashing and security
- **Uvicorn** - ASGI server for Python services

### **DevOps Tools**
- **Docker/Podman** - Containerization platform
- **Jenkins** - CI/CD automation server
- **Nexus** - Artifact and container registry
- **Snyk** - Security vulnerability scanning
- **Pylint** - Python static code analysis

## 📋 Project Requirements & Grading

### **Section 1: Containerizing the Application (25 points)**

#### **1.1 Create Dockerfiles for Each Service**
- **Frontend Dockerfile**: Containerize React application with Nginx
- **Backend API Dockerfile**: Containerize FastAPI main service  
- **Auth Service Dockerfile**: Containerize authentication microservice

**Requirements:**
- Choose appropriate base images (e.g., official Python, Node.js, Nginx images)
- Use multi-stage builds where beneficial
- Optimize for production (minimize image size, security best practices)
- Set proper working directories and copy application code
- Install dependencies and set environment variables
- Expose necessary ports and define startup commands

**Example Dockerfile Structure:**
```dockerfile
# Use the official Python image as the base image
FROM python:3.9-slim

# Set the working directory to /app
WORKDIR /app

# Copy the requirements file and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the app code
COPY . .

# Set environment variables
ENV FLASK_APP=main.py

# Expose the port
EXPOSE 8000

# Start the app
CMD ["python", "main.py"]
```

#### **1.2 Docker Compose Orchestration**
Create a `docker-compose.yml` file that:
- Defines services for frontend, backend, and auth service
- Configures service networking and dependencies
- Sets up environment variables and volume mounts
- Ensures proper startup order and health checks
- Maps ports to host machine

#### **1.3 Container Registry**
- Create a **private registry** in Docker Hub
- Push all application images to the private registry
- Implement proper image tagging strategy

### **Section 2: CI/CD Pipeline with Jenkins (75 points)**

#### **2.1 Jenkins Setup and Configuration (0 points - prerequisite)**
- Install Jenkins on your server
- Configure necessary plugins for Docker, Python, and pipeline management
- Set up GitHub webhook integration (skip if running locally without public IP)
- Configure webhook events: Pushes and Pull requests

#### **2.2 Docker-based Jenkins Agent (10 points)**
Create a custom Jenkins agent Docker image using the provided multi-stage Dockerfile:

```dockerfile
FROM amazonlinux:2 as installer

RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
RUN yum update -y \
 && yum install -y unzip \
 && unzip awscliv2.zip \
 && ./aws/install --bin-dir /aws-cli-bin/

RUN mkdir /snyk && cd /snyk \
   && curl https://static.snyk.io/cli/v1.666.0/snyk-linux -o snyk \
   && chmod +x ./snyk

FROM jenkins/agent
COPY --from=docker /usr/local/bin/docker /usr/local/bin/
COPY --from=installer /usr/local/aws-cli/ /usr/local/aws-cli/
COPY --from=installer /aws-cli-bin/ /usr/local/bin/
COPY --from=installer /snyk/ /usr/local/bin/
COPY --from=installer /snyk/ /usr/bin/

USER root
RUN apt-get update && apt-get install -y python3 python3-pip
USER jenkins
```

**Configure Jenkinsfile to use Docker agent:**
```groovy
agent {
    docker {
        image '<your-image-url>'
        args  '--user root -v /var/run/docker.sock:/var/run/docker.sock'
    }
}
```

#### **2.3 Build Stage Implementation (10 points)**
Configure the build stage in Jenkinsfile:

```groovy
stage('Build app') {
   steps {
       sh '''
            docker login --username <username> --password <password>
            docker build -t <image-name> .
            docker tag <image-name> <registry>/<image-name>:<tag>
            docker push <registry>/<image-name>:<tag>
       '''
   }
}
```

**Tagging Strategy (be creative for full points):**
- Use `BUILD_NUMBER`, `BUILD_TAG`, or timestamp
- Consider semantic versioning (semver)
- Use git commit hash for traceability
- Tag with 'latest' for main branch builds

#### **2.4 Pipeline Features and Best Practices (5 points)**
Add these directives to your Jenkinsfile:

```groovy
pipeline {
    options {
        buildDiscarder(logRotator(daysToKeepStr: '30'))
        disableConcurrentBuilds()
        timestamps()
    }
    
    environment {
        // Global variables
        DOCKER_REGISTRY = 'your-registry-url'
        IMAGE_NAME = 'luxe-jewelry-store'
    }
    
    post {
        always {
            // Cleanup built Docker images
            sh 'docker image prune -f'
        }
    }
}
```

#### **2.5 Security Vulnerability Scanning (5 points)**
Integrate Snyk container scanning:

1. Sign up for Snyk account and get API token
2. Create Jenkins secret text credential with Snyk API token
3. Add security scanning stage:

```groovy
stage('Security Scan') {
    steps {
        withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
            sh '''
                snyk container test ${IMAGE_NAME}:${BUILD_NUMBER} --severity-threshold=high
            '''
        }
    }
}
```

**Optional Bonus (5 points):** Create ignore file for specific vulnerabilities and configure Snyk to use it.

#### **2.6 Unit Testing Integration (5 points)**
Create comprehensive unit test suite:

1. Create `test/` directory with test files
2. Install testing requirements: `pytest`, `unittest2`
3. Add unit test stage:

```groovy
stage('Unit Tests') {
    steps {
        sh '''
            pip3 install pytest unittest2
            python3 -m pytest --junitxml results.xml test/*.py
        '''
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: 'results.xml'
        }
    }
}
```

#### **2.7 Code Quality and Linting (5 points)**
Implement Pylint static code analysis:

1. Install Pylint: `pip install pylint`
2. Generate configuration: `pylint --generate-rcfile > .pylintrc`
3. Add linting stage with parallel execution:

```groovy
stage('Quality Checks') {
    parallel {
        stage('Unit Tests') {
            steps {
                sh 'python3 -m pytest --junitxml results.xml test/*.py'
            }
        }
        stage('Linting') {
            steps {
                sh 'python3 -m pylint -f parseable --reports=no *.py > pylint.log'
            }
            post {
                always {
                    recordIssues(
                        enabledForFailure: true,
                        aggregatingResults: true,
                        tools: [pyLint(name: 'Pylint', pattern: '**/pylint.log')]
                    )
                }
            }
        }
    }
}
```

#### **2.8 Nexus Repository Integration (10 points)**
1. Install and configure Nexus repository manager
2. Create Docker registry in Nexus
3. Configure Jenkins pipeline to push images to Nexus
4. Implement environment-based deployment strategy using environment variables

#### **2.9 Jenkins Shared Libraries (10 points)**
1. Create separate GitHub repository for shared library
2. Structure shared library with proper directory layout
3. Configure Jenkins to use global shared library
4. Refactor main Jenkinsfile to use shared library functions
5. Implement reusable functions for common pipeline tasks

#### **2.10 Deployment Stage (10 points)**
Add deployment stage:

```groovy
stage('Deploy') {
    steps {
        sh '''
            docker-compose down
            docker-compose pull
            docker-compose up -d
        '''
    }
}
```

#### **2.11 Pipeline Organization (5 points)**
Organize stages in logical order:
1. **Checkout** - Source code retrieval
2. **Build** - Docker image creation and registry push
3. **Quality Checks** - Unit tests and linting (parallel)
4. **Security Scan** - Vulnerability assessment
5. **Deploy** - Application deployment

## 📤 Submission Requirements

### **GitHub Repository Setup**
1. Create GitHub account if needed
2. Create new repository named "project-int"
3. Organize code in proper directory structure
4. Create comprehensive README with:
   - Clear instructions on how to run the code
   - Where you ran the project (local/cloud)
   - Dependencies and setup instructions
   - Detailed steps for running, testing, and deploying

### **Required Deliverables**
1. **Source Code**: Complete application code in `src/` directory
2. **Infrastructure Code**: Dockerfiles, docker-compose.yml in `infra/` directory
3. **CI/CD Pipeline**: Jenkinsfile with all required stages
4. **Test Suite**: Unit tests and linting configuration in `test/` directory
5. **Documentation**: Comprehensive README and setup instructions

### **Evaluation Process**
- **Code Review**: Instructor will review your GitHub repository
- **Project Defense**: Scheduled Zoom session to demonstrate your project
- **Live Demo**: Run your project and answer questions about implementation
- **Deadline**: Submit GitHub repository link before specified deadline

## 🎯 Success Criteria

### **Functionality (60%)**
- All services run correctly in containers
- CI/CD pipeline executes successfully
- Automated testing and deployment work
- Security scanning identifies and reports vulnerabilities

### **Best Practices (25%)**
- Proper Docker best practices implementation
- Clean, well-organized code structure
- Appropriate use of environment variables
- Effective error handling and logging

### **Documentation & Presentation (15%)**
- Clear, comprehensive documentation
- Effective demonstration during project defense
- Understanding of implemented solutions
- Ability to explain design decisions

## 🚀 Getting Started

1. **Fork or Clone** the Luxe Jewelry Store repository
2. **Set up local development** environment and verify application works
3. **Create project structure** with src/, infra/, and test/ directories
4. **Start with containerization** - create Dockerfiles for each service
5. **Set up Docker Compose** for local orchestration
6. **Install and configure Jenkins** with required plugins
7. **Create Jenkinsfile** and implement CI/CD stages incrementally
8. **Test and refine** your pipeline with each new stage
9. **Document everything** as you build

## 💡 Tips for Success

- **Start Simple**: Begin with basic Dockerfiles and gradually add complexity
- **Test Locally**: Ensure everything works locally before adding to pipeline
- **Incremental Development**: Add one pipeline stage at a time
- **Use Version Control**: Commit frequently with meaningful messages
- **Document Issues**: Keep track of problems and solutions for your README
- **Be Creative**: Explore different approaches and tools beyond minimum requirements

Good luck with your DevOps journey! 🚀
