Yes — here’s a code audit with exact files and exact changes I recommend, without modifying anything.

# 1. Critical security fix: reservation creation trusts client `studentId`

## Problem
A logged-in user can potentially submit a reservation for another student because `studentId` comes from the request body.

## Files involved
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/reservation/ReservationRequest.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/web/ReservationController.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/reservation/ReservationService.java`

## Current issue
In `ReservationRequest.java`:
- remove client ownership input:
```java
@NotNull
private Integer studentId;
```

In `ReservationController.java`, current method:
```java
@PostMapping("/create")
@PreAuthorize("hasRole('STUDENT')")
public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ReservationRequest request) {
    return ResponseEntity.ok(service.createReservation(request));
}
```

In `ReservationService.java`, it trusts:
```java
Student student = studentService.getStudent(req.getStudentId());
...
student = studentService.deductBalance(req.getStudentId(), price);
...
log.info(... req.getStudentId() ...)
```

## Recommended change
### In `ReservationRequest.java`
- remove:
  - `studentId` field
  - `getStudentId()`
  - `setStudentId()`

### In `ReservationController.java`
Change the controller method to take `Authentication` and pass authenticated ID into the service:
```java
public ResponseEntity<Map<String, Object>> create(
        @Valid @RequestBody ReservationRequest request,
        Authentication authentication) {
    Integer studentId = (Integer) authentication.getPrincipal();
    return ResponseEntity.ok(service.createReservation(studentId, request));
}
```

### In `IReservationService.java`
Change method signature from:
```java
Map<String, Object> createReservation(ReservationRequest req);
```
to:
```java
Map<String, Object> createReservation(Integer studentId, ReservationRequest req);
```

### In `ReservationService.java`
Change signature and all internal usage:
```java
public Map<String, Object> createReservation(Integer studentId, ReservationRequest req)
```
Then replace:
- `req.getStudentId()` → `studentId`

This is the single most important fix.

# 2. Security bug: students can view other students’ profile and balance

## Problem
A student can call `/students/{id}` and `/students/{id}/balance` for another student because only role is checked, not ownership.

## File
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/web/StudentController.java`

## Current issue
```java
@GetMapping("/{id}")
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
public Student getStudent(@PathVariable int id) {
    return studentService.getStudent(id);
}
```

```java
@GetMapping("/{id}/balance")
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
public ResponseEntity<Map<String, Object>> getBalance(@PathVariable int id) {
    Student student = studentService.getStudent(id);
    return ResponseEntity.ok(Map.of("balance", student.getBalance()));
}
```

## Recommended change
Require `Authentication` in both methods and allow:
- admin: any ID
- student: only own ID

Pattern:
```java
Authentication authentication
```

Then compare:
- principal student ID
- roles/authorities

Example logic:
```java
Integer authenticatedId = (Integer) authentication.getPrincipal();
boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

if (!isAdmin && !authenticatedId.equals(id)) {
    throw new AccessDeniedException("Access denied");
}
```

Apply this to:
- `getStudent(...)`
- `getBalance(...)`

# 3. Reservation cancellation compares only time, not date+time

## Problem
Cancellation logic can behave incorrectly for reservations not on the current day because it compares only `startTime` with `LocalTime.now()`.

## File
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/reservation/ReservationService.java`

## Current code
```java
LocalTime start = LocalTime.parse(reservation.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));

if (!start.isAfter(LocalTime.now()))
    throw new ReservationConflictException("Cannot cancel a reservation that has already started");
```

## Why it’s wrong
If reservation is tomorrow at `08:00` and current time is `10:00`, this logic incorrectly says it already started.

## Recommended change
Build a `LocalDateTime` from:
- `reservation.getDate()`
- parsed `reservation.getStartTime()`

Then compare with `LocalDateTime.now()`.

Concept:
```java
LocalDateTime reservationStart = LocalDateTime.of(
    reservation.getDate(),
    LocalTime.parse(reservation.getStartTime(), formatter)
);

if (!reservationStart.isAfter(LocalDateTime.now())) {
    ...
}
```

# 4. Booking conflict detection is too weak

## Problem
Current booking conflict check only looks for exact `startTime`, not overlapping intervals.

## Files
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/reservation/ReservationRepository.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/reservation/ReservationService.java`

## Current code
Repository:
```java
boolean isSlotTaken(Integer machineId, String startTime, LocalDate date);
```

Query checks:
```sql
AND r.startTime = :startTime
```

## Why it’s weak
This only blocks exact same start times. It won’t catch overlaps if:
- one reservation lasts longer
- start times differ but time windows overlap
- cleanup buffer should block adjacent start windows

## Recommended change
### Option A: quick improvement in service
In `ReservationService.createReservation(...)`, before save:
- load active reservations for machine/date via `findByMachineIdAndDate`
- calculate requested interval
- compare against each existing interval plus cleanup buffer

