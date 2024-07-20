INSERT INTO students (fullname, furigana, nickname, mail, address, age, gender, remark)
VALUES
('山田太郎', 'ヤマダタロウ', 'たろう', 'taro.yamada@example.com', '東京都', 20, '男性', NULL),
('佐藤花子', 'サトウハナコ', 'はなちゃん', 'hanako.sato@example.com', '大阪府', 32, '女性', NULL),
('鈴木一郎', 'スズキイチロウ', NULL, 'ichiro.suzuki@example.com', '愛知県', 41, '男性', 'なごや'),
('田中美咲', 'タナカミサキ', 'みさき', 'misaki.tanaka@example.com', '福岡県', 59, '女性', '博多弁'),
('中村健太', 'ナカムラケンタ', 'けん', 'kenta.nakamura@example.com', '北海道', 19, 'その他', '留学経験あり');

INSERT INTO students_courses (student_id, course_name, start_date, end_date)
VALUES
(1, 'Java', '2024-04-01 09:00:00', '2024-07-31 17:00:00'),
(1, 'Ruby', '2024-04-02 13:00:00', '2024-08-01 15:00:00'),
(2, 'Design', '2024-04-01 10:30:00', '2024-07-31 12:30:00'),
(2, 'Front', '2024-04-03 14:00:00', '2024-08-02 16:00:00'),
(3, 'Python', '2024-04-02 09:00:00', '2024-08-01 11:00:00'),
(3, 'Java', '2024-04-04 13:30:00', '2024-08-03 15:30:00'),
(4, 'English', '2024-04-01 11:00:00', '2024-07-31 13:00:00'),
(5, 'AWS', '2024-04-03 10:00:00', '2024-08-02 12:00:00');
