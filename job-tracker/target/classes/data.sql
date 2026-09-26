-- Seed data: runs on every startup (only inserts if table is empty, checked via app logic normally,
-- but for demo simplicity we just insert; delete ./data folder to reset).

MERGE INTO applications (id, company_name, role_title, status, date_applied, resume_version, notes, job_description, last_status_change, follow_up_flagged)
KEY(id)
VALUES
(1, 'TCS', 'Systems Engineer', 'APPLIED', '2026-09-10', 'resume_v2_general.pdf', 'Applied via campus drive', 'Looking for candidates with Java, SQL, and problem-solving skills', '2026-09-10 10:00:00', FALSE),
(2, 'Wipro', 'Project Engineer', 'OA_ROUND', '2026-09-05', 'resume_v2_general.pdf', 'Cleared first screening', 'Entry level role, DSA and communication skills required', '2026-09-15 09:00:00', FALSE),
(3, 'Cognizant', 'GenC Trainee', 'INTERVIEW', '2026-08-28', 'resume_v3_tailored_cognizant.pdf', 'Technical round scheduled', 'Java full stack, Spring Boot experience preferred', '2026-09-20 14:00:00', FALSE),
(4, 'Zoho', 'Software Developer', 'REJECTED', '2026-08-15', 'resume_v1.pdf', 'Did not clear coding round', 'Strong DSA and system design fundamentals expected', '2026-08-25 16:00:00', FALSE),
(5, 'Infosys', 'Digital Specialist Engineer', 'APPLIED', '2026-09-01', 'resume_v2_general.pdf', 'Waiting for OA link', 'Java, database fundamentals, aptitude test based hiring', '2026-09-01 11:00:00', FALSE);
