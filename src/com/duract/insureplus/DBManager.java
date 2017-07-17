package com.duract.insureplus;

import java.io.IOException;

public class DBManager {
//	private static String dbName = "INSURE_PLUS";
	private static CurrentOwner owner;

	public static CurrentOwner readOwner() throws IOException,
			ClassNotFoundException {
		if (owner != null) {
			owner.setNic(HomeActivity.CURRENT_OWNER);
		}
		return owner;
	}

	public static void deleteOwner() throws IOException {
		HomeActivity.CURRENT_OWNER = null;
	}

	public static void saveOwner(CurrentOwner owner) throws IOException,
			NullPointerException {
		DBManager.owner = owner;

		HomeActivity.CURRENT_OWNER = owner.getNic();

	}
}