### Option B: repository-level custom overlap query
Add a new repository method, something like:
```java
List<Reservation> findByMachineIdAndDate(...)
```
already exists, so service-side is easiest.

### Exact place to change
In `ReservationService.java`, replace:
```java
boolean exists = reservationRepo.isSlotTaken(req.getMachineId(), req.getStartTime(), req.getDate());

if (exists) {
    ...
}
```

with full interval overlap logic based on:
- requested start
- requested end or requested start + duration
- existing reservation start
- existing reservation start + washDuration + 15 min buffer

You may even remove `isSlotTaken(...)` entirely afterward.

# 5. `ReservationRequest` needs stronger validation

## File
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/reservation/ReservationRequest.java`

## Current weaknesses
Fields are present, but there is no validation for:
- time format
- end after start
- date not in past
- machine ID positive
- start/end matching duration
- wash type allowed values

## Recommended exact changes
### Add bean validation annotations
For example:
- `machineId` → `@Positive`
- `date` → `@FutureOrPresent`
- possibly regex/pattern on time strings if keeping strings

Change:
```java
@NotNull
private Integer machineId;
```
to:
```java
@NotNull @Positive
private Integer machineId;
```

Change:
```java
@NotNull
private LocalDate date;
```
to:
```java
@NotNull @FutureOrPresent
private LocalDate date;
```

If keeping string times:
```java
@Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Time must be in HH:mm format")
```
for:
- `startTime`
- `endTime`

### Add cross-field validation in service
In `ReservationService.java`, validate:
- `endTime` after `startTime`
- duration consistency

# 6. Cancelled reservations may leak into student history unexpectedly

## Problem
`findByStudentId` does not filter by status. This may be intended, but decide explicitly.

## File
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/reservation/ReservationRepository.java`

## Current code
```java
@Query("SELECT r FROM Reservation r WHERE r.student.studentId = :studentId ORDER BY r.date DESC, r.startTime DESC")
List<Reservation> findByStudentId(...)
```

## Recommendation
Decide one of:
- keep all reservations and expose status clearly
- or create two methods:
  - `findActiveByStudentId`
  - `findAllByStudentId`

Not a bug necessarily, but it should be intentional.

# 7. Use DTOs instead of returning entities directly

## Problem
Controllers expose JPA entities directly.

## Files
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/web/StudentController.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/web/ReservationController.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/web/WashmachineController.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/student/Student.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/reservation/Reservation.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/washmachine/Washmachine.java`

## Why
Returning entities couples API shape to persistence model and can cause:
- accidental field exposure
- serialization issues later
- harder refactoring

## Recommended exact changes
Create DTOs such as:
- `StudentResponse`
- `ReservationResponse`
- `WashmachineResponse`

Then change controller return types:
- `Student` → `StudentResponse`
- `List<Reservation>` → `List<ReservationResponse>`
- etc.

### Example exact spots
In `StudentController.java`:
```java
public Student register(...)
public Student getStudent(...)
public ResponseEntity<Student> loadBalance(...)
```
These should become DTO responses.

# 8. Improve login behavior and email normalization

## Files
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/student/StudentService.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/student/Student.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/web/LoginRequest.java`

## Problems
- email is not normalized to lowercase
- registration does not visibly check for duplicate email before save
- login returns `null` on failure instead of consistent exception-based flow

## Recommended changes
### In `StudentService.registerStudent(...)`
Before save:
- lowercase and trim email
- check `studentRepository.findByEmail(...)`
- throw explicit exception if already exists

### In `StudentService.login(...)`
Before lookup:
- lowercase/trim email

Replace `orElse(null)` pattern with explicit auth failure exception if you want cleaner flow.

### In `Student.java`
Optional:
- add DB uniqueness at entity level:
```java
@Column(unique = true, nullable = false)
private String email;
```
Right now email has validation annotations but no visible JPA uniqueness declaration.

# 9. Add explicit DB constraints at entity level

## Files
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/student/Student.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/reservation/Reservation.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/washmachine/Washmachine.java`

## Recommended exact changes
### `Student.java`
Make columns explicit:
- `email` unique + non-null
- `balance` non-null
- `role` non-null

Example:
```java
@Column(nullable = false, unique = true)
private String email;
```

```java
@Column(nullable = false)
private BigDecimal balance = BigDecimal.ZERO;
```

```java
@Column(nullable = false)
private String role = "STUDENT";
```

### `Reservation.java`
Make columns explicit for integrity:
- `status`
- `startTime`
- `endTime`
- `washDuration`
- maybe `price`

### `Washmachine.java`
Make:
- `type` non-null
- maybe `machineNr` unique if it is a business key

# 10. Replace string fields with enums

## Files
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/student/Student.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/reservation/Reservation.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/washmachine/Washmachine.java`
- service/controller/security code touching these values

