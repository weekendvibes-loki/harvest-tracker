# DBeaver PostgreSQL TimeZone Issue

## Problem

Connection failed with:

```
FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"
```

## Cause

DBeaver was starting the JVM with:

```
user.timezone=Asia/Calcutta
```

PostgreSQL 16 rejects this obsolete timezone identifier.

## Solution

Edit:

```
dbeaver.ini
```

Under:

```
-vmargs
```

Add:

```
-Duser.timezone=UTC
```

Restart DBeaver.

Connection works normally afterwards.