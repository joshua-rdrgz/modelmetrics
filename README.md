# ModelMetrics

ModelMetrics is a full stack web platform that helps AI trainers visualize and keep track of their working hours. It's built using ReactJS as a frontend with Spring Boot as a backend.

# Deployments

Frontend is viewable at: [https://app.modelmetrics.tech](https://app.modelmetrics.tech). Sign up here via: [https://app.modelmetrics.tech/signup](https://app.modelmetrics.tech/signup)
Backend is viewable at: [https://api.modelmetrics.tech](https://api.modelmetrics.tech). You can test it via: [https://api.modelmetrics.tech/api/v1/test/public](https://api.modelmetrics.tech/api/v1/test/public).

# ENV Variables

You'll need these ENV variables at the root of the project to successfully get the project running in your local environment.

```
# MySQL database configuration
MYSQL_DATABASE=database-here
MYSQL_USER=user-here
MYSQL_PASSWORD=password-here
MYSQL_ROOT_PASSWORD=root-password-here

# JWT authentication configuration
JWT_SECRET_KEY=secret-key-here
JWT_EXPIRES_IN=expires-in-here
JWT_COOKIE_KEY=token-here

# Jasypt encryption configuration
JASPYT_PASSWORD=jasypt-password-here

# Application URLs
APP_FRONTEND_URL=frontend-url-here
```

# Running

The app uses Docker for containerization. To run in a local environment, clone the repo and use the make commands below to spin up and down the containers:

```bash
$ make up # builds images and spins them up
$ make down # spins down the containers
```