## Problem
These are strings:
- role
- reservation status
- machine type
- machine status

This is fragile.

## Recommended exact changes
Create enums under appropriate packages:
- `Role`
- `ReservationStatus`
- `MachineType`
- maybe `MachineStatus`

Then in entities:
```java
@Enumerated(EnumType.STRING)
private Role role;
```

```java
@Enumerated(EnumType.STRING)
private ReservationStatus status;
```

Update code references such as:
- `"STUDENT"` → `Role.STUDENT`
- `"active"` → `ReservationStatus.ACTIVE`
- `"cancelled"` → `ReservationStatus.CANCELLED`

Also update `JwtFilter` / `JwtUtil` token role handling accordingly.

# 11. Use `LocalTime` instead of `String` for reservation times

## Files
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/reservation/Reservation.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/reservation/ReservationRequest.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/reservation/ReservationService.java`
- repository queries if needed

## Current issue
`startTime` and `endTime` are strings, causing repeated parsing and weaker typing.

## Recommended exact changes
### In `Reservation.java`
Change:
```java
private String startTime;
private String endTime;
```
to:
```java
private LocalTime startTime;
private LocalTime endTime;
```

### In `ReservationRequest.java`
Either:
- also use `LocalTime`
or
- keep strings externally and convert in controller/service

### In `ReservationService.java`
Remove repeated:
```java
LocalTime.parse(...)
```
where possible.

This is medium effort but worthwhile.

# 12. Fix generic exception leakage

## File
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/web/GlobalExceptionHandler.java`

## Current code
```java
@ExceptionHandler(Exception.class)
public ResponseEntity<Map<String, String>> handleGeneral(Exception e) {
    log.error("Unexpected error: {}", e.getMessage(), e);
    return ResponseEntity.internalServerError()
            .body(Map.of("error", e.getMessage()));
}
```

## Problem
This leaks raw exception messages to clients.

## Recommended change
Return generic message:
```java
.body(Map.of("error", "Internal server error"));
```

Keep detailed logging server-side only.

# 13. Add explicit auth handling for invalid JWTs

## Files
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/security/JwtFilter.java`
- maybe security config / auth entry point later

## Problem
Invalid tokens are silently ignored; request just proceeds unauthenticated. That can be acceptable, but API behavior may be inconsistent.

## Current code
```java
if (jwtUtil.isValid(token)) {
   ...
}
```

## Recommended change
For malformed/invalid bearer tokens:
- optionally short-circuit with `401`
- or keep current behavior intentionally, but document it

If improving:
- add custom authentication entry point / exception handling

This is not critical, but worth tightening.

# 14. Payment endpoint should validate payload with DTO, not raw `Map`

## Files
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/web/PaymentController.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/payment/PaymentService.java`

## Current code
```java
public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> body) throws Exception {
    double amountEuros = ((Number) body.get("amount")).doubleValue();
```

## Problems
- unsafe cast
- weak validation
- poor API contract

## Recommended exact changes
Create a request DTO, e.g. `CreatePaymentIntentRequest`:
```java
@NotNull
@DecimalMin("5.00")
private BigDecimal amount;
```

Then in controller:
```java
public ResponseEntity<Map<String, String>> createPaymentIntent(@Valid @RequestBody CreatePaymentIntentRequest request)
```

In service:
- prefer `BigDecimal` over `double` for money

# 15. Money should use `BigDecimal`, not `double`

## Files
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/payment/PaymentService.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/web/PaymentController.java`

## Current code
```java
public String createPaymentIntent(double amountEuros)
```

## Problem
Floating-point for money is risky.

## Recommended exact changes
Change method signature to:
```java
public String createPaymentIntent(BigDecimal amountEuros)
```

Then convert to cents safely:
```java
long amountCents = amountEuros.movePointRight(2).longValueExact();
```

# 16. CORS origins should be configurable

## File
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/infrastructure/security/CorsConfig.java`

## Current code
Origins are hardcoded:
```java
config.setAllowedOrigins(List.of(
    "http://localhost:3000",
    "https://waschsalon.onrender.com",
    "https://waschsalon.vercel.app"
));
```

## Recommended change
Move allowed origins to properties:
- `app.cors.allowed-origins=...`

Then inject with `@Value` or `@ConfigurationProperties`.

This avoids code changes for deployment environment updates.

# 17. Replace field injection with constructor injection

## Files
Nearly all service/controller/security classes:
- `StudentController.java`
- `ReservationController.java`
- `PaymentController.java`
- `ReservationService.java`
- `StudentService.java`
- `JwtFilter.java`
- `SecurityConfig.java`
- etc.

## Current pattern
```java
@Autowired
private IStudentService studentService;
```

