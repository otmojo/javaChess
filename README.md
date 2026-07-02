# Local Chess (Archive)

This branch preserves an earlier local version of the project.

It implements the core mechanics of a chess game and serves as the foundation for the later online multiplayer implementation.

## Features

- Local two-player chess
- Chess rule validation
- Move history
- PostgreSQL database integration
- Save completed games
- Replay saved games from the database

## Why this version matters

Although this branch only supports local gameplay, building it revealed that online multiplayer is much more than simply adding networking.

Moving from a local application to an online system required rethinking several aspects of the architecture, including:

- Maintaining an authoritative game state on the server instead of relying on each client.
- Synchronizing game state between remote players while preventing conflicts.
- Handling different board perspectives for White and Black without affecting the underlying game logic.
- Detecting game-ending conditions (checkmate, stalemate, resignation, timeout) consistently across all clients.
- Designing room creation, matchmaking, and player session management.
- Deciding how clients should receive updates (polling versus event-driven communication).
- Persisting games in a cloud-hosted PostgreSQL database for replay and recovery.
- Managing player reconnections without losing the current game state.

This local implementation became the foundation for the later online version and significantly influenced the overall architecture of the project.

## Note

This branch is preserved as an archive of the standalone local implementation.

The later online version extends this work with multiplayer networking, cloud deployment (Railway + PostgreSQL), online game rooms, and synchronized gameplay.

Keeping this branch documents the transition from a single-process application to a distributed multiplayer system, and the architectural changes required to support that evolution.

One of the biggest lessons from this project was realizing that networked multiplayer is primarily an architecture problem rather than a networking problem.
