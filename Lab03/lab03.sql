CREATE TABLE Department
(
    name     VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    website  VARCHAR(500) NOT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE Student
(
    name       VARCHAR(100) NOT NULL,
    student_id SERIAL,
    department VARCHAR(100) NOT NULL,
    gender     CHAR(3)      NOT NULL,

    PRIMARY KEY (student_id),
    FOREIGN KEY (department) REFERENCES Department (name)
);

CREATE TABLE Course
(
    name          VARCHAR(100) NOT NULL,
    course_number CHAR(10)     NOT NULL,
    department    VARCHAR(100) NOT NULL,
    credit        INT          NOT NULL,

    PRIMARY KEY (course_number),
    FOREIGN KEY (department) REFERENCES Department (name)
);

CREATE TABLE Student_To_Course
(
    student_id    INT          NOT NULL,
    course_number VARCHAR(100) NOT NULL,

    PRIMARY KEY (student_id, course_number),
    FOREIGN KEY (student_id) REFERENCES Student (student_id),
    FOREIGN KEY (course_number) REFERENCES Course (course_number)
);