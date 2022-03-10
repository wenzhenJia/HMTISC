clear all;
clc;
fid=fopen('num.txt','wt');
for i=0:331
    filename=['D:\HMTISC-DC\Hierarchical_processing\Mobility_trend_dataprocessing\fandemo\' num2str(i) '.csv'];
    A=csvread(filename);
    n = norm(A, 'fro' ); %Çó¾ØÕóAµÄFrobenius·¶Êý 
    fprintf(fid,'%.12f\n',n);
end
