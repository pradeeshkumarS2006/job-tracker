# job-tracker

# Job Application Tracker — My 10x Solution

A backend system that turns messy job-application tracking (spreadsheets, notebooks, memory)
into a structured pipeline with status tracking, automatic follow-up reminders, and
AI-assisted resume tailoring per job description.

## The problem

Students applying to multiple companies lose track of which resume version was sent where,
forget to follow up after a week of silence, and rewrite resumes from scratch for every
job description instead of tailoring a base resume. This tool fixes all three.

## 10x claim

Tracking 10+ applications goes from "which spreadsheet did I update last?" to one API-backed
system that tells you daily what needs a follow-up, and suggests tailored resume bullets in seconds.

## Concepts implemented (5+ required)

| # | Concept | Where it lives |
|---|---------|----------------|
| 1 | API endpoints | `controller/ApplicationController.java`, `AuthController.java` — full CRUD + status patch |
| 2 | Database | H2 file-based DB (`entity/Application.java`, `entity/User.java`) — persists across restarts |
| 3 | Authentication | `security/JwtUtil.java`, `JwtAuthFilter.java`, `config/SecurityConfig.java` — JWT-protected routes |
| 4 | Background/cron job | `service/FollowUpSchedulerService.java` — daily scan flags applications with no update in 7+ days |
| 5 | LLM integration | `service/ResumeSuggestionService.java` — calls Anthropic API for tailored resume bullets, with input validation and a token/cost log line |

No swaps used — all 5 come from the primary concept table.

## Tech stack

Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA, H2 (file-based), JWT (jjwt), Maven.
$0 stack — H2 needs no external DB server, no credit card anywhere.

## How to run (from a clean machine)

**Requirements:** Java 17+, Maven (or use the included `mvnw` if you add one).

```bash
# 1. Clone and enter the project
git clone <your-repo-url>
cd job-tracker

# 2. Set required environment variables
export JWT_SECRET="a-long-random-string-at-least-32-characters-long"
export LLM_API_KEY="your-anthropic-api-key"   # optional - only needed for the /suggest-bullets endpoint

# 3. Run
mvn spring-boot:run
```

The app starts on `http://localhost:8080`. Seed data (5 sample applications) loads automatically.

## 5-minute demo path

1. **Register a user:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"pradeesh","password":"test1234"}'
   ```
   Copy the `token` from the response.

2. **View seeded applications** (use the token from step 1):
   ```bash
   curl http://localhost:8080/api/applications \
     -H "Authorization: Bearer <YOUR_TOKEN>"
   ```
   You'll see 5 pre-loaded applications (TCS, Wipro, Cognizant, Zoho, Infosys).

3. **Add a new application:**
   ```bash
   curl -X POST http://localhost:8080/api/applications \
     -H "Authorization: Bearer <YOUR_TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{"companyName":"Accenture","roleTitle":"ASE","status":"APPLIED","dateApplied":"2026-09-25","resumeVersion":"resume_v2.pdf"}'
   ```

4. **Update status** (e.g., move to interview round):
   ```bash
   curl -X PATCH "http://localhost:8080/api/applications/1/status?status=INTERVIEW" \
     -H "Authorization: Bearer <YOUR_TOKEN>"
   ```

5. **Get AI resume bullet suggestions** for a specific job description:
   ```bash
   curl -X POST http://localhost:8080/api/applications/1/suggest-bullets \
     -H "Authorization: Bearer <YOUR_TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{"jobDescription":"Looking for a Java backend developer with Spring Boot and REST API experience","currentResumeText":"Built a task management app using Java and MySQL. Familiar with OOP concepts and Git."}'
   ```

6. **Background job** — runs automatically every day at 8 AM (`@Scheduled(cron = "0 0 8 * * *")`
   in `FollowUpSchedulerService`). To test it immediately without waiting, temporarily change
   the cron expression to `"*/30 * * * * *"` (every 30 seconds), restart, and watch the console
   log for `FOLLOW-UP NEEDED:` lines.

## Non-goals (explicitly out of scope)

- No multi-user/team collaboration features
- No real email/SMS sending (follow-up alerts are logged to console — swap in `JavaMailSender` if needed)
- No resume file parsing — paste resume text as a string

## Future ideas

- PDF export of full application pipeline
- Real email delivery for follow-up reminders
- Browser extension to auto-log applications from job portals
