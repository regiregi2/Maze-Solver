import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedImage img = ImageIO.read(new File("testMazes\\img.gif"));
        int[][] skelMaze = ZhangSuen(greyscaleToBinary(rgbToGreyscale(img), 128));
        int[] s = {3, 1};
        int[] e = {398, 399};
        s = startEnd(skelMaze, s);
        e = startEnd(skelMaze, e);
        Boolean seeSolve = false;
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        int PAUSE_TIME = 1;
        int[][] sol = randomSolve(skelMaze, s, e, seeSolve);
        if(seeSolve){
            frame.getContentPane().add(new JLabel(new ImageIcon(img)));
            frame.pack();
            frame.setVisible(true);
            for (int[] ints : sol) {
                Thread.sleep(PAUSE_TIME);
                if(new Color(img.getRGB(ints[0], ints[1])).equals(Color.RED)){
                    img.setRGB(ints[0], ints[1], Color.GREEN.getRGB());
                }
                else{
                    img.setRGB(ints[0], ints[1], Color.RED.getRGB());
                }
                frame.getContentPane().add(new JLabel(new ImageIcon(img)));
                frame.revalidate();
            }
        }
        else{
            for (int[] ints : sol) {
                img.setRGB(ints[0], ints[1], Color.RED.getRGB());
            }
            frame.getContentPane().add(new JLabel(new ImageIcon(img)));
            frame.pack();
            frame.setVisible(true);
        }



    }

    /**
     * Method to convert RGB maze to greyscale
     * @param maze RGB image of maze
     * @return greyscale version of inputted maze
     */
    public static int[][] rgbToGreyscale(BufferedImage maze){
        int[][] greyScaleMaze = new int[maze.getWidth()][maze.getHeight()]; //array that is the size of the height and width
        int pixel;

        for(int x = 0; x < maze.getWidth(); x++) { //set x to the width pixel being iterated over
            for(int y = 0; y < maze.getHeight(); y++) { // set y to the height pixel being iterated over
                pixel = maze.getRGB(x, y);
                greyScaleMaze[x][y] = ((((pixel>>16) & 0xff) + ((pixel>>8) & 0xff) + ((pixel) & 0xff)) / 3);
            }
        }
        return greyScaleMaze; //greyscale maze is returned
    }

    /**
     * Method to convert greyscale maze to a binary maze
     * @param greyScaleMaze greyscale image as a 2d array
     * @param threshold threshold for which values bellow it will be 0
     * @return Binary maze
     */
    public static int[][] greyscaleToBinary(int[][] greyScaleMaze, int threshold){
        int[][] binaryMaze = new int[greyScaleMaze.length][greyScaleMaze[0].length]; //array that is the size of the height and width of the image
        int pixel; //pixel variable is initialised

        for(int x=0; x<greyScaleMaze.length; x++){ //set x to the width pixel being iterated over
            for (int y=0; y<greyScaleMaze[0].length; y++){ // set y to the height pixel being iterated over
                pixel = greyScaleMaze[x][y]; //get value of pixel
                if(pixel < threshold){       // if the pixel value is less than 172 then its set to 0
                    binaryMaze[x][y] = 0;
                }
                else {                  // if the pixel value is greater than 172 then its set to 0
                    binaryMaze[x][y] = 1;
                }

            }
        }
        return binaryMaze;
    }

    /**
     * find the connectivity number of a given pixel
     * @param pixels the pixel and all the 8 pixels around it
     * @return connectivity number of a given pixel
     */
    public static int connectivityNum(int[] pixels){
        int connectivityNum = 0;
        for(int x=1; x<8; x++) {
            if(pixels[x] == 0 && pixels[x+1] == 1) {
                connectivityNum += 1;
            }
        }
        if(pixels[8] == 0 && pixels[1] == 1) {
            connectivityNum +=1;
        }
        return connectivityNum;
    }

    /**
     * finds the number of white pixels around a given pixel
     * @param pixels array of the pixel followed by the pixels around it
     * @return number of white pixels around a pixel
     */
    public static int objectNeighbours(int[] pixels){
        int objectNeighbours = 0;
        for(int x=1; x<9; x++){
            if(pixels[x] == 1){
                objectNeighbours += 1;
            }
        }

        return objectNeighbours;
    }

    /**
     * Skeletonizes a binary image using the Zhang Suen algorithm
     * @param binaryImage
     * @return Skeletonized image
     */
    public static int[][] ZhangSuen(int[][] binaryImage){

        //variables are either set or instantiated
        int[] pixels;
        int[][] thinnedImage = binaryImage;

        List<int[]> delete;

        while(true){
            delete = new ArrayList<int[]>(); //delete array is cleared
            for(int x=1; x<thinnedImage.length-1; x++){ // all the pixels in the image are looped through
                for(int y=1; y<thinnedImage[0].length-1; y++) {

                    pixels = new int[] {thinnedImage[x][y], thinnedImage[x-1][y], thinnedImage[x-1][y+1], thinnedImage[x][y+1], thinnedImage[x+1][y+1],
                            thinnedImage[x+1][y], thinnedImage[x+1][y-1], thinnedImage[x][y-1], thinnedImage[x-1][y-1]}; //Array of pixels around the current pixel
                    //is made
                    int conNum = connectivityNum(pixels); //the connectivity number of the pixel is found

                    if(conNum == 1 && (objectNeighbours(pixels) >= 2) && (objectNeighbours(pixels) <= 6) &&
                            (thinnedImage[x-1][y] == 0 || thinnedImage[x][y+1] == 0 || thinnedImage[x+1][y] == 0) &&
                            (thinnedImage[x][y+1] == 0 || thinnedImage[x+1][y] == 0 || thinnedImage[x][y-1] == 0) && thinnedImage[x][y] == 1){ //checks of first set of parameters
                        //are true
                        int[] pixel = {x,y}; //if the parameters are true then the pixels is added to the delete array
                        delete.add(pixel);
                    }
                }
            }

            if(delete.size() != 0) { //if the delete array is not empty then the pixels at the coordinates in the array are removed
                for(int x=0; x<delete.size(); x++){
                    thinnedImage[delete.get(x)[0]][delete.get(x)[1]] = 0;
                }
            }else { // if the array is empty then the image is thinned so the image is returned
                return thinnedImage;
            }


            for(int x=1; x<thinnedImage.length-1; x++){ // all the pixels in the image are looped through
                for(int y=1; y<thinnedImage[0].length-1; y++) {

                    pixels = new int[] {thinnedImage[x][y], thinnedImage[x-1][y], thinnedImage[x-1][y+1], thinnedImage[x][y+1], thinnedImage[x+1][y+1],
                            thinnedImage[x+1][y], thinnedImage[x+1][y-1], thinnedImage[x][y-1], thinnedImage[x-1][y-1]};//Array of pixels around the current pixel
                    //is made
                    int conNum = connectivityNum(pixels); // the connectivity number of the pixel is found

                    if(conNum == 1 && (objectNeighbours(pixels) >= 2) && (objectNeighbours(pixels) <= 6) &&
                            (thinnedImage[x-1][y] == 0 || thinnedImage[x][y+1] == 0 || thinnedImage[x][y-1] == 0) &&
                            (thinnedImage[x-1][y] == 0 || thinnedImage[x+1][y] == 0 || thinnedImage[x][y-1] == 0) && thinnedImage[x][y] == 1){//checks of first set of parameters
                        //are true

                        int[] pixel = {x,y}; //if the parameters are true then the pixels is added to the delete array
                        delete.add(pixel);
                    }
                }
            }

            if(delete.size() != 0) { //if the delete array is not empty then the pixels at the coordinates in the array are removed
                for(int x=0; x<delete.size(); x++){
                    thinnedImage[delete.get(x)[0]][delete.get(x)[1]] = 0;
                }
            }else { // if the array is empty then the image is thinned so the image is returned
                for(int x=0; x<thinnedImage.length; x++) {
                    for(int y=0; y<thinnedImage[0].length; x++) {

                    }
                }
                return thinnedImage;
            }

        }
    }

    /**
     * Returns the inter-class variance of an image at a certain threshold
     * @param histogram Histogram of each greyscale value in an image
     * @param threshold
     * @param pixelNum the number of pixels in the image
     * @return The inter class variance of an image at a specific threshold
     */
    public static float interClassVarience(int[] histogram, int threshold, int pixelNum) {
        float weightB = 0; //weights and means are initialised and set to 0
        float weightW = 0;
        float meanB = 0;
        float meanW = 0;
        float sumB = 0;

        for(int x=0; x<threshold; x++) { //all the colour values from 0 to the threshold a looped through
            meanB += histogram[x] * x; //the amount of pixels that are of a colour are multiplied by the colour
            sumB += histogram[x];
        }

        weightB = sumB/pixelNum; //the final weighB is calculated
        weightW = 1 - weightB; //both weighs add up to 1 so weightW will be  1 - weightB

        for(int x=threshold; x<256; x++) { //the remaining pixels are iterated over
            meanW += histogram[x] * x; //the amount of pixels that are of a colour are multiplied by the colour
        }

        if(sumB == 0) { //if the sum is 0 the mean has to be set to 0 because you can't divide by 0
            meanB = 0;
        }
        else {
            meanB = meanB / sumB; //final mean is calculated
        }

        if(sumB == pixelNum) { //if the sum is 0 the mean has to be set to pixelNum because you can't divide by 0
            meanW = 0;
        }
        else {
            meanW = meanW / (pixelNum - sumB); //final mean is calculated
        }

        return weightB * weightW * ((meanB - meanW) * (meanB - meanW)); //interclassVariance is calculated and returned
    }

    /**
     * a method of finding the optimal threshold for converting a greyscale image to binary
     * @param maze
     * @return the optimal threshold
     */
    public static int otsu(int[][] maze) {
        int pixelNum = maze.length * maze[0].length;
        int[] histogram = new int[256];
        for(int x=0; x<maze.length; x++) {
            for(int y=0; y<maze[0].length; y++) {
                histogram[maze[x][y]] += 1;
            }
        }

        int threshold = 0;
        float highestInterClassVarience = interClassVarience(histogram, 0, pixelNum);

        for(int x=1; x<=255; x++) {
            float interClassVarience = interClassVarience(histogram, x, pixelNum);
            if(highestInterClassVarience < interClassVarience) {
                highestInterClassVarience = interClassVarience;
                threshold = x;
            }
        }

        return threshold;
    }

    /**
     * finds the nearest point on the skeletonized maze based on a pixel location
     * @param maze skeletonized maze
     * @param aproxLocation location of pixel on original maze
     * @return nearest pixel to the input on skeletonized maze
     */
    public static int[] startEnd(int[][] maze, int[] aproxLocation) {

        if(maze[aproxLocation[0]][aproxLocation[1]] == 1) { // if the use has tapped on a path pixel then the location of their tap is returned
            return aproxLocation;
        }
        List<int[]> path;                // path list is declared and instantiated
        path = new ArrayList<int[]>();

        int counter = 1;		//counter and found are both declared
        Boolean found = false;

        while(!found) { //while loop that runs until a path pixel is found

            int xMin = aproxLocation[0] - counter;
            if(xMin < 0) {
                xMin = 0;
            }

            int xMax = aproxLocation[0] + counter;
            if(xMax > maze.length - 1) {
                xMax = maze.length - 1;
            }

            int yMin = aproxLocation[1] - counter;
            if(yMin < 0) {
                yMin = 0;
            }

            int yMax = aproxLocation[1] + counter;
            if(yMax > maze[0].length - 1) {
                yMax = maze.length - 1;
            }


            if(aproxLocation[0] - counter > 0) {
                for(int y=yMin; y<=yMax; y++) {
                    if(maze[xMin][y] == 1) {
                        int[] pixel = {xMin, y};
                        path.add(pixel);
                        found = true;
                    }
                }
            }

            if(aproxLocation[0] + counter < maze.length) {
                for(int y=yMin; y<=yMax; y++) {
                    if(maze[xMax][y] == 1) {
                        int[] pixel = {xMax, y};
                        path.add(pixel);
                        found = true;
                    }
                }
            }

            if(aproxLocation[1] - counter > 0) {
                for(int x=xMin; x<=xMax; x++) {
                    if(maze[x][yMin] == 1) {
                        int[] pixel = {x, yMin};
                        path.add(pixel);
                        found = true;
                    }
                }
            }

            if(aproxLocation[1] + counter < maze[0].length) {
                for(int x=xMin; x<=xMax; x++) {
                    if(maze[x][yMax] == 1) {
                        int[] pixel = {x, yMax};
                        path.add(pixel);
                        found = true;
                    }
                }
            }

            counter = counter + 1;
        }

        if(path.size() == 1) {
            return path.get(0);
        }
        else {
            int[] shortest = path.get(0);
            float sDist = (float) Math.sqrt((path.get(0)[0] - aproxLocation[0]) + (path.get(0)[1] - aproxLocation[1]));

            for(int x=1; x<path.size(); x++) {
                float dist = (float) Math.sqrt((path.get(x)[0] - aproxLocation[0]) + (path.get(x)[1] - aproxLocation[1]));
                if(dist < sDist) {
                    sDist = dist;
                    shortest = path.get(x);
                }
            }

            return shortest;
        }
    }

    /**
     * method of solving the maze by going down paths randomly
     * @param maze skeletonized maze
     * @param start start of maze
     * @param end end of maze
     * @param seeSolve flag for seeing the algorithm traverse the maze
     * @return solution to maze
     */
    public static int[][] randomSolve(int[][] maze, int[] start, int[] end, Boolean seeSolve){
        int[][] solution = new int[maze.length * maze[0].length][3];
        int solutionPointer = 0;

        if(start == end){
            return new int[][]{start};
        }
        int[] newEnd = {end[0], end[1], 0};
        int[] newStart = {start[0], start[1], 0};
        solution[solutionPointer] = newStart;
        solutionPointer += 1;

        int[] currPosition = start;
        boolean solved = false;
        int[] invalid = {-1, -1, 1};
        int[][] avalPixels = new int[8][3];

        //for(int h=0; h<1000; h++) {
        while(!solved) {

            //pixel sort
            int avalPixelPointer = 0;
            int[][] pixels = {{currPosition[0]+1,currPosition[1], 0},
                    {currPosition[0]+1,currPosition[1]+1, 0},
                    {currPosition[0],currPosition[1]+1, 0},
                    {currPosition[0]-1,currPosition[1]+1, 0},
                    {currPosition[0]-1,currPosition[1], 0},
                    {currPosition[0]-1,currPosition[1]-1, 0},
                    {currPosition[0],currPosition[1]-1, 0},
                    {currPosition[0]+1, currPosition[1]-1, 0}};

            for(int x=0; x<8; x++) {
                if(pixels[x][0] < 0 || pixels[x][1] < 0 || pixels[x][0] > maze.length - 1 || pixels[x][1] > maze[0].length - 1) {
                    pixels[x] = invalid;
                }
            }
            //check unmoved pixels
            for(int x=0; x<8; x++) {
                if((pixels[x] != invalid) && (!contains(solution, pixels[x], solutionPointer)) && (maze[pixels[x][0]][pixels[x][1]] == 1)) {
                    avalPixels[avalPixelPointer] = pixels[x];
                    avalPixelPointer += 1;
                }
            }
            //turn back
            if(avalPixelPointer == 0) { //checks if there are any unmoved pixels available
                solution[solutionPointer - 1][2] = 1; //the current pixel is marked as being moved to twice
                for(int x=1; x<solutionPointer; x++) { //find the most recent pixel that has available neighbours
                    if(solution[solutionPointer - 1 - x][2] == 0) {
                        currPosition = solution[solutionPointer - 1 - x]; //if the pixel has available neighbours then its moved to
                        //System.out.println(Arrays.toString(currPosition) + "," + h);
                        break;
                    }
                }
            }

            else {
                boolean contain = false;
                for(int x=0; x<avalPixelPointer; x++) {
                    if(avalPixels[x][0] == end[0] && avalPixels[x][1] == end[1]) {
                        contain = true;
                    }
                }
                if(contain) {
                    currPosition = newEnd;
                    solved = true;
                }
                else {
                    currPosition = avalPixels[(int)(Math.random() * avalPixelPointer + 0)];
                }
            }
            solution[solutionPointer] = currPosition;
            solutionPointer += 1;
        }


        if(seeSolve) {
            return solution;
        }

        else {
            for(int x=0; x<solutionPointer; x++) {
                if(solution[x][2] == 1) {
                    solution[x] = start;
                }
            }
            return solution;
        }

    }

    /**
     * finds out weather a pixel in in an array of pixels
     * @param solution current colution
     * @param pixel pixel coordinates
     * @param pointer end of array
     * @return bollean value on if a pixel is in the solution
     */
    public static boolean contains(int[][] solution, int[]pixel, int pointer) {
        for(int x=0; x<pointer; x++) {
            int[] temp = solution[x];
            if(temp[0] == pixel[0] && temp[1] == pixel[1]) {
                return true;
            }
        }
        return false;
    }

}

