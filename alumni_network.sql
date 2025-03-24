USE alumni_network;

CREATE TABLE User (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(50) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    role ENUM('ADMIN', 'ALUMNI', 'LECTURER'),
    avatar VARCHAR(255),
    cover_avatar VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    active BIT DEFAULT 0
);

CREATE TABLE Alumni_Info (
	id INT PRIMARY KEY AUTO_INCREMENT,
    student_code VARCHAR(20) NOT NULL UNIQUE,
    user_id INT NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE Lecturer_Info (
	id INT PRIMARY KEY AUTO_INCREMENT,
    expired_reset_password_time DATETIME,
    changed_password BOOL DEFAULT 0,
    user_id INT NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE Post (
	id INT PRIMARY KEY AUTO_INCREMENT,
    content TEXT,
    blocked_comment BOOL DEFAULT 0,
    active BIT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE Post_Image (
	id INT PRIMARY KEY AUTO_INCREMENT,
    url VARCHAR(255) NOT NULL,
    post_id INT NOT NULL,
    FOREIGN KEY (post_id) REFERENCES Post(id)
);

CREATE TABLE Reaction (
	id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM ('HAHA', 'LIKE', 'LOVE'),
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (post_id) REFERENCES Post(id)
);

CREATE TABLE Comment (
	id INT PRIMARY KEY AUTO_INCREMENT,
    content TEXT,
    active BIT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    parent_comment_id INT,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (post_id) REFERENCES Post(id),
    FOREIGN KEY (parent_comment_id) REFERENCES Comment(id)
);

CREATE TABLE Survey (
	id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255),
    title VARCHAR(255),
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM ('OPEN', 'CLOSED'),
    user_id INT NOT NULL, # admin tao bai khao sat
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE Question (
	id INT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(255) NOT NULL,
    survey_id INT NOT NULL,
    FOREIGN KEY (survey_id) REFERENCES Survey(id)
);

CREATE TABLE Choice (
	id INT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(100)
);

CREATE TABLE Question_Choice (
	id INT PRIMARY KEY AUTO_INCREMENT,
    choice_id INT NOT NULL,
    question_id INT NOT NULL,
    UNIQUE (choice_id, question_id),
    FOREIGN KEY (choice_id) REFERENCES Choice(id),
    FOREIGN KEY (question_id) REFERENCES Question(id)
);

CREATE TABLE User_Survey_Choice (
	id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL,
	choice_id INT NOT NULL,
    user_id INT NOT NULL,
    UNIQUE (choice_id, question_id, user_id),
    FOREIGN KEY (choice_id) REFERENCES Choice(id),
    FOREIGN KEY (question_id) REFERENCES Question(id),
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE Event (
	id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    description VARCHAR(255),
    content TEXT NOT NULL,
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    url_zoom VARCHAR(255), 
    user_id INT NOT NULL, # admin tao event
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE Group_Network (
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    user_id INT NOT NULL, # admin tao group
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE Event_Invitation (
	id INT PRIMARY KEY AUTO_INCREMENT,
    status ENUM ('PENDING', 'ACCEPTED', 'DECLINED'),
    event_id INT NOT NULL,
    FOREIGN KEY (event_id) REFERENCES Event(id)
);

CREATE TABLE Event_Invitation_User (
	id INT PRIMARY KEY AUTO_INCREMENT,
    event_invitation_id INT NOT NULL,
    user_id INT NOT NULL,
    UNIQUE (event_invitation_id, user_id),
    FOREIGN KEY (event_invitation_id) REFERENCES Event_Invitation(id),
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE Event_Invitation_Group_Network (
	id INT PRIMARY KEY AUTO_INCREMENT,
    event_invitation_id INT NOT NULL,
    group_network_id INT NOT NULL,
    UNIQUE (event_invitation_id, group_network_id),
    FOREIGN KEY (event_invitation_id) REFERENCES Event_Invitation(id),
    FOREIGN KEY (group_network_id) REFERENCES Group_Network(id)
)




