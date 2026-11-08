# Suggested Improvements from Pi — Checklist

## Highest priority

### 1. Fix reservation creation ownership
Files:
- `src/main/java/com/start/waschmachine/application/reservation/ReservationRequest.java`
- `src/main/java/com/start/waschmachine/application/reservation/IReservationService.java`
- `src/main/java/com/start/waschmachine/application/reservation/ReservationService.java`
- `src/main/java/com/start/waschmachine/infrastructure/web/ReservationController.java`

Checklist:
- [ ] Remove `studentId` from `ReservationRequest`
- [ ] Change service signature to `createReservation(Integer studentId, ReservationRequest req)`
- [ ] Read authenticated student ID from `Authentication` in `ReservationController#create`
- [ ] Pass authenticated student ID into the service instead of trusting request body
- [ ] Replace all `req.getStudentId()` usages with the authenticated `studentId`

### 2. Restrict student profile and balance access
Files:
- `src/main/java/com/start/waschmachine/infrastructure/web/StudentController.java`

Checklist:
- [ ] Add `Authentication` parameter to `getStudent(...)`
- [ ] Add `Authentication` parameter to `getBalance(...)`
- [ ] Allow admins to access any student
- [ ] Allow students to access only their own profile/balance
- [ ] Throw `AccessDeniedException` on cross-user access

### 3. Fix cancellation date-time logic
Files:
- `src/main/java/com/start/waschmachine/application/reservation/ReservationService.java`

Checklist:
- [ ] Replace time-only cancellation check with full `LocalDateTime` comparison
- [ ] Build reservation start from `reservation.getDate()` + parsed `startTime`
- [ ] Compare against `LocalDateTime.now()`

### 4. Replace weak slot conflict detection with overlap detection
Files:
- `src/main/java/com/start/waschmachine/application/reservation/ReservationService.java`
- `src/main/java/com/start/waschmachine/domain/reservation/ReservationRepository.java`

Checklist:
- [ ] Stop relying only on exact `startTime` equality for conflicts
- [ ] Load active reservations for machine/date
- [ ] Compare requested interval against existing reservation interval + cleanup buffer
- [ ] Reject overlapping bookings even if start times differ
- [ ] Remove `isSlotTaken(...)` if it becomes obsolete

### 5. Stop leaking raw exception messages
Files:
- `src/main/java/com/start/waschmachine/infrastructure/web/GlobalExceptionHandler.java`

Checklist:
- [ ] Keep server-side logging of full exception
- [ ] Return generic `Internal server error` to client in the fallback handler

---

## Next priority

### 6. Strengthen reservation request validation
Files:
- `src/main/java/com/start/waschmachine/application/reservation/ReservationRequest.java`
- `src/main/java/com/start/waschmachine/application/reservation/ReservationService.java`

Checklist:
- [ ] Add `@Positive` to `machineId`
- [ ] Add `@FutureOrPresent` to `date`
- [ ] Add time-format validation for `startTime` and `endTime`
- [ ] Validate `endTime > startTime`
- [ ] Validate duration consistency in service

### 7. Use DTOs instead of returning entities directly
Files:
- `src/main/java/com/start/waschmachine/infrastructure/web/StudentController.java`
- `src/main/java/com/start/waschmachine/infrastructure/web/ReservationController.java`
- `src/main/java/com/start/waschmachine/infrastructure/web/WashmachineController.java`
- new DTO classes under `infrastructure/web` or dedicated DTO package

Checklist:
- [ ] Create `StudentResponse`
- [ ] Create `ReservationResponse`
- [ ] Create `WashmachineResponse`
- [ ] Change controller return types from entities to DTOs
- [ ] Map entities to DTOs before returning responses

### 8. Improve login and registration robustness
Files:
- `src/main/java/com/start/waschmachine/application/student/StudentService.java`
- `src/main/java/com/start/waschmachine/domain/student/Student.java`

Checklist:
- [ ] Normalize email to lowercase + trim on registration
- [ ] Normalize email to lowercase + trim on login
- [ ] Check for duplicate email before saving a new student
- [ ] Throw explicit exception for duplicate registration
- [ ] Consider replacing login `null` return with explicit auth exception

### 9. Improve payment request typing and money handling
Files:
- `src/main/java/com/start/waschmachine/infrastructure/web/PaymentController.java`
- `src/main/java/com/start/waschmachine/application/payment/PaymentService.java`
- add payment DTO class

Checklist:
- [ ] Replace raw `Map<String, Object>` request with DTO
- [ ] Validate amount with bean validation
- [ ] Replace `double` with `BigDecimal` in payment service
- [ ] Convert euros to cents safely using `BigDecimal`

### 10. Replace field injection with constructor injection
Files:
- controllers, services, security classes

