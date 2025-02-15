/**
 * Copyright (C) 2014-2017 Adrián González Sieira (adrian.gonzalez@usc.es)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.usc.citius.lab.joctomap.octree;

import es.usc.citius.lab.joctomap.iterators.OctreeIterator;
import es.usc.citius.lab.joctomap.util.NativeObject;
import es.usc.citius.lab.motionplanner.core.spatial.Point3D;

import java.util.List;

/**
 * This class wraps an octree object in the Octomap library to be used in 
 * java; native wrapped functions include the following:
 * <ul>
 *      <li>Translation between world coordinates and map keys.</li>
 *      <li>Adjust a key to different depth levels.</li>
 *      <li>Look for a node in the octree.</li>
 *      <li>Query occupancy of a node.</li>
 *      <li>Update occupancy of a node.</li>
 *      <li>Manage limits and usage of the octree bounding box.</li>
 *      <li>Query structure of the octree: size, depth, resolution.</li>
 *      <li>Query size of a node.</li>
 *      <li>Query center/limits of the octree: bounding box, space.</li>
 *      <li>Query if the bounding box is set and applied.</li>
 *      <li>Create iterators to traverse the leaf nodes of the octree.</li>
 *      <li>Prune and expand nodes according to their occupancy.</li>
 *      <li>Recalculate occupancy of inner nodes.</li>
 *      <li>Save and load octree objects to disk.</li>
 *      <li>Create empty octree.</li>
 * </ul>
 * 
 * @author adrian.gonzalez
 */
public class JOctree extends NativeObject{
    
	/**
	 * Initializes the JOctree with a pointer to the native OcTree object of
	 * the Octomap library. Used by native methods.
	 * 
	 * @param pointer pointer to the native OcTree object (JNI global reference)
	 */
	private JOctree(long pointer) {
		super(pointer);
	}

	@Override
	public native void dispose();

	@Override
	protected void finalize() throws Throwable {
		super.finalize(); //To change body of generated methods, choose Tools | Templates.
		dispose();
	}
        
	/*
	 * *******************************************************************************
	 * *					Position query functions	                             *
	 * *******************************************************************************
	 */
	/**
	 * Retrieves the position of the cell at a current 
	 * location.
	 *
	 * @param x query point, coordinate X
	 * @param y query point, coordinate Y
	 * @param z query point, coordinate Z
	 * @return key of the node at the given position
	 */
	public native JOctreeKey coordToKey(float x, float y, float z);
	
	/**
	 * Retrieves the position of the cell at a current 
	 * location, at a given depth.
	 *
	 * @param x query point, coordinate X
	 * @param y query point, coordinate Y
	 * @param z query point, coordinate Z
	 * @param depth level to search (depth=0 means search in the full octree)
	 * @return key of the node at the given position and depth
	 */
	public native JOctreeKey coordToKey(float x, float y, float z, int depth);
	
	/**
	 * Given an {@link JOctreeKey}, adjusts it to a given depth in the octree.
	 * 
	 * @param key to be adjusted
	 * @param depth to obtain the adjusted {@link JOctreeKey}
	 * @return adjusted {@link JOctreeKey} to the specified depth
	 */
	public native JOctreeKey adjustKeyAtDepth(JOctreeKey key, int depth);
	
	/**
	 * Given a {@link Point3D}, retrieves the {@link JOctreeKey} of the node
	 * that occupies that position.
	 * 
	 * @param point coordinates to retrieve the corresponding key
	 * @return {@link JOctreeKey} of the node that occupies the queried position
	 */
	public native JOctreeKey coordToKey(Point3D point);
	
	/**
	 * Given a {@link Point3D}, retrieves the {@link JOctreeKey} of the node
	 * that occupies that position at a current depth level.
	 * 
	 * @param point coordinate to query the key of the node at that position
	 * @param depth of the {@link JOctreeKey} in the octree
	 * @return {@link JOctreeKey} of cell which contains the given point at a given depth
	 */
	public native JOctreeKey coordToKey(Point3D point, int depth);
	
