# octo-booking-room-be
## Running Project 
```bash
./mvnw spring-boot:run # linux
./mvn spring-boot:run  # NT

docker compose down
docker compose up --build
docker compose logs -f backend
docker exec -it booking_mysql mysql -u root -p
```

## Required Environment Variables
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/booking_room?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password
JWT_SECRET=minimum_32_byte_random_secret_value_here
```

Project ini tidak lagi mengirim default database credential, JWT secret, atau akun demo bawaan di migration. Admin sebaiknya diprovision secara aman per environment.
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
