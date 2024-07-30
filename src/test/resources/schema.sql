CREATE TABLE IF NOT EXISTS students (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fullname VARCHAR(100) NOT NULL,
    furigana VARCHAR(100) NOT NULL,
    nickname VARCHAR(100),
    mail VARCHAR(256) NOT NULL,
    address VARCHAR(256),
    age INT,
    gender ENUM('男性', '女性', 'その他'),
    remark VARCHAR(200),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS students_courses (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    start_date DATETIME,
    end_date DATETIME,
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS course_status (
     id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
     course_id INT NOT NULL,
     status ENUM('仮申込', '本申込', '受講中', '受講終了'),
     FOREIGN KEY (course_id) REFERENCES students_courses(id)
);
