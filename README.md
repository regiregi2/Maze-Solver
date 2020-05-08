#M AZE SOLVER

## Description
A-Level coursework project
Maze solver is a program that can solve various images of mazes by just
inputting the start and end of the maze

## how to use
 - convert the maze into greyscale using the provided rbgToGreyscale method
 - convert the greyscale maze to binary using the greyscaleToRgb method 
    (otsu method can be used to find a good threshold)
 - Skeletonize the binary image using the ZhangSuen Method
 - solve and display solution to maze (example for how to display is shown in the main method)


## TODO

 - parallelize Zhang Zuen algorithm
 - Add more algorithms for solving mazes
 - improve speed for high resolution mazes