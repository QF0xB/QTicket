plugins {
    id("qticket.spring-library")
}

dependencies {
    // keep this module lean initially; later you add:
    // - outbox base classes
    // - JSON serialization helpers
    // - idempotency utilities
}