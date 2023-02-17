from database_util import DatabaseUtil
from tabulate import tabulate


class Task(DatabaseUtil):

    def task_2_1_count_tables_sizes(self):
        """
        Since the users only exist in the activities-collection, we have to group by user_id and then count
        """
        print("Task 2.1")

        result = self.activity_collection.aggregate(
            [
                {"$group": {"_id": "$user_id"}},
                {"$group": {"_id": {}, "count": {"$count": {}}}},
            ]
        )
        num_of_users = result.next()["count"]
        num_of_activities = self.activity_collection.count_documents({})
        num_of_track_points = self.track_point_collection.count_documents({})
        print(
            tabulate(
                [
                    ["Users", num_of_users],
                    ["Activities", num_of_activities],
                    ["Track points", num_of_track_points],
                ],
                headers=["Table", "Entries"],
            )
        )

    def task_2_2_average_activities_per_user(self):
        """
        Finds the amount of activities per user and then calulates the average
        """
        print("Task 2.2")
        result = self.activity_collection.aggregate(
            [
                {"$group": {"_id": "$user_id", "count": {"$count": {}}}},
                {
                    "$group": {
                        "_id": {},
                        "average": {"$avg": "$count"},
                    }
                },
            ]
        )

        print(f"Average amount of activites per user: {result.next()['average']}")

    def task_2_3_top_20_users_with_most_activities(self):
        """
        Finds the top 20 users by grouping activities by user_id and sum the count
        """
        print("Task 2.3")
        result = self.activity_collection.aggregate(
            [
                {"$group": {"_id": "$user_id", "numberOfActivities": {"$count": {}}}},
                {
                    "$sort": {
                        "numberOfActivities": -1,
                    }
                },
                {"$limit": 20},
            ]
        )
        print(
            tabulate(
                list(map(lambda x: (x["_id"], x["numberOfActivities"]), result)),
                headers=["User_id", "Number of activities"],
            )
        )

    def task_2_4_users_whos_taken_taxi(self):
        """
        Finding all distinct users with activities where the transportation mode is taxi
        """
        result = self.activity_collection.distinct(
            "user_id", {"transportation_mode": "taxi"}
        )
        print("Task 2.4")
        print(
            tabulate(
                list(map(lambda x: [x], result)),
                headers=["User_id"],
            )
        )

    def task_2_5_count_transportations(self):
        """
        Counting the number of transportation modes by selecting all activities with a transportation mode that is not 'NONE'
        By grouping by transportation_mode, the count of entries represents the number of activities per transportation_mode
        """
        print("Task 2.5")
        result = self.activity_collection.aggregate(
            [
                {"$match": {"transportation_mode": {"$ne": None}}},
                {"$group": {"_id": "$transportation_mode", "count": {"$count": {}}}},
                {
                    "$sort": {
                        "count": -1,
                    }
                },
            ]
        )
        print(
            tabulate(
                list(map(lambda x: (x["_id"], x["count"]), result)),
                headers=["Transportation mode", "Count"],
            )
        )

    def task_2_6_a_year_with_most_activities(self):
        """
        In this task we consider only the year of the start_date_time
        It is possible for a task to start in one year, and end in the next, however we've limited the year to only start_time.
        This is considered appropriate, as the possibility for overlapping activities, would make the sum of counts of activities for each year,
        greater than the total registered activities.
        """

        print("Task 2.6 a)")
        result = self.activity_collection.aggregate(
            [
                {
                    "$group": {
                        "_id": {"$year": "$start_date_time"},
                        "numberOfActivities": {"$count": {}},
                    }
                },
                {
                    "$sort": {
                        "numberOfActivities": -1,
                    }
                },
            ]
        )
        print(
            tabulate(
                list(map(lambda x: (x["_id"], x["numberOfActivities"]), result)),
                headers=["Year", "Number of activities"],
            )
        )

    def task_2_6_b_year_with_longest_activities(self):
        """
        As with task 2.6.b, we only consider the start_date_time when grouping an activity to a year.
        We then find the length of each activity in seconds and divides it to hours.
        This is done in order to get hours as a double and not as an int.
        Finally this is summed for each year.
        """

        print("Task 2.6 b)")
        result = self.activity_collection.aggregate(
            [
                {
                    "$group": {
                        "_id": {"$year": "$start_date_time"},
                        "numberOfHours": {
                            "$sum": {
                                "$divide": [
                                    {
                                        "$dateDiff": {
                                            "startDate": "$start_date_time",
                                            "endDate": "$end_date_time",
                                            "unit": "second",
                                        }
                                    },
                                    3600,
                                ]
                            }
                        },
                    }
                },
                {
                    "$sort": {
                        "numberOfHours": -1,
                    }
                },
            ]
        )
        print(
            tabulate(
                list(map(lambda x: (x["_id"], x["numberOfHours"]), result)),
                headers=["Year", "Number of hours"],
            )
        )

    def task_2_7_total_distance_walked_in_2008_by_112(self):
        """
        Retrieves all activities that were walked by user 112 in 2008.
        The activities is then grouped by user_id.
        The distance is finally summed as each activity contains a field with the activity-distance.
        """
        print("Task 2.7")

        result = self.activity_collection.aggregate(
            [
                {
                    "$match": {
                        "$expr": {
                            "$and": [
                                {"$eq": ["$user_id", "112"]},
                                {"$eq": ["$transportation_mode", "walk"]},
                                {"$eq": [{"$year": "$start_date_time"}, 2008]},
                            ]
                        }
                    }
                },
                {
                    "$group": {
                        "_id": "$user_id",
                        "totalDistance": {"$sum": "$total_distance"},
                    }
                },
            ]
        )
        total_distance = result.next()["totalDistance"]

        print(f"Total distance by user 112 in 2008: {total_distance} km")

    def task_2_8_top_20_users_with_most_altitude_meters(self):
        """
        Activities are grouped by user_id with "altitudeGained" summed from the "altitude_gained"-field in each activity.
        The altitude is also converted from feet to meters.
        """
        print("Task 2.8")
        result = self.activity_collection.aggregate(
            [
                {
                    "$group": {
                        "_id": "$user_id",
                        "altitudeGained": {
                            "$sum": {"$multiply": ["$altitude_gained", 0.3048]}
                        },
                    }
                },
                {
                    "$sort": {
                        "altitudeGained": -1,
                    }
                },
                {"$limit": 20},
            ]
        )
        print(
            tabulate(
                list(map(lambda x: (x["_id"], x["altitudeGained"]), result)),
                headers=["UserId", "Altitude meters gained"],
            )
        )

    def task_2_9_invalid_activities_per_user(self):
        """
        First the track_points are sorted by date_time. Then they're grouped by activity_id and added to each activity_id
        as an array of date_times, one per track_point. Then projection is used to reduce the array of date_times to a boolean
        which tells if the activity is invalid or not based on the distance between each date_time. Finally the activities are
        grouped by they're user_id and for each user a count of invalid activities and then sorted.
        """
        print("Task 2.9")
        result = self.track_point_collection.aggregate(
            [
                {
                    "$sort": {
                        "date_time": 1,
                    },
                },
                {
                    "$group": {
                        "_id": "$activity_id",
                        "user_id": {"$first": "$user_id"},
                        "track_points": {"$push": "$date_time"},
                    }
                },
                {
                    "$project": {
                        "user_id": 1,
                        "invalid": {
                            "$reduce": {
                                "input": "$track_points",
                                "initialValue": {"prev_point": None, "invalid": 0},
                                "in": {
                                    "$cond": {
                                        "if": {
                                            "$lt": [
                                                {
                                                    "$dateDiff": {
                                                        "startDate": "$$value.prev_point",
                                                        "endDate": "$$this",
                                                        "unit": "second",
                                                    }
                                                },
                                                5 * 60,
                                            ]
                                        },
                                        "then": {
                                            "prev_point": "$$this",
                                            "invalid": "$$value.invalid",
                                        },
                                        "else": {
                                            "prev_point": "$$this",
                                            "invalid": True,
                                        },
                                    }
                                },
                            }
                        }
                    },
                },
                {
                    "$group": {
                        "_id": "$user_id",
                        "amount": {"$sum": {
                            '$cond': {
                                "if": { "$eq": ['$invalid.invalid', True]}, 
                                "then": 1, 
                                "else": 0
                            }
                        }}
                    },
                },
                {
                    "$sort": {
                        "amount": -1,
                    },
                },
            ]
        )

        print(tabulate(list(map(lambda x: (x["_id"], x["amount"]), result)), headers=("UserId", "Invalid activities")))

    def task_2_10_users_tracked_forbidden_city(self):
        """
        Since we do not get exact matches on geo location, we consider the Forbidden City to be between the nearest possible geolocations
        This is a consequence of the track_points having more specific geo locations than the Forbidden City
        The disctinct users with activities registered in the between clause is listed
        """
        print("Task 2.10")
        result = self.track_point_collection.aggregate(
            [
                {
                    "$match": {
                        "$expr": {
                            "$and": [
                                {"$gte": ["$lat", 39.916]},
                                {"$lt": ["$lat", 39.917]},
                                {"$gte": ["$lon", 116.397]},
                                {"$lt": ["$lon", 116.398]},
                            ]
                        }
                    }
                },
                {"$group": {"_id": "$user_id"}},
            ]
        )

        print(tabulate(list(map(lambda x: (x["_id"],), result)), headers=["User_id"]))

    def task_2_11_users_most_used_transportation_mode(self):
        """
        Firstly, activities with no transportation_mode is filtered out. We then group in order to
        find a count of activities per transportation_mode per user. Finally we group by user_id and pick
        the most used transportation_mode and order by user_id.
        """
        print("Task 2.11")
        result = self.activity_collection.aggregate(
            [
                {"$match": {"transportation_mode": {"$ne": None}}},
                {
                    "$group": {
                        "_id": {
                            "user_id": "$user_id",
                            "mode": "$transportation_mode",
                        },
                        "numberOfActivities": {"$count": {}},
                    },
                },
                {
                    "$group": {
                        "_id": "$_id.user_id",
                        "maxVal": {
                            "$max": {
                                "max": "$numberOfActivities",
                                "mode": "$_id.mode",
                            }
                        },
                    }
                },
                {
                    "$sort": {
                        "_id": 1,
                    }
                },
            ]
        )
        print(
            tabulate(
                list(map(lambda x: (x["_id"], x["maxVal"]["mode"]), result)),
                headers=["UserId", "Mode", "numberOfActivities"],
            )
        )