## Recommended change
Use constructor injection:
```java
private final IStudentService studentService;

public StudentController(IStudentService studentService, JwtUtil jwtUtil) {
    this.studentService = studentService;
    this.jwtUtil = jwtUtil;
}
```

Benefits:
- easier testing
- immutable dependencies
- cleaner design

# 18. Move hardcoded business constants to config/constants

## Files
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/reservation/ReservationService.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/student/StudentService.java`
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/application/payment/PaymentService.java`

## Current hardcoded values
- opening: `06:00`
- closing: `23:00`
- slot size: `15`
- cleanup buffer: `15`
- min load: `5`
- min Stripe amount: `5`

## Recommended exact changes
Extract constants or config properties.

Example in `ReservationService.java`:
```java
private static final int SLOT_MINUTES = 15;
private static final int CLEANUP_BUFFER_MINUTES = 15;
private static final LocalTime OPENING_TIME = LocalTime.of(6, 0);
private static final LocalTime CLOSING_TIME = LocalTime.of(23, 0);
```

# 19. `Washmachine` has suspicious `users` field

## File
- `/home/seif/Waschsalon/src/main/java/com/start/waschmachine/domain/washmachine/Washmachine.java`

## Current field
```java
@Column(name = "users")
private String users;
```

## Concern
The meaning is unclear:
- current user?
- usage history?
- owner?
- display text?

## Recommended change
Decide whether to:
- remove it if unused
- rename it to something meaningful
- model relationship properly if it represents usage history

At minimum, audit whether frontend/backend actually use this field.

# 20. Swagger config is contradictory

## File
- `/home/seif/Waschsalon/src/main/resources/application.properties`

## Current config
```properties
springdoc.swagger-ui.enabled=false
springdoc.api-docs.enabled=false
springdoc.swagger-ui.path=/swagger-ui.html
```

## Issue
You include Springdoc dependency but disable it entirely.

## Recommended change
Either:
- remove dependency if not used
- or enable in local/dev only with profile-specific properties

Suggested split:
- keep disabled in `application-prod.properties`
- enable in local dev config

# 21. Add migration tooling instead of `ddl-auto=update`

## File
- `/home/seif/Waschsalon/src/main/resources/application.properties`
- `pom.xml`

## Current config
```properties
spring.jpa.hibernate.ddl-auto=update
```

## Risk
Fine for dev, not ideal for controlled production schema changes.

## Recommended exact changes
### In `pom.xml`
Add Flyway dependency.

### In properties
Change:
```properties
spring.jpa.hibernate.ddl-auto=validate
```
or disable schema mutation in prod.

### Add
- `src/main/resources/db/migration/...`

This is a strategic improvement.

# 22. Tests to add or adjust after fixes

## Files to add/update
- `/home/seif/Waschsalon/src/test/java/com/start/waschmachine/Reservation/ReservationControllerTest.java`
- `/home/seif/Waschsalon/src/test/java/com/start/waschmachine/Reservation/ReservationServiceTest.java`
- `/home/seif/Waschsalon/src/test/java/com/start/waschmachine/Student/StudentControllerTest.java`
- `/home/seif/Waschsalon/src/test/java/com/start/waschmachine/Reservation/ReservationFlowIntegrationTest.java`

## Exact test cases recommended
### Reservation auth
- student cannot create reservation for another user
- reservation creation ignores/removes client-supplied student ID

### Student ownership
- student cannot access another student’s profile
- student cannot access another student’s balance
- admin can access both

### Cancellation
- future-date reservation can be cancelled even if local clock time is later than start time
- started reservation cannot be cancelled

### Overlap logic
- overlapping reservation is rejected even if start times differ
- cleanup buffer blocks adjacent booking

# 23. Frontend/docs mismatch

## Files
- `/home/seif/Waschsalon/README.md`
- `/home/seif/Waschsalon/frontend/package.json`
- `/home/seif/Waschsalon/frontend/README.md`

## Exact issues
- root README says React 18
- `frontend/package.json` uses React 19
- `frontend/README.md` is default CRA text

## Recommended changes
- update root README stack versions
- replace frontend README with real project instructions:
  - env vars
  - API base URL
  - Stripe public key setup
  - build/start commands

# Priority order I recommend

## Do first
1. Fix reservation creation ownership
2. Restrict student profile/balance access
3. Fix cancellation date+time logic
4. Replace exact-start conflict check with real overlap logic
5. Stop leaking raw exception messages

## Do next
6. DTOs instead of entities
7. BigDecimal payment DTO
8. constructor injection
9. stronger request validation
10. enums + DB constraints

## Do later
11. Flyway
12. LocalTime migration
13. configurable CORS
14. frontend/docs cleanup

If you want, I can turn this into a checklist by file, grouped as:
- “edit this method”
- “remove this field”
- “change this signature”
- “add these tests”
