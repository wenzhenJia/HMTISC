a = []
b = []
with open("cluster.txt","r") as f1, open("num.txt","r") as f2:
    for i in f1:
        a.append(i.strip("\n").split()[:2])
    for i in f2:
        b.append(i.strip("\n").split()[:])

print len(a)
print len(b)
with open("newfile.txt","w") as f3:
    for i in range(len(a)):
        list1 = a[i]+b[i]
        str1 = '\t'.join(str(e) for e in list1) + "\n"
        f3.write(str1)

