# Implementation Plan - Saathi: Strict Role-Based Access & Staff Deletion

This plan refines the authorization and team workflow:
1. **Super Admin Only (`harshsharma18089@gmail.com`):** Has access to the Admin Panel to add and delete staff members.
2. **Staff Members (`@saathi.team` or registered staff):** Routed strictly to `team_inbox` (Support Messaging Inbox). They cannot see or access the admin panel.
3. **Dynamic Student View:** Students see real-time staff members added by the admin in the Team Support Hub to chat and lighten their heart.

## User Review Required

> [!IMPORTANT]
> **Strict Role Separation:** Staff members only see the messaging inbox (`TeamInboxScreen`) with zero admin rights. Only `harshsharma18089@gmail.com` can add or delete staff. Staff members instantly appear in the student's Team Support Hub when added.

## Proposed Changes

### [Component: Admin Management]

#### [MODIFY] [AdminTeamManagementViewModel.kt](file:///D:/Saathi/app/src/main/java/harshzynx/saathi/ui/screens/AdminTeamManagementViewModel.kt)
- Add `deleteTeamMember(id: String)` so the admin can remove staff members.

#### [MODIFY] [AdminTeamManagementScreen.kt](file:///D:/Saathi/app/src/main/java/harshzynx/saathi/ui/screens/AdminTeamManagementScreen.kt)
- Add delete icon button on staff cards.

#### [MODIFY] [SplashViewModel.kt](file:///D:/Saathi/app/src/main/java/harshzynx/saathi/ui/screens/SplashViewModel.kt)
- Ensure robust check: `harshsharma18089@gmail.com` -> `admin_panel`, any other `@saathi.team` or staff email -> `team_inbox`, students -> `dashboard`.

## Verification Plan

### Manual Verification
- Log in as Super Admin (`harshsharma18089@gmail.com`), add a staff member, and delete a staff member.
- Log in as the staff member (`@saathi.team`), verify they open directly to `team_inbox` with zero admin access.
- Log in as a student, verify the added staff member appears in Team Support Hub.
