package com.openkm.test;

import java.io.File;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.version.Version;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.TransientRepository;

public class IsCheckedOutTest {

	public static void main(String[] args) throws Exception {
		FileUtils.deleteDirectory(new File("repository"));
		Repository repo = new TransientRepository();
		Session session = repo.login(new SimpleCredentials("paco", "".toCharArray()));
		Node rootNode = session.getRootNode();
				
		// Add a new node
		Node child = rootNode.addNode("prueba", "nt:file");
		child.addMixin("mix:versionable");
		Node content = child.addNode("jcr:content", "nt:resource");
		content.setProperty("jcr:mimeType", "text/plain");
		content.setProperty("jcr:data", "En un lugar de La Mancha...");
		content.setProperty("jcr:lastModified", Calendar.getInstance());
		rootNode.save();
		
		System.out.println("isCheckedOut: "+child.isCheckedOut());
		showProperties(child);
		showVersion(child);
		
		// Performs checkin
		child.checkin();
		
		System.out.println();		
		System.out.println("isCheckedOut: "+child.isCheckedOut());
		showProperties(child);
		showVersion(child);
		
		// Say goodbye!
		session.logout();
	}
	
	/**
	 *
	 */
	public static void showProperties(Node node) throws ValueFormatException, IllegalStateException, RepositoryException {
		System.out.println("## PROPERTIES ##");
		
		for (PropertyIterator pit = node.getProperties(); pit.hasNext(); ) {
			Property prop = pit.nextProperty();
			System.out.print("* "+prop.getName()+" => ");
			
			if (prop.getDefinition().isMultiple()) {
				for (int i=0; i<prop.getValues().length; i++) {
					System.out.print(prop.getValues()[i].getString()+", ");
				}
			} else {
				System.out.print(prop.getValue().getString());
			}
			
			System.out.println();
		}
	}
	
	/**
	 * @throws RepositoryException 
	 * @throws UnsupportedRepositoryOperationException 
	 * 
	 */
	public static void showVersion(Node node) throws UnsupportedRepositoryOperationException, RepositoryException {
		System.out.println("## VERSION ##");
		
		Version ver = node.getBaseVersion();
		System.out.println("* Name: "+ver.getName());
		System.out.println("* Created: "+ver.getCreated());
	}
}
