import os
from database_util import DatabaseUtil
from haversine import haversine, Unit

from util import Trajectory, LabelsActivity, User, Activity, TrackPoint


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
            data.append(vars(User(user_id, has_labels)))
        return data

    def insert_users(self):
        users = self._get_users()
        collection = self.db['users']
        collection.insert_many(users)

    def create_activity(self, user, file_path: str):
        user_id = user['_id']
        has_labels = user['has_labels']
        activityId = f"{file_path}_{user_id}"
        activity = None
        with open(
            f"./dataset/dataset/Data/{user_id}/Trajectory/{file_path}", "r"
        ) as trajectory_file:
            trajectory_lines = trajectory_file.readlines()

            # Don't add activities with more then 2500 trajections
            # (2506 because the first 6 lines contains other information)
            if len(trajectory_lines) > 2506:
                return None, None

            track_point_data, total_distance, altitude_gained = self.get_track_points_from_file(
                trajectory_lines, activityId, user_id
            )

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
                            activity = Activity.with_transportation_mode(
                                activityId,
                                labels_activity,
                                user_id,
                                total_distance,
                                altitude_gained,
                            )
                            continue

            if not activity:
                activity = Activity.no_transportation_mode(
                    activityId,
                    first_trajectory.date,
                    last_trajectory.date,
                    user_id,
                    total_distance,
                    altitude_gained,
                )
            
            return activity, track_point_data 


    # 1. For each user:
    # 2. Loop through each activity-file: `trajectory/<yyyyMMddHHmmss>.plt`.
    #    1. Check if there exists a activity in the users `labels.txt`  where start time corresponds with the name of the trajectory-file.
    #       1. If there is a match, use the `labels.txt`-listing to create an activity before all track-points are added to the activity
    #       2. If there is no match, use the first and last point in the trajectory to create an activity before all track-points are added
    def insert_user_activities(self, user):
        path = f"./dataset/dataset/Data/{user['_id']}/Trajectory/"
        activities = []
        track_points = []
        for user_file in os.listdir(f"{path}"):
            activity, activity_track_points = self.create_activity(user, user_file)
            if activity and activity_track_points:
                track_points.extend(activity_track_points)
                activities.append(vars(activity))

        return activities, track_points

    def get_track_points_from_file(self, trajectory_lines: list[str], activity_id: str, user_id: int):
        track_point_data: list[dict] = []
        total_distance = 0

        for line in trajectory_lines[6:]:
            track_point = TrackPoint.from_line(line, activity_id, user_id)
            
            if len(track_point_data) > 0:
                prev_track_point = track_point_data[-1]
                coord_1 = (prev_track_point['lat'], prev_track_point['lon'])
                coord_2 = (track_point.lat, track_point.lon)
                total_distance += haversine(
                      coord_1,
                      coord_2,
                      unit=Unit.KILOMETERS,
                )

            track_point_data.append(vars(track_point))

        altitude_gained = 0

        valid_altitudes = list(filter(lambda altitude: altitude != -777, map(lambda track_point: track_point['altitude'], track_point_data)))

        for i, altitude in enumerate(valid_altitudes):
            if i == 0:
                continue
            prev_altitude = valid_altitudes[i - 1]
            if altitude > prev_altitude:
                  altitude_gained += altitude - prev_altitude

        return track_point_data, total_distance, altitude_gained

        
    def insert(self):
        count = 0
        users = self._get_users()
        activities = []
        

        for user in users:
            print(f"Start {user['_id']}, count: {count}")
            user_activities, user_track_points = self.insert_user_activities(user)
            if len(user_track_points) > 0:
                self.track_point_collection.insert_many(user_track_points)
            activities.extend(user_activities)
            count += 1
            print(f"Finished {user['_id']}, count: {count}")
        
        self.activity_collection.insert_many(activities)
