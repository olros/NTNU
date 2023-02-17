from database_util import DatabaseUtil


class Setup(DatabaseUtil):
    def create_user_table(self):
        query = """CREATE TABLE IF NOT EXISTS users (
                   id VARCHAR(225) NOT NULL PRIMARY KEY,
                   has_labels BOOLEAN NOT NULL DEFAULT FALSE
                   )
                """
        return query

    def create_activity_table(self):
        query = """CREATE TABLE IF NOT EXISTS activities (
                    id VARCHAR(225) NOT NULL PRIMARY KEY,
                    transportation_mode VARCHAR(120),
                    start_date_time DATETIME NOT NULL,
                    end_date_time DATETIME NOT NULL,
                    user_id VARCHAR(225) NOT NULL,
                    CONSTRAINT FK_UserActivity FOREIGN KEY (user_id) REFERENCES users(id)
                )
                """
        return query

    def create_track_point(self):
        query = """CREATE TABLE IF NOT EXISTS track_points (
                    id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
                    lat DOUBLE(20, 10),
                    lon DOUBLE(20, 10),
                    altitude INT(30),
                    date_time DATETIME NOT NULL,
                    activity_id VARCHAR(225) NOT NULL,
                    CONSTRAINT FK_ActivityTrackPoint FOREIGN KEY (activity_id) REFERENCES activities(id)
                    )
                """
        return query

    def drop_all(self):
        query = """
                DROP TABLE track_points;
                DROP TABLE activities;
                DROP TABLE users;
                """
        self.cursor.execute(query)
        self.db_connection.commit()

    def setup(self):
        queries = []
        queries.append(self.create_user_table())
        queries.append(self.create_activity_table())
        queries.append(self.create_track_point())

        self.cursor.execute(queries[0])
        self.cursor.execute(queries[1])
        self.cursor.execute(queries[2])
        self.db_connection.commit()
