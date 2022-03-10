import time

import numpy as np
import pandas as pd

cvspathlist=['./DCdata/2011-processed.csv',
             './DCdata/2012-processed.csv',
             './DCdata/2013-processed.csv',
             './DCdata/2014-processed.csv',
             './DCdata/2015-processed.csv',
             './DCdata/2016-processed.csv'] #填写需要处理的文件个数,z注意路径斜杠数目

clusterpath='cluster_3_18.txt'

#读取类别文件
def readcluster(path):
    file = open(path, 'r')
    type_map = {}
    cluster_list = []
    while True:
        line = file.readline()
        if not line :
            break
        else:
            list = line.split("\t")
            list[2] = list[2].replace('\r\n', '')
            type_map[list[0]+'&'+list[1]] = int(list[2])
            cluster_list.append(list[0]+'&'+list[1])
    return type_map, cluster_list

#读取类别文件，保存
type_map, cluster_list =readcluster(clusterpath)

#时间处理9/11/2014 00:10:00
def time_resolve(timestr='2011-01-01 00:00:00'):
    #2014-04-01 00:00:00 --》1396281600
    #9/11/2014 00:00:00 -->1410364800.0 +1h-->1410368400=3600s
    try:

        mktime = time.mktime(time.strptime(timestr, '%Y-%m-%d %H:%M:%S'))
    except Exception as ex:
        try:
            mktime = time.mktime(time.strptime(timestr, '%m/%d/%Y %H:%M:%S'))
        except Exception as ex:
            print(ex)
    start_mktime = time.mktime(time.strptime('2011-01-01 00:00:00', '%Y-%m-%d %H:%M:%S'))

    mark=(mktime-start_mktime)//3600+1  #取9/11/2014 00:10:00 为1，一次类推
    return mark

#读取cvs文件
def readcvs(paths):
    all_timestamp = []
    for path in paths:#读取所有的路径
        print('正在读取文件{}...'.format(path))
        file_content = pd.read_csv(path,  usecols=['starttime', 'start station latitude', 'start station longitude', 'end station latitude', 'end station longitude']).values
        all_timestamp.append(file_content)
    return np.concatenate(all_timestamp)

def main():
    all_timestamp = readcvs(cvspathlist)
    res = []
    for idx, station in enumerate(cluster_list):
        res = np.zeros((51936, 18))
        for record in all_timestamp:
            if record[1] == 0 or record[3] ==0:
                continue
            start_station = str(record[1])+'&'+str(record[2])
            if start_station == station:
                end_station = str(record[3])+'&'+str(record[4])
                if end_station not in type_map:
                    continue
                end_station_index = type_map[end_station]-1
                time_index = int(time_resolve(record[0]))
                if time_index >= 51936:
                    continue
                res[time_index-1][end_station_index] += 1
        pd.DataFrame(res).to_csv('fandemo/{}.csv'.format(idx), header=None, index=None)

# 5w * 1500w * 3
if __name__ == "__main__":
    main()
