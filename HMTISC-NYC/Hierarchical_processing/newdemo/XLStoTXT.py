import xlrd
import csv

def xls_to_csv():

    x =  xlrd.open_workbook('cluster_Ng.xls')
    x1 = x.sheet_by_name('Sheet1')
    print x1
    csvfile = open('cluster.txt', 'w')
    
    for rownum in xrange(x1.nrows): #To determine the total rows.
        print x1.row_values(rownum)[0]
        csvfile.write(str(x1.row_values(rownum)[0])+"\t"+str(x1.row_values(rownum)[1])+ "\t" + str(int(x1.row_values(rownum)[3])) + "\n")

    csvfile.close()

xls_to_csv()
