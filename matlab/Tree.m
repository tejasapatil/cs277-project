clc;
clear all;

data= load('InputTrainData.txt');
data=reorderData(data);
Y=data(:,1);
X=data(:,2:end);

trainLimit=150000;
testLimit=trainLimit+1;
testEnd=200000;
% 
%X=horzcat(X1,textFeatures);
Xtr=X(1:trainLimit,1:end);
Ytr=Y(1:trainLimit,:);
Xte=X(testLimit:testEnd,1:end);
Yte=Y(testLimit:testEnd,:);
d=[1,10,20,30,40,50,60];
for i=7:7
    i
    mytree = treefit(Xtr, Ytr, 'method', 'regression', 'splitmin', d(i));
    y_hatTr = treeval(mytree, Xtr);
    y_hatTe = treeval(mytree, Xte);

    ErrTr(i)=sqrt(mean( (log(Ytr+1) - log(y_hatTr+1)).^2 ));
    ErrTe(i)=sqrt(mean( (log(Yte+1) - log(y_hatTe+1)).^2 ));
end;

%figure; semilogx(d,ErrTr,'r-',d,ErrTe,'g-','linewidth',2);


data= load('test_outputdata');
%data=reorderData(data);
Review_id=data(:,1);
Xtest=data(:,2:end);
y_hatTest = treeval(mytree, Xtest);
  
fid = fopen('results_tree.txt', 'w');
 
 fprintf(fid, '%d %d\n',[Review_id, y_hatTest]');

 fclose(fid);


