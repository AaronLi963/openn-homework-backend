# User Points Service

## Overview
A high-performance microservice designed to handle user point transactions and real-time leaderboard rankings. The system balances immediate data persistence with fast, low-latency reads.

## Note
1. Some configurations are from environment variables, those are included in Makefile.
We can run the service by running
``
make run-app
``

2. You may need to update the `brokerIP1` in `broker.conf` and un-comment it.
Otherwise, you may get the connection issue. 



## Architecture
The service utilizes a layered architecture with the following components:
- **Spring Boot 3**: Core application framework.
- **MySQL**: Source of truth for all point transactions.
- **Redis**: 
  - **String Cache**: Stores individual user total points for fast retrieval.
  - **ZSet (Sorted Set)**: Maintains real-time leaderboard rankings.
- **RocketMQ**: Handles asynchronous event notification for downstream services.

### Data Flow
1. **Write**: Points are saved to MySQL -> RocketMQ message is sent -> Redis cache is cleared.
2. **Read**: Points are fetched from Redis cache -> On miss, recalculated from MySQL `SUM()` -> Results are re-cached.
3. **Ranking**: Leaderboard is served directly from Redis ZSet.

## Technical Decisions

### 1. Hard Delete Policy
We currently use **Hard Delete** for user records to accelerate development. 
- **Production Note**: In a production environment, Soft Delete is generally preferred to maintain audit trails, though the final choice depends on specific product and compliance requirements.

### 2. Cache Warm-up Strategy
On application launch, the service performs a **Full Cache Warm-up** for the leaderboard.
- **Context**: This ensures that even after a deletion, the leaderboard remains accurate by reloading all valid users from the DB.
- **Scalability Note**: This approach is suitable for **small-to-medium datasets**. For large-scale production systems, we would transition to incremental rebuilds or streaming synchronization to avoid long startup times.

### 3. Messaging Implementation
The `AddUserPointsHandler` currently acts as a placeholder that **prints the event message**. 
- **Current State**: It does not update scores or perform secondary logic.
- **Intent**: Designed to demonstrate the integration of RocketMQ into the transaction flow.

## Data Model
### Point (MySQL)
| Field     | Type | Description                    |
|-----------|------|--------------------------------|
| id        | Long | Primary Key                    |
| userId    | String | Unique User Identifier         |
| amount    | Integer | Points added/deducted          |
| reason    | String | Description of the transaction |
| createdAt | Timestamp | Record creation time           |
| updatedAt | Timestamp | Record updated time            |

## Consistency Model
- **Point Totals**: Follows a **Cache-Aside** pattern. Database is the source of truth. Cache is cleared on update to ensure eventual consistency.
- **Leaderboard**: Maintained via manual updates and startup warm-ups.

## API Examples

### Add Points
**POST** `/points`
```json
{
  "userId": "user_123",
  "amount": 100,
  "reason": "test"
}
```
**Response**:
```json
{
  "status": "success",
  "errorCode": 0,
  "errorMessage": null,
  "data": {
    "userId": "user_123",
    "totalPoints": 400
  }
}
```

### Get User Points
**GET** `/points/{userId}`
**Response**:
```json
{
  "status": "success",
  "errorCode": 0,
  "errorMessage": null,
  "data": {
    "userId": "user_123",
    "totalPoints": 400
  }
}
```

### Update Reason
**POST** `/points/{pointId}`
```json
{
  "reason": "test updating"
}
```
**Response**:
```json
{
  "status": "success",
  "errorCode": 0,
  "errorMessage": null,
  "data": {
    "id": 5,
    "userId": "user_123",
    "reason": "test updating",
    "amount": 100
  }
}
```

### Get Leaderboard
**GET** `/points/leaderboard`
**Response**:
```json
{
  "status": "success",
  "errorCode": 0,
  "errorMessage": null,
  "data": [
    {
      "userId": "user_123",
      "total": 400
    },
    {
      "userId": "user_1234",
      "total": 100
    }
  ]
}
```

### Delete User Points
**GET** `/points/{userId}`
**Response**:
```json
{
  "status": "success",
  "errorCode": 0,
  "errorMessage": null,
  "data": "Done"
}
```


## Event Message Format
**Topic**: `user-points-topic`
**Payload (JSON)**:
```json
{
  "id": 1001,
  "userId": "user_123",
  "amount": 100,
  "reason": "test",
  "timestamp": "2026-05-08T09:08:37.376+00:00"
}
```

## Error Handling
The service uses a centralized `GlobalExceptionHandler` and a standard `Response` wrapper.
- **Success**: `errorCode: 0`
- **Internal Error**: `errorCode: 500`
- **Validation Error**: Returns 400 Bad Request with specific field errors.

## Testing
- **Unit Tests**: Use Mockito for Service/Controller logic.
- **Integration Tests**: Use H2 (In-memory DB) and Embedded Redis.
- **Run**: `./mvnw test`

## Configuration
Requires the following environment variables:
- `MYSQL_HOST`, `MYSQL_USER`, `MYSQL_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`
- `ROCKETMQ_NAMESRV_ADDR`

## Future Improvements
1. **Soft Delete**: Implement `deleted_at` field for better data retention.
2. **Incremental Warm-up**: Optimize startup for large datasets using batch processing.
3. **Idempotency**: Add message keys to RocketMQ to prevent double-processing of point events.
4. **Enhanced Consumer**: Implement actual business logic in the `AddUserPointsHandler`.
