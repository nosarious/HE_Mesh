/*
 *
 */
package wblut.hemesh;

/**
 * Random Access Set of HE_Element
 * Combines advantages of an ArrayList - random access, sizeable -
 * with those of a HashMap - fast lookup, unique members -.
 */
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javolution.util.FastTable;

/**
 *
 *
 * @param <E>
 */
public class HE_RASVertex extends HE_RAS<HE_Vertex> {
    /**
     *
     */
    List<HE_Vertex> objects;
    /**
     *
     */
    TLongIntMap indices;
    TDoubleArrayList ordinates;

    /**
     *
     */
    public HE_RASVertex() {
	objects = new FastTable<HE_Vertex>();
	indices = new TLongIntHashMap(10, 0.5f, -1L, -1);
	ordinates = new TDoubleArrayList(100, Double.NaN);
    }

    /**
     *
     *
     * @param n
     */
    public HE_RASVertex(final int n) {
	this();
    }

    /**
     *
     *
     * @param items
     */
    public HE_RASVertex(final Collection<HE_Vertex> items) {
	this();
	for (final HE_Vertex e : items) {
	    add(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#add(wblut.hemesh.HE_Element)
     */
    @Override
    public boolean add(final HE_Vertex item) {
	if (item == null) {
	    return false;
	}
	if (indices.putIfAbsent(item._key, objects.size()) < 0) {
	    objects.add(item);
	    return true;
	}
	return false;
    }

    /**
     * Override element at position <code>id</code> with last element.
     *
     * @param id
     * @return
     */
    @Override
    public HE_Vertex removeAt(final int id) {
	if (id >= objects.size()) {
	    return null;
	}
	final HE_Vertex res = objects.get(id);
	indices.remove(res._key);
	final HE_Vertex last = objects.remove(objects.size() - 1);
	// skip filling the hole if last is removed
	if (id < objects.size()) {
	    indices.put(last._key, id);
	    objects.set(id, last);
	}
	return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#remove(wblut.hemesh.HE_Element)
     */
    @Override
    public boolean remove(final HE_Vertex item) {
	if (item == null) {
	    return false;
	}
	// @SuppressWarnings(value = "element-type-mismatch
	final int id = indices.get(item._key);
	if (id == -1) {
	    return false;
	}
	removeAt(id);
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#get(int)
     */
    @Override
    public HE_Vertex get(final int i) {
	return objects.get(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#getByIndex(int)
     */
    @Override
    public HE_Vertex getByIndex(final int i) {
	return objects.get(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#getByKey(java.lang.Long)
     */
    @Override
    public HE_Vertex getByKey(final Long key) {
	final int i = indices.get(key);
	if (i == -1) {
	    return null;
	}
	return objects.get(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#getIndex(wblut.hemesh.HE_Element)
     */
    @Override
    public int getIndex(final HE_Vertex object) {
	return indices.get(object._key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#pollRandom(java.util.Random)
     */
    @Override
    public HE_Vertex pollRandom(final Random rnd) {
	if (objects.isEmpty()) {
	    return null;
	}
	final int id = rnd.nextInt(objects.size());
	return removeAt(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#size()
     */
    @Override
    public int size() {
	return objects.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#contains(wblut.hemesh.HE_Element)
     */
    @Override
    public boolean contains(final HE_Vertex object) {
	if (object == null) {
	    return false;
	}
	return indices.containsKey(object._key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#containsKey(java.lang.Long)
     */
    @Override
    public boolean containsKey(final Long key) {
	return indices.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#iterator()
     */
    @Override
    public Iterator<HE_Vertex> iterator() {
	return objects.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_RAS#getObjects()
     */
    @Override
    public List<HE_Vertex> getObjects() {
	return objects;
    }
}