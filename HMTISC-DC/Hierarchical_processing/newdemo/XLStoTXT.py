import pandas as pd
import numpy as np

def xls_to_csv():

    x = pd.read_excel('cluster_AP.xls',header=None).values
    raw_cluster = open('cluster.txt')
    raw_cluster = raw_cluster.readlines()
    csvfile = open('cluster_3_18.txt', 'w')
    for rownum in range(0, x.shape[0]): #To determine the total rows.
        csvfile.write( raw_cluster[rownum].split("\n")[0] + "\t" + str(int(x[rownum,3])) + "\n")
    csvfile.close()

xls_to_csv()
