Thomas Ortiz - tdortiz - CSC 484 - Eclipse Version: Eclipse Neon.2 

[Project 1](#project-1)

[Project 2](#project-2)

[Project 3](#project-3)


# Project-3
## Files in Submission Folder
   - GameAI --> Eclipse Project (this is what you import)
   - Part 3 - Media --> this has pictures of my trees and video of the program
   - Game-AI-HW-3.jar --> The jar executable to launch the program
   - README.md --> Readme in markdown language
   - README.txt --> Readme in plaintext

## Import Guidelines:

1. Unzip the submission folder
2. Open Eclipse and go to Package Explorer
3. Right click the Package Explorer and click "Import"
4. Under "General," select "Existing Project into Workspace"
5. Click the first "Browse" button and go to where you unzipped the submission folder
6. Select "Game AI" and press finish

## To Run with JAR:

1. In the base of the project folder there is "Game AI - Project 3.jar". Open it.

## To Run on Eclipse:

1. Go to default package
2. Run the DecisionMakingClient.java file 

## Special Actions When Running The Program
Buttons:

* Toggle Graph - toggles the visual for the graph.
* Use Decision Tree/Use Behavior Tree - toggles which tree Red is currently using.

Clicks (outside of buttons):

* Left Click - Resets the world .
* Right Click - Set Red's target.

## Client Files:

* DecisionMakingClient.java

## DecisionMakingClient
Using behavior trees and decision trees I simulated basic behavior for two AI. 

The green character, "Green", acts as follows:

* Wander around by default.
* If "Awakeness" is 0, go to bed. This action is prioritized.
* If "Hunger" is 0, go to the fridge. 

The red character, "Red", acts as follows:

* Wander around by default.
* If Red is close to Green, start pursuing him. If Red gets really close, Red will arrive ontop of Green killing him.
* If Red is close to the TV, travel infront of the tv and randomly dance or sit for 3 seconds. These actions are on a 3 second cooldown.










# Project-2
## Import Guidelines:

1. Unzip the submission folder
2. Open Eclipse and go to Package Explorer
3. Right click the Package Explorer and click "Import"
4. Under "General," select "Existing Project into Workspace"
5. Click the first "Browse" button and go to where you unzipped the submission folder
6. Select "Game AI" and press finish
    
## To Run:

1. Go to default package
2. Run the PathFollowingClient.java file

## Client Files:

* PathFollowingClient.java
* PathFindingAnalyzer.java (if you want to run my analysis program)

## PathFollowingClient - Testing Different Algorithms
By default, the client will use A*-Euclidean pathfinding. 

If you want to switch the algorithm though here are the steps:

1. Open PathFollowingClient.java
2. Goto Line 227 (Theres a TODO here for a quick link)
3. Comment out the previous pathfinding algorithm and uncomment the version you want to test with.

## Big Graph Creation
I created a java class with the code at this repository: https://github.com/Tdortiz/Graph_Tools

## Imgur Album: 
http://imgur.com/a/IuiXg










# Project-1
## Import Guidelines:

1. Unzip the submission folder
2. Open Eclipse and go to Package Explorer
3. Right click the Package Explorer and click "Import"
4. Under "General," select "Existing Project into Workspace"
5. Click the first "Browse" button and go to where you unzipped the submission folder
6. Select all projects and press finish
    
## To Run:

1. Go to default package of project you want to run
2. Run the __________Client.java file

## Client Files:

* KinematicSeekClient.java
* SteeringArriveClient.java
* SteeringWanderClient.java
* SteeringWanderClient.java

## Flocking Specific (if you want to see wanderers)

1. Open the SteeringFlockingClient.java file
2. Uncomment line 38 which will now cause the client to spawn two black wanderers for 
   the flock to follow. Note the wanderers are the same size but they will demonstrate
   the wander behavior


## Imgur Album: 
* Disclaimer - These animations were recorded before I messed with colors for the characters, background, and removing debug information.

Imgur Album of Animations: http://imgur.com/a/lNB5L

Kinematic Seek - http://imgur.com/gKcaHTk

Steering Arrive Ideal - http://imgur.com/B9DTm8r

Steering Arrive Bad Behavior - http://imgur.com/Pu1xXYn

Steering Wander Ideal - http://imgur.com/ZUVZsEI

Steering Wander - Small Max Rotation - http://imgur.com/k9JEe42

Steering Wander - Small Max Angular Acceleration - http://imgur.com/vgonISQ

Steering Wander - Bigger Max Angular Acceleration - http://imgur.com/pugLRmE

Flocking - http://imgur.com/D1asCxZ

Color Flocking - http://imgur.com/bA59LM0
