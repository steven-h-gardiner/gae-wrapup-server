import ntpath
import csv
import datetime
import numpy as np

def main():
    pars_data_dir = 'participant_data'
    dir_files = ntpath.os.listdir(pars_data_dir)

    participant_data = {}
    for file in dir_files:
        participants = process_file(pars_data_dir + '/' + file)
        for part in participants:
            participant_data[part] = participants[part]

    for participant in participant_data:
        print participant_data[participant]
        # participant._summarize()

def process_file(participant_file):
    f = open(participant_file, 'rb')
    csv_reader = csv.DictReader(f, delimiter=",")

    participants = {}
    for row in csv_reader:
        id = row['hash']
        if id not in participants:
            participants[id] = Participant(id, row)
        else:
            participant = participants[id]
            participant.data.append(row)

    f.close()
    
    return participants


class Participant():

    def __init__(self, id, data):
        self.id = id
        self.data = [data]
        self.summarize = None

    def __getitem__(self, item):
        return item

    def _summarize(self):
        data = self.data
        task_times = {}
        for task in data:

            no = task['taskno']
            if no not in task_times:
                task_times[no] = [task['timestamp']]
            else:
                timestamp = task['timestamp']
                # print time.mktime(timestamp)
        # print self.id, self.data


if __name__ == "__main__":
    main()
