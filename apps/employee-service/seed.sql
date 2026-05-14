
-- ============================================================
-- 5. EMPLOYEE SKILLS  (S000001 … S000018)
-- ============================================================
INSERT INTO employee_skills (skill_id, employee_id, skill_name, competency_level, certification, certification_id, status, acquired_date, last_updated_date) VALUES
('S000001','E000003','Java',                'EXPERT',      'Oracle Certified Professional Java','OCP-2022-001', 'ACTIVE','2019-06-15',NOW()),
('S000002','E000003','Spring Boot',          'EXPERT',      'Pivotal Spring Professional',       'PSP-2022-002', 'ACTIVE','2020-01-01',NOW()),
('S000003','E000003','System Architecture',  'ADVANCED',    'AWS Solutions Architect',           'AWS-2023-003', 'ACTIVE','2021-03-01',NOW()),
('S000004','E000007','Java',                 'ADVANCED',    'Oracle Certified Associate Java',   'OCA-2021-001', 'ACTIVE','2020-11-01',NOW()),
('S000005','E000007','Spring Boot',          'ADVANCED',    NULL,                                 NULL,          'ACTIVE','2021-01-01',NOW()),
('S000006','E000007','React',                'INTERMEDIATE',NULL,                                 NULL,          'ACTIVE','2021-06-01',NOW()),
('S000007','E000009','Java',                 'INTERMEDIATE',NULL,                                 NULL,          'ACTIVE','2023-01-15',NOW()),
('S000008','E000009','Angular',              'BEGINNER',    NULL,                                 NULL,          'ACTIVE','2023-03-01',NOW()),
('S000009','E000006','Recruitment',          'EXPERT',      'SHRM-CP',                           'SHRM-2020-001','ACTIVE','2021-09-01',NOW()),
('S000010','E000006','Labour Law',           'ADVANCED',    NULL,                                 NULL,          'ACTIVE','2022-01-01',NOW()),
('S000011','E000010','Payroll Management',   'INTERMEDIATE',NULL,                                 NULL,          'ACTIVE','2023-04-01',NOW()),
('S000012','E000010','Employee Relations',   'BEGINNER',    NULL,                                 NULL,          'ACTIVE','2023-04-01',NOW()),
('S000013','E000008','Financial Analysis',   'ADVANCED',    'CFA Level 1',                       'CFA-2022-001', 'ACTIVE','2022-02-01',NOW()),
('S000014','E000008','SAP FI',              'INTERMEDIATE',NULL,                                 NULL,          'ACTIVE','2022-06-01',NOW()),
('S000015','E000004','IFRS Reporting',       'EXPERT',      'ACCA',                              'ACCA-2019-001','ACTIVE','2021-01-10',NOW()),
('S000016','E000004','Budget Management',    'EXPERT',      NULL,                                 NULL,          'ACTIVE','2021-01-10',NOW()),
('S000017','E000005','Supply Chain Mgmt',    'ADVANCED',    'APICS CPIM',                        'CPIM-2021-001','ACTIVE','2021-05-20',NOW()),
('S000018','E000002','HR Strategy',          'EXPERT',      'SHRM-SCP',                          'SHRM-2018-001','ACTIVE','2020-03-01',NOW());


-- ============================================================
-- 6. EMPLOYEE INSURANCES  (INS000001 … INS000011)
-- ============================================================
INSERT INTO employee_insurances (insurance_id, employee_id, policy_number, policy_number_unique, insurance_type, provider_name, coverage_amount, policy_start_date, policy_end_date, premium_amount, beneficiary_name, beneficiary_relationship, beneficiary_phone, beneficiary_email, status, claim_details, claim_date, claim_amount, created_at, updated_at) VALUES
('INS000001','E000001','POL-CEO-L-001','POL-CEO-L-001-U','LIFE',  'AXA Assurance Maroc',500000.00,'2024-01-01','2026-12-31',2500.00,'Fatima Bennani','Spouse', '+212661000003','fatima.bennani@gmail.com', 'ACTIVE',NULL,NULL,NULL,NOW(),NOW()),
('INS000002','E000001','POL-CEO-H-001','POL-CEO-H-001-U','HEALTH','CNOPS',              200000.00,'2024-01-01','2026-12-31',1200.00,'Fatima Bennani','Spouse', '+212661000003','fatima.bennani@gmail.com', 'ACTIVE',NULL,NULL,NULL,NOW(),NOW()),
('INS000003','E000002','POL-HR-H-001', 'POL-HR-H-001-U', 'HEALTH','CNOPS',              300000.00,'2024-01-01','2026-12-31',1500.00,'Hassan Chraibi','Spouse', '+212662000003','hassan.chraibi@gmail.com', 'ACTIVE',NULL,NULL,NULL,NOW(),NOW()),
('INS000004','E000003','POL-IT-H-001', 'POL-IT-H-001-U', 'HEALTH','AXA Assurance Maroc',300000.00,'2024-01-01','2026-12-31',1500.00,'Sara Alami',    'Spouse', '+212663000003','sara.alami@gmail.com',     'ACTIVE',NULL,NULL,NULL,NOW(),NOW()),
('INS000005','E000004','POL-FIN-L-001','POL-FIN-L-001-U','LIFE',  'Wafa Assurance',     300000.00,'2024-01-01','2026-12-31',1500.00,'Khalid Fassi',  'Spouse', '+212664000003','khalid.fassi@gmail.com',   'ACTIVE',NULL,NULL,NULL,NOW(),NOW()),
('INS000006','E000005','POL-OPS-H-001','POL-OPS-H-001-U','HEALTH','RMA Assurance',      250000.00,'2024-01-01','2026-12-31',1200.00,'Amina Berrada', 'Spouse', '+212665000003','amina.berrada@gmail.com',  'ACTIVE',NULL,NULL,NULL,NOW(),NOW()),
('INS000007','E000006','POL-HRM-H-001','POL-HRM-H-001-U','HEALTH','CNOPS',              200000.00,'2024-01-01','2026-12-31', 900.00,'Tariq Mansouri','Spouse', '+212666000003','tariq.mansouri@gmail.com', 'ACTIVE',NULL,NULL,NULL,NOW(),NOW()),
('INS000008','E000007','POL-SRD-H-001','POL-SRD-H-001-U','HEALTH','AXA Assurance Maroc',200000.00,'2024-01-01','2026-12-31', 900.00,'Khadija Tahiri','Mother', '+212667000003','khadija.tahiri@gmail.com', 'ACTIVE',NULL,NULL,NULL,NOW(),NOW()),
('INS000009','E000008','POL-FNA-H-001','POL-FNA-H-001-U','HEALTH','Wafa Assurance',     150000.00,'2024-01-01','2026-12-31', 750.00,'Rachid Lahlou', 'Father', '+212668000003','rachid.lahlou@gmail.com',  'ACTIVE',NULL,NULL,NULL,NOW(),NOW()),
('INS000010','E000009','POL-SD-H-001', 'POL-SD-H-001-U', 'HEALTH','AXA Assurance Maroc',100000.00,'2024-01-01','2026-12-31', 600.00,'Laila Ouazzani','Mother', '+212669000003','laila.ouazzani@gmail.com', 'ACTIVE',NULL,NULL,NULL,NOW(),NOW()),
('INS000011','E000010','POL-HRS-H-001','POL-HRS-H-001-U','HEALTH','CNOPS',              100000.00,'2024-01-01','2026-12-31', 600.00,'Yassine Belhaj','Brother','+212670000003','yassine.belhaj@gmail.com','ACTIVE',NULL,NULL,NULL,NOW(),NOW());


