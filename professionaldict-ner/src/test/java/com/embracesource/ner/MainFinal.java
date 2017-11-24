package com.embracesource.ner;

public class MainFinal {

	public static void main(String[] args) {
		System.out.println(ed("china", "chino"));
		System.out.println(ed("sailn", "failing"));
		System.out.println(ed("recoginze", "recognize"));
		System.out.println(ed("hack", "hankcs"));
		System.out.println(ed("国中人", "我们是中国人"));
		System.out.println(ed("中国人", "我们是中国人"));
	}

	public static int ed(String wrongWord, String rightWord) {
		// 构造两个 NULL+字串，免得下标越界
		wrongWord = '+' + wrongWord;
		rightWord = '+' + rightWord;
		final int m = wrongWord.length();
		final int n = rightWord.length();

		int[][] d = new int[m + 1][n + 1];
		final int boarder = Math.max(m, n);
		for (int j = 2; j <= n; ++j) {
			d[0][j] = boarder;
			d[1][j] = j;
		}
		for (int i = 2; i <= m; ++i) {
			d[i][0] = boarder;
			d[i][1] = i;
		}

		// for (int[] l : d)
		// {
		// System.out.println(Arrays.toString(l));
		// }

		for (int i = 2; i <= m; ++i) {
			char ci = wrongWord.charAt(i - 1);
			for (int j = 2; j <= n; ++j) {
				char cj = rightWord.charAt(j - 1);
				if (ci == cj) {
					d[i][j] = d[i - 1][j - 1];
				} else if (ci == rightWord.charAt(j - 2) && cj == wrongWord.charAt(i - 2)) {
					d[i][j] = Math.min(d[i - 2][j - 2], Math.min(d[i - 1][j], d[i][j - 1])) + 1;
				} else {
					// 等号右边的分别代表 将ci改成cj 错串加cj 错串删ci
					d[i][j] = Math.min(d[i - 1][j - 1] + 1, Math.min(d[i][j - 1] + 1, d[i - 1][j] + 1));
				}
			}
		}

		// System.out.println();
		// for (int[] l : d)
		// {
		// System.out.println(Arrays.toString(l));
		// }

		return d[m][n];
	}
}