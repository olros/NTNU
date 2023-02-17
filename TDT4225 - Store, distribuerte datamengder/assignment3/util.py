from dataclasses import dataclass
from datetime import datetime

string_to_datetime = lambda x: datetime.strptime(x, '%Y-%m-%d %H:%M:%S')
"""Convert a string with format: `%Y-%m-%d %H:%M:%S` to a datetime-object"""

class Trajectory:
    
    def __init__(self, line: str):
        # Example line:
        # 39.975818526,116.331600228,0,492.126286089239,40758.3839351852,2011-08-03,09:12:52
        parts = line.strip().split(',')
        self.lat = parts[0]
        self.lon = parts[1]
        self.altitude = parts[3]
        self.date = f"{parts[5]} {parts[6]}"
    
    def to_tuple(self, activity_id):
        return (self.lat, self.lon, self.altitude, self.date, activity_id)
 
class LabelsActivity:

    def __init__(self, line: str):
        # Example line:
        # 2008/12/06 10:30:11	2008/12/06 11:17:54	subway
        parts = line.split()
        self.start_date = f"{parts[0].replace('/', '-')} {parts[1]}"
        self.end_date = f"{parts[2].replace('/', '-')} {parts[3]}"
        self.transportation_mode = parts[4]
    


@dataclass
class User:
    _id: int
    has_labels: bool

@dataclass
class TrackPoint:
    lat: float
    lon: float
    altitude: float
    date_time: datetime
    activity_id: str
    user_id: int

    @staticmethod
    def from_line(line: str, activity_id: str, user_id: int):
        parts = line.strip().split(',')
        return TrackPoint(
            float(parts[0]),
            float(parts[1]),
            float(parts[3]),
            string_to_datetime(f"{parts[5]} {parts[6]}"),
            activity_id,
            user_id
        )


@dataclass
class Activity:
    _id: int
    transportation_mode: str
    start_date_time: str
    end_date_time: str
    user_id: int
    total_distance: float
    altitude_gained: float

    @staticmethod
    def with_transportation_mode(activity_id: str, labels_activity: LabelsActivity, user_id: str, total_distance: float, altitude_gained: float):
        return Activity(
            _id = activity_id,
            transportation_mode = labels_activity.transportation_mode,
            start_date_time = string_to_datetime(labels_activity.start_date),
            end_date_time = string_to_datetime(labels_activity.end_date),
            user_id = user_id,
            total_distance = total_distance,
            altitude_gained = altitude_gained
        )

    @staticmethod
    def no_transportation_mode(activity_id: str, start_date: str, end_date: str, user_id: str, total_distance: float, altitude_gained: float):
        return Activity(
            _id = activity_id,
            transportation_mode = None,
            start_date_time = string_to_datetime(start_date),
            end_date_time = string_to_datetime(end_date),
            user_id = user_id,
            total_distance = total_distance,
            altitude_gained = altitude_gained
        )