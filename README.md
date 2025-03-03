# Simple Drive

### Description
The goal of the project is to implement a simplified Dropbox like Product, where users
can upload and download files through a Web Application.

### How to run
Prerequisites:
  - docker or podman for running backend and mongo containers
  - npm and vite for frontend

1. git clone and check out the main branch ```git clone https://github.com/mranish592/simple-drive.git```
2. ```cd simple-drive```
3. ```mkdir -p ./local_data/filstore```
4. ```docker-compose up``` or ```podman-compose up``` to start the mongodb and backend containers (please be a little patient. downloading all images and artifiacts might take upto 5 minutes.)
5. In a new terminal window, ```cd frontend```
6. ```npm install```
7. ```npm run dev```  to start the front end server (default port 5173)
8. Visit http://localhost:5173/ . Signup and enjoy your Simple Drive :)

### Tech stack
- Backend:
    - Language: Kotlin (Non-blocking Coroutines support with backward compatibility with robust java libraries)
    - Web framework: Ktor (Out of the box Coroutine support and Simple to implement and test for small applications)
    - DB: MongoDB 
    - File Storage: Local File Storage with adapters for s3 for future integration.
    - Authentication: access/refresh token via jwt
    - Build framework: maven
    - Containerisation: docker
- Frontend:
  - Language: Typescript
  - Framework: React
  - UI Library: ShadCN + tailwindcss
  - State management: redux toolkit

### APIs
Auth: 
  POST /auth/signup
  POST /auth/login
  POST /auth/refresh
  POST /auth/logout

Functional:
  POST /api/upload
  GET /download
  GET /list

### UI Features
- Allow users to sign up and log in using email and password.
- Persist the session of the user using jwt access and refresh tokens to avoid frequent re-logins
- Drag and drop files to upload
- A feature-rich table containing the file names, modified date and file size
- Ability to sort on different parameters
- Ability to search files by name.
