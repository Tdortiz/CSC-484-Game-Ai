#set up matplotlib and the figure
import matplotlib.pyplot as plt
plt.figure

#add in labels and title
plt.xlabel("Amount of Nodes")
plt.ylabel("RunTime in ms")
plt.title("Pathing Finding # Vertices vs Time")

x_Series = [5000,10000,15000,20000, 25000, 50000, 75000]

dijk = [375.05, 2577.03, 5164.97, 13649.70, 19097.10, 136798.70, 578310.90]
aStar_eucl = [99.24, 477.37, 1187.49, 3310.28, 4712.90, 47820.12, 58766.78]
aStar_constant = [370.48, 2628.69, 5152.52, 13417.07, 18473.04, 138913.31, 562526.83]
aStar_random = [372.15, 2564.20, 5047.67, 13134.61, 17102.77, 126742.00, 462484.18 ]

array_dijk = [5.76, 16.62, 26.03, 59.76, 62.55, 259.22, 519.96]
array_aStar_eucl = [1.77, 3.90, 6.08, 15.93, 16.83, 77.94, 104.41]
array_aStar_constant = [4.54, 15.67, 23.56, 58.03, 56.90, 250.25, 468.22]
array_aStar_random = [5.05, 16.06, 23.79, 59.96, 56.69, 252.97, 457.52]

plt.plot(x_Series, dijk, label="Dijkstras")
plt.plot(x_Series, aStar_eucl, label="A*-Euclidean")
plt.plot(x_Series, aStar_constant, label="A*-Euclidean")
plt.plot(x_Series, aStar_random, label="A*-Euclidean")
plt.plot(x_Series, array_dijk, label="Array-Dijkstras")
plt.plot(x_Series, array_aStar_eucl, label="Array-A*-Euclidean")
plt.plot(x_Series, array_aStar_constant, label="Array-A*-Constant")
plt.plot(x_Series, array_aStar_random, label="Array-A*-Random")
#plt.xlim(5000, 75000)
#plt.ylim(0, 600) 

plt.legend(loc="upper left")
plt.show()