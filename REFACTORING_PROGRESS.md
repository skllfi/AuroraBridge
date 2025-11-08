## Phase 1: Code Consolidation and Cleanup

- [x] Consolidate duplicated `NotificationHelper.kt` files.
- [ ] Move all `ViewModel` classes to a unified `ui/vm` package.
- [x] Relocate `MainActivity.kt` and `MainApplication.kt` to a more appropriate package (e.g., `ui` or `app`).
- [ ] Create a dedicated `model` package for all data classes (e.g., `Settings.kt`, `Limiter.kt`).
- [ ] Standardize the use of `Context` across the application, preferring dependency injection or `ApplicationContext` where appropriate.

## Phase 2: Architectural Improvements

- [ ] Implement a repository pattern for data access, separating data sources from the UI.
- [ ] Introduce a dependency injection framework (e.g., Hilt) to manage dependencies and improve testability.
- [ ] Refactor `AdbCommander` to be a singleton or a scoped dependency, avoiding multiple instances.
- [ ] Convert `BrandAutoOptimizer` to a non-static class, allowing for easier testing and dependency management.
- [ ] Evaluate and improve the threading model, ensuring that long-running operations are off the main thread.

## Phase 3: UI and UX Enhancements