	/**
	 * Retrieves the {@link Point3D} that corresponds to the coordinates of the
	 * center of the node identified by the given its {@link JOctreeKey}.
	 * 
	 * @param key identifier of the node
	 * @return coordinates of the center of the node
	 */
	public native Point3D keyToCoord(JOctreeKey key);
	
	/**
	 * Retrieves the {@link Point3D} that corresponds to the coordinates of the
	 * center of the node identified by the given its {@link JOctreeKey} and the
	 * depth of the node.
	 * 
	 * @param key identifier of the node
	 * @param depth of the {@link JOctreeKey} in the octree
	 * @return coordinates of the center of the node
	 */
	public native Point3D keyToCoord(JOctreeKey key, int depth);
	
	/*
	 * *******************************************************************************
	 * *					Node query functions	                             	 *
	 * *******************************************************************************
	 */
	/**
	 * Retrieves a node of the octree given the identifier of the node ({@link JOctreeKey}) and
	 * the depth to search in the {@link JOctree}.
	 * 
	 * @param key identifier of the node
	 * @param depth level to search (depth=0 means search in the full octree)
	 * @return node of the octree, if found, null otherwhise
	 */
	public native JOctreeNode search(JOctreeKey key, int depth);
	
	/**
	 * Calls the method {@link #search(JOctreeKey, int)} with the 
	 * default value depth=0. Default values in argument methods are 
	 * not supported by Java, so this method overload tries to emulate 
	 * the call with the default value depth = 0;
	 * 
	 * @param key identifier of the node
	 * @return node of the octree, if found, null otherwhise
	 */
	public JOctreeNode search(JOctreeKey key){
		return search(key, 0);
	}
	
	/**
	 * Retrieves a node of the {@link JOctree} in a given position of the 3D space and a depth level
	 * of the octree.
	 *
	 * @param x query point, coordinate X
	 * @param y query point, coordinate Y
	 * @param z query point, coordinate Z
	 * @param depth level to search (depth=0 means search in the full octree)
	 * @return node of the octree, if found, null otherwhise
	 */
	public native JOctreeNode search(float x, float y, float z, int depth);
	
	/**
	 * Calls the method {@link #search(float, float, float, int)} with the default value of 
	 * depth=0. Default values in argument methods are not supported by Java, so this method overload
	 * tries to emulate the call with the default value depth = 0;
	 * @param x query point, coordinate X
	 * @param y query point, coordinate Y
	 * @param z query point, coordinate Z
	 * @return node of the octree, if found, null otherwise
	 */
	public JOctreeNode search(float x, float y, float z){
		return search(x, y, z, 0);
	}
	
	/**
	 * Queries if a node is occupied, according to its occupancy probability.
	 * 
	 * @param node node to query if is occupied
	 * @return result of the query
	 */
	public native boolean isNodeOccupied(JOctreeNode node);
	
	
	/*
	 * *******************************************************************************
	 * *					Update functions		                             	 *
	 * *******************************************************************************
	 */
	/**
	 * Updates the occupancy information of a {@link JOctreeNode}, retrieving the node 
	 * instance affected.
	 *
	 * @param x query point, coordinate X
	 * @param y query point, coordinate Y
	 * @param z query point, coordinate Z
	 * @param occupied occupancy value to set
	 * @return {@link JOctreeNode} affected
	 */
	public native JOctreeNode updateNode(float x, float y, float z, boolean occupied);
	
	/**
	 * Enables or disables the usage of the bounding box to limit the octree 
	 * update region.
	 * 
	 * @param value true/false
	 */
	public native void useBBXLimit(boolean value);
	
	/**
	 * Updates the bounding box limits.
	 * 
	 * @param min minimum {@link Point3D} of the bounding box
	 * @param max maximum {@link Point3D} of the bounding box
	 */
	public native void setBBX(Point3D min, Point3D max);
	
	/**
	 * Updates the minimum bounding box limit.
	 * 
	 * @param min {@link Point3D} of the bounding box
	 */
	public native void setBBXMin(Point3D min);
	
