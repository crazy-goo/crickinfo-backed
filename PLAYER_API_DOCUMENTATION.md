# CrickInfo Player API Documentation

## Overview
This API provides CRUD operations for managing cricket player data in the CrickInfo application.

## Base URL
```
http://localhost:8080
```

## Authentication
Most endpoints require JWT authentication. Obtain a token by logging in as admin:
- **POST** `/api/auth/admin/login`
- Body: `{"username": "admin", "password": "admin123"}`
- Use the returned token in `Authorization: Bearer <token>` header.

## Endpoints

### 1. Get All Players
- **Method**: GET
- **URL**: `/api/players`
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer <jwtToken>`
- **Response**: Array of player objects

### 2. Get Player by ID
- **Method**: GET
- **URL**: `/api/players/{id}`
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer <jwtToken>`
- **Response**: Single player object

### 3. Create Player
- **Method**: POST
- **URL**: `/api/players`
- **Headers**:
  - `Authorization: Bearer <jwtToken>`
- **Body**: Form-data
  - `data`: (Text) JSON string of the player object (without imageUrl and video/thumbnail URLs)
  - `image`: (File) Player image file (optional)
  - `videos`: (File) Video files (multiple, optional)
  - `thumbnails`: (File) Thumbnail files (multiple, optional, must match videos count)
- **Response**:
  ```json
  {
    "message": "Player created successfully",
    "success": true,
    "player": { ... player object with URLs set ... }
  }
  ```

### 4. Update Player
- **Method**: PUT
- **URL**: `/api/players/{id}`
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer <jwtToken>`
- **Body**: Same as Create, with updated values
- **Response**: Updated player object

### 5. Delete Player
- **Method**: DELETE
- **URL**: `/api/players/{id}`
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer <jwtToken>`
- **Response**: 204 No Content

## Data Models

### Player
- `id`: string (MongoDB ObjectId)
- `player`: PlayerInfo
- `battingStats`: BattingStats
- `bowlingStats`: BowlingStats
- `strengths`: array of strings
- `weaknesses`: array of strings
- `videos`: array of Video
- `ratings`: Ratings
- `meta`: Meta

### PlayerInfo
- `name`: string
- `age`: integer
- `country`: string
- `team`: string
- `role`: string
- `battingStyle`: string
- `bowlingStyle`: string
- `jerseyNumber`: integer
- `imageUrl`: string

### BattingStats
- `matches`: integer
- `innings`: integer
- `runs`: integer
- `average`: float
- `strikeRate`: float
- `highestScore`: integer
- `centuries`: integer
- `halfCenturies`: integer
- `fours`: integer
- `sixes`: integer

### BowlingStats
- `overs`: float
- `wickets`: integer
- `economy`: float
- `bowlingAverage`: float
- `bestFigures`: string
- `maidens`: integer

### Video
- `title`: string
- `type`: string
- `description`: string
- `videoUrl`: string
- `thumbnailUrl`: string

### Ratings
- `fitness`: integer
- `consistency`: integer
- `fielding`: integer

### Meta
- `createdBy`: string
- `createdAt`: LocalDateTime
- `updatedAt`: LocalDateTime

## Error Handling
- 200: Success
- 204: No Content (for DELETE)
- 400: Bad Request
- 401: Unauthorized
- 404: Not Found
- 500: Internal Server Error
