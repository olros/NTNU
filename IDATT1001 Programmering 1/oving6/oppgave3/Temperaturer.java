public class Temperaturer {

  private static int temperaturer[][] = {{-5,-6,-5,-7,-7},{-2,-5,-4,-1,0},{0,1,2,1,2},{3,4,3,6,4},{6,8,7,9,6},{8,10,9,12,11}};

  public static int[][] getAlt(){
    return temperaturer;
  }



  public static double[] getMiddelTempDag() {
    double[] resultatTabell = new double[temperaturer.length];
    for (int i = 0; i < temperaturer.length; i++) {
      double tempSum = 0;
      for (int k = 0; k < temperaturer[i].length; k++) {
        tempSum = tempSum + temperaturer[i][k];
      }
      double middelTemp = tempSum / temperaturer[i].length;
      resultatTabell[i] = middelTemp;
    }
    return resultatTabell;
  }

  public static double[] getMiddelTempTime() {
    double[] resultatTabell = new double[24];
    for (int i = 0; i < 24; i++) {
      double timeSum = 0;
      int antallDager = 0;
      for (int k = 0; k < temperaturer.length; k++) {
        if (temperaturer[k].length > i) {
          antallDager += 1;
          timeSum = timeSum + temperaturer[k][i];
        }
      }
      double middelTemp = timeSum / antallDager;
      resultatTabell[i] = middelTemp;
    }
    return resultatTabell;
  }

  public static double getMiddelTempMonth() {
    double sum = 0;
    double[] middelTempDag = getMiddelTempDag();
    for (int i = 0; i < middelTempDag.length; i++) {
      sum = sum + middelTempDag[i];
    }
    double resultat = sum / middelTempDag.length * 1.0;
    return resultat;
  }

  public static int[] getAntallDagTemp() {
    int[] resultatTabell = new int[5];
    double[] middelTempDag = getMiddelTempDag();
    for (int i = 0; i < middelTempDag.length; i++) {
      if (middelTempDag[i] < -5) {
        resultatTabell[0] += 1;
      } else if (middelTempDag[i] < 0) {
        resultatTabell[1] += 1;
      } else if (middelTempDag[i] < 5) {
        resultatTabell[2] += 1;
      } else if (middelTempDag[i] < 10) {
        resultatTabell[3] += 1;
      } else {
        resultatTabell[4] += 1;
      }
    }
    return resultatTabell;
  }
}
