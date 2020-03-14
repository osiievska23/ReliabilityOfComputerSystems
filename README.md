# ReliabilityOfComputerSystems
## Lab1
### Set distribution 

Open lab1/Ranner.java and set file name allowed ones
```
private static double[] sample = getContentFromFile(EXPONENTIAL_FILE_NAME);
private static double[] sample = getContentFromFile(GAMMA_FILE_NAME);
private static double[] sample = getContentFromFile(NORMAL_FILE_NAME);
private static double[] sample = getContentFromFile(UNIFORM_FILE_NAME);
```
## Lab2
1. Open lab2/Ranner.java
2. To set filePath to scheme:
```
    private static final int[][] matrix = getMatrixFromFile("src/main/resources/scheme.csv");
```
3. To set elemetns probabilities:
```
    private static final double[] probabilities = new double[]{0.5, 0.6, 0.7, 0.8, 0.85, 0.9, 0.92, 0.94};
```
