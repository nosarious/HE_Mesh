/*
 * This file is part of HE_Mesh, a library for creating and manipulating meshes.
 * It is dedicated to the public domain. To the extent possible under law,
 * I , Frederik Vanhoutte, have waived all copyright and related or neighboring
 * rights.
 *
 * This work is published from Belgium. (http://creativecommons.org/publicdomain/zero/1.0/)
 *
 */

package wblut.core;

public class WB_Version {

	public static final WB_Version CURRENT_VERSION = new WB_Version();

	public static final int MAJOR = 5;

	public static final int MINOR = 1;

	public static final int PATCH = 0;

	private static final String releaseInfo = "Saturn";

	/**
	 *
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		System.out.println(CURRENT_VERSION);
	}

	/**
	 *
	 */
	private WB_Version() {
	}

	/**
	 *
	 *
	 * @return
	 */
	public static int getMajor() {
		return MAJOR;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static int getMinor() {
		return MINOR;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static int getPatch() {
		return PATCH;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final String ver = "W:Blut HE_Mesh " + MAJOR + "." + MINOR + "." + PATCH;
		if (releaseInfo != null && releaseInfo.length() > 0) {
			return ver + " " + releaseInfo;
		}
		return ver;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static String version() {
		return CURRENT_VERSION.toString();
	}
}