Checklist:
- [ ] Remove field-level `@Autowired` usages
- [ ] Add `final` fields for dependencies
- [ ] Add constructors for dependency injection

---

## Medium priority

### 11. Add explicit DB constraints
Files:
- `src/main/java/com/start/waschmachine/domain/student/Student.java`
- `src/main/java/com/start/waschmachine/domain/reservation/Reservation.java`
- `src/main/java/com/start/waschmachine/domain/washmachine/Washmachine.java`

Checklist:
- [ ] Make `Student.email` unique + non-null
- [ ] Make `Student.balance` non-null
- [ ] Make `Student.role` non-null
- [ ] Tighten `Reservation` column nullability where appropriate
- [ ] Make `Washmachine.type` non-null
- [ ] Decide whether `Washmachine.machineNr` should be unique

### 12. Replace string fields with enums
Files:
- `domain/student/Student.java`
- `domain/reservation/Reservation.java`
- `domain/washmachine/Washmachine.java`
- related service/security code

Checklist:
- [ ] Create `Role` enum
- [ ] Create `ReservationStatus` enum
- [ ] Create `MachineType` enum
- [ ] Optionally create `MachineStatus` enum
- [ ] Replace string literals with enum values
- [ ] Update JWT role handling if needed

### 13. Use `LocalTime` instead of `String` for reservation times
Files:
- `domain/reservation/Reservation.java`
- `application/reservation/ReservationRequest.java`
- `application/reservation/ReservationService.java`

Checklist:
- [ ] Change `startTime` type to `LocalTime`
- [ ] Change `endTime` type to `LocalTime`
- [ ] Update parsing/mapping code accordingly
- [ ] Reduce repeated `LocalTime.parse(...)` calls

### 14. Make CORS configuration externalized
Files:
- `src/main/java/com/start/waschmachine/infrastructure/security/CorsConfig.java`
- `src/main/resources/application.properties`

Checklist:
- [ ] Move allowed origins into configuration properties
- [ ] Inject configured origins into `CorsConfig`

### 15. Move business constants into config/constants
Files:
- `ReservationService.java`
- `StudentService.java`
- `PaymentService.java`

Checklist:
- [ ] Extract slot size constant
- [ ] Extract cleanup buffer constant
- [ ] Extract opening/closing hours
- [ ] Extract minimum load amount
- [ ] Extract minimum Stripe amount

---

## Lower priority / cleanup

### 16. Clarify or remove `Washmachine.users`
Files:
- `src/main/java/com/start/waschmachine/domain/washmachine/Washmachine.java`

Checklist:
- [ ] Audit whether `users` is used anywhere
- [ ] Remove it if unused
- [ ] Rename/remodel it if its meaning is different

### 17. Revisit Swagger/OpenAPI setup
Files:
- `src/main/resources/application.properties`
- `pom.xml`

Checklist:
- [ ] Decide whether Springdoc should stay
- [ ] If yes, enable it in local/dev only
- [ ] If not, remove dependency

### 18. Introduce proper DB migrations
Files:
- `pom.xml`
- `src/main/resources/application.properties`
- new `src/main/resources/db/migration/`

Checklist:
- [ ] Add Flyway dependency
- [ ] Replace `ddl-auto=update` with safer approach
- [ ] Add versioned migration files

### 19. Improve invalid JWT handling
Files:
- `src/main/java/com/start/waschmachine/infrastructure/security/JwtFilter.java`
- security config classes

Checklist:
- [ ] Decide whether invalid bearer tokens should return `401`
- [ ] Add custom auth entry point if needed
- [ ] Keep behavior consistent across secured endpoints

### 20. Clean up frontend/docs mismatch
Files:
- `README.md`
- `frontend/package.json`
- `frontend/README.md`

Checklist:
- [ ] Update root README to match actual frontend versions
- [ ] Replace default CRA frontend README
- [ ] Document frontend env vars and API base URL
- [ ] Document Stripe frontend setup

---

## Tests to add/update
Files:
- `src/test/java/com/start/waschmachine/Reservation/ReservationControllerTest.java`
- `src/test/java/com/start/waschmachine/Reservation/ReservationServiceTest.java`
- `src/test/java/com/start/waschmachine/Student/StudentControllerTest.java`
- `src/test/java/com/start/waschmachine/Reservation/ReservationFlowIntegrationTest.java`

Checklist:
- [ ] Test that a student cannot create a reservation for another student
- [ ] Test that cross-user student profile access is forbidden
- [ ] Test that cross-user balance access is forbidden
- [ ] Test that admin can access student data
- [ ] Test cancellation logic for future-date reservations
- [ ] Test overlap detection for non-identical start times
- [ ] Test cleanup buffer blocking
- [ ] Test invalid/expired JWT scenarios
