# **Blog platform**

### **Overview**

📌 The goal is to build a blog-application, focusing on authentication and authorization using JWT tokens and Spring Security

⚙️ Current progress : working on Stripe API integration. Goal is to provide to only premium users access to the upcoming AI features.   
 
### Tech-stack and tools

- Java 21
- Maven 3.9
- Spring Boot
- Spring Data
- Spring Security
- Docker 28.1
- IntelliJ IDEA
- PostgreSQL
- Stripe API
- Redis
- Open API / Swagger documentation


### Features
- Stripe API integration for managing premium subscriptions
- JWT authentication / role-based authorization
- AI-generated front-end

### **Run locally**

#### Go to:

    application.properties 

#### and configure the following variables:

PostgreSQL credentials:

    db_username= {YOUR_USER}
    db_password= {YOUR_PASSWORD}

Redis password:

    spring.data.redis.password= {YOUR_PASSWORD}

JWT secret key (AES256, base 64):

    jwt.secret= {YOUR_SECRET}


Default administrator account credentials:

    admin_email= {YOUR_EMAIL}
    admin_password= {YOUR_PASSWORD}

#### Then go to:

    docker-compose.yml

#### and configure the following:

    POSTGRES_PASSWORD: {YOUR_PASSWORD}
    POSTGRES_USER: {YOUR_USER}
    
    command: redis-server --requirepass {YOUR_PASSWORD}

⚠️ _NOTE: must match values in the application.properties_

#### Open the terminal in your IDE and type:

- > mvn clean install
- > docker compose up
- > mvn spring-boot:run

### Run with the front-end
#### Tech stack:
- React 
- Vite 
- TypeScript 
- Tailwind


#### Open the terminal in your IDE and type:

    git pull https://github.com/nazar2312/frontend

    cd frontend

    npm install

    npm run dev



[MIT LICENSE](LICENSE)

