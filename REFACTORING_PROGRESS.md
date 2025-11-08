# Refactoring Progress

This document tracks the progress of refactoring the codebase to improve its structure, readability, and maintainability.

## Phase 1: Code Consolidation and Cleanup

- [ ] **Consolidate Duplicate Classes:**
    - [x] `BrandAutoOptimizer.kt` (3 files)
    - [x] `AdbAnalyzer.kt` (2 files)
    - [x] `MainActivity.kt` (2 files)
    - [ ] `NotificationHelper.kt` (2 files)
    - [ ] `AppMonitorWorker.kt` (2 files)
    - [ ] Consolidate all ViewModels into a single package.

## Phase 2: Package Reorganization

- [ ] **Restructure Packages:**
    - [ ] Create `com.aurorabridge.optimizer.data` for models.
    - [ ] Move all ViewModels to `com.aurorabridge.optimizer.ui.viewmodel`.
    - [ ] Move all UI screens to `com.aurorabridge.optimizer.ui.screens`.
    - [ ] Move all services and workers to `com.aurorabridge.optimizer.service`.
    - [ ] Move all utility classes to `com.aurorabridge.optimizer.util`.

## Phase 3: Naming Convention Standardization

- [ ] **Rename UI Components:**
    - [ ] Ensure all screen composables have a `Screen` suffix.

