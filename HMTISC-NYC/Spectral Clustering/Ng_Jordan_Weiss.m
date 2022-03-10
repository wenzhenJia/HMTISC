clear all;
close all;

% generate the data
data = load('D:\HMTISC\Spectral Clustering\cluster_9-10.txt');
% data=data(:,1:2);
data=data(:,:);
% calculate the affinity / similarity matrix
affinity = CalculateAffinity(data);
%figure,imshow(affinity,[]), title('Affinity Matrix')

% compute the degree matrix
for i=1:size(affinity,1)
    D(i,i) = sum(affinity(i,:));
end

% compute the normalized laplacian / affinity matrix (method 1)
%NL1 = D^(-1/2) .* L .* D^(-1/2);
for i=1:size(affinity,1)
    for j=1:size(affinity,2)
        NL1(i,j) = affinity(i,j) / (sqrt(D(i,i)) * sqrt(D(j,j)));  
    end
end

% compute the normalized laplacian (method 2)  eye command is used to
% obtain the identity matrix of size m x n
% NL2 = eye(size(affinity,1),size(affinity,2)) - (D^(-1/2) .* affinity .* D^(-1/2));

% perform the eigen value decomposition
[eigVectors,eigValues] = eig(NL1);

% select k largest eigen vectors
k = 30;
nEigVec = eigVectors(:,(size(eigVectors,1)-(k-1)): size(eigVectors,1));
for i=1:size(nEigVec,1)
    n = sqrt(sum(nEigVec(i,:).^2));    
    U(i,:) = nEigVec(i,:) ./ n; 
end
[IDX,C] = kmeans(U,k); 
figure,
hold on;
for i=1:k
    I = find(IDX == i);
    scatter(data(I,1), data(I,2),50, 'filled');
    hold on
end
hold off;
title('Clustering Results using Spcl');
grid on;shg
cluster=[data,IDX];
xlswrite('cluster_Ng.xls',cluster)