-- ============================================================
-- 7. EMPLOYMENT HISTORY  (EH000001 … EH000011)
-- ============================================================
INSERT INTO employment_history (history_id, employee_id, effective_date, change_type, previous_designation_id, new_designation_id, previous_department_id, new_department_id, previous_grade, new_grade, reason, created_at) VALUES
('EH000001','E000002','2020-03-01','HIRE',      NULL,     'D000002',NULL,2,NULL, 'G2','Initial hire as HR Director',                             NOW()),
('EH000002','E000003','2019-06-15','HIRE',      NULL,     'D000003',NULL,3,NULL, 'G2','Initial hire as IT Director',                             NOW()),
('EH000003','E000004','2021-01-10','HIRE',      NULL,     'D000004',NULL,4,NULL, 'G2','Initial hire as Finance Director',                        NOW()),
('EH000004','E000005','2021-05-20','HIRE',      NULL,     'D000008',NULL,5,NULL, 'G2','Initial hire as Operations Manager',                      NOW()),
('EH000005','E000006','2021-09-01','HIRE',      NULL,     'D000010',NULL,2,NULL, 'G4','Initial hire as HR Specialist',                           NOW()),
('EH000006','E000006','2022-09-01','PROMOTION','D000010','D000005',2,   2, 'G4','G3','Promoted to HR Manager based on outstanding performance', NOW()),
('EH000007','E000007','2020-11-01','HIRE',      NULL,     'D000009',NULL,3,NULL, 'G4','Initial hire as Software Developer',                      NOW()),
('EH000008','E000007','2022-05-01','PROMOTION','D000009','D000006',3,   3, 'G4','G3','Promoted to Senior Developer after exceptional delivery', NOW()),
('EH000009','E000008','2022-02-01','HIRE',      NULL,     'D000007',NULL,4,NULL, 'G3','Initial hire as Financial Analyst',                       NOW()),
('EH000010','E000009','2023-01-15','HIRE',      NULL,     'D000009',NULL,3,NULL, 'G4','Initial hire as Software Developer',                      NOW()),
('EH000011','E000010','2023-04-01','HIRE',      NULL,     'D000010',NULL,2,NULL, 'G4','Initial hire as HR Specialist',                           NOW());


-- ============================================================
-- 8. ORGANIZATION CHART  (OC000001 … OC000010)
-- ============================================================
INSERT INTO organization_chart (chart_id, employee_id, manager_id, hierarchy_level, created_at, updated_at) VALUES
('OC000001','E000001', NULL,      1, NOW(), NOW()),   -- CEO
('OC000002','E000002','E000001',  2, NOW(), NOW()),   -- HR Director       → CEO
('OC000003','E000003','E000001',  2, NOW(), NOW()),   -- IT Director       → CEO
('OC000004','E000004','E000001',  2, NOW(), NOW()),   -- Finance Director  → CEO
('OC000005','E000005','E000001',  2, NOW(), NOW()),   -- Ops Manager       → CEO
('OC000006','E000006','E000002',  3, NOW(), NOW()),   -- HR Manager        → HR Director
('OC000007','E000007','E000003',  3, NOW(), NOW()),   -- Senior Developer  → IT Director
('OC000008','E000008','E000004',  3, NOW(), NOW()),   -- Financial Analyst → Finance Director
('OC000009','E000009','E000007',  4, NOW(), NOW()),   -- Developer         → Senior Developer
('OC000010','E000010','E000006',  4, NOW(), NOW());   -- HR Specialist     → HR Manager