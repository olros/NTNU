from database_util import DatabaseUtil


class Setup(DatabaseUtil):

    def __create_coll(self, collection_name):
        self.db.create_collection(collection_name)    

    def __drop_coll(self, collection_name):
        self.db.drop_collection(collection_name)

    def drop_all(self):
        self.__drop_coll(self.ACTIVITIES_COLLECTION_NAME)
        self.__drop_coll(self.TRACK_POINTS_COLLECTION_NAME)

    def setup(self):
        self.__create_coll(self.ACTIVITIES_COLLECTION_NAME)
        self.__create_coll(self.TRACK_POINTS_COLLECTION_NAME)
