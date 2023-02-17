from DbConnector import DbConnector


class DatabaseUtil:
    ACTIVITIES_COLLECTION_NAME = 'activities'
    TRACK_POINTS_COLLECTION_NAME = 'track_points'

    def __init__(self):
        self.connection = DbConnector()
        self.client = self.connection.client
        self.db = self.connection.db

        self.activity_collection = self.db[self.ACTIVITIES_COLLECTION_NAME]
        self.track_point_collection = self.db[self.TRACK_POINTS_COLLECTION_NAME]

    def cleanup(self):
        self.connection.close_connection()
