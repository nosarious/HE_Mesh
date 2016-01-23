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

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public abstract class HE_Element {

	protected static AtomicLong _currentKey=new AtomicLong(0);
	protected final long _key;
	protected long _labels;


	/**
	 *
	 */
	public HE_Element() {
		_key = _currentKey.getAndAdd(1);
		_labels = mergeLabels(-1, -1);
	}



	/**
	 *
	 *
	 * @param internal
	 * @param external
	 * @return
	 */
	private long mergeLabels(final int internal, final int external) {
		return (((long) internal) << 32) | (external & 0xffffffffL);

	}

	/**
	 *
	 *
	 * @param label
	 * @deprecated Use {@link #setTemporaryLabel(int)} instead
	 */
	public final void setInternalLabel(final int label) {
		setTemporaryLabel(label);
	}



	/**
	 *
	 *
	 * @param label
	 */
	public final void setTemporaryLabel(final int label) {
		_labels = mergeLabels(label, getLabel());
	}

	/**
	 *
	 *
	 * @param label
	 */
	public final void setLabel(final int label) {
		_labels = mergeLabels(getTemporaryLabel(), label);
	}


	/**
	 *
	 *
	 * @return
	 */
	public final long getKey() {
		return _key;
	}

	/**
	 *
	 *
	 * @return
	 * @deprecated Use {@link #getTemporaryLabel()} instead
	 */
	public final int getInternalLabel() {
		return getTemporaryLabel();
	}



	/**
	 *
	 *
	 * @return
	 */
	public final int getTemporaryLabel() {
		return (int) (_labels >> 32);

	}

	/**
	 *
	 *
	 * @return
	 */
	public final int getLabel() {
		return (int) _labels;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (int) (_key ^ (_key >>> 32));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof HE_Element)) {
			return false;
		}
		return ((HE_Element) other).getKey() == _key;
	}

	/**
	 *
	 *
	 * @param el
	 */
	public void copyProperties(final HE_Element el) {
		_labels = mergeLabels(el.getTemporaryLabel(), el.getLabel());
	}

	/**
	 *
	 */
	protected abstract void clear();
}
