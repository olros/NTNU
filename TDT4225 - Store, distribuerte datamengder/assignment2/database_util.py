from DbConnector import DbConnector


class DatabaseUtil:
    def __init__(self):
        self.connection = DbConnector()
        self.db_connection = self.connection.db_connection
        self.cursor = self.connection.cursor

    def cleanup(self):
        self.connection.close_connection()
