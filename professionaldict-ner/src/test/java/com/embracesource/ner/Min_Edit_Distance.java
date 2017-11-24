package com.embracesource.ner;

//最小编辑距离的算法动态规划实现
public class Min_Edit_Distance {
	
	private static int cost = 0;

	public static int minEdit_distance(String source, String target) {
		final int n = target.length();
		final int m = source.length();

		if (m == 0)
			return m;
		if (n == 0)
			return n;

		int[][] distance_matrix = new int[m + 1][n + 1];
		distance_matrix[0][0] = 0;

		for (int i = 0; i <= n; i++) {
			distance_matrix[0][i] = i;
		}
		for (int j = 0; j <= m; j++) {
			distance_matrix[j][0] = j;
		}

		for (int i = 1; i <= m; i++) {
			char ci = source.charAt(i - 1);
			for (int j = 1; j <= n; j++) {
				char cj = target.charAt(j - 1);
				if (ci == cj) {
					cost = 0;
				} else {
					cost = 2;
				}
				distance_matrix[i][j] = Math.min(distance_matrix[i - 1][j - 1] + cost,
						Math.min(distance_matrix[i - 1][j] + 1, distance_matrix[i][j - 1] + 1));
			}
		}

		return distance_matrix[m][n];
	}

	public static void main(String[] args) {
		System.out.println(minEdit_distance("china", "chino"));
		System.out.println(minEdit_distance("sailn", "failing"));
		System.out.println(minEdit_distance("recoginze", "recognize"));
		System.out.println(minEdit_distance("hack", "hankcs"));
		System.out.println(minEdit_distance("国中人", "我们是中国人"));
		System.out.println(minEdit_distance("中国人", "我们是中国人"));
	}
}