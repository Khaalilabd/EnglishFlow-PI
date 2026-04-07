# WebSocket Implementation - Sponsors Service

## Overview
Real-time notifications for sponsor CRUD operations using Spring WebSocket (STOMP over SockJS).

## Backend Architecture

### Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### Configuration
- **Endpoint**: `/ws` (with SockJS fallback)
- **Topic**: `/topic/sponsors` (broadcast to all subscribers)
- **App Prefix**: `/app` (for client messages)
- **Heartbeat**: 10 seconds

### Components

#### 1. WebSocketConfig
- Enables WebSocket message broker
- Configures STOMP endpoints
- Sets up heartbeat for connection monitoring

#### 2. SponsorNotificationDTO
```java
{
  "type": "CREATED" | "UPDATED" | "DELETED",
  "sponsorId": 123,
  "sponsorName": "Company XYZ",
  "message": "Sponsor created: Company XYZ",
  "timestamp": "2026-03-27T22:30:00",
  "sponsor": { /* Full SponsorDTO for CREATED/UPDATED */ }
}
```

#### 3. WebSocketNotificationService
- `notifySponsorCreated(SponsorDTO)`: Broadcast when sponsor is created
- `notifySponsorUpdated(SponsorDTO)`: Broadcast when sponsor is updated
- `notifySponsorDeleted(Long, String)`: Broadcast when sponsor is deleted

#### 4. SponsorService Integration
- Calls WebSocket notification after each CRUD operation
- Notifications sent AFTER successful database save
- Includes full sponsor data in notifications

## Frontend Architecture

### Service: SponsorWebSocketService
- Connects to `/sponsors-service/ws` endpoint
- Subscribes to `/topic/sponsors`
- Provides Observable for notifications
- Auto-reconnect on disconnect

### Integration: DataSyncService
- Coordinates WebSocket updates across all services
- Provides `onSponsorDataChanged()` Observable
- Allows manual refresh triggers

### Component: SponsorsListComponent
- Auto-connects to WebSocket on init
- Listens for sponsor notifications
- Updates UI in real-time without page refresh
- Shows connection status indicator
- Cleans up on destroy

## Usage Flow

### Create Sponsor
1. User submits form → POST `/sponsors`
2. Backend saves to DB
3. Backend sends WebSocket notification to `/topic/sponsors`
4. All connected clients receive notification
5. Clients update their local sponsor list

### Update Sponsor
1. User updates form → PUT `/sponsors/{id}`
2. Backend updates DB
3. Backend sends WebSocket notification
4. Clients update the specific sponsor in their list

### Delete Sponsor
1. User confirms deletion → DELETE `/sponsors/{id}`
2. Backend deletes from DB
3. Backend sends WebSocket notification
4. Clients remove sponsor from their list

## Benefits
- Real-time updates across all connected clients
- No polling required
- Reduced server load
- Better user experience
- Automatic cache invalidation

## Testing
1. Open sponsors list in two browser tabs
2. Create/update/delete a sponsor in one tab
3. Observe real-time update in the other tab
4. Check browser console for WebSocket logs

## Connection URL
```
ws://localhost:8087/ws (via API Gateway: ws://localhost:8080/sponsors-service/ws)
```

## Monitoring
- Connection status shown in UI
- Console logs for all WebSocket events
- Backend logs for notification sending
