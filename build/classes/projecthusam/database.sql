
-- Create the necessary tables
CREATE TABLE courses (
  course_id SERIAL PRIMARY KEY,
  subject VARCHAR(255) NOT NULL,
  book VARCHAR(255) NOT NULL,
  teacher VARCHAR(255) NOT NULL,
  virtual_meeting_place VARCHAR(255) NOT NULL
);

CREATE TABLE users (
  user_id SERIAL PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL
);

CREATE TABLE lectures (
  lecture_id SERIAL PRIMARY KEY,
  course_id INT NOT NULL,
  title VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL,
  location VARCHAR(255) NOT NULL,
  virtual_lecture_hall VARCHAR(255) NOT NULL,
  FOREIGN KEY (course_id) REFERENCES courses (course_id)
);

CREATE TABLE students (
  student_id SERIAL PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  university_number VARCHAR(50) NOT NULL,
  mobile_number VARCHAR(20) NOT NULL,
  area_of_residence VARCHAR(255) NOT NULL
);

CREATE TABLE student_courses (
  student_course_id SERIAL PRIMARY KEY,
  student_id INT NOT NULL,
  course_id INT NOT NULL,
  FOREIGN KEY (student_id) REFERENCES students (student_id),
  FOREIGN KEY (course_id) REFERENCES courses (course_id)
);

CREATE TABLE attendance (
  attendance_id SERIAL PRIMARY KEY,
  student_course_id INT NOT NULL,
  lecture_id INT NOT NULL,
  is_present BOOLEAN NOT NULL,
  FOREIGN KEY (student_course_id) REFERENCES student_courses (student_course_id),
  FOREIGN KEY (lecture_id) REFERENCES lectures (lecture_id)
);

-- Create indexes for performance optimization
CREATE INDEX idx_attendance_student_course_id ON attendance (student_course_id);
CREATE INDEX idx_attendance_lecture_id ON attendance (lecture_id);
