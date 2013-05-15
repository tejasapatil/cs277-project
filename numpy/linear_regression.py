from numpy import *
from time import gmtime, strftime
import numpy
import matplotlib.pyplot as plt

def logger(message):
    print(strftime("%H:%M:%S", gmtime()) + " " + message);

def loadDataSet(fileName):
    numFeat = len(open(fileName).readline().split(','))
    dataMat = []; labelMat = []    
    fr = open(fileName)
    for line in fr:
        lineArr = []
        curLine = line.strip().split(',')
        for i in range(numFeat):
            if i == 3:
                labelMat.append(float(curLine[i]))
            else:
                lineArr.append(float(curLine[i]))
        dataMat.append(lineArr)
    return dataMat,labelMat

def standRegres(xArr,yArr):
    xMat = mat(xArr); yMat = mat(yArr).T
    xTx = xMat.T*xMat
    if linalg.det(xTx) == 0.0:
        logger("This matrix is singular, cannot do inverse");
        return
    ws = xTx.I * (xMat.T*yMat)
    return ws

logger("Begin");
logger("Loading training data...");
xArr,yArr=loadDataSet('joined.train.data')

logger("Running standard regression...");
ws = standRegres(xArr,yArr)

logger("Loading test data...");
testArr,fluff=loadDataSet('outer.joined_test.txt')

logger("Generating predictions...");
predictionMat=mat(testArr) * ws

logger("Dumping the predictions...");
numpy.savetxt("result.csv", predictionMat, delimiter=",")
logger("Done");
exit()
