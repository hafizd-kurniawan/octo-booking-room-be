# octo-booking-room-be
```txt
## Struktur Folder Project

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

```txt
## Flow Arsitektur Project
Controller
   ↓
Service
   ↓
Repository
   ↓
Database (MySQL)
```
