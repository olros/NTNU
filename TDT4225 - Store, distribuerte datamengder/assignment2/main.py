import sys
from tabulate import tabulate

from setup import Setup
from insert import Insert
from task import Task


def main():
    try:
        arguments = sys.argv
        if not len(arguments) > 1:
            print("Supply an argument with what to do:")
            print(
                tabulate(
                    [
                        ["setup", "Create tables"],
                        ["insert", "Insert data to tables"],
                        ["tasks", "Run tasks"],
                    ],
                    headers=["Argument", "Description"],
                )
            )
            return

        todo = sys.argv[1]

        if todo == "setup":
            setup = Setup()

            setup.setup()

            setup.cleanup()

        if todo == "insert":
            insert = Insert()

            insert.insert_users()
            insert.insert_trajectories()

            insert.cleanup()

        if todo == "tasks":
            tasks = Task()

            tasks.task_2_1_count_tables_sizes()
            tasks.task_2_2_average_activities_per_user()
            tasks.task_2_3_top_20_users_with_most_activities()
            tasks.task_2_4_users_whos_taken_taxi()
            tasks.task_2_5_count_transportations()
            tasks.task_2_6_a_year_with_most_activities()
            tasks.task_2_6_b_year_with_longest_activities()
            tasks.task_2_7_total_distance_walked_in_2008_by_112()
            tasks.task_2_8_top_20_users_with_most_altitude_meters()
            tasks.task_2_9_invalid_activities_per_user()
            tasks.task_2_10_users_tracked_forbidden_city()
            tasks.task_2_11_users_most_used_transportation_mode()

            tasks.cleanup()

    except Exception as e:
        raise e


main()
