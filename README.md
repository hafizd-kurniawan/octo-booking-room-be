# octo-booking-room-be
## Semantic Commit Messages
```txt
feat: (new feature for the user, not a new feature for build script)
fix: (bug fix for the user, not a fix to a build script)
docs: (changes to the documentation)
style: (formatting, missing semi colons, etc; no production code change)
refactor: (refactoring production code, eg. renaming a variable)
test: (adding missing tests, refactoring tests; no production code change)
chore: (updating grunt tasks etc; no production code change)
```

## Struktur Folder Project
```txt
src/main/java/com/octo/booking_room

├── BookingRoomApplication.java

├── config
│   └── DatabaseConfig.java

├── controller
│   ├── CustomerController.java
│   ├── RoomController.java
│   └── BookingController.java

├── service
│   ├── CustomerService.java
│   ├── RoomService.java
│   └── BookingService.java

├── repository
│   ├── CustomerRepository.java
│   ├── RoomRepository.java
│   ├── RoomTypeRepository.java
│   ├── BookingRepository.java
│   └── BookingSlotRepository.java

├── entity
│   ├── Customer.java
│   ├── Room.java
│   ├── RoomType.java
│   ├── Booking.java
│   └── BookingSlot.java

├── dto
│   ├── CustomerRequest.java
│   ├── BookingRequest.java
│   └── RoomResponse.java

├── mapper
│   └── CustomerMapper.java

├── exception
│   ├── GlobalExceptionHandler.java
│   └── NotFoundException.java

└── util
    └── IdGenerator.java

```

## Flow Arsitektur Project
```txt
Controller
   ↓
Service
   ↓
Repository
   ↓
Database (MySQL)
```
