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
    