	/**
	 * Updates the maximum bounding box limit.
	 * 
	 * @param max {@link Point3D} of the bounding box
	 */
	public native void setBBXMax(Point3D max);
	
	/*
	 * *******************************************************************************
	 * *				Octree information query functions                           *
	 * *******************************************************************************
	 */
	/**
	 * Retrieves the number of leafs in the octree.
	 *
	 * @return number of leafs in the tree
	 */
	public native int size();
	/**
	 * Retrieves the maximum depth of the octree.
	 * 
	 * @return maximum depth value
	 */
	public native int getTreeDepth();
	
	/**
	 * Retrieves the minimum resolution of the octree nodes.
	 * 
	 * @return minimum node resolution
	 */
	public native float getResolution();
	
	/**
	 * Retrieves the size of the nodes at a given depth (aka. resolution at a given depth).
	 * 
	 * @param depth object of the query
	 * @return node resolution at the depth level
	 */
	public native float getNodeSize(int depth);
	
	/**
	 * Returns the central point of the BBX queries, if set.
	 * 
	 * @return {@link Point3D} instance with the center of the BBX, null if not set
	 */
	public native Point3D getBBXCenter();
	
	/**
	 * Returns the min point of the BBX queries, if set.
	 * 
	 * @return {@link Point3D} instance with the min of the BBX, null if not set
	 */
	public native Point3D getBBXMin();
	
	/**
	 * Returns the max point of the BBX queries, if set.
	 * 
	 * @return {@link Point3D} instance with the max of the BBX, null if not set
	 */
	public native Point3D getBBXMax();
	
	/**
	 * Minimum point of the bounding box of the known space in the octree.
	 * 
	 * @return the minimum {@link Point3D} of the known space
	 */
	public native Point3D getMetricMin();
	
	/**
	 * Maximum point of the bounding box of the known space in the octree.
	 * 
	 * @return the maximum {@link Point3D} of the known space
	 */
	public native Point3D getMetricMax();
	
	/**
	 * Dimensions of the bounding box that contains the known space of the octree.
	 * 
	 * @return a float[3] array with the dimensions x, y and z of the known space
	 */
	public native float[] getMetricSize();
	
	/**
	 * Queries if the bounding box is set. Returns true if {@link #getBBXMin()} and {@link #getBBXMax()}
	 * return the same value, as the BBX with the same beginning and ending point does not exist.
	 * 
	 * @return true if {@link #getBBXMin()} != {@link #getBBXMax()}
	 * @see #setBBX(Point3D, Point3D)
	 */
	public native boolean isBBXSet();
	
	/**
	 * Queries if the bounding box is being applied to the octree. When it is, 
	 * the octree only applies updates for the region within the bounding box.
	 * 
	 * @return true if a BBX being applied in the octree, false otherwise
	 * @see #useBBXLimit(boolean)
	 */
	public native boolean isBBXApplied();


	/*
	 * *******************************************************************************
	 * *				Node interaction functions 		                             *
	 * *******************************************************************************
	 */
	/**
	 * Retrieve the i-th child of the node, if exists.
	 *
	 * @param node instance of {@link JOctreeNode} to query
	 * @param i child number
	 * @return corresponding {@link JOctreeNode} if child exists, null otherwise
	 */
	public native JOctreeNode getNodeChild(JOctreeNode node, int i);

	/**
	 * Queries if this is a leaf node.
	 *
	 * @param node instance of {@link JOctreeNode} to query
	 * @return true if this is not a leaf node
	 */
	public native boolean nodeHasChildren(JOctreeNode node);

	/**
	 * Queries if this is a collapsible node.
	 *
	 * @param node instance of {@link JOctreeNode} to query
	 * @return true if all childs of the node exist, and they have not children of their own
	 */
	public native boolean isNodeCollapsible(JOctreeNode node);

	/**
	 * Queries if the i-th child of the node exists.
	 *
	 * @param node instance of {@link JOctreeNode} to query
	 * @param i i-th element
	 * @return true if the i-th child of this node exists
	 */
	public native boolean nodeChildExists(JOctreeNode node, int i);

