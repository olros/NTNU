import os
from database_util import DatabaseUtil

from util import Trajectory, LabelsActivity


class Insert(DatabaseUtil):
    def _get_users_with_labeled_ids(self):
        """Get a list of users with labeled activities from the `labeled_ids`.txt-file"""
        path = "./dataset/dataset/labeled_ids.txt"
        with open(path, "r") as labeled_ids:
            return list(map(lambda x: x.rstrip("\n"), labeled_ids.readlines()))

    def _get_users(self):
        """Get a list of users and if they have created labels"""
        path = "./dataset/dataset/Data/"
        data = []
        users_with_labeled_ids = self._get_users_with_labeled_ids()
        for user_id in os.listdir(path):
            has_labels = user_id in users_with_labeled_ids
            data.append((user_id, has_labels))
        return data

    def insert_users(self):
        users = self._get_users()

        query = "INSERT INTO users (id, has_labels) VALUES (%s, %s)"
        self.cursor.executemany(query, users)
        self.db_connection.commit()

    def _insert_activity_query(
        self, id, transportation_mode, start_date_time, end_date_time, user_id
    ):
        query = """INSERT INTO activities (id, transportation_mode, start_date_time, end_date_time, user_id)
                    VALUES ('%s', '%s', '%s', '%s', '%s')"""

        return query % (
            id,
            transportation_mode,
            start_date_time,
            end_date_time,
            user_id,
        )

    def _insert_track_point_query(self):
        return """INSERT INTO track_points (lat, lon, altitude, date_time, activity_id)
                    VALUES (%s, %s, %s, %s, %s)"""

    def create_activity(self, user, file_path):
        user_id, has_labels = user
        activityId = f"{file_path}_{user_id}"
        with open(
            f"./dataset/dataset/Data/{user_id}/Trajectory/{file_path}", "r"
        ) as trajectory_file:
            trajectory_lines = trajectory_file.readlines()

            # Don't add activities with more then 2500 trajections
            # (2506 because the first 6 lines contains other information)
            if len(trajectory_lines) > 2506:
                return

            activityQuery = None
            # The first 6 lines in each trajectory file contains useless information,
            # we therefore start a index: 6
            first_trajectory = Trajectory(trajectory_lines[6])
            last_trajectory = Trajectory(trajectory_lines[-1])
            # If the user has_labels
            if has_labels:
                with open(
                    f"./dataset/dataset/Data/{user_id}/labels.txt", "r"
                ) as labels_file:
                    # For all lines in label file, compare to trajectory date, insert if match
                    for label_line in labels_file.readlines()[1:]:
                        labels_activity = LabelsActivity(label_line)
                        if (
                            labels_activity.start_date == first_trajectory.date
                            and labels_activity.end_date == last_trajectory.date
                        ):
                            activityQuery = self._insert_activity_query(
                                activityId,
                                labels_activity.transportation_mode,
                                labels_activity.start_date,
                                labels_activity.end_date,
                                user_id,
                            )
                            continue

            if not activityQuery:
                activityQuery = self._insert_activity_query(
                    activityId,
                    None,
                    first_trajectory.date,
                    last_trajectory.date,
                    user_id,
                )

            self.cursor.execute(activityQuery)

            track_point_data = self.get_track_points_from_file(
                trajectory_lines, activityId
            )
            self.cursor.executemany(self._insert_track_point_query(), track_point_data)

    # 1. For each user:
    # 2. Loop through each activity-file: `trajectory/<yyyyMMddHHmmss>.plt`.
    #    1. Check if there exists a activity in the users `labels.txt`  where start time corresponds with the name of the trajectory-file.
    #       1. If there is a match, use the `labels.txt`-listing to create an activity before all track-points are added to the activity
    #       2. If there is no match, use the first and last point in the trajectory to create an activity before all track-points are added
    def insert_user_activities(self, user):
        path = f"./dataset/dataset/Data/{user[0]}/Trajectory/"
        for user_file in os.listdir(f"{path}"):
            self.create_activity(user, user_file)

    def get_track_points_from_file(self, trajectory_lines, activity_id):
        return list(
            map(
                lambda line: Trajectory(line).to_tuple(activity_id),
                trajectory_lines[6:],
            )
        )

    def insert_trajectories(self):
        count = 0
        users = self._get_users()
        for user in users:
            print(f"Start {user[0]}, count: {count}")
            self.insert_user_activities(user)
            self.db_connection.commit()
            count += 1
            print(f"Finished {user[0]}, count: {count}")
