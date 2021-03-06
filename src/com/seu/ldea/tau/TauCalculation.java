package com.seu.ldea.tau;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * Tau的计算，向量距离不是bigdecimal类型的
 * 1-2*reversion/(n*(n-1))
 * @author Lynn
 *
 */
public class TauCalculation {
	 private long counter = 0;
	// 排序计算kendall tau值
	public  double calculateTau(int[] standard, int[] comparison) {
		
		if (standard.length != comparison.length) {
			throw new IllegalArgumentException("Array dimensions is not same");
		}
		BigDecimal bigN = new BigDecimal(String.valueOf(standard.length));
		int N = bigN.intValue();
		int[] aIndex = new int[N];// 记录a数组的索引
		for (int i = 0; i < N; i++) {
			aIndex[standard[i]] = i;
		}
		int[] bIndex = new int[N];// b数组引用a数组的索引
		for (int i = 0; i < N; i++) {
			bIndex[i] = aIndex[comparison[i]];
		}
		//BigDecimal distance = insertionCount(bIndex);
		BigDecimal distance = mergeCount(bIndex);
		System.out.println("distance is " + distance + " N is " + N + " bigN " + bigN);
		// Kendell correlation co-efficient
		// BigDecimal nBigDecimal = new BigDecimal(N * (N - 1) );
		BigDecimal denominator = bigN.multiply(bigN.subtract(BigDecimal.valueOf(1)));
		System.out.println("denominator " + denominator);
		
		BigDecimal one = new BigDecimal(1.0);
		BigDecimal nominator = distance.multiply(BigDecimal.valueOf(2));
		//BigDecimal inconsist = distance.multiply(BigDecimal.valueOf(4));
		// BigDecimal divide = mutiple.divide(nBigDecimal, 5, 4);
		BigDecimal kendellTau = one.subtract(nominator.divide(denominator, 5, 4));
		System.out.println("Tau  " + kendellTau);
		return kendellTau.doubleValue();
	}

	
	// 使用归并排序方法求逆序数
    private  int[] aux;

    public  BigDecimal mergeCount(int[] a) {
        aux = new int[a.length];
        mergeSort(a, 0, a.length-1);
        return new BigDecimal(counter);
    }

    
    private  void mergeSort(int[] a, int lo, int hi) {
        if (hi <= lo) {
            return;
        }
        int mid = lo + (hi - lo) / 2;
        mergeSort(a, lo, mid);
        mergeSort(a, mid + 1, hi);
        merge(a, lo, mid, hi);
    }

    public  void merge(int[] a, int lo, int mid, int hi) {
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                a[k] = aux[j++];
            } else if (j > hi) {
                a[k] = aux[i++];
            } else if (aux[j] < aux[i]) {
                a[k] = aux[j++];
                counter += mid - i + 1;// 每个比前子数组小的后子数组元素，逆序数为前子数组现有的长度
            } else {
                a[k] = aux[i++];
            }
        }
    }
	// 使用插入排序方法求逆序数
	public  long insertionCount(int[] b) {
		long counter = 0;
		for (int i = 1; i < b.length; i++) {
			for (int j = i; j > 0 && b[j] < b[j - 1]; j--) {
				//System.out.println("a[j] is " + b[j] + "a[j-1] is " + b[j-1]);
				int temp = b[j];
				b[j] = b[j - 1];
				b[j - 1] = temp;
				//System.out.println("a[j] is " + b[j] + "a[j-1] is " + b[j-1]);
				counter++;
				//System.out.println("counter- " + counter);
			}
		}
		for(int i = 0; i< b.length; i++)
			System.out.print(b[i] + " " );
		return counter;
	}

	public static void main(String[] args) throws IOException {
		long t1 = System.currentTimeMillis();

		String rescalEmbending = "C:\\Users\\Lynn\\Desktop\\Academic\\LinkedDataProject\\NormalizedEmbeddingFile\\Normailzed-www-2010-complete-latent10-lambda0.embeddings.txt";
		// RESCAL张量的对象距离矩阵
		Double[][] rescalDistanceMatrix = TauRescalDistance.getVectorDistanceMatrix(rescalEmbending, "Cosine-2");
	   // TauRescalDistance.printMatrix(rescalDistanceMatrix);
		//System.out.println(rescalDistanceMatrix.length );
		// 将向量距离排序
		ArrayList<Entry<Integer, Double>> rescalDistanceList = TauRescalDistance.sortVectorDistance(rescalDistanceMatrix);
		String fileName = "www-2010-complete";
		String entityFile = "C:\\Users\\Lynn\\Desktop\\Academic\\LinkedDataProject\\rescalInput\\" + fileName + "\\entity-ids";
		String tripleFile = "C:\\Users\\Lynn\\Desktop\\Academic\\LinkedDataProject\\rescalInput\\" + fileName + "\\triple";
		// 标准距离
		int[][] matrix = StandardDistance.getMatrix(entityFile, tripleFile);
		int[][] standardDistanceMatrix  = StandardDistance.floydDistance(matrix);
		// 标准距离排序
		ArrayList<Entry<Integer, Integer>> standardDistanceList = StandardDistance.sortStandard(standardDistanceMatrix);
		
		String resultFile = "C:\\Users\\Lynn\\Desktop\\Academic\\LinkedDataProject\\TauResult\\tauResult-" + fileName;
				
		FileWriter fileWriter = new FileWriter(resultFile);
		int length = rescalDistanceList.size();
		int[] standard = new int[length];
		int[] comparison = new int[length];
		for (int i = 0; i < length; i++) {
			standard[i] = standardDistanceList.get(i).getKey();
			comparison[i] = rescalDistanceList.get(i).getKey();
			fileWriter.write(standard[i] + "   " + standardDistanceList.get(i).getValue() + " ; " + comparison[i] + "  "
					+ rescalDistanceList.get(i).getValue() + "\n");
		}
		fileWriter.close();
		/* int[] standard = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
		 int[] comparison = new int[]{7,6,5,4,3,2,1,0};*/
	/*	int[] standard1 = new int[]{0,3,1,6,2,5,4};
		 int[] comparison1 = new int[]{1,0,3,6,4,2,5};*/
		System.out.println(new TauCalculation().calculateTau(standard, comparison));
		long t2 = System.currentTimeMillis();
		System.out.println("Calculation time cost " + (t2 - t1) / 60000.0);
		System.out.println("********************************");

	}
}