	/**
	 * Retrieves the list of children of the given node.
	 *
	 * @param node instance of {@link JOctreeNode} to query
	 * @return list of children of the node
	 */
	public native List<JOctreeNode> getNodeChildren(JOctreeNode node);

	/**
	 * Queries the number of children of this node.
	 *
	 * @param node instance of {@link JOctreeNode} to query
	 * @return number of children
	 */
	public native int nodeNumChildren(JOctreeNode node);


	/*
	 * *******************************************************************************
	 * *				Iterator retrieval functions	                             *
	 * *******************************************************************************
	 */
	/**
	 * Retrieve a bounding box iterator over the {@link JOctreeKey} of the leaf nodes 
	 * of an octree. It is possible to define a maximum depth of the elements returned by
	 * the iterator.
	 * 
	 * @param min min position of the bbx
	 * @param max max position of the bbx
	 * @param maxDepth max depth of the leaf nodes
	 * @return {@link JOctreeKey} iterator
	 */
	public native OctreeIterator leafBBXIterator(Point3D min, Point3D max, int maxDepth);
	
	/**
	 * Retrieve bounding box iterator over {@link JOctreeKey} of the leaf nodes of an octree.
	 * It is possible to define a maximum depth of the elements returned by
	 * the iterator.
	 * 
	 * @param min min node key of the bbx
	 * @param max max node key of the bbx
	 * @param maxDepth max depth of the leaf nodes
	 * @return {@link JOctreeKey} iterator
	 */
	public native OctreeIterator leafBBXIterator(JOctreeKey min, JOctreeKey max, int maxDepth);
        
	/**
	 * Prunes all the nodes in the octree (cuts all the children when they have the same
	 * value).
	 */
	public native void prune();

	/**
	 * Expands all the nodes in the octree to the minimum resolution.
	 */
	public native void expand();

	/**
	 * Updates the occupancy of inner nodes to reflect the children's occupancy; needed to
	 * correct the multi-resolution behavior when updated the occupancy with lazy evaluation enabled.
	 */
	public native void updateInnerOccupancy();
	
	/*
	 * *******************************************************************************
	 * *							I/O functions                                    *
	 * *******************************************************************************
	 */
	/**
	 * Native implementation of the native function "write to file", that stores the 
	 * information contained in the pointer of the direction of memory of the native 
	 * OcTree object in the given path.
	 * 
	 * @param filename path to store the file
	 * @return true if the file has correctly been stored
	 */
	public native boolean write(String filename);
	
	/**
	 * Native implementation of the native function "read from file", that loads
	 * the information using the native implementation of Octomap and returns a new instance
	 * of {@link JOctree}.
	 * 
	 * @param filename name of file to read
	 * @return pointer of the direction of memory of the loaded native OcTree object
	 */
	public static native JOctree read(String filename);
	
	/**
	 * Creates a new {@link JOctree} with a minimum resolution. An empty octree is not fully
	 * usable (you cannot query information about the bounds of the octree, or query the 
	 * {@link JOctreeKey} corresponding to a posiition) until the first occupancy information 
	 * is added using the modification methods like {@link #updateNode(float, float, float, boolean)}.
	 * 
	 * @param res minimum node resolution
	 * @return octree with the given resolution
	 */
	public static native JOctree create(float res);


	/*
	 * *******************************************************************************
	 * *							Changes detection                                *
	 * *******************************************************************************
	 */

	/**
	 * Enables/disables changes detection. If enabled, a list of changes can be retrieved from this
	 * object when information in cells change over time.
	 *
	 * @param enable true for enable, false otherwise
	 */
	public native void enableChangeDetection(boolean enable);

	/**
	 * Clear the list of changes produced in the octree since the last reset.
	 */
	public native void resetChangeDetection();

	/**
	 * Retrieves the list of changed keys since the last reset.
	 *
	 * @return list of {@link JOctreeKey}, cells changed since the last reset.
	 */
	public native List<JOctreeKey> keysChanged();

	/**
	 * Retrieves the tree type.
	 *
	 * @return C/C++ class name.
	 */
	public native String getTreeType();
}
