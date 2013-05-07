clc;
clear all;


data= load('YelpData/TrainData');
data=reorderData(data);
s=[5,6,7];
Y1=data(:,4);
textFeatures=data(:,16:end);
X1=data(:,s);


trainLimit=100000;
testLimit=trainLimit+1;
testEnd=150000;
% 
%X=horzcat(X1,textFeatures);
Xtr=X1(1:trainLimit,1:end);
Ytr=Y1(1:trainLimit,:);
Xte=X1(testLimit:testEnd,1:end);
Yte=Y1(testLimit:testEnd,:);
% 
% %[Xtr, M, S] = whiten(X); Xte = whiten(Xt, M, S);
  for d=25:25
       d
%       %[XtrP,T] = rescale(Xtr); XteP = rescale(Xte,T);
  [XtrP,T] = rescale(fpoly(Xtr,d)); XteP = rescale(fpoly(Xte,d),T);
  lr = linearRegress(XtrP,Ytr); % create &     train model
  mse1(d) = sqrt(mean( (log(Ytr+1) - log(predict(lr,XtrP)+1)).^2 )); 
  mse2(d) = sqrt(mean( (log(Yte+1) - log(predict(lr,XteP)+1)).^2 ));
  end;
%   figure; semilogx(1:d,mse1,'r-',1:d,mse2,'g-','linewidth',2);
% 
%  
TestData= load('YelpData/TestData.txt');
s1=[1,5,6,7];
ReviewId=TestData(:,1);
TesttextFeatures=TestData(:,16:end);
Xt1=TestData(:,s1);
Xtest=horzcat(Xt1,TesttextFeatures);

%Xtest=horzcat(Xt1,TesttextFeatures);
XtestP = rescale(fpoly(Xtest,25),T);
YtHat=predict(lr,XtestP);
fid = fopen('results3.txt', 'w');
 
 fprintf(fid, '%d %d\n',[ReviewId, YtHat]');

 fclose(fid);



% classesTe = predict(lr, Xtest);
% fid = fopen('results-1SVM5000-rescale+whiten.txt', 'w');
%  for i = 1:size(classesTe, 1)
%  fprintf(fid, '%d %d\n', 50000+i, classesTe(i));
%  end
%  fclose(fid);
% end