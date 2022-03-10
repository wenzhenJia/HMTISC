# -*- coding: utf-8 -*-
#在程序目录下创建output目录
# 读取csv文件
import csv
import time

import h5py
import numpy as np

cvspathlist=['./DCdata/2011-processed.csv',
             './DCdata/2012-processed.csv',
             './DCdata/2013-processed.csv',
             './DCdata/2014-processed.csv',
             './DCdata/2015-processed.csv',
             './DCdata/2016-processed.csv'] #填写需要处理的文件个数,z注意路径斜杠数目
clusterpath='./cluster_20.txt'

#读取类别文件
def readcluster(path):
    file = open(path, 'r')
    line_list=[]
    while True:
        line = file.readline()
        if not line :
            break
        else:
            list = line.split("\t")
            list[2] = list[2].replace('\r\n', '')
            line_list.append(list)
    return line_list

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

#处理得到type 类型
def type_get(x,y,line_list):
    type=None
    for i in line_list:
        if i[0]==x and i[1]==y:
            type=i[2]
            break
    return  type
#读取类别文件，保存
line_list=readcluster(clusterpath)

#读取cvs文件
def readcvs(paths):
    start_list=[]
    stop_list=[]
    for  path in paths:#读取所有的路径
        print('正在读取文件{0}...'.format(path))
    #这两个同级
        with open(path, 'r') as f:
            reader = csv.reader(f)
            #['1941', '2014-04-01 08:34:13', '2014-04-01 09:06:34', '519', 'E 42 St & Vanderbilt Ave', '40.752416', '-73.97837', '315', 'South St & Gouverneur Ln', '40.70355377', '-74.00670227', '20536', 'Subscriber', '1971', '1']
            for index, row in  enumerate(reader):
                if index == 0: continue
                try:
                    res=[row[1],row[2],row[5],row[6],row[9],row[10]]
                    start_mk=time_resolve(res[0])
                    stop_mk=time_resolve(res[1])
                    if start_mk>0 and start_mk<=52608:
                        start_type = type_get(res[2],res[3],line_list)
                        end_type = type_get(res[4],res[5],line_list)
                        if (not start_type) or (not end_type): continue
                        # 1、处理条目保存为两类;开始时间，开始类型，结束时间，结束类型
                        start_list.append([start_mk, start_type ,stop_mk, end_type])
                except Exception as ex:
                    print(ex)
    return start_list

def main():

    startlist = readcvs(cvspathlist)
    res = np.zeros((52608, 2, 30, 30))
    for a in startlist:
        if int(a[0]) < 52609 and int(a[2]) < 52609:
            res[int(a[0])-1][0][int(a[1])-1][int(a[3])-1] += 1
            res[int(a[2])-1][1][int(a[1])-1][int(a[3])-1] += 1
    print('final res shape: ', res.shape)
    f = h5py.File('data_20.h5', 'w')  # 打开h5文件
    f.create_dataset('data', shape=(52608, 2, 30, 30), dtype='float64', data=res)

# 5w * 1500w * 3
if __name__ == "__main__":
    main()
