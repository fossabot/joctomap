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
package es.usc.citius.lab.joctomap;

import static org.junit.Assert.*;

import java.util.List;

import es.usc.citius.lab.joctomap.octree.JOctree;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import es.usc.citius.lab.joctomap.octree.JOctreeNode;

/**
 * Test case over the methods in {@link JOctreeNode}. The tests are executing in
 * ascending order to guarantee dependencies between them, if exist.
 * 
 * @author Adrián González Sieira {@literal <adrian.gonzalez@usc.es>}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JOctreeNodeTest {

	private static JOctree octree;
	private static JOctreeNode octreeNode; //used in tests
	private static boolean hasChildren; //used in tests
	private static double occupancy; //used in tests
	private static int numChildren; //used in tests
	private static List<JOctreeNode> children; //used in tests
	
	
	
	/**
	 * Initializes 
	 */
	@BeforeClass
	public static void initialize(){
		//store node of the readed octree
		octree = JOctreeTest.getOctree();
		octreeNode = octree.search(0, 0, 0, 0);
	}
	
	/**
	 * Test if the occupancy of the node is between 0 and 1
	 */
	@Test
	public void test01_getOccupancyTest() {
		occupancy = octreeNode.getOccupancy();
		assertTrue("Occupancy be between 0 and 1 > 1", occupancy >=0 && occupancy <= 1);
	}
	
	/**
	 * Test if the current node has children.
	 */
	@Test
	public void test02_hasChildrenTest(){
		hasChildren = octree.nodeHasChildren(octreeNode);
	}
	
	/**
	 * Test the query of number of children.
	 */
	@Test
	public void test03_numChildrenTest(){
		numChildren = octree.nodeNumChildren(octreeNode);
		assertTrue("hasChildren = " + hasChildren + " but numchildren = " + numChildren, (!hasChildren && numChildren == 0) || (hasChildren && numChildren > 0));
	}
	
	/**
	 * Test the method to retrieve the childrens of a node.
	 */
	@Test
	public void test04_getChildrenTest(){
		children = octree.getNodeChildren(octreeNode);
		assertTrue("numchildren and children array size differ", children.size() == numChildren);
		assertTrue("haschildren and numchildren with different information", (hasChildren && numChildren > 0) || (!hasChildren && numChildren == 0));
	}
	
	/**
	 * Test if the children exist.
	 */
	@Test
	public void test05_childExistsTest(){
		for(int i = 0; i < numChildren; i++){
			boolean exists = octree.nodeChildExists(octreeNode, i);
			assertTrue("child " + i + " does not exist but numchildren=" + numChildren, exists);
		}
	}
	
	/**
	 * Test if the current node is collapsible (not has children, or 
	 * has children and all of them exist, and they have all the same occupancy information).
	 */
	@Test
	public void test06_collapsibleTest(){
		//retrieve collapsibility
		boolean collapsible = octree.isNodeCollapsible(octreeNode);
		//check equality of the nodes
		boolean equals = true;
		//check occupancy of the children
		for(JOctreeNode child : children){
			if(Double.compare(child.getOccupancy(), occupancy) != 0){
				equals = false;
				break;
			}
		}
		//assertion conditions
		if(!collapsible){
			assertTrue("Not collapsibility conditions are not matched", !hasChildren || (hasChildren && (numChildren < 8 || (numChildren == 8 && !equals))));
		}
		else{
			assertTrue("Collapsibility conditions are not matched", hasChildren && numChildren == 8 && equals);
		}
	}
	


}
