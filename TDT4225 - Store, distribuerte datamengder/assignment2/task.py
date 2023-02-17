from itertools import groupby
from database_util import DatabaseUtil
from tabulate import tabulate
from haversine import haversine, Unit


class Task(DatabaseUtil):
    def _run_query(self, query: str):
        self.cursor.execute(query)
        rows = self.cursor.fetchall()
        print(tabulate(rows, headers=self.cursor.column_names))

    def task_2_1_count_tables_sizes(self):
        """
        Counting the number of entries in each of the tables: users, activities and track_points
        """
        tables = [("users",), ("activities",), ("track_points",)]
        query = """
                SELECT COUNT(*) FROM %s
                """
        print("Task 2.1")
        for table in tables:
            # for each table in the list, execute the count
            self.cursor.execute(query % table)
            rows = self.cursor.fetchall()
            print(tabulate(rows, headers=table))

    def task_2_2_average_activities_per_user(self):
        """
        Find the average amount of activites per user by dividing the number of entries in activities by entries in users
        """
        query = """
                select (select count(*) from activities) / (select count(*) from users) AS divide from dual;
                """

        print("Task 2.2")
        self._run_query(query)

    def task_2_3_top_20_users_with_most_activities(self):
        """
        Find the top 20 users by left joining users on activities
        By grouping on the user id, we can count entries as number of activities
        Limiting by 20 and ordering gives us the top 20 users
        """
        query = """
                SELECT u.id, COUNT(*) as num_of_activities
                FROM users u
                LEFT JOIN activities a ON (u.id = a.user_id)
                GROUP BY u.id
                ORDER BY num_of_activities DESC
                LIMIT 20
                """
        print("Task 2.3")
        self._run_query(query)

    def task_2_4_users_whos_taken_taxi(self):
        """
        Finding all distinct users with activties where the transportation mode is taxi
        """
        query = """
                SELECT DISTINCT u.id from users u 
                JOIN activities a ON (u.id = a.user_id AND a.transportation_mode = 'taxi')
                """

        print("Task 2.4")
        self._run_query(query)

    def task_2_5_count_transportations(self):
        """
        Counting the number of transportation modes by selecting all activities with a transportation mode that is not 'NONE'
        By grouping by transportation_mode, the count of entries represents the number of registrered activties for the specific tranportation
        """
        query = """
                SELECT transportation_mode, COUNT(*) as transportation_count
                FROM activities
                WHERE transportation_mode != 'NONE'
                GROUP BY transportation_mode
                ORDER BY transportation_count DESC
                """

        print("Task 2.5")
        self._run_query(query)

    def task_2_6_a_year_with_most_activities(self):
        """
        In this task we consider only the year of the start_date_time
        It is possible for a task to start in one year, and end in the next, however the group limits the year to only the start time
        This is considered appropriate, as the possibility for overlapping activities, would make the sum of counts of activities for each year,
        greater than the total registered activities.
        """
        query = """
                SELECT YEAR(start_date_time) as start_year, COUNT(*) as year_count
                FROM activities
                GROUP BY start_year
                ORDER BY year_count DESC
                """

        print("Task 2.6 a)")
        self._run_query(query)

    def task_2_6_b_year_with_longest_activities(self):
        """
        As with task 2.6.b, we only consider the start_data_time
        By doing the sum of the time difference in hours, we can calculate the total hours spent in activities starting in a specific year
        It is important to note that if an activity starts in 2020, but ends in 2021, all hours registrered in the activity will count for 2020
        By defining the years in this manner, it is easier to get consistent results.
        As the specific implementations are up to the group it was considered the most intuitive option
        """
        query = """
                SELECT YEAR(a.start_date_time) as start_year, ROUND(SUM(TIME_TO_SEC(TIMEDIFF(a.end_date_time, a.start_date_time))) / 3600) as sum_time
                FROM activities as a
                GROUP BY start_year
                ORDER BY sum_time DESC
                """

        print("Task 2.6 b)")
        self._run_query(query)

    def task_2_7_total_distance_walked_in_2008_by_112(self):
        """
        Retrieves all track-points in walk-activities by user 112 in 2008.
        The track-points is then grouped into their activities.
        The distance is finally calculated for each activity and then summed.
        """

        def calculate_distance(positions):
            """
            Calculates distance in km through a list of geo-coordinates using the Haversine-function
            """
            result = 0
            for i in range(1, len(positions)):
                track_point = positions[i - 1]
                next_track_point = positions[i]
                result += haversine(
                    (track_point[0], track_point[1]),
                    (next_track_point[0], next_track_point[1]),
                    unit=Unit.KILOMETERS,
                )

            return result

        query = """
                SELECT t.id, t.lat, t.lon, t.activity_id, a.user_id
                FROM track_points t
                JOIN activities a
                ON (
                  a.id = t.activity_id
                  AND a.transportation_mode = 'walk'
                  AND a.user_id = '112'
                  AND t.date_time between '2008-01-01 00:00:00' and '2008-12-31 23:59:59'
                )
                ORDER BY t.date_time
        """

        self.cursor.execute(query)
        rows = self.cursor.fetchall()

        activities = groupby(rows, lambda row: row[3])

        total_distance = 0
        for activity in activities:
            points = activity[1]
            total_distance += calculate_distance(
                list(map(lambda point: (point[1], point[2]), points))
            )

        print("Task 2.7")
        print(f"Total distance by user 112 in 2008: {total_distance} km")

    def task_2_8_top_20_users_with_most_altitude_meters(self):
        """
        All track-points with valid altitude is retrieved with the user_id,
        ordered by user, activity_id and date_time to traverse them in the correct order.
        When traversing them, a altitude-sum for each user is increased when a track-point has a lower
        altitude compared to the next track-point's altitude, and the track-points belong to the same user and activity.
        Finally the users altitudes are sorted and limited to top 20.
        """
        query = """
                SELECT a.user_id, t.activity_id, t.altitude  * 0.3048, t.date_time FROM track_points t 
                JOIN activities a 
                ON t.activity_id = a.id
                WHERE t.altitude != -777
                ORDER BY a.user_id, t.activity_id, t.date_time
                """

        print("Task 2.8")
        self.cursor.execute(query)
        rows = self.cursor.fetchall()

        usersAltitudes = {}
        for i in range(len(rows) - 1):
            trackPoint = rows[i]
            nextTrackPoint = rows[i + 1]
            sameActivityId = trackPoint[1] == nextTrackPoint[1]
            sameUser = trackPoint[0] == nextTrackPoint[0]
            if sameActivityId and sameUser:
                nextTrackPointHasHigherAltitude = trackPoint[2] < nextTrackPoint[2]
                if nextTrackPointHasHigherAltitude:
                    try:
                        usersAltitudes[trackPoint[0]] += (
                            nextTrackPoint[2] - trackPoint[2]
                        )
                    except:
                        usersAltitudes[trackPoint[0]] = (
                            nextTrackPoint[2] - trackPoint[2]
                        )

        results = sorted(usersAltitudes.items(), key=lambda item: item[1], reverse=True)

        print(tabulate(results[:20], headers=("UserId", "Altitude meters gained")))

    def task_2_9_invalid_activities_per_user(self):
        """
        All track-points are retrieved with user_id sorted by user, activity_id and date_time
        to traverse in correct order. For each track-point, we check if there is more then
        5 minutes to the next track-point. If so, the activity is added to a set of invalid activities
        if not already there to avoid double-registration of invalid activities. The count of invalid activities
        per user is also increased. Finally the users are sorted
        """
        print("Task 2.9")
        query = """
                SELECT t.activity_id, t.date_time, a.user_id 
                FROM track_points t JOIN activities a 
                ON t.activity_id = a.id 
                ORDER BY a.user_id, t.activity_id, t.date_time
                """
        self.cursor.execute(query)
        rows = self.cursor.fetchall()

        invalidActivities = set()
        usersInvalidActivities = {}
        for i in range(len(rows) - 1):
            trackPoint = rows[i]
            if trackPoint[0] in invalidActivities:
                # This track-point's activity have already been marked as invalid
                continue
            nextTrackPoint = rows[i + 1]
            sameActivityId = trackPoint[0] == nextTrackPoint[0]
            sameUser = trackPoint[2] == nextTrackPoint[2]
            if sameActivityId and sameUser:
                trackpointsDiffInSeconds = (nextTrackPoint[1] - trackPoint[1]).seconds
                if trackpointsDiffInSeconds >= 5 * 60:  # 5 minutes
                    invalidActivities.add(trackPoint[0])
                    if trackPoint[2] in usersInvalidActivities:
                        usersInvalidActivities[trackPoint[2]] += 1
                    else:
                        usersInvalidActivities[trackPoint[2]] = 1

        results = sorted(
            usersInvalidActivities.items(), key=lambda x: x[1], reverse=True
        )

        print(tabulate(results, headers=("UserId", "Invalid activities")))

    def task_2_10_users_tracked_forbidden_city(self):
        """
        Since we do not get exact matches on geo location, we consider the Forbidden City to be between the nearest possible geolocations
        This is a consequence of the track_points having more specific geo locations than the Forbidden City
        The disctinct users with activities registered in the between clause is listed
        """
        print("Task 2.10")
        query = """
                SELECT DISTINCT u.id
                FROM users u
                LEFT JOIN activities a ON (u.id = a.user_id)
                LEFT JOIN track_points t ON (a.id = t.activity_id)
                WHERE t.lat BETWEEN 39.916 AND 39.917 AND t.lon BETWEEN 116.397 AND 116.398
                """
        self._run_query(query)

    def task_2_11_users_most_used_transportation_mode(self):
        """
        We can find the users most used transportation mode by joining user id on the activties user_id
        given an inner query where we group the id and transportation mode for the activities
        """
        print("Task 2.11")
        query = """
                SELECT u.id, MAX(act.transportation_mode) as most_used_transportation_mode
                FROM users u
                JOIN (
                  SELECT a.user_id, a.transportation_mode
                  FROM activities a
                  WHERE a.transportation_mode != 'None'
                  GROUP BY a.transportation_mode, a.user_id
                ) as act on act.user_id = u.id
                GROUP BY u.id
                """
        self._run_query(query)
