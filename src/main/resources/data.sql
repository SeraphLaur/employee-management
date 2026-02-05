
INSERT INTO department (department_name) VALUES
                                             ('Engineering'),
                                             ('HR'),
                                             ('Finance'),
                                             ('Sales');


INSERT INTO employee (employee_id, department_id, salary, name, date_of_birth)
VALUES
    ('EMP-1001', (SELECT d.department_id FROM department d WHERE d.department_name = 'Engineering'), 65000.00, 'Alice Dela Cruz', '1992-04-15'),
    ('EMP-1002', (SELECT d.department_id FROM department d WHERE d.department_name = 'Engineering'), 72000.00, 'Miguel Santos',    '1988-11-03'),
    ('EMP-1003', (SELECT d.department_id FROM department d WHERE d.department_name = 'HR'),          54000.00, 'Bianca Reyes',     '1995-02-20'),
    ('EMP-1004', (SELECT d.department_id FROM department d WHERE d.department_name = 'Finance'),     83000.00, 'Carlo Lim',        '1985-07-12'),
    ('EMP-1005', (SELECT d.department_id FROM department d WHERE d.department_name = 'Sales'),       50000.00, 'Jasmine Tan',      '1999-09-30');