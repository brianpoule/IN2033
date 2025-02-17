# Risk Assessment v1


```
title: Risk management main overview
author: Filippo Vicini
date of publication: 17/02/2024
version number: v1.0
```

## Risk Assessment

This document contains a nullptr risk assessment.

| Risk ID | Description                                    | Current (L) | Current (I) | Current (S) | Owner | Date        | Mitigation                                                | Residual (L) | Residual (I) | Residual (S) |
|---------|-----------------------------------------------|-------------|-------------|-------------|-------|-------------|-----------------------------------------------------------|--------------|--------------|--------------|
| **R1**  | *Developer’s computer fails and data is lost* | 2           | 5           | 10          | FV    | 17 Feb 2025 | Setup and maintain a daily offsite backup of all data. | 2            | 2            | 4            |
| **R2**  | *Communication Gaps* | 3           | 3           | 6          | FV    | 17 Feb 2025 | Regular standup meetings, effective chat communication, clear project strcuture and repository | 1        | 2            | 2        |
| **R3**  | *Mismatch betwen DB specifications* | 3           | 3           | 6          | FV    | 17 Feb 2025 | Clear communication between different teams with effective documentation written | 2            | 1            | 2          |
| **R4**  | *Dependency issues* | 2 | 3 | 5 | Mismatch in developers versions and dependency during collaboration solved by using `direnv` or similar technologies to keep dependencies under contol | 1 | 1 |1| 


**Legend:**
- **L** = Likelihood (1-5)
- **I** = Impact (1-5)
- **S** = Severity (1-10)
