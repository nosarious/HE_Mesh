/*
 * This file is part of HE_Mesh, a library for creating and manipulating meshes.
 * It is dedicated to the public domain. To the extent possible under law,
 * I , Frederik Vanhoutte, have waived all copyright and related or neighboring
 * rights.
 *
 * This work is published from Belgium. (http://creativecommons.org/publicdomain/zero/1.0/)
 *
 */
package wblut.hemesh;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javolution.util.FastTable;

/**
 *
 */
public class HEC_FromHemeshFile extends HEC_Creator {

	/**
	 *
	 */
	private String path;

	/**
	 *
	 */
	public HEC_FromHemeshFile() {
		super();
		override = true;
	}

	/**
	 *
	 *
	 * @param path
	 */
	public HEC_FromHemeshFile(final String path) {
		this();
		this.path = path;
	}

	/**
	 *
	 *
	 * @param path
	 * @return
	 */
	public HEC_FromHemeshFile setPath(final String path) {
		this.path = path;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (path == null) {
			return null;
		}
		final StringBuilder contents = new StringBuilder();
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			final BufferedReader input = new BufferedReader(new FileReader(path));
			try {
				String line = null; // not declared within while loop
				/*
				 * readLine is a bit quirky : it returns the content of a line
				 * MINUS the newline. it returns null only for the END of the
				 * stream. it returns an empty String if two newlines appear in
				 * a row.
				 */
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		final String data = contents.toString();
		final String[] result = data.split(System.getProperty("line.separator"));
		int id = 0;
		String[] subresult = result[id].split("\\s");
		id++;
		final int numVertices = Integer.parseInt(subresult[0]);
		final int numHalfedges = Integer.parseInt(subresult[1]);
		final int numFaces = Integer.parseInt(subresult[2]);
		final HE_Mesh mesh = new HE_Mesh();
		final FastTable<HE_Vertex> vertices = new FastTable<HE_Vertex>();
		for (int i = 0; i < numVertices; i++) {
			vertices.add(new HE_Vertex());
		}
		final FastTable<HE_Halfedge> halfedges = new FastTable<HE_Halfedge>();
		for (int i = 0; i < numHalfedges; i++) {
			halfedges.add(new HE_Halfedge());
		}
		final FastTable<HE_Face> faces = new FastTable<HE_Face>();
		for (int i = 0; i < numFaces; i++) {
			faces.add(new HE_Face());
		}
		double x, y, z;
		int heid, vid, henextid, hepairid, fid, hasuvw;
		HE_Vertex v;
		for (int i = 0; i < numVertices; i++) {
			v = vertices.get(i);
			subresult = result[id].split("\\s");
			x = Double.parseDouble(subresult[0]);
			y = Double.parseDouble(subresult[1]);
			z = Double.parseDouble(subresult[2]);
			heid = Integer.parseInt(subresult[3]);
			v.setColor(Integer.parseInt(subresult[4]));
			v.labels = Long.parseLong(subresult[5]);
			hasuvw = Integer.parseInt(subresult[6]);
			v.set(x, y, z);
			if (heid > -1) {
				mesh.setHalfedge(v, halfedges.get(heid));
			}
			if (hasuvw == 1) {
				v.setUVW(Double.parseDouble(subresult[7]), Double.parseDouble(subresult[8]),
						Double.parseDouble(subresult[9]));
			}

			id++;
		}
		HE_Halfedge he;
		for (int i = 0; i < numHalfedges; i++) {
			he = halfedges.get(i);
			subresult = result[id].split("\\s");
			vid = Integer.parseInt(subresult[0]);
			henextid = Integer.parseInt(subresult[1]);
			hepairid = Integer.parseInt(subresult[2]);
			fid = Integer.parseInt(subresult[3]);
			he.setColor(Integer.parseInt(subresult[4]));
			he.labels = Long.parseLong(subresult[5]);
			hasuvw = Integer.parseInt(subresult[6]);
			if (vid > -1) {
				mesh.setVertex(he, vertices.get(vid));
			}
			if (henextid > -1) {
				mesh.setNext(he, halfedges.get(henextid));
			}
			if (hepairid > -1) {
				mesh.setPair(he, halfedges.get(hepairid));

			}
			if (fid > -1) {
				mesh.setFace(he, faces.get(fid));
			}
			if (hasuvw == 1) {
				he.setUVW(Double.parseDouble(subresult[7]), Double.parseDouble(subresult[8]),
						Double.parseDouble(subresult[9]));
			}
			id++;
		}

		HE_Face f;
		for (int i = 0; i < numFaces; i++) {
			f = faces.get(i);
			subresult = result[id].split("\\s");
			heid = Integer.parseInt(subresult[0]);
			if (heid > -1) {
				mesh.setHalfedge(f, halfedges.get(heid));
			}
			f.setColor(Integer.parseInt(subresult[1]));
			f.setTextureId(Integer.parseInt(subresult[2]));
			f.labels = Long.parseLong(subresult[3]);
			id++;
		}
		mesh.addVertices(vertices);
		mesh.addHalfedges(halfedges);
		mesh.addFaces(faces);
		return mesh;
	}
}
