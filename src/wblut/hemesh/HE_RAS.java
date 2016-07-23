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

/**
 * Random Access Set of HE_Element
 * Combines advantages of an ArrayList - random access, sizeable -
 * with those of a HashMap - fast lookup, unique members -.
 */
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 *
 * @param <E>
 */
public abstract class HE_RAS<E extends HE_Element> extends AbstractSet<E> {

	/**
	 *
	 */
	public HE_RAS() {
	}

	/**
	 *
	 *
	 * @param items
	 */
	public HE_RAS(final Collection<E> items) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#add(java.lang.Object)
	 */
	@Override
	public abstract boolean add(final E item);

	/**
	 * Override element at position <code>id</code> with last element.
	 *
	 * @param id
	 * @return
	 */
	public abstract E removeAt(final int id);

	/**
	 *
	 *
	 * @param item
	 * @return
	 */
	public abstract boolean remove(final E item);

	/**
	 *
	 *
	 * @param i
	 * @return
	 */
	public abstract E get(final int i);

	/**
	 *
	 *
	 * @param i
	 * @return
	 */
	public abstract E getWithIndex(final int i);

	/**
	 *
	 *
	 * @param key
	 * @return
	 */
	public abstract E getWithKey(final long key);

	/**
	 *
	 *
	 * @param object
	 * @return
	 */
	public abstract int indexOf(final E object);

	/**
	 *
	 *
	 * @param rnd
	 * @return
	 */
	public abstract E pollRandom(final Random rnd);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public abstract int size();

	/**
	 *
	 *
	 * @param object
	 * @return
	 */
	public abstract boolean contains(final E object);

	/**
	 *
	 *
	 * @param key
	 * @return
	 */
	public abstract boolean containsKey(final Long key);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public abstract Iterator<E> iterator();

	/**
	 *
	 *
	 * @return
	 */
	protected abstract List<E> getObjects();
}